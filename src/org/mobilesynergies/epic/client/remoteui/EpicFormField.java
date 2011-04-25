package org.mobilesynergies.epic.client.remoteui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jivesoftware.smackx.FormField;

public class EpicFormField {
	
	private Parameter mParameter = null;
	private String mLabel = "";

	public EpicFormField(String label, Parameter parameter) {
		mParameter  = parameter;
		mLabel = label;
	}

	public Set<FormField> getFields() {
		Set<FormField> set = null;
		if(mParameter!=null) {
			set = mParameter.toFormFields(mLabel);
		}
		return set;
	}

}
