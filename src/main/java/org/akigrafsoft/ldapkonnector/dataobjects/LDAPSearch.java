package org.akigrafsoft.ldapkonnector.dataobjects;

import org.akigrafsoft.ldapkonnector.NetworkErrorException;

import com.akigrafsoft.knetthreads.Message;
import com.akigrafsoft.knetthreads.konnector.KonnectorException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;

/**
 * Dataobject to perform a LDAP Search
 * 
 * @author kmoyse
 * 
 */
public class LDAPSearch extends LDAPOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6902677888098153862L;

	private final String m_dn;
	private com.unboundid.ldap.sdk.SearchRequest m_searchRequest;

	public LDAPSearch(Message message, String baseDN, int searchScope,
			String filter, String... attributes) throws KonnectorException {
		super(message);
		m_dn = baseDN;

		// TODO handle SearchScope
		try {
			m_searchRequest = new com.unboundid.ldap.sdk.SearchRequest(m_dn,
					com.unboundid.ldap.sdk.SearchScope.SUB, filter, attributes);
		} catch (LDAPException e) {
			e.printStackTrace();
			throw new KonnectorException(e.getMessage());
		}
	}

	@Override
	public void execute(LDAPConnection connection) throws NetworkErrorException {

		// com.unboundid.ldap.sdk.SearchResult searchResults;
		try {
			this.result = new LDAPSearchResult(
					connection.search(m_searchRequest));
		}
		// catch (LDAPSearchException le) {
		// searchResults = le.toLDAPResult();
		// }
		catch (LDAPSearchException e) {
			this.result = new LDAPSearchResult(e.toLDAPResult());
			if (isNetworkError(e.getResultCode())) {
				throw new NetworkErrorException(e.getMessage());
			}
		}

		// this.result = new LDAPSearchResult(searchResults);
	}

	@Override
	com.unboundid.ldap.sdk.LDAPResult doExecute(LDAPConnection connection,
			UpdatableLDAPRequest request) throws LDAPException {
		return connection.search((SearchRequest) request);
	}

	@Override
	UpdatableLDAPRequest getRequest() {
		return m_searchRequest;
	}
}
