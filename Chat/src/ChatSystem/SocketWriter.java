package ChatSystem;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Classe permettant l'écriture des messages
 */
public class SocketWriter extends Thread {

	private Socket tcpSocket;
	private Controller controller;
	private Group group;
	
	/**
	 * Crée un SocketWriter
	 * @param name Nom du thread
	 * @param tcpSocket Socket associé a ce SocketWriter
	 * @param controller Controlleur de l'application
	 * @param group Le groupe associe a ce SocketWriter
	 */
	public SocketWriter(String name, Socket tcpSocket, Controller controller, Group group) {
		super(name);
		this.tcpSocket = tcpSocket;
		this.controller = controller;
		this.group = group;
	}
	
	/**
	 * Permet d'encoder un message sous forme de chaine de caractères
	 * @param msg Message à encoder
	 * @return Message encodé sous forme de chaine de caractères
	 * @throws IOException Exception d'entrees-sorties
	 */
	private static String encodeAMessageToString(Message msg) throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutput objectOutput;
		objectOutput = new ObjectOutputStream(byteOutputStream);
		objectOutput.writeObject(msg);
		objectOutput.close();
		return Base64.getEncoder().encodeToString(byteOutputStream.toByteArray());
	}
	
	/**
	 * Permet d'encoder un fichier sous forme de chaîne de caractères
	 * @param file Le fichier à encoder
	 * @return Le fichier encode sous forme de chaine de caractères
	 * @throws IOException Exception d'entrees-sorties
	 */
	private static String encodeAFileToString(File file) throws IOException {
		FileInputStream receivedFileStream = new FileInputStream(file);
		byte[] inputFile = new byte[(int) file.length()];
		receivedFileStream.read(inputFile);
		receivedFileStream.close();
		return Base64.getEncoder().encodeToString(inputFile);
	}
	
	/**
	 * Définition du fonctionnement du Thread qui permet d'envoier les messages vers les utilisateurs souhaités
	 */
	@Override
	public void run() {
		try {
			PrintWriter outputDataWriter = new PrintWriter(tcpSocket.getOutputStream(), true);
			Message msg;
			while(!tcpSocket.isClosed()) {
				// On récupère le message à envoyer
				msg = controller.getMessageToSend();
				if(msg != null) {
					// On vérifie si le message est un message de fin de conversation (déconnexion)
					if(msg.getFunction() == Message.STOP_FUNCTION) break;
					// On envoie le message s'il est bien destiné à ce groupe
					if(this.group.equals(msg.getDestinationGroup())) {	
						// Envoi d'un fichier ou d'une image
						if(msg.getFunction() == Message.FILE_FUNCTION || msg.getFunction() == Message.IMAGE_FUNCTION) {
							// Envoi de deux messages : le message prévu + un message contenant le fichier
							outputDataWriter.println(encodeAMessageToString(msg));
							File outputFile = new File(msg.getMsg());
							outputDataWriter.println(encodeAFileToString(outputFile));
							controller.messageSent();
						}
						// Envoi d'un message texte
						else {
							outputDataWriter.println(encodeAMessageToString(msg));
							controller.messageSent();
						}
					}
				}
			}
		}
		catch(Exception e) {
			GUI.showError("Erreur dans l'ecriture du message.");
		}
		finally {
			if (tcpSocket != null) {
				try {
					tcpSocket.close();
				} catch (Exception e) {
					GUI.showError("Erreur lors de la déconnexion du writer des messages envoyés.");
				}
			}
		}
	}
	
}
