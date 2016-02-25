/**
 * Open-source, by AkiGrafSoft.
 *
 * $Id:  $
 *
 **/
package org.akigrafsoft.ldapkonnector.dataobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class LDAPResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2683539445531430590L;

	public class Control {
		final String name;
		final String oID;

		// TODO handle ASN1 value ?

		public Control(String name, String oID) {
			super();
			this.name = name;
			this.oID = oID;
		}

		@Override
		public String toString() {
			String o_out = "";
			o_out += "name=" + name;
			o_out += ",oID=" + oID;
			// TODO handle ASN1 value ?
			return o_out;
		}
	}

	public final int resultCode;
	public final String diagnosticMessage;
	public final String matchedDN;
	public final int messageId;
	public final String[] referralURLs;
	public final Control[] controls;

	// not exposed constructor, because we have to hide lib specifics
	LDAPResult(com.unboundid.ldap.sdk.LDAPResult result) {
		this.resultCode = result.getResultCode().intValue();
		this.diagnosticMessage = result.getDiagnosticMessage();
		this.matchedDN = result.getMatchedDN();
		this.messageId = result.getMessageID();
		this.referralURLs = result.getReferralURLs();

		com.unboundid.ldap.sdk.Control[] controls = result.getResponseControls();
		ArrayList<Control> l_controls = new ArrayList<Control>();
		for (com.unboundid.ldap.sdk.Control control : controls) {
			// TODO get controls ASN1 control.getValue() ?
			l_controls.add(new Control(control.getControlName(), control.getOID()));
		}
		this.controls = l_controls.toArray(new Control[l_controls.size()]);
	}

	@Override
	public String toString() {
		String o_out = "";
		o_out += "resultCode=" + resultCode;
		o_out += ", diagnosticMessage=" + diagnosticMessage;
		o_out += ", matchedDN=" + matchedDN;
		o_out += ", messageId=" + messageId;
		o_out += ", referralURLs=";
		for (String url : referralURLs)
			o_out += url + ";";
		o_out += ", controls=";
		for (Control control : controls)
			o_out += control.toString() + ";";
		return o_out;
	}

}
