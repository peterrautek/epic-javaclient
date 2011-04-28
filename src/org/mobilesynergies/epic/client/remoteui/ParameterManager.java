package org.mobilesynergies.epic.client.remoteui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ParameterManager {

	// Innere private Klasse, die erst beim Zugriff durch die umgebende Klasse initialisiert wird
	private static final class InstanceHolder {
		// Die Initialisierung von Klassenvariablen geschieht nur einmal 
		// und wird vom ClassLoader implizit synchronisiert
		static final ParameterManager INSTANCE = new ParameterManager();
	}
	
	Map<String, ParameterCreator> mMap = new HashMap<String, ParameterCreator>(); 

	// make it impossible to create a new instance via other methods
	private ParameterManager () {}
	
	public static ParameterManager getInstance () {
		return InstanceHolder.INSTANCE;
	}
	
	public void registerParameterCreator(ParameterCreator p){
		mMap.put(p.getXmlType(), p);
	}

	public ParameterCreator getParameterCreator(String name){
		return mMap.get(name);
	}
	
	public Parameter parseXml(XmlPullParser parser) throws XmlPullParserException, IOException{
		int event = parser.getEventType();
		//proceed to the next start or end tag
		while((event != XmlPullParser.START_TAG)&&(event != XmlPullParser.END_TAG)){
			event = parser.next();
		}
		
		if(event==XmlPullParser.END_TAG){
			return null; 
		}
			
		//read out the type			
		String type = parser.getAttributeValue(null, "type");
		
		if(type==null){
			return null;
		}
		//get the parameter creator for the given type
		ParameterCreator pc = ParameterManager.getInstance().getParameterCreator(type);

		//create the parameter from xml
		Parameter p = pc.fromXml(parser);
		return p;
	}


}
