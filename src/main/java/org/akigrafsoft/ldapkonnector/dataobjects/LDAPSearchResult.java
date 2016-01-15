package org.akigrafsoft.ldapkonnector.dataobjects;

import java.util.ArrayList;
import java.util.HashSet;

import com.unboundid.ldap.sdk.SearchResultEntry;

public class LDAPSearchResult extends LDAPResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7840424324386943201L;

	public class LDAPAttribute {
		private String name;
		private String[] values;

		LDAPAttribute(String name, String[] values) {
			this.name = name;
			this.values = values;
		}

		public String getName() {
			return name;
		}

		public String[] getValues() {
			return values;
		}
	}

	public class LDAPEntry {
		HashSet<LDAPAttribute> attributes = new HashSet<LDAPAttribute>();

		public String[] getAttributeValues(String name) {
			for (LDAPAttribute attr : attributes) {
				if (attr.getName().equals(name))
					return attr.getValues();
			}
			return null;
		}
	}

	public final ArrayList<LDAPEntry> entries = new ArrayList<LDAPEntry>();

	LDAPSearchResult(com.unboundid.ldap.sdk.SearchResult ldapSearchResult) {
		super(ldapSearchResult);

		for (SearchResultEntry ldapSearchEntry : ldapSearchResult
				.getSearchEntries()) {
			LDAPEntry entry = new LDAPEntry();
			for (com.unboundid.ldap.sdk.Attribute ldapAttribute : ldapSearchEntry
					.getAttributes()) {
				entry.attributes.add(new LDAPAttribute(ldapAttribute.getName(),
						ldapAttribute.getValues()));
			}
			entries.add(entry);
		}
	}

	@Override
	public String toString() {

		String o_out = super.toString();
		o_out += ", entries=(";
		for (LDAPEntry entry : entries) {
			o_out += "(";
			for (LDAPAttribute attribute : entry.attributes) {
				String name = attribute.getName();
				String[] values = attribute.getValues();
				o_out += "name=" + name;
				o_out += ",values=";
				for (String value : values) {
					o_out += value + ",";
				}
				o_out += ";";
			}
			o_out += ")";
		}
		o_out += ")";

		return o_out;
	}
}
