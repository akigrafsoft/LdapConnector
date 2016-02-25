/**
 * Open-source, by AkiGrafSoft.
 *
 * $Id:  $
 *
 **/
package org.akigrafsoft.ldapkonnector.dataobjects;

import java.util.ArrayList;

import org.akigrafsoft.ldapkonnector.NetworkErrorException;

import com.akigrafsoft.knetthreads.Message;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;

/**
 * Dataobject to perform a LDAP Add
 * 
 * @author kmoyse
 * 
 */
public class LDAPAdd extends LDAPOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8645407206360812894L;
	private final String m_dn;
	private final ArrayList<Attribute> m_attributes = new ArrayList<Attribute>();

	public LDAPAdd(Message message, String dn) {
		super(message);
		m_dn = dn;
	}

	// ------------------------------------------------------------------------
	// Fluent API

	public LDAPAdd attribute(String name, String... values) {
		m_attributes.add(new Attribute(name, values));
		return this;
	}

	// ------------------------------------------------------------------------

	@Override
	public void execute(LDAPConnection connection) throws NetworkErrorException {
		try {
			this.result = new LDAPResult(connection.add(m_dn, m_attributes));
		} catch (LDAPException e) {
			this.result = new LDAPResult(e.toLDAPResult());
			if (isNetworkError(e.getResultCode())) {
				throw new NetworkErrorException(e.getMessage());
			}
		}
	}

	@Override
	UpdatableLDAPRequest getRequest() {
		return new AddRequest(m_dn, m_attributes);
	}

	@Override
	com.unboundid.ldap.sdk.LDAPResult doExecute(LDAPConnection connection, UpdatableLDAPRequest request)
			throws LDAPException {
		return connection.add((AddRequest) request);
	}
}
