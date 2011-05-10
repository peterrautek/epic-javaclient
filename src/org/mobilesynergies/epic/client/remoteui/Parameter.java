package org.mobilesynergies.epic.client.remoteui;

import java.util.ArrayList;
import java.util.Set;

import org.jivesoftware.smackx.FormField;

/**
 *
 * The Parameter is a class that holds a parameter with constraints and certain hints. 
 * The parameter can be of different types (e.g., Boolean, String, Integer, Float, Option, etc.) that need to subclass the Parameter class. 
 * The ParameterMap maps key Strings to any kind of Parameter. Together they hide the complexity of XMPP form fields. 
 * Each parameter (unless specified to be hidden) is thought to be displayed in a automatically generated user interface on the client side.
 * 
 * The parameters are transformed to form fields using the following conventions:
 * boolean parameters are transformed to form fields of type boolean
 * string, integer, and float, parameters are transformed to form fields of type text-single
 * Specializations are transformed to form fields of type hidden. They follow the naming convention variable name + “_” + specialization name. 
 * The minimum value 0 for a integer variable named “myint” would for example be transformed to a form field with name “myint_min” with a value of 0.
 * The names of the specializations are “min”, “max”, “type”, “submithint”
 * 
 * @see ParameterMap
 */


public abstract class Parameter implements ParameterCreator{

	
	
	public static final String TYPENAME_FLOAT = "float";
	public static final String TYPENAME_INT = "int";
	public static final String TYPENAME_STRING = "string";
	public static final String TYPENAME_BOOLEAN = "boolean";
	public static final String TYPENAME_OPTION = "option";
	public static final String TYPENAME_ARRAY = "array";
	public static final String TYPENAME_MAP = "map";
	public static final String TYPENAME_UNKNOWN = "unknown";
	public static final String LOG_TAG = "Parameter";
	
	
		
	/**
	 * Default value or default values for this parameter. these values might be used by the ui generator.
	 */
	public ArrayList<String> mValues = new ArrayList<String>();

	/**
	 * True if this field must be submitted by the client.
	 */
	protected boolean mRequired = false;
	
	/**
	 * Is true if this parameter shall NOT be displayed to the user, and false otherwise.
	 */
	protected boolean mIsHidden = false;
	
	/**
	 * A description about the meaning of the parameter. the description shall give a clue about the usage and meaning of the parameter to assist the user in specifying this parameter. the description might be shown to the user on request.
	 */
	protected String mDescription = "";
	
	/**
	 * A label that shall be shown to the user.
	 */
	protected String mLabel = "";
	
	/**
	 * If true the client should immediately submit the ParameterMap when this parameter was changed by the user.
	 */
	protected boolean mSubmitActionHint = false;

	public abstract String getType();

	public abstract Set<FormField>  toFormFields(String name);
	
	public String getValueAsString(int index){
		if(index >= mValues.size())
			return null;
		return mValues.get(index);
	}
	
	public String getValueAsString(){
		if(mValues.size()==0){
			return null;	
		}
		return mValues.get(0);
	}

	public static Parameter create(String type, String label, String value) {
		Parameter p = null;
		if(type.equalsIgnoreCase(TYPENAME_BOOLEAN)){
			BooleanParameter bParam = new BooleanParameter(value);
			p=bParam;
		} else if(type.equalsIgnoreCase(TYPENAME_INT)){
			IntParameter iParam = new IntParameter(value);
			p=iParam;
		} else if(type.equalsIgnoreCase(TYPENAME_FLOAT)){
			FloatParameter fParam = new FloatParameter(value);
			p=fParam;
		} else if(type.equalsIgnoreCase(TYPENAME_OPTION)){
			OptionParameter bParam = new OptionParameter(value);
			p=bParam;
		} else {
			StringParameter sParam = new StringParameter(value);  
			p=sParam;
		}
		
		if(p!=null){
			p.setLabel(label);
		}
		return p;
	}

	public static Parameter create(Specialization specialization,
			String label, String description, ArrayList<Parameter> optionList,
			ArrayList<String> valuesList) throws SpecializationException {
		Parameter param = null;
		if(specialization==null){
			StringParameter p = new StringParameter();
			if(valuesList!=null){
				p.setValue(valuesList.get(0));
			}
			param = p;
		} else {
			String type = specialization.get(Specialization.KEY_TYPE);
			if((type==null) || (type.equalsIgnoreCase(TYPENAME_STRING))){
				StringParameter p = new StringParameter();
				if(valuesList!=null){
					p.setValue(valuesList.get(0));
				}
				param = p;
			} else if(type.equalsIgnoreCase(TYPENAME_BOOLEAN)){
				BooleanParameter p = new BooleanParameter();
				if(valuesList!=null){
					p.setValue(valuesList.get(0));
				}
				param = p;
			} else if (type.equalsIgnoreCase(TYPENAME_INT)){
				IntParameter p = new IntParameter();
				
				String min = specialization.get(Specialization.KEY_MIN);
				String max = specialization.get(Specialization.KEY_MAX);
				
				if(min!=null){
					p.setMin(Integer.parseInt(min));
				}
				if(max!=null){
					p.setMax(Integer.parseInt(max));
				}
				
				if(valuesList!=null){
					p.setValue(valuesList.get(0));
				}
				param = p;
			}  else if (type.equalsIgnoreCase(TYPENAME_FLOAT)){
				FloatParameter p = new FloatParameter();
				
				String min = specialization.get(Specialization.KEY_MIN);
				String max = specialization.get(Specialization.KEY_MAX);
				
				if(min!=null){
					p.setMin(Float.parseFloat(min));
				}
				if(max!=null){
					p.setMax(Float.parseFloat(max));
				}
				
				
				if(valuesList!=null){
					p.setValue(valuesList.get(0));
				}
				param = p;

			}  else if (type.equalsIgnoreCase(TYPENAME_OPTION)){
				OptionParameter p = new OptionParameter(optionList);
				
				if(valuesList!=null){
					p.setValues(valuesList);
				}
				
				param = p;

			} else {
				StringParameter p = new StringParameter();
				if(valuesList!=null){
					p.setValue(valuesList.get(0));
				}
				param = p;

			}
			
			if(specialization.containsKey(Specialization.KEY_SUBMITACTIONHINT)){
				String hint = specialization.get(Specialization.KEY_SUBMITACTIONHINT);
				param.setSubmitActionHint(Boolean.parseBoolean(hint));
			}
		}
		param.mLabel = label;
		param.mDescription = description;
		return param;
	}

	public ArrayList<String> getValues() {
		return mValues;
	}

	public void setLabel(String label) {
		mLabel=label;
	}

	public void setDescription(String description) {
		mDescription=description;
	}

	public void setIsHidden(boolean bHidden) {
		mIsHidden = bHidden;
	}
/*
	public void addOption(Option option) {
		mOptions.add(option);
	}*/
	
	public String getLabel(){
		return mLabel;
	}
	
	public String getDescription(){
		return mDescription;
	}
	
	public boolean getIsHidden(){
		return mIsHidden;
	}
	
	public boolean getSubmitActionHint(){
		return mSubmitActionHint;
	}
	
	public void setSubmitActionHint(boolean hint){
		mSubmitActionHint=hint;
	}
	
	public abstract String asXml(String name);
	
	

	
}
