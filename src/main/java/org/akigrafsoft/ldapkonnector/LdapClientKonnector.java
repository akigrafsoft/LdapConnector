package org.akigrafsoft.ldapkonnector;

import com.akigrafsoft.knetthreads.ExceptionDuplicate;
import com.akigrafsoft.knetthreads.konnector.ExceptionCreateSessionFailed;
import com.akigrafsoft.knetthreads.konnector.KonnectorConfiguration;
import com.akigrafsoft.knetthreads.konnector.KonnectorDataobject;
import com.akigrafsoft.knetthreads.konnector.SessionBasedClientKonnector;
import com.unboundid.ldap.sdk.DisconnectHandler;
import com.unboundid.ldap.sdk.DisconnectType;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;

/**
 * LDAP client connector
 * 
 * @author kmoyse
 * 
 */
public class LdapClientKonnector extends SessionBasedClientKonnector {

	String m_host;
	int m_port;
	String m_username;
	String m_password;

	public LdapClientKonnector(String name) throws ExceptionDuplicate {
		super(name);
	}

	@Override
	public Class<? extends KonnectorConfiguration> getConfigurationClass() {
		return LdapClientConfig.class;
	}

	@Override
	protected void doLoadConfig(KonnectorConfiguration config) {
		super.doLoadConfig(config);
		LdapClientConfig l_config = (LdapClientConfig) config;

		m_host = l_config.getHost();
		m_port = l_config.getPort();
		m_username = l_config.getUsername();
		m_password = l_config.getPassword();
	}

	class LDAPSession {
		LDAPConnection connection;

		final LDAPConnectionOptions options;

		// Decorator pattern
		// final Session konnectorSession;

		LDAPSession(final Session konnectorSession) {
			options = new LDAPConnectionOptions();
			options.setDisconnectHandler(new DisconnectHandler() {
				@Override
				public void handleDisconnect(LDAPConnection connection,
						String host, int port, DisconnectType disconnectType,
						String message, Throwable cause) {
					System.out.println("handleDisconnect called : " + host
							+ port + ", type=" + disconnectType + ", message="
							+ message);
					sessionDied(konnectorSession);
				}
			});
		}

		void start() throws LDAPException {
			if ((m_username != null) && (m_password != null)) {
				// logger.debug("LDAP ClientEndpoint \"" + name
				// + "\" using authentication.");
				connection = new LDAPConnection(options, m_host, m_port,
						m_username, m_password);
			} else {
				connection = new LDAPConnection(options, m_host, m_port);
			}
		}
	}

	@Override
	protected void createSession(Session session)
			throws ExceptionCreateSessionFailed {
		session.setUserObject(new LDAPSession(session));
	}

	@Override
	public void async_startSession(Session session) {
		LDAPSession l_session = (LDAPSession) session.getUserObject();
		try {
			l_session.start();
		} catch (LDAPException e) {
			e.printStackTrace();
			this.sessionDied(session, e.getMessage());
			return;
		}
		this.sessionStarted(session);
	}

	@Override
	protected void execute(KonnectorDataobject dataobject, Session session) {
		LdapClientDataobject l_dataobject = (LdapClientDataobject) dataobject;
		LDAPSession l_session = (LDAPSession) session.getUserObject();
		LDAPConnection l_connection = l_session.connection;

		try {
			l_dataobject.execute(l_connection);
		} catch (NetworkErrorException e) {
			this.notifyNetworkError(l_dataobject, session, e.getMessage());
			e.printStackTrace();
		}

		this.notifyExecuteCompleted(l_dataobject);
	}

	@Override
	protected void async_stopSession(Session session) {
		LDAPSession l_session = (LDAPSession) session.getUserObject();
		LDAPConnection l_connection = l_session.connection;
		l_connection.close();
		this.sessionStopped(session);
	}

}
