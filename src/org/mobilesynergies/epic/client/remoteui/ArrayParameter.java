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

public class ArrayParameter extends Parameter {

	private ArrayList<Parameter> mArray = new ArrayList<Parameter>();
	
	public ArrayParameter(){
		
	}
			
	public ArrayParameter(ArrayList<Parameter> array) {
		try {
			setArray(array);
		} catch (SpecializationException e) {
			e.printStackTrace();
			mArray.clear();
		}
	}

		public void addValue(String value) {
		mValues.add(value);
	}
	
	public void setValues(ArrayList<String> values){
		mValues = values;
	}


	@Override
	public String getType() {
		return TYPENAME_ARRAY;
	}

	public void setArray(ArrayList<Parameter> array) throws SpecializationException {
		mArray.clear(); 
		for(int i=0; i<array.size(); i++){
			addEntry(array.get(i));
		}
	}
	
	public void addEntry(Parameter entry) throws SpecializationException{
		//String type = entry.getType();
		mArray.add(entry);
	}
	
	public ArrayList<Parameter> getArray(){
		return mArray;
	}
	
	
	@Override
	public Set<FormField> toFormFields(String name) {
		Set<FormField> set = new HashSet<FormField>();
		if(mArray==null)
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
		
		for(int i=0; i<mArray.size(); i++){
			String label = mArray.get(i).getLabel();
			ArrayList<String> entries = mArray.get(i).getValues();
			if((entries!=null)&&(entries.size()>0)) {
				String entry = entries.get(0);
				f1.addOption(new Option(label, entry));
			}
		}
		if(mValues.size()>0){
			f1.addValue(getValueAsString(0));
		}
		set.add(f1);
		FormField f2 = new FormField(name+Specialization.SPECIALIZECHARACTER+Specialization.KEY_TYPE);
		f2.setType(FormField.TYPE_HIDDEN);
		f2.addValue(Parameter.TYPENAME_ARRAY);
		set.add(f2);		
		
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
		Iterator<Parameter> iterItems = mArray.iterator();
		while(iterItems.hasNext()){
			Parameter param = iterItems.next();
			buf.append(param.asXml("entry"));
		}
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
		ArrayParameter array = new ArrayParameter();
		
		//record the depth of the array's xml 
		int depth = parser.getDepth();
		int startdepth = depth;
		
		//proceed by one tag (out of array tag)
		int eventType = parser.nextTag();
		
		while( ! ((eventType == XmlPullParser.END_TAG)&&(depth==startdepth))){
			
			//proceed to the next start tag
			if(eventType==XmlPullParser.START_TAG){
				Parameter parameter = ParameterManager.getInstance().parseXml(parser);
				try {
					array.addEntry(parameter);
				} catch (SpecializationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			eventType = parser.next();
			depth = parser.getDepth();
		}
		return array;
	}
	
}
