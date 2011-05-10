package org.mobilesynergies.epic.client.remoteui;
/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/* 
 * This file was taken from The Android Open Source Project and modified.
 * Originally it was the Bundle data structure.
 * Android specifics have been removed to make it useful in a broader context.
 * 
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.FormField.Option;
import org.mobilesynergies.epic.client.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;




/**
 * A data structure that holds a mapping from strings to various data types. 
 * This class is used to translate between XMPP DataForms of the Smack library and Parameters.
 * An application can make use of the very simple interface that is provided by the ParameterMap, 
 * without knowing the details of the (rather complicated) underlying XMPP DataForms and the Smack implementation.
 * The internal translation of different data types to DataForms uses multiple FormFields to represent one variable. 
 * A limitation that follows because of this implementation is the restriction of variable names that 
 * MUST NOT contain the character "_", that is used to identify the FormFields belonging to one Parameter.       
 *
 */
public class ParameterMap extends Parameter{
	private static final String LOG_TAG = "ParameterMap";



	/**
	 * The map holding the data
	 */
	Map<String, Parameter> mMap = null;


	/**
	 * Constructs a new, empty ParameterMap.
	 */
	public ParameterMap() {
		mMap = new LinkedHashMap<String, Parameter>();
	}


	/**
	 * Constructs a ParameterMap containing a copy of the mappings from the given
	 * ParameterMap.
	 *
	 * @param b a ParameterMap to be copied.
	 */
	public ParameterMap(ParameterMap b) {

		if (b.mMap != null) {
			mMap = new LinkedHashMap<String, Parameter>(b.mMap);
		} else {
			mMap = null;
		}
	}


	/**
	 * Extracts the information of the form and creates a ParameterMap from it. 
	 * All previously contained Parameters are erased from the map.
	 * 
	 * @param form The input form that is translated to the ParameterMap
	 *  
	 */
	public void initializeFromForm(Form form) {
		mMap.clear();
		if(form!=null){
			LinkedHashMap<String, Specialization> mapSpecializations = new LinkedHashMap<String, Specialization>(); 
			Iterator<FormField> iterFields = form.getFields();
			ArrayList<FormField> actualParameters = new ArrayList<FormField>(); 

			//TODO this is a hack to enable special data types and validation
			//like integers with min and max values.
			//This should be replaced with XEP-0122.
			while(iterFields.hasNext()) {
				FormField field = iterFields.next();
				String variable = field.getVariable();
				String[] specialization = variable.split(Specialization.SPECIALIZECHARACTER);
				if(specialization.length == 2) {
					//some special parameter settings occured
					Specialization p = null;
					if( ! mapSpecializations.containsKey(specialization[0]))
					{
						p = new Specialization();
						mapSpecializations.put(specialization[0], p);

					} else {
						p = mapSpecializations.get(specialization[0]);
					}
					Iterator<String> values = field.getValues();
					if((values!=null)&&values.hasNext()){
						p.put(specialization[1], values.next());
					}
				} else if(specialization.length==1) {
					actualParameters.add(field);
				} else {
					System.out.println("Unexpected behaviour when transforming a Form to a ParameterMap: A FormField contained multiple underscore ('_') characters.");
				}

			}

			Iterator<FormField> iterParameters = actualParameters.iterator();
			while(iterParameters.hasNext()) {
				FormField field = iterParameters.next();
				String variable = field.getVariable();
				String label = field.getLabel();
				//String type = field.getType();
				String description = field.getDescription();
				Iterator<Option> options = field.getOptions();
				Iterator<String> values = field.getValues();
				ArrayList<String> valuesList = new ArrayList<String>();
				while(values.hasNext()){
					valuesList.add(values.next());
				}

				ArrayList<Parameter> optionList = new ArrayList<Parameter>();

				Specialization specialization = mapSpecializations.get(variable);
				if(specialization!=null) {
					String subType = specialization.get(Specialization.KEY_SUBTYPE);

					if(subType!=null) {
						while(options.hasNext()){
							Option option = options.next();
							String optionLabel = option.getLabel();
							String optionValue = option.getValue();
							Parameter optionParameter = Parameter.create(subType, optionLabel, optionValue);
							optionList.add(optionParameter);
						}
					}
				}
				Parameter parameter = null;
				try {
					parameter = Parameter.create(specialization, label, description, optionList, valuesList);
				} catch (SpecializationException e) {
					System.out.println("Unexpected behaviour when transforming a Form to a ParameterMap: A FormField could not be parsed.");
				}
				if(parameter!=null){
					putParameter(variable, parameter);
				}

			}
		}
	}


	

