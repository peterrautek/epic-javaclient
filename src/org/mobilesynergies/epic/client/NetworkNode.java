package org.mobilesynergies.epic.client;

import org.jivesoftware.smack.packet.Presence;
import org.mobilesynergies.epic.client.PresenceTypeAdapter.UnknownPresenceException;

public class NetworkNode {
	/**
	 * Availability is unknown.
	 */
	public static final String AVAILABILITY_UNKNONW = "unknown";
	/**
	 * The node is unavailable.
	 */
	public static final String AVAILABILITY_UNAVAILABLE = "unavailable";
	/**
	 * The node is available.
	 */
	public static final String AVAILABILITY_AVAILABLE = "available";
	public static final String AVAILABILITY_SUBSCRIBE = "subscribe";
	public static final String AVAILABILITY_SUBSCRIBED = "subscribed";
	public static final String AVAILABILITY_UNSUBSCRIBE = "unsubscribe";
	public static final String AVAILABILITY_UNSUBSCRIBED = "unsubscribed";
	
	/**
	 * A error occurred
	 */
	public static final String AVAILABILITY_ERROR = "error";

	
	/**
	 * The address of the node
	 */
	public Address mAddress = null;
	
	/**
	 * A string describing the availability status of this node
	 */
	public String mAvailability = AVAILABILITY_UNKNONW;

	/**
	 * A string describing the subscription status of this node
	 */
	public String mSubscription = AVAILABILITY_UNKNONW;

	public NetworkNode(){
	}
	
	
	public NetworkNode(Address addr, String availability) {
		mAddress = addr;
		mAvailability = availability;
	}
	
	
	/**
     * Constructor only setting the address
     * @param addr The unique address of this node
     */
	public NetworkNode(Address addr){
		mAddress = addr;
	}
	
	
	
	/**
	 * Constructor 
	 * @param addr The unique address of this node
	 * @param availability
	 */
	public NetworkNode(Address addr, String availability, String subscription){
		mAddress = addr;
		mAvailability = availability;
		mSubscription = subscription;
	}
	
	/**
	 * Copy constructor
	 * @param otherNode Node that will be copied
	 */
	public NetworkNode(NetworkNode otherNode) {
		mAddress = otherNode.mAddress;
		mAvailability = otherNode.mAvailability;
		mSubscription = otherNode.mSubscription;
	}


	public NetworkNode(Presence presence) {
		String address = presence.getFrom();
		String name = org.jivesoftware.smack.util.StringUtils.parseName(address);
		String server = org.jivesoftware.smack.util.StringUtils.parseServer(address);
		String resource = org.jivesoftware.smack.util.StringUtils.parseResource(address);
		mAddress = new Address(name, server, resource);
		String stringPresence = "";
		try {
			stringPresence = PresenceTypeAdapter.toString(presence.getType());
		} catch (UnknownPresenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAvailability=stringPresence;
	}


	


}
