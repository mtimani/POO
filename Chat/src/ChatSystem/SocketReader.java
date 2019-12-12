package ChatSystem;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;
import javax.swing.*;

/**
 * Classe permettant la lecture des messages 
 */
public class SocketReader extends Thread {
	
	private Socket socket;
	private Controller controller;
	private volatile Group group = null;

	/**
	 * Crée un SocketReader
	 * @param name Nom du thread
	 * @param socket Socket associé
	 * @param controller Controller associé
	 */
	public SocketReader(String name, Socket socket, Controller controller) {
		super(name);
		this.socket = socket;
		this.controller = controller;
	}
	
	/**
	 * Retourne le groupe qui utilise ce SocketReader
	 * @return Le groupe qui utilise ce SocketReader
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Définition du Thread qui permet de lire les messages reçus
	 */
	@Override
	public void run() {

		try {

			Message message = null;
			
			BufferedReader inputData = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			
			// Récupère les donnees reçues
			String stringData = inputData.readLine();
			
			// Tant que la connexion est ouverte, on lit les messages
			while (stringData != null) {
				
				// On décode le message reçu et on l'envoie au controlleur
				message = decodeMessageFromString(stringData);
				
				if (this.group == null) {
					this.group = message.getReceiverGroup();
				}
				
				// Réception d'un fichier ou d'une image
				if(message.getFunction() == Message.FUNCTION_FILE || message.getFunction() == Message.FUNCTION_IMAGE) {
					
					String FileString = "un nouveau fichier";
					if (message.getFunction() == Message.FUNCTION_IMAGE) FileString = "une nouvelle image";
					
					// On demande à l'utilisateur s'il souhaîte enregistrer le fichier ou l'image
					int dialogResult = JOptionPane.showConfirmDialog(null,
							"Vous avez reçu " + FileString + " de la part de " + message.getSender().getUsername() +
							".\nSouhaitez-vous l'enregistrer ?", 
							"Fichier reçu", JOptionPane.YES_NO_OPTION);
					
					
					if(dialogResult == JOptionPane.YES_OPTION) {
					
						// On lit le deuxième message reçu (le fichier)
						String fileData = inputData.readLine();
						
						File receivedFile = new File(message.getContent());
						
						// Séléction de l'emplacement de l'enregistrement du fichier reçu
						JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
						chooser.setDialogTitle("Selectionner où enregistrer le fichier reçu");
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						chooser.setMultiSelectionEnabled(false);
						chooser.setSelectedFile(receivedFile);
						int returnValue = chooser.showSaveDialog(null);
						
						// On enregistre que si l'utilisateur le souhaite
						if(returnValue == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chooser.getSelectedFile();
	
							decodeAndSaveFileFromString(fileData, selectedFile.toPath());
							message.setContent(selectedFile.toString());
						}
					
					}
					
				}

				controller.receiveMessage(message);
				
				// Lecture du prochain message
				stringData = inputData.readLine();
			
			}
			
		} catch (SocketException e) {

			// Socket fermé : pas d'erreur

		} catch (Exception e) {
			GUI.showError("Erreur dans la lecture des messages reçus.");
			
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				GUI.showError("Erreur lors de la déconnexion du lecteur des messages reçus.");
			}
		}

	}
	
	/**
	 * Permet de décoder un message à partir d'une chaîne de caractères
	 * @param stringData La chaîne de caractères à décoder
	 * @return Le Message decodé
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Message decodeMessageFromString(String stringData) throws ClassNotFoundException, IOException {

		byte[] data = Base64.getDecoder().decode(stringData);

		ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
		return (Message) inputStream.readObject();
	
	}
	
	/**
	 * Permet de décoder un fichier à partir d'une chaîne de caractères et de l'enregistrer
	 * @param fileData La chaîne de caractères à décoder
	 * @param filePath Déstination finale dans laquelle enregistrer le fichier sur le disque
	 * @throws IOException 
	 */
	private void decodeAndSaveFileFromString(String fileData, Path filePath) throws IOException {
		
		byte[] data = Base64.getDecoder().decode(fileData);
		
		FileOutputStream OutputFile = new FileOutputStream(filePath.toString());

        OutputFile.write(data);
        OutputFile.close();
	}
	
}
