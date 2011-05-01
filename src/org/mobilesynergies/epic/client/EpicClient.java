package org.mobilesynergies.epic.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.commands.AdHocCommandManager;
import org.jivesoftware.smackx.commands.RemoteCommand;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.IBBProviders;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.mobilesynergies.epic.client.remoteui.ArrayParameter;
import org.mobilesynergies.epic.client.remoteui.BooleanParameter;
import org.mobilesynergies.epic.client.remoteui.EpicCommand;
import org.mobilesynergies.epic.client.remoteui.EpicCommandInfo;
import org.mobilesynergies.epic.client.remoteui.FloatParameter;
import org.mobilesynergies.epic.client.remoteui.IntParameter;
import org.mobilesynergies.epic.client.remoteui.OptionParameter;
import org.mobilesynergies.epic.client.remoteui.ParameterManager;
import org.mobilesynergies.epic.client.remoteui.ParameterMap;
import org.mobilesynergies.epic.client.remoteui.StringParameter;

/**
 * 
 * The EpicClient encapsulates all xmpp usages and is platform independent (should run on windows, linux, and android).
 * It provides methods for establishing a server connection, registration, authentication, epic network exploration, announcing and executing remote commands, etc. 
 * 
 * @author Peter
 *
 */

public class EpicClient {

	protected static final String CLASS_TAG = EpicClient.class.getSimpleName();

	/**
	 * The default xmpp port
	 */
	private static final int DEFAULT_PORT = 5222;

	/**
	 * The XMPP connection that does all the communication with the XMPP server
	 */
	private XMPPConnection mConnection;

	/**
	 * A username to register at the server 
	 
	private String mUsername;*/

	/**
	 * The password that corresponds to the username
	 
	private String mPassword;*/

	/**
	 * The name of the server
	 
	private String mServer;*/

	/**
	 * The service discovery manager 
	 */
	private ServiceDiscoveryManager mDiscoManager = null;

	/**
	 * The AdHocCommandManager used to announce EpicCommands at the server and for discovery of EpicCommands at other nodes.
	 * 
	 */
	private AdHocCommandManager mAdHocCommandManager = null;

	/**
	 * A map of commands that is currently in (remote) execution. The commands are identified with their session id (the key of the map).
	 */
	private ConcurrentHashMap<String, RemoteCommand> mRemoteCommands = new ConcurrentHashMap<String, RemoteCommand>();

	/**
	 * The roster holds the list of remote nodes that are connected to the same EPIC network.
	 */
	Roster mRoster = null;

	/**
	 * A callback for availability changes of network nodes in the same EPIC network 
	 */
	private PresenceCallback mPresenceCallback = null;
	
	/**
	 * A callback that is called when connectivity of the epic network changed. 
	 * This might be due to server errors, etc 
	 */
	private EpicNetworkConnectivityCallback mEpicNetworkConnectivityCallback = null;

	/**
	 * Callbackfunctions for changes in EPIC network connectivity.
	 */
	private ConnectionListener mConnectionListener = new ConnectionListener(){

		public void connectionClosed() {
			if(mEpicNetworkConnectivityCallback!=null){
				mEpicNetworkConnectivityCallback.onConnectionClosed();
			}
			Log.d(CLASS_TAG, "connectionClosed");
		}

		public void connectionClosedOnError(Exception e) {
			if(mEpicNetworkConnectivityCallback!=null){
				mEpicNetworkConnectivityCallback.onConnectionClosedOnError();
			}
			
			Log.d(CLASS_TAG, "connectionClosedOnError: "+e.getMessage());
			XMPPException x = new XMPPException(e);
			StreamError streamerror = x.getStreamError();
			if(streamerror != null){
				Log.d(CLASS_TAG, "StreamError: "+streamerror.toString());
				
			}
		}

		public void reconnectingIn(int seconds) {
			/*if(mEpicNetworkConnectivityCallback!=null){
				mEpicNetworkConnectivityCallback.onConnectivityChanged(false);
			}*/
			Log.d(CLASS_TAG, "reconnectingIn "+seconds+ "seconds");				
		}

		public void reconnectionFailed(Exception e) {
			if(mEpicNetworkConnectivityCallback!=null){
				mEpicNetworkConnectivityCallback.onConnectionClosedOnError();
			}
			Log.d(CLASS_TAG, "reconnectionFailed: "+e.getMessage());				
		}

		public void reconnectionSuccessful() {
			/*if(mEpicNetworkConnectivityCallback!=null){
				mEpicNetworkConnectivityCallback.onConnectivityChanged(true);
			}*/
			Log.d(CLASS_TAG, "reconnectionSuccessful");				
		}

	};

	
	private IncomingMessageCallback mEpicMessageCallback = null;
	
	
	public void registerEpicMessageCallback(IncomingMessageCallback callback){
		mEpicMessageCallback = callback;
	}
	
	
	
