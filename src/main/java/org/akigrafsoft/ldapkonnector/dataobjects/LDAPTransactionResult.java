package org.akigrafsoft.ldapkonnector.dataobjects;

import java.io.Serializable;

public class LDAPTransactionResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4285383597670725834L;
	private LDAPResult m_endTransactionResult;
	private LDAPResult[] m_ldapResults;

	LDAPTransactionResult(LDAPResult endTransactionResult) {
		m_endTransactionResult = endTransactionResult;
	}

	public LDAPResult getEndTransactionResult() {
		return m_endTransactionResult;
	}

	LDAPTransactionResult setLDAPResults(LDAPResult[] results) {
		m_ldapResults = results;
		return this;
	}

	public LDAPResult[] getLDAPResults() {
		return m_ldapResults;
	}

}
