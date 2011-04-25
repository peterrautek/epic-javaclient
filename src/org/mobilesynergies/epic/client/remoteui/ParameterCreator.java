package org.mobilesynergies.epic.client.remoteui;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public interface ParameterCreator {

	public Parameter fromXml(XmlPullParser parser) throws XmlPullParserException, IOException;
	public String getXmlType();
	
}