	public void registerEpicNetworkConnectivityCallback(EpicNetworkConnectivityCallback callback){
		mEpicNetworkConnectivityCallback = callback;
	}
	
	
	
	/**
	 * The roster listener that implements callback functions for changes, requests, etc. at the roster
	 */
	private RosterListener mRosterListener = new RosterListener(){

		public void entriesAdded(Collection<String> addresses) {
			// TODO Auto-generated method stub
		}

		public void entriesDeleted(Collection<String> addresses) {
			// TODO Auto-generated method stub
		}

		public void entriesUpdated(Collection<String> addresses) {
			// TODO Auto-generated method stub
		}

		public void presenceChanged(Presence presence) {
			if(mPresenceCallback!=null) {
				mPresenceCallback.onPresenceChanged(new NetworkNode(presence));
			}
		}
	};

	
	public boolean establishConnection(String server) throws EpicClientException{
		return establishConnection(server, DEFAULT_PORT, server);
	}
	
	public boolean establishConnection(String server, int port) throws EpicClientException{
		return establishConnection(server, port, server);
	}

	public boolean establishConnection(String server, String service) throws EpicClientException{
		return establishConnection(server, DEFAULT_PORT, service);
	}



	/**
	 * Establishes a connection  
	 * @param server The name of the xmpp server
	 * @return True if the connection could be established 
	 * @throws EpicClientException An exception is thrown if the connection could not be established. This might be due to unavailable internet connectivity, a server problem, etc.
	 */
	public boolean establishConnection(String server, int port, String service) throws EpicClientException{
		if(mConnection == null){
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			ProviderManager pm = ProviderManager.getInstance();
			//init the provider manager (adding extensions that are implemented in smack)
			configure(pm);
			ConnectionConfiguration connConfig = new ConnectionConfiguration(server, port, service);
			connConfig.setReconnectionAllowed(false);
			mConnection = new XMPPConnection( connConfig );
		}

		// Obtain the ServiceDiscoveryManager associated with my XMPPConnection
		// This actually yields a null pointer! (strange behavior of smack!?). 
		// It however triggers the static block of this class initializing an instance.
		// Therefore it must not be removed here!
		mDiscoManager = ServiceDiscoveryManager.getInstanceFor(mConnection);
		// Obtain the AdHocCommandManager associated with my XMPPConnection
		// This actually yields a null pointer! (strange behavior of smack!?). 
		// It however triggers the static block of this class initializing an instance.
		// Therefore it must not be removed here!
		mAdHocCommandManager = AdHocCommandManager.getAddHocCommandsManager(mConnection);

		try {
			mConnection.connect();
		} catch (XMPPException e) {
			throw new EpicClientException(e);
		}

		//Due to the strange behavior of smack we get the pointers to the managers in the following.
		//The first calls to the getInstance functions do not return an instance of the managers but null!
		//However, they have to be called in order to trigger the class loader (and the static block). 
		//This creates instances of the managers and we can finally retrieve them here (calling the same commands a second time).
		mDiscoManager = ServiceDiscoveryManager.getInstanceFor(mConnection);
		mAdHocCommandManager = AdHocCommandManager.getAddHocCommandsManager(mConnection);


		boolean bIsConnected = mConnection.isConnected();
		if(bIsConnected) {
			mConnection.addConnectionListener(mConnectionListener );
		}

		return bIsConnected;
	}

