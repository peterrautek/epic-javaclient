package org.mobilesynergies.epic.client.remoteui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.FormField.Option;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class OptionParameter extends Parameter {

	private ArrayList<Parameter> mOptions = new ArrayList<Parameter>();
	private String mSubType = Parameter.TYPENAME_UNKNOWN;
	
	public OptionParameter(ArrayList<Parameter> options) {
		try {
			setOptions(options);
		} catch (SpecializationException e) {
			e.printStackTrace();
			mOptions.clear();
			mSubType = Parameter.TYPENAME_UNKNOWN;
		}
	}

	public OptionParameter(String value) {
		setValue(value);
	}

	public OptionParameter() {
		// TODO Auto-generated constructor stub
	}

	public void setValue(String value) {
		mValues.clear();
		mValues.add(value);
	}
	
	public void addValue(String value) {
		mValues.add(value);
	}
	
	public void setValues(ArrayList<String> values){
		mValues = values;
	}


	@Override
	public String getType() {
		return TYPENAME_OPTION;
	}

	public Parameter getValue(){
		if((mValues==null)||(mValues.size()==0)){
			return null;
		}
		return Parameter.create(mSubType, null, mValues.get(0));
	}
	
	public void setOptions(ArrayList<Parameter> optionList) throws SpecializationException {
		mOptions.clear(); 
		mSubType = Parameter.TYPENAME_UNKNOWN;		
		for(int i=0; i<optionList.size(); i++){
			addOption(optionList.get(i));
		}
	}
	
	public void addOption(Parameter option) throws SpecializationException{
		String type = option.getType();
		if(mSubType.equalsIgnoreCase(Parameter.TYPENAME_UNKNOWN)){
			mSubType =type;
		} else if (mSubType.equalsIgnoreCase(type)) {
			//they are equal -> everything is fine
		} else {
			//different options have different type -> error
			mSubType = Parameter.TYPENAME_UNKNOWN;
			throw new SpecializationException("Options have inconsistent type.");
		}
		mOptions.add(option);
	}
	
	public ArrayList<Parameter> getOptions(){
		return mOptions;
	}
	
	
	@Override
	public Set<FormField> toFormFields(String name) {
		Set<FormField> set = new HashSet<FormField>();
		if(mOptions==null)
			return set;
		
		FormField f1 = new FormField(name);
		f1.setLabel(mLabel);
		f1.setDescription(mDescription);
		f1.setRequired(mRequired);
		if(mIsHidden){
			f1.setType(FormField.TYPE_HIDDEN);
		} else {
			f1.setType(FormField.TYPE_LIST_SINGLE);
		}
		
		for(int i=0; i<mOptions.size(); i++){
			String label = mOptions.get(i).getLabel();
			ArrayList<String> options = mOptions.get(i).getValues();
			if((options!=null)&&(options.size()>0)) {
				String option = options.get(0);
				f1.addOption(new Option(label, option));
			}
		}
		if(mValues.size()>0){
			f1.addValue(getValueAsString(0));
		}
		set.add(f1);
		FormField f2 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_TYPE);
		f2.setType(FormField.TYPE_HIDDEN);
		f2.addValue(Parameter.TYPENAME_OPTION);
		set.add(f2);		
		
		FormField f3 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_SUBTYPE);
		f3.setType(FormField.TYPE_HIDDEN);
		f3.addValue(mSubType);
		set.add(f3);
		
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
		Iterator<Parameter> iterItems = mOptions.iterator();
		while(iterItems.hasNext()){
			Parameter param = iterItems.next();
			buf.append(param.asXml("option"));
		}
		buf.append(getValue().asXml("value"));
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
		OptionParameter p = new OptionParameter();
		
		int depth = parser.getDepth();
		int startdepth = depth;
		
		//proceed by one tag (out of option tag)
		int eventType = parser.nextTag();
		
		while( ! ((eventType == XmlPullParser.END_TAG)&&(depth==startdepth))){
			
			//proceed to the next start tag
			if(eventType==XmlPullParser.START_TAG){
				Parameter parameter = ParameterManager.getInstance().parseXml(parser);
				String name = parser.getName().trim();
				if(name.equalsIgnoreCase("option")){
					try {
						p.addOption(parameter);
					} catch (SpecializationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (name.equalsIgnoreCase("value")){
					String value = parser.nextText();
					p.addValue(value);
				}
			}
			eventType = parser.next();
			depth = parser.getDepth();
		}
		return p;
	}
	
}
