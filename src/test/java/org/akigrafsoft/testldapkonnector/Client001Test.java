package org.akigrafsoft.testldapkonnector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.akigrafsoft.ldapkonnector.LdapClientConfig;
import org.akigrafsoft.ldapkonnector.LdapClientKonnector;
import org.akigrafsoft.ldapkonnector.dataobjects.LDAPOperation;
import org.akigrafsoft.ldapkonnector.dataobjects.LDAPSearch;
import org.akigrafsoft.ldapkonnector.dataobjects.LDAPSearchResult;
import org.akigrafsoft.ldapkonnector.dataobjects.LDAPSearchResult.LDAPEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.akigrafsoft.knetthreads.Message;
import com.akigrafsoft.knetthreads.konnector.KonnectorDataobject;
import com.akigrafsoft.knetthreads.konnector.KonnectorDataobject.SyncMode;
import com.akigrafsoft.knetthreads.konnector.KonnectorException;

//@Ignore
public class Client001Test {

	private static String LDIF_FILENAME = "example.ldif";
	private static int LDAP_PORT;

	static LdapClientKonnector connector;

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println("******" + Client001Test.class.getName() + "::setUpClass******");
		LDAP_PORT = Simulator.startSim(LDIF_FILENAME);

		// Configure the konnector

		final LdapClientConfig config = new LdapClientConfig().host("localhost").port(LDAP_PORT)
				.username("cn=Directory Manager").password("password");
		config.numberOfSessions(3);

		connector = new LdapClientKonnector("LPDAP01");
		connector.configure(config);

		connector.start();

		Utils.sleep(1);

	}

	@AfterClass
	public static void tearDownClass() throws Exception {

		connector.stop();

		Utils.sleep(1);

		System.out.println("******" + Client001Test.class.getName() + "::tearDownClass******");

		Simulator.stopSim();
		Utils.sleep(1);
		System.out.println("Sim Server stopped");
	}

	@Test
	public void test() {

		final Message message = new Message();

		final KonnectorDataobject dataobject;
		try {
			dataobject = new LDAPSearch(message, "ou=people,dc=example,dc=com", 1, "uid=kmoyse", "cn");
			dataobject.operationSyncMode = SyncMode.SYNC;
		} catch (KonnectorException e) {
			fail(e.getMessage());
			return;
		}

		connector.handle(dataobject);
		System.out.println(dataobject.toString());

		assertEquals(KonnectorDataobject.ExecutionStatus.PASS, dataobject.executionStatus);

		LDAPOperation ldapOp = (LDAPOperation) dataobject;
		System.out.println("result=" + ldapOp.getResult().toString());

		assertEquals(0, ldapOp.getResult().resultCode);

		LDAPSearchResult searchResult = (LDAPSearchResult) ldapOp.getResult();

		Optional<LDAPEntry> first = searchResult.getEntries().stream().findFirst();
		if (!first.isPresent()) {
			fail("not present");
		}

		assertEquals("Kevin Moyse", first.get().getAttributeValues("cn")[0]);

	}

}
