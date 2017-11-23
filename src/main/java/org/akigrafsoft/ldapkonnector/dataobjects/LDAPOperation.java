/**
 * Open-source, by AkiGrafSoft.
 *
 * $Id:  $
 *
 **/
package org.akigrafsoft.ldapkonnector.dataobjects;

import org.akigrafsoft.ldapkonnector.LdapClientDataobject;

import com.akigrafsoft.knetthreads.Message;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;

public abstract class LDAPOperation extends LdapClientDataobject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9137631331954968085L;

	protected LDAPResult result;

	public LDAPOperation(final Message message) {
		super(message);
	}

	abstract com.unboundid.ldap.sdk.UpdatableLDAPRequest getRequest();

	abstract com.unboundid.ldap.sdk.LDAPResult doExecute(final LDAPConnection connection,
			final UpdatableLDAPRequest request) throws LDAPException;

	public LDAPResult getResult() {
		return result;
	}
}
