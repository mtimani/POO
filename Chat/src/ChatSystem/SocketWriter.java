package ChatSystem;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Classe permettant l'écriture des messages
 */
public class SocketWriter extends Thread {

	private Socket socket;
	private Controller controller;
	private Group group;
	
	/**
	 * Crée le SocketWriter
	 * @param name Nom du thread
	 * @param socket Socket associé
	 * @param controller Controlleur associé
	 * @param group Le groupe associe a ce SocketWriter
	 */
	public SocketWriter(String name, Socket socket, Controller controller, Group group) {
		super(name);
		this.socket = socket;
		this.controller = controller;
		this.group = group;
	}
	
	/**
	 * Définition du Thread qui permet d'envoier les messages vers les utilisateurs souhaités
	 */
	@Override
	public void run() {
		
		try {
		
			PrintWriter outputData = new PrintWriter(socket.getOutputStream(), true);
			
			Message messageToSend;

			while(!socket.isClosed()) {
				
				// On récupère le message à envoyer
				messageToSend = controller.getMessageToSend();
				
				if(messageToSend != null) {

					// On vérifie si le message est un message de fin de conversation (déconnexion)
					if(messageToSend.getFunction() == Message.FUNCTION_STOP) break;
					
					// On envoie le message s'il est bien destiné à ce groupe
					if(this.group.equals(messageToSend.getReceiverGroup())) {	
						
						// Envoi d'un fichier ou d'une image
						if(messageToSend.getFunction() == Message.FUNCTION_FILE || messageToSend.getFunction() == Message.FUNCTION_IMAGE) {
						    
							// Envoi de deux messages : le message prévu + un message contenant le fichier
							outputData.println(encodeMessageToString(messageToSend));
							
							File file = new File(messageToSend.getContent());
							outputData.println(encodeFileToString(file));
							
							controller.messageSent();
						}
						
						// Envoi d'un message texte
						else {
							outputData.println(encodeMessageToString(messageToSend));
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
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					GUI.showError("Erreur lors de la déconnexion du writer des messages envoyés.");
				}
			}
		}
	}
	
	/**
	 * Permet d'encoder un message sous forme de chaine de caractères
	 * @param message Message à encoder
	 * @return Message encodé sous forme de chaine de caractères
	 * @throws IOException
	 */
	private static String encodeMessageToString(Message message) throws IOException {
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutput objOutput;
		
		objOutput = new ObjectOutputStream(byteStream);
		objOutput.writeObject(message);
		objOutput.close();
		
		return Base64.getEncoder().encodeToString(byteStream.toByteArray());
	}
	
	/**
	 * Permet d'encoder un fichier sous forme de chaîne de caractères
	 * @param file Le fichier à encoder
	 * @return Le fichier encode sous forme de chaine de caractères
	 * @throws IOException
	 */
	private static String encodeFileToString(File file) throws IOException {
		
		FileInputStream receivedFile = new FileInputStream(file);
		
		byte[] fileData = new byte[(int) file.length()];
		receivedFile.read(fileData);
		
		receivedFile.close();
		
		return Base64.getEncoder().encodeToString(fileData);
	}
	
}
