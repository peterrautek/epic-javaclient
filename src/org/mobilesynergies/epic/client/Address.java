package org.mobilesynergies.epic.client;

/**
 * This class represents addresses of epic nodes. The addresses are equivalent to Jabber Ids and consist of the name of a node the name of a server and the name of a resource. The full address is node + @ + server + / + resource.    
 * @author Peter
 *
 */
public class Address {

	/**
	 * The name of the epic network node
	 */
	private String mName;
	/**
	 * The name of the server 
	 */
	private String mServer;
	/**
	 * The name of the resource
	 */
	private String mResource;

	/**
	 * Constructor for a full EPIC network address.
	 * @param name The name of the epic network node 
	 * @param server The name of the server
	 * @param resource The name of the resource
	 */
	public Address(String name, String server, String resource) {
		setName(name);
		setServer(server);
		setResource(resource);
	}
	
	/**
	 * Generate a string representing the full EPIC network address. This includes the resource.
	 * @return The full address
	 */
	public String getFullAddressString(){
		String address = getBareAddressString();
		if((mResource!=null)&&(mResource.length()>0)){
			address += "/"+mResource;
		}
		return address;
	}
	
	/**
	 * Generate a string representing the bare EPIC network address. This is the address without the resource
	 * @return The bare address
	 */
	public String getBareAddressString(){
		return mName+"@"+mServer;
	}


	/**
	 * @param resource the resource to set
	 */
	public void setResource(String resource) {
		this.mResource = resource;
	}

	/**
	 * @return the resource
	 */
	public String getResource() {
		return mResource;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.mServer = server;
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return mServer;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.mName = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}
	
	

}
