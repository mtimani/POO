package ChatSystem;

import java.io.*;
import java.net.*;

/**
 * Classe qui s'occupe de la communication UDP
 */
public class Udp extends Thread {

	private Controller controller ;
	private DatagramSocket socket;
	private int port;
	
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
	
	/**
	 * Création d'un Thread UDP
	 * @param controller Controlleur associé au Thread UDP
	 * @param port Port que le Thread UDP utilise
	 * @throws SocketException
	 */
	public Udp(Controller controller, int port) throws SocketException {
		super("UDP");
		this.controller = controller;
		this.port = port;
		this.socket = new DatagramSocket(port);
		this.socket.setBroadcast(true);
	}
	
	/**
	 * Création du message UDP
	 * @param status Information dur le statut de Connection
	 * @param user L'utilisateur qui envoie le message
	 * @return Le message UDP sous forme binaire
	 * @throws IOException
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
	 * @throws IOException
	 */
	public void sendUdpMessage(byte[] message, InetAddress ipAddress) throws IOException {
		DatagramPacket out = new DatagramPacket(message, message.length, ipAddress, port);
		socket.send(out);
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
				socket.receive(in);
			
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
						if (!controller.getUser().getIp().equals(in.getAddress())) {
							controller.receivedConnection(receivedUser);
							sendUdpMessage(createMessage(CONNECTION_RESPONSE_STATUS, controller.getUser()), in.getAddress());
						}
						break;
					case CONNECTION_RESPONSE_STATUS:
						controller.receivedConnection(receivedUser);
						break;
					case USERNAME_CHANGED_STATUS:
						if (!controller.getUser().getIp().equals(in.getAddress()))
							controller.receivedUsernameChanged(receivedUser);		
				}
				
			} catch (StreamCorruptedException | EOFException e) {
				// Message pas pour nous, ne rien faire
			} catch (IOException | ClassNotFoundException e1) {
				GUI.showError("Erreur lors de la lecture d'un message UDP.");
			}
		}
	}
}
