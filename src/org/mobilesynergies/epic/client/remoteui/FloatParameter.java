package org.mobilesynergies.epic.client.remoteui;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smackx.FormField;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FloatParameter extends Parameter {
	
	private Float mMin = null;
	private Float mMax = null;
	

	public FloatParameter() {
		
	}

	public FloatParameter(String value) {
		try {
			setValue(value);
		} catch (SpecializationException e) {
			e.printStackTrace();
		}
	}
	
	public FloatParameter(float value) {
		setValue(value);
	}

	public FloatParameter(float value, float min, float max) {
		setMin(min);
		setMax(max);
		setValue(value);		
	}
	

	/**
	 * @param mValue the mValue to set
	 */
	public void setValue(float value) {
		
		mValues.clear();
		mValues.add(String.valueOf(clamp(value)));
	}
	
	public void setValue(String value) throws SpecializationException {
		float val = 0;
		try{
			val = Float.parseFloat(value);
		} catch(NumberFormatException e) {
			throw new SpecializationException(e);
		}
		setValue(val);
		
	}
	
	private float clamp(float fVal) {
		if(mMax!=null){
			fVal = Math.min(mMax, fVal);
		}
		if(mMin!=null){
			fVal = Math.max(mMin, fVal);
		}
		return fVal;
		
	}

	/**
	 * @return the mValue
	 */
	public float getValue() {
		return Float.parseFloat(mValues.get(0));
	}
	
	/**
	 * @param min the min to set
	 */
	public void setMin(float min) {
		this.mMin = new Float(min);
	}

	/**
	 * @return the mMin
	 */
	public Float getMin() {
		return mMin;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(float max) {
		this.mMax = new Float(max);
	}

	/**
	 * @return the mMax
	 */
	public Float getMax() {
		return mMax;
	}

	
	@Override
	public String getType() {
		return Parameter.TYPENAME_FLOAT;
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
			f2.addValue(Float.toString(mMin));
			set.add(f2);
		}
		
		if(mMax!=null){
			FormField f3 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_MAX);
			f3.setLabel(name);
			f3.setType(FormField.TYPE_HIDDEN);
			f3.addValue(Float.toString(mMax));
			set.add(f3);
		}
		
		
		FormField f5 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_TYPE);
		f5.setLabel(name);
		f5.setType(FormField.TYPE_HIDDEN);
		f5.addValue(Parameter.TYPENAME_FLOAT);
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

	@Override
	public Parameter fromXml(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		String floatparam = parser.nextText(); 
		Parameter p = new FloatParameter(Float.parseFloat(floatparam));
		return p;
	}

	
	

	

}
