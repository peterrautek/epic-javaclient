package org.mobilesynergies.epic.client.remoteui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.FormField.Option;
import org.mobilesynergies.epic.client.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class IntParameter extends Parameter {

	private Integer mMin = null;
	private Integer mMax = null;
	
	public IntParameter() {

	}
	
	public IntParameter(String value) {
		setValue(value);
	}


	public IntParameter(int value) {
		setValue(value);
	}

	public IntParameter(int value, int min, int max) {
		setMin(min);
		setMax(max);
		setValue(value);
		
	}
	
	public IntParameter(Specialization specialization) {
		HashMap<String, String> map = specialization.getMap();
		if(map.containsKey(Specialization.KEY_MIN)){
			try{
				mMin = Integer.parseInt(map.get(Specialization.KEY_MIN));				
			} catch (Exception e){
				System.out.println("Unable to parse minimum value");
			}
		}
		if(map.containsKey(Specialization.KEY_MAX)){
			try{
				mMax = Integer.parseInt(map.get(Specialization.KEY_MAX));				
			} catch (Exception e){
				System.out.println("Unable to parse maximum value");
			}
		}
	}

	


	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		mValues.clear();
		mValues.add(String.valueOf(clamp(value)));
	}
	
	public void setValue(String value) {
		int iVal = Integer.parseInt(value);
		setValue(iVal);		
	}
	
	private int clamp(int iVal) {
		if(mMax!=null){
			iVal = Math.max(mMin, iVal);
		}
		if(mMin!=null){
			iVal = Math.min(mMax, iVal);
		}
		return iVal;
	}


	/**
	 * @return the mValue
	 */
	public int getValue() {
		String val = mValues.get(0);
		return Integer.parseInt(val);
	}
	
		
	/**
	 * @param min the min to set
	 */
	public void setMin(int min) {
		this.mMin = new Integer(min);
	}

	/**
	 * @return the mMin
	 */
	public Integer getMin() {
		return mMin;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.mMax = new Integer(max);
	}

	/**
	 * @return the mMax
	 */
	public Integer getMax() {
		return mMax;
	}

	
	@Override
	public String getType() {
		return Parameter.TYPENAME_INT;
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
		
		if(mMin!=null){
			FormField f2 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_MIN);
			f2.setLabel(name);
			f2.setType(FormField.TYPE_HIDDEN);
			f2.addValue(Integer.toString(mMin));
			set.add(f2);
		}
		
		if(mMax!=null){
			FormField f3 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_MAX);
			f3.setLabel(name);
			f3.setType(FormField.TYPE_HIDDEN);
			f3.addValue(Integer.toString(mMax));
			set.add(f3);
		}
		
		FormField f5 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_TYPE);
		f5.setLabel(name);
		f5.setType(FormField.TYPE_HIDDEN);
		f5.addValue(Parameter.TYPENAME_INT);
		set.add(f5);		
		
		if(mSubmitActionHint){
			FormField f4 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_SUBMITACTIONHINT);
			f4.setType(FormField.TYPE_HIDDEN);
			f4.addValue(String.valueOf(mSubmitActionHint));
			set.add(f4);
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
	 * 
	 * Parses an integer parameter from the next text of the parser.
	 * @param parser The XmlPullParser at the position of the parameter.
	 * @return Returns a parameter of type IntParameter with the value of the next text. 
	 * If the string cannot be parsed to a int, the IntParameter will have value 0.  
	 */
	@Override
	public Parameter fromXml(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		String integer = parser.nextText();
		int i = 0;
		try{
			i = Integer.parseInt(integer);
		}catch (NumberFormatException e){
			Log.w(Parameter.LOG_TAG, "Exception parsing value for parameter of type integer.");
		}
		Parameter p = new IntParameter(i);
		return p;
	}

	

	
}
