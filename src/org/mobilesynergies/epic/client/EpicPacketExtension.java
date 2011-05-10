package org.mobilesynergies.epic.client;

import org.jivesoftware.smack.packet.PacketExtension;
import org.mobilesynergies.epic.client.remoteui.ParameterMap;



/**
 * Implementation of the EpicPacketExtension. 
 * 
 * @author Peter Rautek
 */

public class EpicPacketExtension implements PacketExtension{

	public static final String ELEMENTNAME = "application";
    public static final String NAMESPACE = "http://mobilesynergies.org/protocol/epic";
    
    private ParameterMap mData;
    private String mAction;
    private String mPackage;
    private String mClass;
    private String mSession;
    
  
    public EpicPacketExtension(String action, String session, String spackage, String sclass,
			ParameterMap data) {
		mAction = action; 
		mPackage = spackage;
		mClass = sclass;
		mData = data;
		mSession = session;
	}
    
    
    public String getSessionId(){
    	return mSession;
    }

	/*public Parameter getValue(String key){
    	return mData.get(key);
    }*/

     /**
     * Returns the XML element name of the extension sub-packet root element.
     * @return the XML element name of the packet extension.
     */
    public String getElementName() {
        return ELEMENTNAME;
    }

    /**
     * Returns the XML namespace of the extension sub-packet root element.
     *
     * @return the XML namespace of the packet extension.
     */
    public String getNamespace() {
        return NAMESPACE;
    }
    
    /**
     * Returns the XML namespace of the extension sub-packet root element.
     *
     * @return the XML namespace of the packet extension.
     */
    public String getAction() {
        return mAction;
    }

    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(ELEMENTNAME).append(" xmlns=\"").append(NAMESPACE).append("\" action=\"").append(mAction).append("\"");
        if((mSession!=null)&&(mSession.trim().length()>0)){
        	buf.append(" session=\"").append(mSession.trim()).append("\"");
        }
        
        if((mPackage!=null)&&(mClass!=null)){
        	buf.append(" package=\"").append(mPackage).append("\"");
        	buf.append(" class=\"").append(mClass).append("\"");
        }
        
        buf.append(" >");
        if(mData!=null){
        	buf.append(mData.asXml("data"));
        }
        buf.append("</").append(ELEMENTNAME).append(">");
        return buf.toString();
    }

	public ParameterMap getParameterMap() {
		
		return mData;
	}


	public String getClassName() {
		return mClass;
	}

	public String getPackageName() {
		return mPackage;
	}


   

}