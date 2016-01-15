package org.akigrafsoft.ldapkonnector;

import com.akigrafsoft.knetthreads.ExceptionAuditFailed;
import com.akigrafsoft.knetthreads.konnector.SessionBasedClientKonnectorConfiguration;

public class LdapClientConfig extends SessionBasedClientKonnectorConfiguration {

	public String host;
	public int port = -1;
	public String username = null;
	public String password = null;

	@Override
	public void audit() throws ExceptionAuditFailed {
		super.audit();

		if (numberOfSessions <= 0) {
			throw new ExceptionAuditFailed("numberOfSessions must be > 0");
		}

		if (host == null || host.equals("")) {
			throw new ExceptionAuditFailed("host must be configured");
		}
		if (port <= 0) {
			throw new ExceptionAuditFailed("port must be configured");
		}
		// if (dbName == null || dbName.equals("")) {
		// throw new ExceptionAuditFailed("dbName must be configured");
		// }
		// if (connectTimeoutMilliseconds <= 0) {
		// throw new
		// ExceptionAuditFailed("connectTimeoutMilliseconds must be > 0");
		// }
	}

	public LdapClientConfig host(String host) {
		this.host = host;
		return this;
	}

	public LdapClientConfig port(int port) {
		this.port = port;
		return this;
	}

	public LdapClientConfig username(String username) {
		this.username = username;
		return this;
	}

	public LdapClientConfig password(String password) {
		this.password = password;
		return this;
	}
}
