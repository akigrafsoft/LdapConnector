package org.akigrafsoft.ldapkonnector.dataobjects;

import org.akigrafsoft.ldapkonnector.NetworkErrorException;

import com.akigrafsoft.knetthreads.Message;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;

/**
 * Dataobject to perform a LDAP Delete
 * 
 * @author kmoyse
 * 
 */
public class LDAPDelete extends LDAPOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5901743544280055264L;
	private final String m_dn;

	public LDAPDelete(Message message, String dn) {
		super(message);
		m_dn = dn;
	}

	@Override
	public void execute(LDAPConnection connection) throws NetworkErrorException {
		try {
			this.result = new LDAPResult(connection.delete(m_dn));
		} catch (LDAPException e) {
			this.result = new LDAPResult(e.toLDAPResult());
			if (isNetworkError(e.getResultCode())) {
				throw new NetworkErrorException(e.getMessage());
			}
		}
	}

	@Override
	com.unboundid.ldap.sdk.LDAPResult doExecute(LDAPConnection connection,
			UpdatableLDAPRequest request) throws LDAPException {
		return connection.delete((DeleteRequest) request);
	}

	@Override
	UpdatableLDAPRequest getRequest() {
		return new DeleteRequest(m_dn);
	}

}
