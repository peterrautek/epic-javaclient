package org.mobilesynergies.epic.client;

import org.mobilesynergies.epic.client.remoteui.ParameterMap;



/**
 * 
 * @author Peter
 * Interface for message callbacks. This interface is implemented by classes that wait for incoming messages.
 *
 */
public interface IncomingMessageCallback {

	/**
     * This implementation is used to receive message callbacks from the Epic service.
	 * @param  
     */
    public abstract boolean handleMessage(String from, String action, String sessionid, String packageName, String className, ParameterMap message);

}
