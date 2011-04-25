package org.mobilesynergies.epic.client;

import org.jivesoftware.smack.XMPPException;

public class EpicClientException extends Exception {

	
	
	/**
	 * A wrapper around Exception 
	 */
	private static final long serialVersionUID = 1L;

	public EpicClientException(String string) {
		super(string);
	}
	
	public EpicClientException(XMPPException e) {
		super(e);
	}

	public String getMessage(){
		return "EpicClientException: " + super.getMessage();
	}

}
