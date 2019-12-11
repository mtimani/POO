package ChatSystem;

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.*;
import java.nio.file.Files;

/**
 * Controlleur de l'application de Chat
 */
public class Controller {

	private User user;
	private volatile ArrayList<User> connectedUsers;
	private ArrayList<Message> messages;
	private ArrayList<Group> groups;
	private Udp udp;
	private InetAddress ipBroadcast;
	private volatile Message messageToSend = null;
	private Timer timer;
	private GUI gui;
	
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
	
	public void setGUI(GUI gui) {
		this.gui = gui;
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
	
	/**
	 * Connexion de l'Utilisateur à l'application et envoi d'un message de signalisation de sa présence
	 * @param id ID de l'Utilisateur
	 * @param username Username de l'Utilisateur
	 * @param ip L'adresse IP de l'Utilisateur
	 * @throws IOException
	 */
	public void connection(int id, String username, InetAddress ip) throws IOException {
		//Création du User associé au controlleur
		user = new User(id,username,ip);
		
		//Choix du port random pour le nouveau User, compris entre 20000 et 65500
		Random rand = new Random();
		int randomUserPort = rand.nextInt(65500 - 20000 + 1) + 20000;
		user.setPort(randomUserPort);
		
		// Démarage du serveur d'écoute (Cas dans lequel un autre User veuille communiquer avec notre User)
		int serverPort = user.getPort();
		ServerSocket serverSocket = new ServerSocket(serverPort);
		ServerSocketWaiter serverSocketWaiter = new ServerSocketWaiter(serverSocket,this);
		serverSocketWaiter.start();
		
		//Démarage du service UDP et l'envoi du message de connexion
		udp.start();
		udp.sendUdpMessage(udp.createMessage(Udp.CONNECTION_STATUS, this.getUser()), this.ipBroadcast);
		
		//Ajout des groupes sauvegardés auparavant au GUI
		for(Group g : groups) gui.addGroup(g);
	}
	
	/**
	 * Déconnexion de l'Utilisateur de l'application et envoi d'un message de signalisation de son départ
	 * @throws IOException
	 */
	public void disconnection() throws IOException {
		//Tous les groupes du User passent en mode hors-ligne
		for (Group g : groups) {
			g.setOnline(false);
		}
		
		//Enregistrement de toutes les données
		DataManager.writeAllMessages(messages);
		DataManager.writeAllGroups(groups);
		
		//Envoi du message de déconnexion
		udp.sendUdpMessage(udp.createMessage(Udp.DECONNEXION_STATUS, this.getUser()), this.ipBroadcast);
	}
	
	/**
	 * Récéption et traîtement du packet qui signale la connexion d'un nouvel utilisateur
	 * @param receivedUser User qui signale sa connexion 
	 */
	public void receivedConnection(User receivedUser) {
		if (receivedUser == null) return;
		
		boolean listHasChanged = false;
		boolean userHasChanged = false;
		
		//Vérification que le user reçu n'est pas encore connu et que l'on ne reçoit pas son propre message
		if (!connectedUsers.contains(receivedUser) && !receivedUser.equals(this.user)) {
			listHasChanged = true;
			userHasChanged = true;
			connectedUsers.add(receivedUser);
		}
		
		//Mise a jour des groupes avec les nouvelles informations sur le user mis-à-jour
		String oldUsername, newUsername;
		for (Group g : groups) {
			oldUsername = g.getGroupNameForUser(this.user);
			userHasChanged = userHasChanged || g.updateMember(receivedUser);
			newUsername = g.getGroupNameForUser(this.user);
			
			//Mise à jour des information sur l'envoyeur du message (si c'est lui qui a changé) 
			if (userHasChanged) {
				listHasChanged = true;
				for (Message m : messages) {
					m.updateSender(receivedUser);
				}
			}
			
			//Ajout de l'utilisateur au GUI
			if (gui != null) {
				gui.updateConnectedUsers();
			}
			
			//Mise à jour du User dans le GUI
			if (listHasChanged) gui.replaceUsernameInList(oldUsername, newUsername);
		}
		
	}
	
	/**
	 * Récéption et traîtement du packet qui signale la déconnexion d'un utilisateur
	 * @param receivedUser User qui signale sa déconnexion 
	 */
	public void receivedDisconnection(User receivedUser) {
		if (receivedUser == null) return;
		
		//Suppréssion de l'utilisateur qui se déconnecte de la liste des utilisateurs en ligne
		User userDisconnecting;
		
		for (User u : this.connectedUsers) {
			if (u.equals(receivedUser)) {
				userDisconnecting = u;
			}
		}
		
		if (userDisconnecting != null) this.connectedUsers.remove(userDisconnecting);
		
		//Mise à jour des groupes, ce dernier passe en mode offline
		for (Group g : groups) {
			if (g.isMember(receivedUser)) {
				g.setOnline(false);
				g.setOrigin(this.user);
			}
		}
		
		//Mise à jour des Users dans le GUI
		if (gui != null) gui.updateConnectedUsers();
	}
	
	/***************************************************************************** User Management Methods ********************************************************************************/
	
	/**
	 * Modifie le Username de l'Utilisateur
	 * @param newUsername Le nouveau Username
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void changeUsername(String newUsername) throws IOException, ClassNotFoundException {
		//Sauvegarde du nouveau Username
		DataManager.changeUsername(newUsername);
		this.user.setUsername(newUsername);
		
		//Envoi du message signalant aux autres Users que le Username a été changé
		udp.sendUdpMessage(udp.createMessage(Udp.USERNAME_CHANGED_STATUS, this.user), this.ipBroadcast);
	}
	
	/**
	 * Indique le changement d'un Username d'un User
	 * @param receivedUser User qui a changé de Username
	 */
	public void receivedUsernameChanged(User receivedUser) {
		String oldUsername;
		
		//Mise à jour du User dans la liste des Users connectés
		for (User u : this.connectedUsers) {
			if (u.equals(receivedUser)) {
				oldUsername = u.getUsername();
				this.connectedUsers.remove(u);
				this.connectedUsers.add(receivedUser);
				break;
			}
		}
		
		//Mise à jour des groupes avec les nouvelles informations sur le User
		for (Group g : groups) {
			g.updateMember(receivedUser);
		}
		
		//Mise à jour des messages avec les nouvelles informations sur le User
		for (Message m : messages) {
			m.updateSender(receivedUser);
		}
		
		//Mise à jour du User dans le GUI
		if (gui != null) gui.replaceUsernameInList(oldUsername, receivedUser.getUsername());
	}
	
	/***************************************************************** Conversation Management Methods / Sending Messages Methods *********************************************************/

	/**
	 * Démarage d'une nouvelle conversation
	 * @param members Membres de la communication
	 * @return Groupe crée
	 * @throws IOException
	 */
	private Group startGroup(ArrayList<User> groupMembers) throws IOException {
		User contact = groupMembers.get(0);

		// Random pour l'ID du nouveau groupe
		Random rand = new Random();
		int idGroup = rand.nextInt(999999999);
		
		// Demarrage d'un groupe
		Group group = new Group(idGroup, groupMembers, user);
		groups.add(group);
		
		
		// Creation d'un socket client : l'utilisateur se connecte a l'utilisateur distant
		Socket socket = new Socket(contact.getIp(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter("clientSocketWriter",socket, this, group);
		SocketReader socketReader = new SocketReader("clientSocketReader", socket, this);
		socketWriter.start();
		socketReader.start();
		
		// Mise à jour de la liste des groupes au sein GUI
		gui.addGroup(group);
		gui.selectGroupInList(group);
		
		return group;
	}
	/**
	 * Fonction permettant de relancer une conversation déjà existante
	 * @param group Groupe à restart
	 * @throws IOException
	 */
	private void restartGroup(Group group) throws IOException {
		// On passe le groupe est mode actif
		group.setOrigin(user);
		group.setOnline(true);
					
		ArrayList<User> groupMembers = group.getMembers();
		User contact;
				
		if(groupMembers.get(0).equals(user))
			contact = groupMembers.get(1);
		else
			contact = groupMembers.get(0);
				
		// On recree un socket client : l'utilisateur se reconnecte a l'utilisateur distant
		Socket socket = new Socket(contact.getIp(), contact.getPort());
				
		SocketWriter socketWriter = new SocketWriter("restartclientSocketWriter",socket, this, group);
		SocketReader socketReader = new SocketReader("restartclientSocketReader", socket, this);
		socketWriter.start();
		socketReader.start();
	}
	
	/**
	 * Fonction d'envoi de messages
	 * @param textToSend Charge Utile du message a envoyer
	 * @param receiverGroupNameForThisUser Le groupe destinataire du message
	 * @param function Fonction du message
	 * @throws IOException
	 */
	public void sendMessage(String textToSend, String receiverGroupNameForThisUser, int function) throws IOException {
		Group group = getGroupByName(receiverGroupNameForThisUser);
		
		if (group != null) {
			//Liste de Users membres du groupe
			ArrayList<User> groupMembers = group.getMembers();
			
			User destinataire;
			if (groupMembers.get(0).equals(this.user)) {
				destinataire = groupMembers.get(1); 
			} 
			else {
				destinataire = groupMembers.get(0);
			}
			
			if (!group.isOnline() && this.connectedUsers.contains(destinataire)) {
				restartGroup(group);
			}
		}
		else {
			// Creation d'un nouveau groupe si ce dernier n'existe pas
			ArrayList<User> groupMembers = new ArrayList<User>();
			groupMembers.add(findUserByName(receiverGroupNameForThisUser));
			groupMembers.add(this.user);
			group = startGroup(groupMembers);
		}
		
		// Transformation d'un message "FILE" en message "IMAGE" si besoin
		// Dans le cas d'un fichier, le contenu est son chemin vers le fichier
		if(function == Message.FUNCTION_FILE) {
			File file = new File(textToSend);
			String mimeType = Files.probeContentType(file.toPath());
						
			if(mimeType != null) {
				switch(mimeType) {
					case "image/jpeg":
					case "image/png":
					case "image/gif":
						function = Message.FUNCTION_IMAGE;
				}
			}
		}
					
		// Envoi du message
		Message message = new Message(new Date(), textToSend, this.user, group, function);
		messageToSend = message;
					
		// Enregsitrement du message
		messages.add(message);
	}
	
	/**
	 * Remontée d'un message reçu vers le controlleur 
	 * @param message Message reçu
	 */
	public void receiveMessage(Message message) {
		//Création et ajout du groupe si ce dernier n'existe pas
		Group group = message.getReceiverGroup();
				
		if(!groupKnownByController(group)) {
			this.groups.add(group);
			gui.addGroup(group);
		}
		else {
			// Si le groupe est connu, on le set online
			Group groupToUpdate = getGroupByID(group.getId());
			groupToUpdate.setOnline(true);
		}
				
		// Enregistrement du message reçu
		messages.add(message);
				
		gui.setGroupNoRead(group);
	}
	
}
