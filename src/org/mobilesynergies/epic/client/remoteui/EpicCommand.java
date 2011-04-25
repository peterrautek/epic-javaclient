package org.mobilesynergies.epic.client.remoteui;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.commands.LocalCommand;

/**
 * The EpicCommand class wraps the semantics of the LocalCommand class of the Smack library and translates to EPIC semantics.
 * This class needs to be implemented to offer a EpicCommand (i.e., a Remote Human User Interface) that can be executed by a remote node.
 * 
 * The Remote Human User Interfaces are designed to allow humans to interact with remote machines. There are certain strength of this approach:
 * Remote control: A device or machine that is not physically accessible by the user can be controlled via a mobile phone or other device
 * Ad-hoc control: The client does not need to have any knowledge, about the device or process that is controlled, at the first place.
 * Implicit user interface definition: The server (the device that offers commands to control it) declares parameters for its functions and does not need to worry about how this parameters are displayed to the user. Therefore, devices without an advanced user interface (like a touchscreen) can offer complex functionality to the user.
 * 
 * The EPIC client and its applications have typically no prior knowledge about the remote machine and the commands it offers. To better reflect the fact that the client is agnostic about the remote command it was named Remote Human User Interfaces (opposed to Remote Machine Commands, and the misleading name Ad-Hoc Commands of XMPP).
 * The protocol is however implemented on top of Ad-Hoc Commands (XEP-0050), that do in fact have the same use case.
 * 
 * One of the main concepts for Remote Human User Interfaces is the ParameterMap class. The server implements methods that are called by the client. The ParameterMap is used to define neccessary input and output parameters.
 *    
 * @see ParameterMap
 * @author Peter
 */ 

public abstract class EpicCommand extends LocalCommand{
	
	
	/**
	 * Provides an EpicCommandInfo object that holds the properties of this command.
	 * @param epicNodeId The id of the node providing this command.
	 * @return The EpicCommandInfo object.
	 */
	public EpicCommandInfo getCommandInfo(String epicNodeId){
		return new EpicCommandInfo(epicNodeId, getId(), getHumanReadableName());
	}

	/**
	 * Provides an id of this EpicCommand that is unique for this node. 
	 * @return The unique id for this command
	 */
	public abstract String getId();

	/**
	 * Provides the human readable name for this EPIC command.
	 * @return The human readable name of this command.
	 */
	public abstract String getHumanReadableName();
		
	/**
	 * Allows for fine grained access control. The EPIC command might decide which users can perform this remote action. 
	 * @param node The full address of the node that wants to execute the remote command.
	 * @return Returns true if the requesting node is allowed to perform this action, and false otherwise. 
	 */
	public abstract boolean hasPermission(String node);

	/**
	 * This method is called if the client requests cancellation. It should implement all necessary steps to cancel the command.  
	 */
	public abstract void cancel();

	/**
	 * Proceed to the next stage. This method is called if the client requested to proceed to the next stage. 
	 * @param response The result of the previous stage (i.e., the parameters that are supplied by the client).
	 * @return The parameters needed to complete this stage.  
	 */
	public abstract ParameterMap nextStage(ParameterMap response);

	/**
	 * Go back to previous stage. This function is called if the client requested to go to the previous stage.
	 */
	public abstract ParameterMap previousStage();

	/**
	 * Information if this is the last stage.
	 * @return Returns true if the current stage is the last stage of this command, and false otherwise.
	 */
	public abstract boolean isLastStage();
	
	/**
	 * Implements actions that need to be performed upon initialization of the command.
	 */
	public abstract ParameterMap initialize();
	
	/**
	 * Implements actions that need to be performed upon finalization of the command.
	 * @param response The result from the previous stage.
	 */
	public abstract void finalize(ParameterMap response);
	
	@Override
	public final void complete(Form response) throws XMPPException {
		ParameterMap pm = new ParameterMap();
		pm.initializeFromForm(response);
		finalize(pm);
	}

	@Override
	public final void execute() throws XMPPException {
		ParameterMap map = initialize();
		
		if(isLastStage()){
			this.addActionAvailable(Action.complete);
			this.setExecuteAction(Action.complete);
		} else {
			this.addActionAvailable(Action.next);
			this.setExecuteAction(Action.next);
		}
		
		if((map==null)||(map.isEmpty())){
			//TODO
		} else {
			Form form = new Form(Form.TYPE_FORM);
			map.createForm(form);
			setForm(form);
		}				
	}

	@Override
	public final void next(Form response) throws XMPPException {
		ParameterMap responseMap = new ParameterMap();
		responseMap.initializeFromForm(response);
		ParameterMap pmNextStage = nextStage(responseMap);
		
		if(isLastStage()){
			this.addActionAvailable(Action.complete);
			this.setExecuteAction(Action.complete);
		} else {
			this.addActionAvailable(Action.next);
			this.setExecuteAction(Action.next);
		}

		if((pmNextStage==null)||(pmNextStage.isEmpty())){
			//TODO
		} else {
			Form form = new Form(Form.TYPE_FORM);
			pmNextStage.createForm(form);
			setForm(form);
		}				
	}

	@Override
	public final void prev() throws XMPPException {
		previousStage();
	}
	
		
}