	/**
	 * Clones the current ParameterMap. The internal map is cloned, but the keys and
	 * values to which it refers are copied by reference.
	 */
	@Override
	public Object clone() {
		return new ParameterMap(this);
	}


	/**
	 * Returns the number of mappings contained in this ParameterMap.
	 *
	 * @return the number of mappings as an int.
	 */
	public int size() {
		return mMap.size();
	}

	/**
	 * Returns true if the mapping of this ParameterMap is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return mMap.isEmpty();
	}

	/**
	 * Removes all elements from the mapping of this ParameterMap.
	 */
	public void clear() {
		mMap.clear();
	}

	/**
	 * Returns true if the given key is contained in the mapping
	 * of this ParameterMap.
	 *
	 * @param key a String key
	 * @return true if the key is part of the mapping, false otherwise
	 */
	public boolean containsKey(String key) {
		return mMap.containsKey(key);
	}

	/**
	 * Returns the entry with the given key as a Parameter.
	 *
	 * @param key a String key
	 * @return an Object, or null
	 */
	public Parameter get(String key) {
		return mMap.get(key);
	}

	/**
	 * Removes any entry with the given key from the mapping of this ParameterMap.
	 *
	 * @param key a String key
	 */
	public void remove(String key) {
		mMap.remove(key);
	}

	/**
	 * Inserts all mappings from the given ParameterMap into this ParameterMap.
	 *
	 * @param map a ParameterMap
	 */
	public void putAll(ParameterMap map) {
		mMap.putAll(map.mMap);
	}

	/**
	 * Returns a Set containing the Strings used as keys in this ParameterMap.
	 *
	 * @return a Set of String keys
	 */
	public Set<String> keySet() {
		return mMap.keySet();
	}

	




	public Map<String, Parameter> getMap(){
		return mMap;
	}


	public void setLabel(String key, String label){
		Parameter p = mMap.get(key);
		if (p!=null) {
			p.setLabel(label);
		}
	}

	public void setDescription(String key, String description){
		Parameter p = mMap.get(key);
		if (p!=null) {
			p.setDescription(description);
		}
	}

	public void setHidden(String key, boolean bHidden){
		Parameter p = mMap.get(key);
		if (p!=null) {
			p.setIsHidden(bHidden);
		}
	}


	/**
	 * Inserts an int value into the mapping of this ParameterMap, replacing
	 * any existing value for the given key.
	 *
	 * @param key a String, or null
	 * @param value an int, or null
	 */
	public void putInt(String key, int value) {
		mMap.put(key, new IntParameter(value));
	}



	/**
	 * Inserts a float value into the mapping of this ParameterMap, replacing
	 * any existing value for the given key.
	 *
	 * @param key a String, or null
	 * @param value a float
	 */
	public void putFloat(String key, float value) {
		mMap.put(key, new FloatParameter(value));
	}



	/**
	 * Inserts a String value into the mapping of this ParameterMap, replacing
	 * any existing value for the given key.  Either key or value may be null.
	 *
	 * @param key a String, or null
	 * @param value a String, or null
	 */
	public void putString(String key, String value) {
		mMap.put(key, new StringParameter(value));
	}
	
