package org.akigrafsoft.testldapkonnector;

import org.akigrafsoft.ldapkonnector.LdapClientConfig;
import org.akigrafsoft.ldapkonnector.LdapClientKonnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class Client001Test {

	private static String LDIF_FILENAME = "example.ldif";
	private static int LDAP_PORT;

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println("******" + Client001Test.class.getName()
				+ "::setUpClass******");
		LDAP_PORT = Simulator.startSim(LDIF_FILENAME);

		// Configure the konnector

		LdapClientConfig config = new LdapClientConfig().host("localhost")
				.port(LDAP_PORT).username("cn=Directory Manager")
				.password("password");
		config.numberOfSessions(3);

		LdapClientKonnector connector = new LdapClientKonnector("LPDAP01");
		connector.configure(config);
		
		connector.start();

	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		System.out.println("******" + Client001Test.class.getName()
				+ "::tearDownClass******");

		Simulator.stopSim();
		Utils.sleep(2);
		System.out.println("Sim Server stopped");
	}

	@Test
	public void test() {
		// TODO
	}

}
