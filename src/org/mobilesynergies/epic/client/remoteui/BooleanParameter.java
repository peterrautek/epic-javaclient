package org.mobilesynergies.epic.client.remoteui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smackx.FormField;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class BooleanParameter extends Parameter {

	public BooleanParameter() {
	}

	
	public BooleanParameter(boolean value) {
		setValue(value);
	}

	public BooleanParameter(String value) {
		setValue(value);
	}


	public boolean getValue() {
		if(mValues==null)
			return false;
		
		return Boolean.parseBoolean(mValues.get(0)) || mValues.get(0).trim().equals("1");
	}
		
	public void setValue(boolean value) {
		mValues.clear();
		mValues.add(String.valueOf(value));
	}
	
	public void setValue(String value) {
		boolean bVal = Boolean.parseBoolean(value) || value.trim().equals("1");
		setValue(bVal);		
	}

	@Override
	public String getType() {
		return Parameter.TYPENAME_BOOLEAN;
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
			f1.setType(FormField.TYPE_BOOLEAN);
		}
		f1.addValue(mValues.get(0));
		set.add(f1);
		
		FormField f2 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_TYPE);
		//f2.setLabel();
		f2.setType(FormField.TYPE_HIDDEN);
		f2.addValue(Parameter.TYPENAME_BOOLEAN);
		set.add(f2);		
		
		if(mSubmitActionHint){
			FormField f3 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_SUBMITACTIONHINT);
			f3.setType(FormField.TYPE_HIDDEN);
			f3.addValue(String.valueOf(mSubmitActionHint));
			set.add(f3);
		}

		
		return set;
	}

	
	@Override
	public String asXml(String name) {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(name).append(" type=\"").append(getXmlType()).append("\">");
		buf.append(getValueAsString());
		buf.append("</").append(name).append(">");
		return buf.toString();
	}
	
	@Override
	public String getXmlType(){
		return getType();
	}

	/**
	 * Parses a boolean parameter from the next text of the parser.
	 * @param parser The XmlPullParser at the position of the parameter.
	 * @return Returns a parameter of type BoolesnParameter with the value of the next text. 
	 * The value is true if (and only if) the string equals 'true' (not case sensitive).  
	 */
	@Override
	public Parameter fromXml(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		String boolString = parser.nextText();
		boolean b = false;
		b = Boolean.parseBoolean(boolString);
		Parameter p = new BooleanParameter(b);
		return p;
	}
	
	


	
	

}