	/**
	 * Inserts a Boolean value into the mapping of this ParameterMap, replacing
	 * any existing value for the given key.  Either key or value may be null.
	 *
	 * @param key a String, or null
	 * @param value a Boolean, or null
	 */
	public void putBoolean(String key, boolean value) {
		mMap.put(key, new BooleanParameter(value));
	}
	
	public void putParameter(String variable, Parameter parameter) {
		mMap.put(variable, parameter);
	}
	
	public void putMap(String variable, ParameterMap map) {
		mMap.put(variable, map);
	}


	public void putOption(String key, ArrayList<Parameter> options){
		mMap.put(key, new OptionParameter(options));
	}

	public void putOption(String key, OptionParameter option) {
		mMap.put(key, option);
	}

	public void putArrayParameter(String key, ArrayParameter array) {
		mMap.put(key, array);

	}




	// Log a message if the value was non-null but not of the expected type
	private void typeWarning(String key, Object value, String className,
			Object defaultValue, ClassCastException e) {
		StringBuilder sb = new StringBuilder();
		sb.append("Key ");
		sb.append(key);
		sb.append(" expected ");
		sb.append(className);
		sb.append(" but value was a ");
		sb.append(value.getClass().getName());
		sb.append(".  The default value ");
		sb.append(defaultValue);
		sb.append(" was returned.");
		Log.w(LOG_TAG, sb.toString());
		Log.w(LOG_TAG, "Attempt to cast generated internal exception:", e);
	}

	private void typeWarning(String key, Object value, String className,
			ClassCastException e) {
		typeWarning(key, value, className, "<null>", e);
	}