	private static void configure(ParameterManager paramManager) {
		paramManager.registerParameterCreator(new StringParameter());
		paramManager.registerParameterCreator(new FloatParameter());
		paramManager.registerParameterCreator(new IntParameter());
		paramManager.registerParameterCreator(new ArrayParameter());
		paramManager.registerParameterCreator(new BooleanParameter());
		paramManager.registerParameterCreator(new OptionParameter());
		paramManager.registerParameterCreator(new ParameterMap());
	}



	public void disconnect(){
		if(mConnection!=null){
			mConnection.disconnect();
		}
	}

	/**
	 * Authenticates the user at the server.
	 *
	 * @param username The user name
	 * @param password The password of the user
	 * @return Returns true if the user could be authenticated. False if the user is not registered, or a wrong user name and password combination was provided.
	 * @throws EpicClientException An exception is thrown if the user could not be authenticated or the EPIC network information could not be accessed. This might be due to unavailable connection, a server problem, etc. 
	 */
	public boolean authenticateUser(String username, String password, String ressource) throws EpicClientException{

		//mUsername = username;
		//mPassword = password;
		if((mConnection==null)||(!mConnection.isConnected())){
			throw new EpicClientException("No open connection available.");
		}
		try {
			mConnection.login(username, password, ressource);
		} catch (XMPPException e) {
			throw new EpicClientException(e);
		}

		boolean bAuthenticated = mConnection.isAuthenticated(); 
		if(bAuthenticated){
			mRoster = mConnection.getRoster();
			if(mRoster!=null){
				mRoster.addRosterListener(mRosterListener);
			} else {
				mConnection.disconnect();
				throw new EpicClientException("The EPIC network information was not available. Closed the connection.");
			}
		}
		
		registerEpicMessageListener();
		
		return bAuthenticated;		
	}

	
		
	private void registerEpicMessageListener(){
		PacketListener pl = new PacketListener(){
			@Override
			public void processPacket(Packet packet) {
				if(mEpicMessageCallback == null){
					//nobody is listening -> nothing to do
					return;
				}
				String from = packet.getFrom();
				EpicPacketExtension pe = (EpicPacketExtension)packet.getExtension("http://mobilesynergies.org/protocol/epic");
				if(pe!=null){
					String action = pe.getAction();
					if(action==null){
						Log.d(CLASS_TAG, "Ignoring epic message without 'action' element."); 
					} else {
						action = action.trim();
						ParameterMap pm = pe.getParameters();
						
						if(action.length()>0) {
							String sessionid = pe.getSessionId();
							if(sessionid!=null){
								sessionid = sessionid.trim();
							}
							String packageName = pe.getPackageName();
							if(packageName!=null){
								packageName = packageName.trim();
							}
							String className = pe.getClassName();
							if(className!=null)
							{
								className = className.trim();
							}
							mEpicMessageCallback.handleMessage(from, action, sessionid, packageName, className, pm);
						}
					}
					
				} else {
					Log.d(CLASS_TAG, "P: "+packet.toXML());
				}
			}
		};
		
		PacketFilter pf = new PacketFilter(){
			@Override
			public boolean accept(Packet packet) {
				return true;
			}
		};
	
		mConnection.addPacketListener(pl, pf);
	}

	public void sendMessage(String receivernode, String action, String sessionid, ParameterMap data) {		
		Packet packet = new Message(receivernode);
		EpicPacketExtension extension = new EpicPacketExtension(action, sessionid, null, null, data);
		packet.addExtension(extension);
		mConnection.sendPacket(packet);
	}
	


	/**
	 * Registers a presence callback  
	 *
	 * @param callback The callback object
	 * 
	 */
	public void registerPresenceCallback(PresenceCallback callback) {
		mPresenceCallback  = callback;
	}

	/**
	 * Connection status
	 * @return Returns true if the client is connected to the xmpp server
	 */
	public boolean isConnected() {
		if(mConnection==null)
			return false;
		return mConnection.isConnected();
	}

	/**
	 * Performs checks if there is connectivity to the server, the client could be authenticated
	 * @return Returns true if connected to EPIC network 
	 */
	public boolean isConnectedToEpicNetwork() {
		if(!isConnected()){
			return false;
		}
		if(!mConnection.isAuthenticated()){
			return false;
		}
		return true;			
	}


