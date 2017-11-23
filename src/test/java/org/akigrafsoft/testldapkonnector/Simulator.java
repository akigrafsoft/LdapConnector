package org.akigrafsoft.testldapkonnector;

import java.net.URL;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;

public class Simulator {

	private static InMemoryDirectoryServer ds;

	/**
	 * Starts a simple LDAP sim
	 * 
	 * @param filename
	 * @return
	 */
	public static int startSim(String filename) {
		return startSim(filename, -1);
	}

	/**
	 * Starts a simple LDAP sim
	 * 
	 * @param filename
	 * @param listenPort
	 * @return
	 */
	public static int startSim(String filename, int listenPort) {

		// Create the configuration to use for the server.
		InMemoryDirectoryServerConfig config;
		try {
			config = new InMemoryDirectoryServerConfig("dc=example,dc=com");

			config.addAdditionalBindCredentials("cn=Directory Manager", "password");

			if (listenPort != -1) {
				config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("DEFAULT", listenPort));
			}

			// Create the directory server instance, populate it with data from
			// the file, and start listening for client connections.
			ds = new InMemoryDirectoryServer(config);
			URL inUrl = ds.getClass().getClassLoader().getResource(filename);
			ds.importFromLDIF(true, inUrl.getFile());
			if (listenPort != -1) {
				ds.startListening("DEFAULT");
			} else {
				ds.startListening();
			}
			return ds.getListenPort();
		} catch (LDAPException e) {
			System.out.println("startSim|LDAPException|" + e.getMessage());
			e.printStackTrace();
			return -1;
		}

		// Get a client connection to the server and use it to perform various
		// operations.
		// LDAPConnection conn = ds.getConnection();
		// SearchResultEntry entry = conn.getEntry("dc=example,dc=com");

		// Do more stuff here....

		// conn.close();

	}

	public static void stopSim() {
		// Server shut down.
		ds.shutDown(true);
	}

	public static void restartSim() {
		// Server shut down.
		try {
			ds.restartServer();
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
