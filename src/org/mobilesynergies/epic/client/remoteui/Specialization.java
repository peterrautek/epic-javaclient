package org.mobilesynergies.epic.client.remoteui;

import java.util.HashMap;

public class Specialization {

	
	public static final String KEY_TYPE = "type";
	public static final String KEY_MIN = "min";
	public static final String KEY_MAX = "max";
	public static final String KEY_SUBTYPE = "subtype";
	public static final String KEY_SUBMITACTIONHINT = "submithint";
	
	public static final String SPECIALIZECHARACTER = "_";
	
	HashMap<String, String> mMapKeyValue = new HashMap<String, String>();
	
	void insert(String key, String value){
		mMapKeyValue.put(key, value);
	}
	
	HashMap<String, String> getMap(){
		return mMapKeyValue;
	}

	public String get(String key) {
		return mMapKeyValue.get(key);
	}

	public boolean containsKey(String key) {
		return mMapKeyValue.containsKey(key);
	}

	public void put(String key, String value) {
		mMapKeyValue.put(key, value);
	}
	
}
