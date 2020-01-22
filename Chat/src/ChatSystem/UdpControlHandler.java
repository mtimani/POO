package ChatSystem;

import java.io.*;
import java.net.*;

/**
 * Classe qui s'occupe de la communication UDP
 */
public class UdpControlHandler extends Thread {

	private Controller controller ;
	private DatagramSocket udpSocket;
	private int portNum;
	
	/**
	 * Header de paquets UDP, qui permet de les identifier, 
	 * Afin de ne pas traiter les paquets d'autres applications
	 */
	private static final int UDP_IDENTITY = 64529;
	
	/**
	 * Statuts de connexion
	 */
	public static final int NONE_STATUS = -1;
	public static final int CONNECTION_STATUS = 0;
	public static final int DECONNECTION_STATUS = 1;
	public static final int CONNECTION_RESPONSE_STATUS = 2;
	public static final int USERNAME_CHANGED_STATUS = 3;
	public static final int USERNAME_OCCUPIED = 4;
	public static final int USERNAME_MODIFIED_OCCUPIED = 5;
	
	/**
	 * Création d'un Thread UDP
	 * @param controller Controlleur associé au Thread UDP
	 * @param portNum Port que le Thread UDP utilise
	 * @throws SocketException Exception de socket
	 */
	public UdpControlHandler(Controller controller, int portNum) throws SocketException {
		super("UdpControlHandler");
		this.controller = controller;
		this.portNum = portNum;
		this.udpSocket = new DatagramSocket(portNum);
		this.udpSocket.setBroadcast(true);
	}
	
	/**
	 * Création du message UDP
	 * @param status Information dur le statut de Connection
	 * @param user L'utilisateur qui envoie le message
	 * @return Le message UDP sous forme binaire
	 * @throws IOException Exception d'entrees-sorties
	 */
	public byte[] createMessage(int status, User user) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream);
		oo.writeInt(UDP_IDENTITY);
		oo.writeInt(status);
		oo.writeObject(user);
		oo.close();
		return bStream.toByteArray();
	}
	
	/**
	 * Envoi d'un message UDP
	 * @param message Message à envoyer
	 * @param ipAddress Adresse IP a laquelle envoyer le message
	 * @throws IOException Exception d'entrees-sorties
	 */
	public void sendUdpMessage(byte[] message, InetAddress ipAddress) throws IOException {
		DatagramPacket out = new DatagramPacket(message, message.length, ipAddress, portNum);
		udpSocket.send(out);
		//System.out.println("message UDP envoye : " + message + " a " + ipAddress.toString());
	}
	
	/**
	 * Action effectuée par le Thread UDP :
	 * Ecoute en UDP et traîte les messages en fonction du contenu
	 */
	@Override
	public void run() {
		
		//Variables
		byte [] buffer = new byte[1024];
		DatagramPacket in = new DatagramPacket(buffer, buffer.length);
		int status = NONE_STATUS;
		User receivedUser = null;
		int udpIdentity = -1;
		
		while(true) {
			try {
				udpSocket.receive(in);
			
				// Reception des donnees
				byte[] receivedMessage = in.getData();
				ObjectInputStream iStream;
				
				iStream = new ObjectInputStream(new ByteArrayInputStream(receivedMessage));
				udpIdentity = (int) iStream.readInt();
				
				// On verifie qu'on doit traiter le paquet
				if(udpIdentity != UDP_IDENTITY)
					continue;
				
				// Recuperation des informations du message
				status = (int) iStream.readInt();
				receivedUser = (User) iStream.readObject();
				iStream.close();
				
				// Traitement du message recu
				switch (status) {
					case DECONNECTION_STATUS:
						controller.receivedDisconnection(receivedUser);
						break;
					case CONNECTION_STATUS:
						if (!controller.getUser().getIpAddr().equals(in.getAddress())) {
							if (controller.getUser().getUsername().equals(receivedUser.getUsername())) {
								sendUdpMessage(createMessage(USERNAME_OCCUPIED, controller.getUser()), in.getAddress());
								System.out.println("Username Occupied Sent");
							}
							else {
								controller.receivedConnection(receivedUser);
								sendUdpMessage(createMessage(CONNECTION_RESPONSE_STATUS, controller.getUser()), in.getAddress());
							}
						}
						break;
					case CONNECTION_RESPONSE_STATUS:
						controller.receivedConnection(receivedUser);
						break;
					case USERNAME_CHANGED_STATUS:
						if (!controller.getUser().getIpAddr().equals(in.getAddress())) {
							if (controller.getUser().getUsername().equals(receivedUser.getUsername())) {
								sendUdpMessage(createMessage(USERNAME_MODIFIED_OCCUPIED, controller.getUser()), in.getAddress());
								System.out.println("Username Occupied Sent");
							}
							else {
								controller.receivedUsernameChanged(receivedUser);
							}
						}
						break;
					case USERNAME_OCCUPIED:
						controller.receivedUsernameOccupied(receivedUser);
						break;
					case USERNAME_MODIFIED_OCCUPIED:
						controller.receivedModifiedUsernameOccupied(receivedUser);
				}
				
			} catch (StreamCorruptedException | EOFException e) {
				// Message pas pour nous, ne rien faire
			} catch (IOException | ClassNotFoundException e1) {
				GUI.showError("Erreur lors de la lecture d'un message UDP.");
			}
		}
	}
}
