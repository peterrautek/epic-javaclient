package org.mobilesynergies.epic.client.remoteui;


public class SpecializationException extends Exception {
	
	/**
	 * A wrapper around Exception 
	 */
	private static final long serialVersionUID = 1L;

	public SpecializationException(String string) {
		super(string);
	}
	
	public SpecializationException(Exception e) {
		super(e);
	}

	public String getMessage(){
		return "SpecializationException: " + super.getMessage();
	}


}
