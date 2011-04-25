package org.mobilesynergies.epic.client;

/**
 * 
 * A callback interface that is called whenever presence changes
 * 
 *   @author Peter
 */
public interface PresenceCallback {
	/**
	 * Called when the presence of a node changes
	 * @param node The network node that changed presence
	 */
	public abstract void onPresenceChanged(NetworkNode node);
}
