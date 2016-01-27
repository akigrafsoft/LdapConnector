package org.akigrafsoft.ldapkonnector;

import com.akigrafsoft.knetthreads.ExceptionAuditFailed;
import com.akigrafsoft.knetthreads.konnector.SessionBasedClientKonnectorConfiguration;

/**
 * Configuration class for {@link LdapClientKonnector}
 * <p>
 * <b>This MUST be a Java bean</b>
 * </p>
 * 
 * @author kmoyse
 * 
 */
public class LdapClientConfig extends SessionBasedClientKonnectorConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7509795825624016156L;

	private String host;
	private int port = -1;
	private String username = null;
	private String password = null;

	// ------------------------------------------------------------------------
	// Java Bean

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// ------------------------------------------------------------------------
	// Configuration

	@Override
	public void audit() throws ExceptionAuditFailed {
		super.audit();

		if (this.getNumberOfSessions() <= 0) {
			throw new ExceptionAuditFailed("numberOfSessions must be > 0");
		}

		if (host == null || host.equals("")) {
			throw new ExceptionAuditFailed("host must be configured");
		}
		if (port <= 0) {
			throw new ExceptionAuditFailed("port must be configured");
		}
	}

	// ------------------------------------------------------------------------
	// Fluent API

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
