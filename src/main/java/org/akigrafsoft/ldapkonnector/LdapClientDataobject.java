/**
 * Open-source, by AkiGrafSoft.
 *
 * $Id:  $
 *
 **/
package org.akigrafsoft.ldapkonnector;

import com.akigrafsoft.knetthreads.Message;
import com.akigrafsoft.knetthreads.konnector.KonnectorDataobject;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.ResultCode;

public abstract class LdapClientDataobject extends KonnectorDataobject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5681708635811549024L;

	public LdapClientDataobject(Message message) {
		super(message);
	}

	public abstract void execute(final LDAPConnection connection) throws NetworkErrorException;

	public static boolean isNetworkError(ResultCode resultCode) {
		return ResultCode.UNAVAILABLE.equals(resultCode) || ResultCode.SERVER_DOWN.equals(resultCode);
	}
}
