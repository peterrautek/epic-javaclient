package org.mobilesynergies.epic.client.remoteui;

public class EpicCommandInfo {

	private String mHumanReadableName;
	private String mEpicNodeId;
	private String mEpicCommandId;

	public EpicCommandInfo(){
		
	}
	
	public EpicCommandInfo(String nodeId, String commandId, String humanReadableName) {
		setEpicNodeId(nodeId);
		setEpicCommandId(commandId);
		setHumanReadableName(humanReadableName);
	}

	/**
	 * @param id the id to set
	 */
	public void setEpicNodeId(String id) {
		this.mEpicNodeId = id;
	}

	/**
	 * @return the mId
	 */
	public String getEpicNodeId() {
		return mEpicNodeId;
	}

	/**
	 * @param humanReadableName the humanReadableName to set
	 */
	public void setHumanReadableName(String humanReadableName) {
		this.mHumanReadableName = humanReadableName;
	}

	/**
	 * @return the mHumanReadableName
	 */
	public String getHumanReadableName() {
		return mHumanReadableName;
	}

	/**
	 * @param epicCommandId the epicCommandId to set
	 */
	public void setEpicCommandId(String epicCommandId) {
		this.mEpicCommandId = epicCommandId;
	}

	/**
	 * @return the mEpicCommandId
	 */
	public String getEpicCommandId() {
		return mEpicCommandId;
	}

}
