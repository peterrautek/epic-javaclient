package org.mobilesynergies.epic.client.remoteui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smackx.FormField;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class StringParameter extends Parameter implements ParameterCreator {

	public StringParameter(String value) {
		setValue(value);
	}

	public StringParameter() {
	}

	/**
	 * @param mValue the mValue to set
	 */
	public void setValue(String value) {
		mValues.clear();
		mValues.add(value);
	}

	/**
	 * @return the mValue
	 */
	public String getValue() {
		if(mValues.size()==0)
			return "";
		
		return mValues.get(0);
	}
	
	@Override
	public String getType() {
		return Parameter.TYPENAME_STRING;
	}
	
	@Override
	public Set<FormField> toFormFields(String name) {
		Set<FormField> set = new HashSet<FormField>();
		FormField f1 = new FormField(name);
		f1.setLabel(mLabel);
		f1.setDescription(mDescription);
		f1.setRequired(mRequired);
		
		if(mIsHidden){
			f1.setType(FormField.TYPE_HIDDEN);
		} else {
			f1.setType(FormField.TYPE_TEXT_SINGLE);
		}
		
		f1.addValue(mValues.get(0));		
		
		set.add(f1);
		
		
		if(mSubmitActionHint){
			FormField f2 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_SUBMITACTIONHINT);
			f2.setType(FormField.TYPE_HIDDEN);
			f2.addValue(String.valueOf(mSubmitActionHint));
			set.add(f2);
		}
		
		return set;

	}

	@Override
	public String asXml(String name) {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(name).append(" type=\"").append(getXmlType()).append("\">");
		buf.append(XmlUtility.escapeTextForXML(getValue()));
		buf.append("</").append(name).append(">");
		return buf.toString();
	}
	
	@Override
	public String getXmlType(){
		return getType();
	}

	
	@Override
	public Parameter fromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
		String text = parser.nextText().trim();
		StringParameter p = new StringParameter(text);
		return p;
	}



}
