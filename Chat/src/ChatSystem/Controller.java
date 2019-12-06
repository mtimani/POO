package ChatSystem;

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.*;

/**
 * Controlleur de l'application de Chat
 */
public class Controller {

	//Ajouter GUI
	private User user;
	private volatile ArrayList<User> connectedUsers;
	private ArrayList<Message> messages;
	private ArrayList<Group> groups;
	private Udp udp;
	private InetAddress ipBroadcast;
	private volatile Message messageToSend = null;
	private Timer timer;
	
	/**
	 * Constantes
	 */
	public static final int EXIT_WITHOUT_ERROR = 0;
	public static final int EXIT_GET_CONNECTED_USERS = 1;
	public static final int EXIT_ERROR_SEND_CONNECTION = 2;
	public static final int EXIT_ERROR_SEND_DECONNECTION = 3;
	
	/**
	 * Erreurs 
	 */
	@SuppressWarnings("serial")
	public static class ConnectionError extends Exception {};
	@SuppressWarnings("serial")
	public static class SendConnectionError extends Exception {};
	@SuppressWarnings("serial")
	public static class SendDeconnectionError extends Exception {};
	
	/**
	 * Constructeur du controlleur de l'application
	 * @param ipBroadcast L'adresse IP de la machine hôte
	 * @throws SocketException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Controller (InetAddress ipBroadcast) throws SocketException, ClassNotFoundException, FileNotFoundException, IOException  {
		this.connectedUsers = new ArrayList<User>();
		this.groups = new ArrayList<Group>();
		this.messages = new ArrayList<Message>();
		this.messages = DataManager.readAllMessages();
		this.groups = DataManager.readAllGroups();
		
		int udpPort = Integer.parseInt(DataManager.getSetting("udp", "port", "25000"));
		this.ipBroadcast = ipBroadcast;
		udp = new Udp(this,udpPort);
	}
	
	/********************************************************************* Getters/Setters/Search and existance functions ******************************************************************/
	/**
	 * Retourne l'utilisateur associé au controleur
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}
	
	/**
	 * Retourne la liste des messages d'un groupe donné
	 * @param group Le groupe dont les messages sont recherchés
	 * @return Liste des messages du groupe souhaité
	 */
	public ArrayList<Message> getGroupMessages(Group group) {
		ArrayList<Message> groupMessages = new ArrayList<Message>();
		for (Message m : messages) {
			if (m.getReceiverGroup().equals(group)) groupMessages.add(m);
		}
		return groupMessages;
	}
	
	/**
	 * Retourne la liste d'utilisateurs connectés
	 * @return connectedUsers
	 */
	public ArrayList<User> getConnectedUsers() {
		return this.connectedUsers;
	}
	
	/**
	 * Retourne un utilisateur grâce à son Username
	 * @param username Username du User à retrouver
	 * @return User
	 */
	public User findUserByName(String username) {
		for (User u : this.connectedUsers) {
			if (u.getUsername().equals(username)) return u;
		}
		return null;
	}
	
	/**
	 * Retourne un groupe grâce à son GroupeName
	 * @param groupname Nom du Groupe à retrouver
	 * @return Group
	 */
	public Group getGroupByName(String groupname) {
		for (Group g : this.groups) {
			if (g.getGroupNameForUser(this.user).equals(groupname)) return g;
		}
		return null;
	}
	
	/**
	 * Retourne un groupe à partir d'un ID de groupe
	 * @param groupID ID du groupe recherché
	 * @return Group
	 */
	public Group getGroupByID(int groupID) {
		for (Group g : this.groups) {
			if (g.getId() == groupID) return g;
		}
		return null;
	}
	
	/**
	 * Indique si un groupe est déjà connu par le controlleur
	 * @param group Groupe à tester
	 * @return Vrai si le groupe est dans la liste, Faux dans le cas contraire
	 */
	public boolean groupKnownByController(Group group) {
		for (Group g : this.groups) {
			if (g.equals(group)) return true;
		}
		return false;
	}
	
	/**
	 * Retourne le message à envoyer 
	 * Null dans le cas où aucun message à envoyer
	 * @return Message à envoyer
	 */
	public Message getMessageToSend() {
		return this.messageToSend;
	}
	
	/**
	 * Permet au Threads d'écriture d'indiquer que le message a bien été envoyé
	 */
	//@see SocketWriter class
	public void messageSent() {
		messageToSend = null;
	}
	
	/***************************************************************************** Internet Addresses Methods *****************************************************************************/
	
	/**
	 * Recupere toutes les adresses IP de la machine et les adresses de broadcast associées
	 * @return Map avec toutes les addresses IP
	 * @throws SocketException
	 */
	public static Map<InetAddress, InetAddress> getAllIpAndBroadcast() throws SocketException {
		
		Map<InetAddress, InetAddress> listIP = new HashMap<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;
			for (InterfaceAddress a : networkInterface.getInterfaceAddresses()) {
				if (a.getAddress() instanceof Inet4Address) {
					listIP.put(a.getAddress(), a.getBroadcast());
				}
			}
		}
		return listIP;
	}
	
	/************************************************************************* Connection and Disconnection Methods ***********************************************************************/
	
	public void connection(int id, String username, InetAddress ip) throws IOException {
		
	}
	
	public void disconnection() throws IOException, ConnectionError, SendDeconnectionError {
		
	}
	
	public void receivedConnection(User receivedUser) {
		
	}
	
	public void receivedDisconnection(User receivedUser) {
		
	}
	
	/***************************************************************************** User Management Methods ********************************************************************************/
	
	public void changeUsername(String newUsername) throws IOException, ClassNotFoundException {
		
	}
	
	public void receivedUsernameChanged(User receivedUser) {
		
	}
	
	/***************************************************************** Sending Messages Methods / Conversation Management Methods *********************************************************/

	public void sendMessage(String textToSend, String receiverGroupNameForThisUser, int function) throws IOException {
		
	}
	
	public void receiveMessage(Message message) {
		
	}
	
	private Group startGroup(ArrayList<User> members) throws IOException {
		return null;
	}
	
	private void restartGroup(Group group) throws IOException {
		
	}
	
	
	
}
