/**
 * Open-source, by AkiGrafSoft.
 *
 * $Id:  $
 *
 **/
package org.akigrafsoft.ldapkonnector.dataobjects;

import org.akigrafsoft.ldapkonnector.NetworkErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akigrafsoft.knetthreads.Message;
import com.akigrafsoft.knetthreads.konnector.KonnectorException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;

/**
 * Dataobject to perform a LDAP Search
 * 
 * @author kmoyse
 * 
 */
public class LDAPSearch extends LDAPOperation {

	private static final Logger logger = LoggerFactory.getLogger(LDAPSearch.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -6902677888098153862L;

	private final String m_dn;
	private final com.unboundid.ldap.sdk.SearchRequest m_searchRequest;

	public LDAPSearch(final Message message, final String baseDN, final int searchScope, final String filter,
			final String... attributes) throws KonnectorException {
		super(message);
		m_dn = baseDN;

		try {
			m_searchRequest = new com.unboundid.ldap.sdk.SearchRequest(m_dn, SearchScope.valueOf(searchScope), filter,
					attributes);
		} catch (LDAPException e) {
			logger.warn("LDAPSearch", e);
			throw new KonnectorException(e.getMessage());
		}
	}

	@Override
	public void execute(final LDAPConnection connection) throws NetworkErrorException {
		try {
			this.result = new LDAPSearchResult(connection.search(m_searchRequest));
		} catch (final LDAPSearchException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			this.result = new LDAPSearchResult(e.toLDAPResult());
			if (isNetworkError(e.getResultCode())) {
				throw new NetworkErrorException(e.getMessage());
			}
		}
	}

	@Override
	com.unboundid.ldap.sdk.LDAPResult doExecute(final LDAPConnection connection, final UpdatableLDAPRequest request)
			throws LDAPException {
		return connection.search((SearchRequest) request);
	}

	@Override
	UpdatableLDAPRequest getRequest() {
		return m_searchRequest;
	}
}
