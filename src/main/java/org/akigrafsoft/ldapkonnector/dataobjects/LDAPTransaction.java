/**
 * Open-source, by AkiGrafSoft.
 *
 * $Id:  $
 *
 **/
package org.akigrafsoft.ldapkonnector.dataobjects;

import java.util.ArrayList;

import org.akigrafsoft.ldapkonnector.LdapClientDataobject;
import org.akigrafsoft.ldapkonnector.NetworkErrorException;

import com.akigrafsoft.knetthreads.Message;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;
import com.unboundid.ldap.sdk.controls.TransactionSpecificationRequestControl;
import com.unboundid.ldap.sdk.extensions.EndTransactionExtendedRequest;
import com.unboundid.ldap.sdk.extensions.EndTransactionExtendedResult;
import com.unboundid.ldap.sdk.extensions.StartTransactionExtendedRequest;
import com.unboundid.ldap.sdk.extensions.StartTransactionExtendedResult;

public class LDAPTransaction extends LdapClientDataobject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2013222107547850384L;

	public LDAPTransactionResult result;

	private ArrayList<LDAPOperation> m_sequence = new ArrayList<LDAPOperation>();

	public LDAPTransaction(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(LDAPConnection connection) throws NetworkErrorException {

		// Use the start transaction extended operation to begin a transaction.
		StartTransactionExtendedResult startTxnResult;
		try {
			startTxnResult = (StartTransactionExtendedResult) connection
					.processExtendedOperation(new StartTransactionExtendedRequest());
			// This doesn't necessarily mean that the operation was successful,
			// since some kinds of extended operations return non-success
			// results under normal conditions.
		} catch (LDAPException e) {

			if (isNetworkError(e.getResultCode()))
				throw new NetworkErrorException(e.getMessage());

			// For an extended operation, this generally means that a problem
			// was encountered while trying to send the request or read the
			// result.
			startTxnResult = new StartTransactionExtendedResult(new ExtendedResult(e));
		}

		// LDAPTestUtils.assertResultCodeEquals(startTxnResult,
		// ResultCode.SUCCESS);

		if (startTxnResult.getResultCode() != ResultCode.SUCCESS) {
			this.result = new LDAPTransactionResult(new LDAPResult(startTxnResult));
			return;
		}

		final ASN1OctetString txnID = startTxnResult.getTransactionID();

		// At this point, we have a transaction available for use. If any
		// problem arises, we want to ensure that the transaction is aborted, so
		// create a try block to process the operations and a finally block to
		// commit or abort the transaction.
		LDAPResult[] ldapResults = new LDAPResult[m_sequence.size()];
		boolean commit = false;
		boolean networkError = false;
		int i = -1;
		try {
			for (i = 0; i < m_sequence.size(); i++) {
				LDAPOperation request = m_sequence.get(i);
				UpdatableLDAPRequest l_request = request.getRequest();
				l_request.addControl(new TransactionSpecificationRequestControl(txnID));
				com.unboundid.ldap.sdk.LDAPResult l_result = request.doExecute(connection, l_request);
				System.out.println("Transaction|" + new LDAPResult(l_result));
				if (l_result.getResultCode() != ResultCode.SUCCESS) {
					System.out.println("FAILURE!!!");
				}
				ldapResults[i] = new LDAPResult(l_result);
			}

			// If we've gotten here, then all writes have been processed
			// successfully and we can indicate that the transaction should be
			// committed rather than aborted.
			commit = true;
		} catch (LDAPException e) {
			e.printStackTrace();

			if (isNetworkError(e.getResultCode())) {
				networkError = true;
				this.result = new LDAPTransactionResult(new LDAPResult(e.toLDAPResult()));
				this.result.setLDAPResults(ldapResults);
				throw new NetworkErrorException(e.getMessage());
			}

			// Just add this Exception as a result
			if (i >= 0)
				ldapResults[i] = new LDAPResult(e.toLDAPResult());
		} finally {
			// I a network error was met, no need to try to abort the
			if (networkError) {
				return;
			}

			// Commit or abort the transaction.
			EndTransactionExtendedResult endTxnResult = null;
			try {
				endTxnResult = (EndTransactionExtendedResult) connection
						.processExtendedOperation(new EndTransactionExtendedRequest(txnID, commit));

				this.result = new LDAPTransactionResult(new LDAPResult(endTxnResult));
				this.result.setLDAPResults(ldapResults);
			} catch (LDAPException le) {
				try {
					endTxnResult = new EndTransactionExtendedResult(new ExtendedResult(le));
					this.result = new LDAPTransactionResult(new LDAPResult(endTxnResult));
				} catch (LDAPException e) {
					this.result = new LDAPTransactionResult(new LDAPResult(e.toLDAPResult()));
				}
			}
		}
	}

	public LDAPTransaction append(LDAPOperation request) {
		m_sequence.add(request);
		return this;
	}
}
