package ChatSystem;

import java.io.*;
import java.util.*;
import ChatSystem.DataManager.PasswordError;
import java.net.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import com.google.gson.Gson;
import ChatSystemServer.ChatServer;
import ChatSystemServer.ChatServer.ServerResponse;

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
	private GUI gui;
	
	// Informations sur le serveur (si besoin)
	private static boolean useServer;
	private static String serverIP;
	private static int serverPort;
	private static int timeoutConnection;
	private static int updateInterval;
	private static String pathWebpage = null;
		
	// Timer utilise pour les requetes
	private Timer timer;
	
	/**
	 * Constantes
	 */
	// Utilise sur les machines Linux
	private static final String PATH_WEBPAGE_LOWERCASE = "/chatsystem/ChatServer";
	// Utilise sur les machines Windows
	private static final String PATH_WEBPAGE_UPPERCASE = "/ChatSystem/ChatServer";
	
	/**
	 * Constantes
	 */
	public static final int EXIT_WITHOUT_ERROR = 0;
	public static final int EXIT_GET_CONNECTED_USERS = 1;
	public static final int EXIT_ERROR_SEND_CONNECTION = 2;
	public static final int EXIT_ERROR_SEND_DECONNECTION = 3;
	public static final int EXIT_WITH_ERROR = 4;
	public static final int EXIT_ERROR_SERVER_UNAVAILABLE = 5;
	
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
	public Controller (InetAddress ipBroadcast, String serverIP, int serverPort) throws SocketException, ClassNotFoundException, FileNotFoundException, IOException  {
		this.connectedUsers = new ArrayList<User>();
		this.groups = new ArrayList<Group>();
		this.messages = new ArrayList<Message>();
		this.messages = DataManager.readAllMessages();
		this.groups = DataManager.readAllGroups();
		
		// On utilise le serveur
		if(serverPort > 0) {
			useServer = true;
			Controller.serverIP = serverIP;
			Controller.serverPort = serverPort;	
			timeoutConnection = Integer.parseInt(DataManager.getSetting("server", "timeout", "5000"));
			updateInterval = Integer.parseInt(DataManager.getSetting("server", "update_interval", "1000"));
		}
				
		// On utilise le service UDP
		else {
			useServer = false;
			Controller.serverIP = null;
			Controller.serverPort = -1;	
			int udpPort = Integer.parseInt(DataManager.getSetting("udp", "port", "5000"));
			this.ipBroadcast = ipBroadcast;
			udp = new Udp(this, udpPort);
		}
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
	
	public GUI getGUI() {
		return this.gui;
	}
	
	/**
	 * Associe un GUI au controlleur
	 * @param gui GUI à associer
	 */
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
		
		// Si on utilise le serveur
		if(useServer) {
			// Lancement du timer
			// Ce timer sert a recuperer les utilisateurs connectes et a indiquer sa presence de facon reguliere
			timer = new Timer();
			timer.scheduleAtFixedRate(new RequestTimer(this), 0, updateInterval);
		}
				
		// Si on utilise UDP
		else {
			// Demarrage du service UDP et envoi du message de presence
			udp.start();
			udp.sendUdpMessage(udp.createMessage(Udp.CONNECTION_STATUS, this.getUser()), this.ipBroadcast);
		}
		
		//Ajout des groupes sauvegardés auparavant au GUI
		for(Group g : groups) gui.addGroup(g);
	}
	
	/**
	 * Déconnexion de l'Utilisateur de l'application et envoi d'un message de signalisation de son départ
	 * @throws IOException
	 * @throws ConnectionError 
	 * @throws SendDeconnectionError 
	 */
	public void disconnection() throws IOException, ConnectionError, SendDeconnectionError {
		//Tous les groupes du User passent en mode hors-ligne
		for (Group g : groups) {
			g.setOnline(false);
		}
		
		//Enregistrement de toutes les données
		DataManager.writeAllMessages(messages);
		DataManager.writeAllGroups(groups);
		
		// Si on utilise le serveur
		if(useServer) {
			Gson gson = new Gson();
			
			// Connexion au serveur et envoie des donnees au format JSON
			String paramValue = "userdata=" + gson.toJson(user);
			
			// Test de la connexion
			if(!testConnectionServer())
				throw new ConnectionError();
			
			// Connexion au serveur et traitement de la reponse
			HttpURLConnection con = sendRequestToServer(ChatSystemServer.ChatServer.ACTION_USER_DECONNECTION, paramValue);		
					
			int status = con.getResponseCode();
			if(status != HttpURLConnection.HTTP_OK)
				throw new SendDeconnectionError();
					
			// On recupere les donnees
			String jsonResponse = getResponseContent(con);
			ServerResponse serverResponse = gson.fromJson(jsonResponse, ServerResponse.class);
		
			if(serverResponse.getCode() != ChatServer.NO_ERROR)
				throw new SendDeconnectionError();
		}
				
		// Si on utilise le service UDP
		else {
			udp.sendUdpMessage(udp.createMessage(Udp.DECONNECTION_STATUS, this.getUser()), this.ipBroadcast);
		}
				
	}
	
	/**
	 * Récéption et traîtement du packet qui signale la connexion d'un nouvel utilisateur
	 * @param receivedUser User qui signale sa connexion 
	 * @throws IOException
	 */
	public void receivedConnection(User receivedUser) throws IOException {
		if(receivedUser == null)
			return;

		//System.out.println("connexion recue! iduser=" +receivedUser.getID());

		boolean listHasChanged = false;
		boolean userHasChanged = false;
		
		// On verifie qu'on ne recoit pas sa propre annonce et qu'on ne connait pas deja l'utilisateur
		if(!connectedUsers.contains(receivedUser) && !receivedUser.equals(user)) {
			userHasChanged = true;
			listHasChanged = true;
			connectedUsers.add(receivedUser);
		}
		
		// Mise a jour des groupes avec les nouvelles informations de l'utilisateur connecte
		String oldUsername = "", newUsername = "";
		
		for(Group group : groups) {
			oldUsername = group.getGroupNameForUser(user);
			userHasChanged = userHasChanged || group.updateMember(receivedUser);
			newUsername = group.getGroupNameForUser(user);
		}
		
		if(userHasChanged) {
			listHasChanged = true;
			
			// Mise a jour des messages avec les nouvelles informations de l'utilisateur
			for(Message m : messages)
				m.updateSender(receivedUser);
		}
		
		// Ajout du nouvel utilisateur (GUI)
		if(gui != null)
			gui.updateConnectedUsers();
		
		// Mise a jour des usernames
		if(listHasChanged)
			gui.replaceUsernameInList(oldUsername, newUsername);
		
	}
	
	/**
	 * Récéption et traîtement du packet qui signale la déconnexion d'un utilisateur
	 * @param receivedUser User qui signale sa déconnexion 
	 */
	public void receivedDisconnection(User receivedUser) {
		if (receivedUser == null) return;
		
		//Suppréssion de l'utilisateur qui se déconnecte de la liste des utilisateurs en ligne
		User userDisconnecting = null;
		
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
	
	/**
	 * Traîtement du cas où le User choisi est déjà occupé
	 * @param receivedUser User qui nous l'a indiqué
	 * @throws SocketException 
	 */
	public void receivedUsernameOccupied(User receivedUser) throws SocketException {
		gui.setVisible(false);
		GUI.showError("Username occupied. Please choose another Username and reopen the program");
		System.exit(Controller.EXIT_WITHOUT_ERROR);
	}
	
	/**
	 * Traîtement du cas où le User modifié est déjà occupé
	 * @param receivedUser User qui nous l'a indiqué
	 * @throws SocketException
	 */
	public void receivedModifiedUsernameOccupied(User receivedUser) throws SocketException {
		GUI.showError("Username occupied. Please choose another one if you wish to modify it.");
		
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
		
		if(!useServer)
			//Envoi du message signalant aux autres Users que le Username a été changé
			udp.sendUdpMessage(udp.createMessage(Udp.USERNAME_CHANGED_STATUS, this.user), this.ipBroadcast);
	}
	
	/**
	 * Modifie le mot de passe de l'utilisateur
	 * @param oldPassword L'ancien mot de passe
	 * @param newPassword Le nouveau mot de passe
	 * @throws ClassNotFoundException Si erreur a l'ecriture du fichier
	 * @throws NoSuchAlgorithmException Si erreur a l'ecriture du fichier
	 * @throws IOException Si erreur a l'ecriture du fichier
	 * @throws PasswordError Si erreur a l'ecriture du fichier
	 */
	public void changePassword(char[] oldPassword, char[] newPassword) throws ClassNotFoundException, NoSuchAlgorithmException, IOException, PasswordError {
		DataManager.changePassword(oldPassword, newPassword);		
	}
	
	/**
	 * Indique le changement d'un Username d'un User
	 * @param receivedUser User qui a changé de Username
	 */
	public void receivedUsernameChanged(User receivedUser) {
		String oldUsername = "";
		
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
	
	/***************************** Methodes liees a l'utilisation du serveur *****************************/

	/**
	 * Envoie une requete au serveur
	 * @param action L'action demandee au serveur
	 * @param paramValue La valeur du parametre passe
	 * @return La connexion au serveur (contenant le status, la reponse, etc.)
	 * @throws IOException Si le serveur est inaccessible
	 */
	public static HttpURLConnection sendRequestToServer(int action, String paramValue) throws IOException {
		
		// Creation de l'URL
		URL url = new URL("http://" + serverIP + ":" + serverPort + pathWebpage +"?action=" + action + "&" + paramValue);
		
		// Envoi de la requete
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setConnectTimeout(timeoutConnection);
		//con.setReadTimeout(500);
			
		return con;
	}
	
	/**
	 * Teste si le serveur est accessible
	 * @param ip L'IP du serveur
	 * @param port Le port sur lequel se connecter
	 * @return True si le serveur est accessible, False sinon
	 */
	public static boolean testConnectionServer() {
		
		// On a deja teste la connexion et on connait l'URL correcte
		if(pathWebpage != null) {
			
			try {
				
				// Creation de l'URL
				URL url = new URL("http://" + serverIP + ":" + serverPort + pathWebpage);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("HEAD");
				con.setConnectTimeout(timeoutConnection);
				
				// Renvoie True si tout se passe bien
				return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
			}
			
			// Permet de detecter un timeout
			catch (IOException e) {
				return false;
			}
			
		}

		// On teste les URL avec et sans majuscules (configuration differente selon Windows ou Linux)
		else {
			boolean connectionOK = false;
			
			// Test pour Linux (sans majuscule)
			try {
				// Creation de l'URL
				URL url = new URL("http://" + serverIP + ":" + serverPort + PATH_WEBPAGE_LOWERCASE);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("HEAD");
				con.setConnectTimeout(timeoutConnection);
				
				if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					connectionOK = true;
					pathWebpage = PATH_WEBPAGE_LOWERCASE;
				}
			}
			// Permet de detecter un timeout
			catch (IOException e) {}

			// Test pour Windows (avec majuscules) si le premier test a echoue
			if(!connectionOK) {
				try {
					// Creation de l'URL
					URL url = new URL("http://" + serverIP + ":" + serverPort + PATH_WEBPAGE_UPPERCASE);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("HEAD");
					con.setConnectTimeout(timeoutConnection);
					
					if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
						connectionOK = true;
						pathWebpage = PATH_WEBPAGE_UPPERCASE;
					}

				}
				// Permet de detecter un timeout
				catch (IOException e) {}
			}
			
			return connectionOK;
		}

	}
	
	/**
	 * Retourne le contenu texte d'une reponse du serveur
	 * @param con La connexion au serveur
	 * @return Le contenu texte de la reponse
	 * @throws IOException Si une erreur dans la connexion survient
	 */
	public static String getResponseContent(HttpURLConnection con) throws IOException {
		
		int responseCode = con.getResponseCode();
		InputStream inputStream;
		
		if(200 <= responseCode && responseCode <= 299)
			inputStream = con.getInputStream();
		else
			inputStream = con.getErrorStream();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		
		StringBuilder content = new StringBuilder();
		String currentLine;
		
		while((currentLine = in.readLine()) != null)
			content.append(currentLine);
		
		in.close();
		
		return content.toString();
	}
	
	/**
	 * Met a jour la liste des utilisateurs connectes (et leurs informations)
	 * Les donnees sont envoyees par le serveur
	 * @param receivedUsers La liste des utilisateurs connectes sur le serveur
	 */
	public void receiveConnectedUsersFromServer(ArrayList<User> receivedUsers) {
	
		boolean hasNewUser = false;
		boolean listHasChanged = false;
		
		String oldUsername = "", newUsername = "";
		
		// Utilise pour supprimer les utilisateurs deconnectes
		ArrayList<User> disconnectedUsers = new ArrayList<User>(connectedUsers);
		
		// On traite chaque utilisateur recu
		for(User u : receivedUsers) {
			
			disconnectedUsers.remove(u);
			
			boolean userHasChanged = false;
			
			// On verifie qu'on ne recoit pas sa propre annonce et qu'on ne connait pas deja l'utilisateur
			if(!connectedUsers.contains(u) && !u.equals(user)) {
				userHasChanged = true;
				hasNewUser = true;
				listHasChanged = true;
				connectedUsers.add(u);
			}
			
			// Mise a jour des groupes avec les nouvelles informations de l'utilisateur connecte
			for(Group group : groups) {
				oldUsername = group.getGroupNameForUser(user);
				userHasChanged = userHasChanged || group.updateMember(u);
				newUsername = group.getGroupNameForUser(user);
			}
			
			if(userHasChanged) {
				listHasChanged = true;
				
				// Mise a jour des messages avec les nouvelles informations de l'utilisateur
				for(Message m : messages)
					m.updateSender(u);
			}
		}
		
		// Gestion des utilisateurs deconnectes
		if(!disconnectedUsers.isEmpty()) {
			hasNewUser = true;
			
			for(User u : disconnectedUsers)
				connectedUsers.remove(u);
		}
		
		if(hasNewUser) {
			// Ajout du nouvel utilisateur (GUI)
			if(gui != null)
				gui.updateConnectedUsers();
		}
		
		// Mise a jour des usernames
		if(listHasChanged)
			gui.replaceUsernameInList(oldUsername, newUsername);
		
	}
}


