package org.akigrafsoft.ldapkonnector.dataobjects;

import java.util.ArrayList;

import org.akigrafsoft.ldapkonnector.NetworkErrorException;

import com.akigrafsoft.knetthreads.Message;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;

/**
 * Dataobject to perform an LDAP Modify
 * 
 * @author kmoyse
 * 
 */
public class LDAPModify extends LDAPOperation {

	/***
	 * Wrapping of ModificationType
	 * 
	 * @author kmoyse
	 * 
	 */
	public enum ModificationType {

		ADD(com.unboundid.ldap.sdk.ModificationType.ADD), DELETE(
				com.unboundid.ldap.sdk.ModificationType.DELETE), INCREMENT(
				com.unboundid.ldap.sdk.ModificationType.INCREMENT), REPLACE(
				com.unboundid.ldap.sdk.ModificationType.REPLACE);

		private com.unboundid.ldap.sdk.ModificationType m_value;

		ModificationType(com.unboundid.ldap.sdk.ModificationType value) {
			m_value = value;
		}

		/**
		 * Use this to get the value to set in the modification() operation
		 * 
		 * @return the value of the Enum
		 */
		com.unboundid.ldap.sdk.ModificationType getValue() {
			return m_value;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -264866842222980348L;
	private final String m_dn;
	private final ArrayList<Modification> m_modifications = new ArrayList<Modification>();

	public LDAPModify(Message message, String dn) {
		super(message);
		m_dn = dn;
	}

	// ------------------------------------------------------------------------
	// Fluent API

	public LDAPModify modification(ModificationType modificationType,
			String name, String... values) {
		m_modifications.add(new Modification(modificationType.getValue(), name,
				values));
		return this;
	}

	@Override
	public void execute(LDAPConnection connection) throws NetworkErrorException {
		try {
			this.result = new LDAPResult(connection.modify(m_dn,
					m_modifications));
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
		return connection.modify((ModifyRequest) request);
	}

	@Override
	UpdatableLDAPRequest getRequest() {
		return new ModifyRequest(m_dn, m_modifications);
	}

	// ------------------------------------------------------------------------

}
