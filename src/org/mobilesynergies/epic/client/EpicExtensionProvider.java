package org.mobilesynergies.epic.client;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.mobilesynergies.epic.client.remoteui.Parameter;
import org.mobilesynergies.epic.client.remoteui.ParameterManager;
import org.mobilesynergies.epic.client.remoteui.ParameterMap;
import org.xmlpull.v1.XmlPullParser;

public class EpicExtensionProvider implements PacketExtensionProvider{

	public static final String NAMESPACE = "http://mobilesynergies.org/protocol/epic"; 
	/**
	 * Creates a new MessageEventProvider.
	 * ProviderManager requires that every PacketExtensionProvider has a public, no-argument constructor
	 */
	public EpicExtensionProvider() {
	}

	/**
	 * Parses a MessageEvent packet (extension sub-packet).
	 *
	 * @param parser the XML parser, positioned at the starting element of the extension.
	 * @return a PacketExtension.
	 * @throws Exception if a parsing error occurs.
	 */
	public PacketExtension parseExtension(XmlPullParser parser)
	throws Exception {
		//String receiver = parser.getAttributeValue(null, "to");
		String action = parser.getAttributeValue(null, "action");
		String session = parser.getAttributeValue(null, "session");
		String spackage = parser.getAttributeValue(null, "package");
		String sclass = parser.getAttributeValue(null, "class");
		//proceed to the next start tag (the data) or end tag ('application' tag closes without data).
		parser.next();
		int event = parser.getEventType();
		while((event != XmlPullParser.START_TAG) && (event != XmlPullParser.END_TAG)){
			//proceed to the next tag
			event = parser.next();
		}
		
		ParameterMap map = (ParameterMap) ParameterManager.getInstance().parseXml(parser);
		EpicPacketExtension messageEvent = new EpicPacketExtension(action, session, spackage, sclass, map);
		return messageEvent;
	}

}