	/**
	 * Get the list of epic nodes that are connected with this EPIC network node.
	 * @return A list of epic nodes 
	 * @throws EpicClientException Thrown if the client is not connected to the EPIC network.
	 */
	public NetworkNode[] getNetworkNodes() throws EpicClientException{
		if(! isConnectedToEpicNetwork()){
			throw new EpicClientException("The client is not connected to the EPIC network.");
		}

		Collection<RosterEntry> entries = mRoster.getEntries();
		if(entries==null) {
			return null;
		}

		if(entries.size()<=0) {
			return null;
		}

		ArrayList<NetworkNode> list = new ArrayList<NetworkNode>();
		Iterator<RosterEntry> iterUsers = entries.iterator();

		while (iterUsers.hasNext()){
			RosterEntry entry = iterUsers.next();
			String user = entry.getUser();
			Iterator<Presence> iterPresences = mRoster.getPresences(user);
			while (iterPresences.hasNext()){
				Presence presence = iterPresences.next();
				NetworkNode node = new NetworkNode(presence);
				list.add(node);
			}
		}
		NetworkNode[] nodes = null;
		if(list.size()>0){
			nodes = (NetworkNode[]) list.toArray(new NetworkNode[list.size()]);
		}
		return nodes; 
	}

	/**
	 * Executes a remote command at a different network node. The remote node might reject executing the command based on the address of the calling node. If it executes the command it will send a ParameterMap that contains the necessary parameters to complete one stage of the command.  
	 * @param epicNode The full address of the epic node that shall execute the command.
	 * @param command The (in the context of the epicNode) unique  name of the command.
	 * @param sessionId The id that identifies the current session of response/request messages that are necessary for completion of the (multi-stage) remote command.
	 * @param parametersIn Parameters that are needed for the execution of the current stage of the remote command. The caller of the command needs to provide the parametersIn parameter. When calling this function for the first time of a session the required parameters are unknown. Therefore parametersIn may be null.  
	 * @param parametersOut Parameters that are needed for the execution of the next stage of the remote command. The remote node will provide the parametersOut. When calling this function the parametersOut should be an empty ParameterMap that will be filled by the remote node.   
	 * @return The session id of this remote command. If it is a multi-stage command this session id needs to be provided by the caller to identify the proceeding execution of this command. If calling this command for the first time in an session the sessionId may be null. 
	 * @throws EpicClientException Is thrown if the command was unknown, 
	 */
	public String executeRemoteCommand(String epicNode, String command, String sessionId, ParameterMap parametersIn, ParameterMap parametersOut) throws EpicClientException {
		RemoteCommand remoteCommand = null;
		if(sessionId!=null) {
			remoteCommand = mRemoteCommands.get(sessionId);
		}
		if (remoteCommand == null) {
			remoteCommand = mAdHocCommandManager.getRemoteCommand(epicNode, command);
		}

		if(remoteCommand == null) {
			throw new EpicClientException("Command unknown");
		} 

		Form form = remoteCommand.getForm();
		if(form!=null){
			//execute command with provided data
			form = form.createAnswerForm();
			if(parametersIn!=null){
				parametersIn.fillForm(form);
			}
			try {
				//we allow 10 seconds
				remoteCommand.setPacketReplyTimeout(10000);
				remoteCommand.execute(form);
			} catch (XMPPException e) {
				throw new EpicClientException(e);
			}
		} else {
			//execute form without data
			try {
				//we allow 10 seconds
				remoteCommand.setPacketReplyTimeout(10000);
				remoteCommand.execute();
			} catch (XMPPException e) {
				throw new EpicClientException(e);
			}
		}

		//fill the output parameter map with the answer of the executed field  
		if(parametersOut!=null) {
			parametersOut.initializeFromForm(remoteCommand.getForm());
		}

		//generate a unique id for this command
		int id = remoteCommand.hashCode();
		String sid = epicNode + "_" + command + "_" + String.valueOf(id);
		mRemoteCommands.put(sid, remoteCommand);

		return sid;
	}