	/**
	 * Returns the value associated with the given key, or false if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a boolean value
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}


	/**
	 * Returns the value associated with the given key, or defaultValue if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a boolean value
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		Parameter o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return ((BooleanParameter) o).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "BooleanParameter", defaultValue, e);
			return defaultValue;
		}
	}



	/**
	 * Returns the value associated with the given key, or 0 if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return an int value
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}

	/**
	 * Returns the value associated with the given key, or defaultValue if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return an int value
	 */
	public int getInt(String key, int defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return ((IntParameter) o).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "IntParameter", defaultValue, e);
			return defaultValue;
		}
	}



	/**
	 * Returns the value associated with the given key, or 0.0f if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a float value
	 */
	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}

	/**
	 * Returns the value associated with the given key, or defaultValue if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a float value
	 */
	public float getFloat(String key, float defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return ((FloatParameter)o).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "FloatParameter", defaultValue, e);
			return defaultValue;
		}
	}
	
	
	public ParameterMap getMap(String key,ParameterMap defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (ParameterMap)o;
		} catch (ClassCastException e) {
			typeWarning(key, o, "ParameterMap", defaultValue, e);
			return defaultValue;
		}
	}




	/**
	 * Returns the value associated with the given key, or defaultValue if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a float OptionParameter value
	 */
	public float getOptionValue(String key, float defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return ((FloatParameter)((OptionParameter)o).getValue()).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "FloatParameter", defaultValue, e);
			return defaultValue;
		}
	}



	/**
	 * Returns the value associated with the given key, or defaultValue if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a int OptionParameter value
	 */
	public int getOptionValue(String key, int defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return ((IntParameter)((OptionParameter)o).getValue()).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "IntParameter", defaultValue, e);
			return defaultValue;
		}
	}


	/**
	 * Returns the value associated with the given key, or defaultValue if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a String OptionParameter value
	 */
	public String getOptionValue(String key, String defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return ((StringParameter)((OptionParameter)o).getValue()).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "StringParameter", defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Returns the value associated with the given key, or defaultValue if
	 * no mapping of the desired type exists for the given key.
	 *
	 * @param key a String
	 * @return a boolean OptionParameter value
	 */
	public boolean getOptionValue(String key, boolean defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return ((BooleanParameter)((OptionParameter)o).getValue()).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "BooleanParameter", defaultValue, e);
			return defaultValue;
		}
	}




	/**
	 * Returns the value associated with the given key, or null if
	 * no mapping of the desired type exists for the given key or a null
	 * value is explicitly associated with the key.
	 *
	 * @param key a String, or null
	 * @return a String value, or null
	 */
	public String getString(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return ((StringParameter) o).getValue();
		} catch (ClassCastException e) {
			typeWarning(key, o, "StringParameter", e);
			return null;
		}
	}


	public void createForm(Form form) {
		Set<String> keys =  keySet();
		if(keys.isEmpty()) {
			return;
		} else {

			Iterator<String> iterKeys = keys.iterator();
			while(iterKeys.hasNext()) {
				String key = iterKeys.next();
				Parameter parameter = get(key);
				if(parameter != null) {
					EpicFormField formField = new EpicFormField(key, parameter);
					Set<FormField> fields = formField.getFields();
					Iterator<FormField> iterFields = fields.iterator();
					while(iterFields.hasNext()){
						form.addField(iterFields.next());
					}
				}
			}
		}
	}

	public void fillForm(Form form) {
		Set<String> keys =  keySet();
		if(keys.isEmpty()) {
			return;
		} else {
			Iterator<String> iterKeys = keys.iterator();
			while(iterKeys.hasNext()) {
				String key = iterKeys.next();
				Parameter parameter = get(key);
				if(parameter != null) {
					FormField f = form.getField(key);
					if(f==null){
						//if field doesn't exist (might be the case for updates of known fields) -> create it
						Set<FormField> set = parameter.toFormFields(key);
						Iterator<FormField> iterator = set.iterator();  
						while(iterator.hasNext()){
							form.addField(iterator.next());
						}
					}

					//setting the results 
					if(parameter.getType().equalsIgnoreCase(Parameter.TYPENAME_BOOLEAN)) {
						boolean bValue = ((BooleanParameter)parameter).getValue();
						form.setAnswer(key, bValue);						
					} else if (parameter.getType().equalsIgnoreCase(Parameter.TYPENAME_OPTION)) {
						form.setAnswer(key, parameter.getValues());
					} else {
						form.setAnswer(key, parameter.getValues().get(0));
					}
				}
			}
		}
	}


	public void setSubmitActionHint(String key, boolean hint) {
		if(mMap.containsKey(key)){
			Parameter param = mMap.get(key);
			param.setSubmitActionHint(hint);
		}
	}


	@Override
	public String getType() {
		// 
		return Parameter.TYPENAME_MAP;
	}


	@Override
	public Set<FormField> toFormFields(String name) {
		// not supported!!!
		return null;
	}


	@Override
	public String asXml(String name) {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(name).append(" type=\"").append(getXmlType()).append("\">");
		buf.append(contentAsXml());
		buf.append("</").append(name).append(">");
		return buf.toString();
	}


	public String contentAsXml() {
		StringBuilder buf = new StringBuilder();
		Set<String> keys = mMap.keySet();
		Iterator<String> iterKeys = keys.iterator();
		while(iterKeys.hasNext()){
			String key = iterKeys.next();
			Parameter parameter = mMap.get(key);
			if(parameter!=null){
				buf.append(parameter.asXml(key));
			}
		}
		return buf.toString();
	}


	
	@Override
	public Parameter fromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
		ParameterMap map = new ParameterMap();
		
		//record the depth of the map's xml 
		int depth = parser.getDepth();
		int startdepth = depth;
		
		//proceed by one tag (out of map tag)
		int eventType = parser.nextTag();
		
		while( ! ((eventType == XmlPullParser.END_TAG)&&(depth==startdepth))){
			
			//proceed to the next start tag
			if(eventType==XmlPullParser.START_TAG){
				//String type = parser.getAttributeValue(null, "type");
				String name = parser.getName().trim();
				Parameter parameter = ParameterManager.getInstance().parseXml(parser);
				map.putParameter(name, parameter);
			}
			
			eventType = parser.next();
			depth = parser.getDepth();
		}
		return map;
	}
	
	@Override
	public String getXmlType(){
		return getType();
	}


	
	











}
