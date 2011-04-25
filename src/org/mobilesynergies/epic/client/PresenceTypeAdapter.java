package org.mobilesynergies.epic.client;

import org.jivesoftware.smack.packet.Presence;

/**
 * 
 * Adapter class converting the smack.Presence.Type class into a string
 * @author Peter 
 *
 */
public class PresenceTypeAdapter {
	
	public static String AVAILABLE = "available"; 
	public static String UNAVAILABLE = "unavailable";
	public static String SUBSCRIBE = "subscribe";
	public static String UNSUBSCRIBE = "unsubscribe";
	public static String SUBSCRIBED = "subscribed";
	public static String UNSUBSCRIBED = "unsubscribed";
	public static String ERROR = "error";
	
	public static class UnknownPresenceException extends Exception{
	};
	
	/**
	 * Converting the Presence.Type to a string.
	 * 
	 */
	public static String toString(Presence.Type t) throws UnknownPresenceException{
		if(t==Presence.Type.available){
			return AVAILABLE;
		} else if (t==Presence.Type.unavailable) {
			return UNAVAILABLE;
		} else if (t==Presence.Type.subscribe) {
			return SUBSCRIBE;
		} else if (t==Presence.Type.subscribed) {
			return SUBSCRIBED;
		} else if (t==Presence.Type.unsubscribe) {
			return UNSUBSCRIBE;
		} else if (t==Presence.Type.unsubscribed) {
			return UNSUBSCRIBED;
		} else if (t==Presence.Type.error) {
			return ERROR;
		} 

		throw new UnknownPresenceException();
		
	}

}