	/**
	 * Announces a command that can be executed from a remote host.
	 * @param command Implementation of the command that will be executed locally.	
	 * @return True if the command could be registered at the server. False otherwise. 
	 * @throws EpicClientException Thrown if the command manager or the EPIC service is not available 
	 */
	public boolean announceRemoteCommand(EpicCommand command) throws EpicClientException {
		if(!isConnectedToEpicNetwork()){
			throw new EpicClientException("The EPIC network service is currently not available.");
		}
		if(mAdHocCommandManager==null){
			throw new EpicClientException("The command manager is not available.");
		} 

		mAdHocCommandManager.registerCommand(command.getId(), command.getHumanReadableName(), command.getClass());
		return true;
	}

	/**
	 * Retrieves a list of EPIC commands that are announced by a specific remote node.
	 * @param node The full address of the node that is queried
	 * @return A list of EPIC commands that can be executed by the remote node.
	 * @throws EpicClientException Thrown if the command manager or the EPIC service is not available
	 */
	public EpicCommandInfo[] getEpicCommands(String node) throws EpicClientException {
		if(!isConnectedToEpicNetwork()){
			throw new EpicClientException("The EPIC network service is currently not available.");
		}
		if(mAdHocCommandManager==null){
			throw new EpicClientException("The command manager is not available.");
		}
		

		ArrayList<EpicCommandInfo> commands = new ArrayList<EpicCommandInfo>();
		try {
			DiscoverItems items = mAdHocCommandManager.discoverCommands(node);
			Iterator<Item> itemIterator = items.getItems();
			while(itemIterator.hasNext()){
				Item item = itemIterator.next();
				commands.add(new EpicCommandInfo(item.getEntityID(), item.getNode(), item.getName()));
			}
		} catch (XMPPException e) {
			throw new EpicClientException(e);
		}
		EpicCommandInfo[] commandsArray = new EpicCommandInfo[commands.size()];
		return commands.toArray(commandsArray);
	}


	/**
	 * Sets the extension providers for the smack library.
	 * This function is a bugfix described here:
	 * http://www.igniterealtime.org/community/message/201866
	 *  
	 * And also loads the EpicExtensionProvider
	 * @param pm The ProviderManager 
	 */
	private static void configure(ProviderManager pm) {

		// add the epic extension provider
		pm.addExtensionProvider("application", EpicExtensionProvider.NAMESPACE, new EpicExtensionProvider());
		//  Private Data Storage
		pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());


		//  Time
		/*
		 * not working in asmack

		try {

			pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			System.out.println("Can't load class for org.jivesoftware.smackx.packet.Time");
		}
		 */

		//  Roster Exchange
		pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());

		//  Message Events
		pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());

		//  Chat State
		pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		//  XHTML
		pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

		//  Group Chat Invitations
		pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());

		//  Service Discovery # Items    
		pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

		//  Service Discovery # Info
		pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

		//  Data Forms
		pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());

		//  MUC User
		pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());

		//  MUC Admin    
		pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());


		//  MUC Owner    
		pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

		/*
		 * not working for asmack 
		 * 

		//  Delayed Delivery
		pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());
		 */

		/*
		 * not working for asmack 
		 * 

		//  Version
		try {
			pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			//  Not sure what's happening here.
		}
		 */

		//  VCard
		pm.addIQProvider("vCard","vcard-temp", new VCardProvider());

		//  Offline Message Requests
		pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

		//  Offline Message Indicator
		pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

		//  Last Activity
		pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());

		//  User Search
		pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());

		//  SharedGroupsInfo
		pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

		//  JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

		//   FileTransfer
		pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());

		pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

		pm.addIQProvider("open","http://jabber.org/protocol/ibb", new IBBProviders.Open());

		pm.addIQProvider("close","http://jabber.org/protocol/ibb", new IBBProviders.Close());

		pm.addExtensionProvider("data","http://jabber.org/protocol/ibb", new IBBProviders.Data());

		//  Privacy
		pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());

		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
	}



	static {
	
		ParameterManager paramManager = ParameterManager.getInstance();
		configure(paramManager);

	}

}
