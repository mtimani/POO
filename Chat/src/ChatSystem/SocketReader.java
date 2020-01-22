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
	
	private Socket tcpSocket;
	private Controller controller;
	private volatile Group group = null;

	/**
	 * Crée un SocketReader
	 * @param name Nom du thread
	 * @param tcpSocket Socket associé
	 * @param controller Controlleur de l'application
	 */
	public SocketReader(String name, Socket tcpSocket, Controller controller) {
		super(name);
		this.tcpSocket = tcpSocket;
		this.controller = controller;
	}
	
	/**
	 * Permet de décoder un message à partir d'une chaîne de caractères
	 * @param stringData La chaîne de caractères à décoder
	 * @return Le Message decodé
	 * @throws ClassNotFoundException Exception de classe inexistante
	 * @throws IOException Exception d'entrees-sorties
	 */
	private Message decodeAMessageFromString(String stringData) throws ClassNotFoundException, IOException {
		byte[] data = Base64.getDecoder().decode(stringData);
		ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
		return (Message) inputStream.readObject();
	}
	
	/**
	 * Permet de décoder un fichier à partir d'une chaîne de caractères et de l'enregistrer
	 * @param fileData La chaîne de caractères à décoder
	 * @param filePath Déstination finale dans laquelle enregistrer le fichier sur le disque
	 * @throws IOException Exception d'entrees-sorties
	 */
	private void decodeAndSaveAFileFromString(String fileData, Path filePath) throws IOException {
		byte[] data = Base64.getDecoder().decode(fileData);
		FileOutputStream OutputFile = new FileOutputStream(filePath.toString());
        OutputFile.write(data);
        OutputFile.close();
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
			Message msg = null;
			BufferedReader inputDataReader = new BufferedReader(new InputStreamReader(this.tcpSocket.getInputStream()));
			// Récupère les donnees reçues
			String stringDataInput = inputDataReader.readLine();
			// Tant que la connexion est ouverte, on lit les messages
			while (stringDataInput != null) {
				// On décode le message reçu et on l'envoie au controlleur
				msg = decodeAMessageFromString(stringDataInput);
				if (this.group == null) {
					this.group = msg.getDestinationGroup();
				}
				// Réception d'un fichier ou d'une image
				if(msg.getFunction() == Message.FILE_FUNCTION || msg.getFunction() == Message.IMAGE_FUNCTION) {	
					String FileString = "un nouveau fichier";
					if (msg.getFunction() == Message.IMAGE_FUNCTION) FileString = "une nouvelle image";
					// On demande à l'utilisateur s'il souhaîte enregistrer le fichier ou l'image
					int dialogWindowResult = JOptionPane.showConfirmDialog(null,
							"Vous avez reçu " + FileString + " de la part de " + msg.getSender().getUsername() +
							".\nSouhaitez-vous l'enregistrer ?", 
							"Fichier reçu", JOptionPane.YES_NO_OPTION);
					if(dialogWindowResult == JOptionPane.YES_OPTION) {
						// On lit le deuxième message reçu (le fichier)
						String fileDataInput = inputDataReader.readLine();
						File receivedFileInput = new File(msg.getMsg());
						// Séléction de l'emplacement de l'enregistrement du fichier reçu
						JFileChooser dirChooser = new JFileChooser(System.getProperty("user.dir"));
						dirChooser.setDialogTitle("Selectionner où enregistrer le fichier reçu");
						dirChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						dirChooser.setMultiSelectionEnabled(false);
						dirChooser.setSelectedFile(receivedFileInput);
						int returnValue = dirChooser.showSaveDialog(null);
						// On enregistre que si l'utilisateur le souhaite
						if(returnValue == JFileChooser.APPROVE_OPTION) {
							File selectedFile = dirChooser.getSelectedFile();
							decodeAndSaveAFileFromString(fileDataInput, selectedFile.toPath());
							msg.setMsg(selectedFile.toString());
						}
					}
				}
				controller.receiveMessage(msg);
				// Lecture du prochain message
				stringDataInput = inputDataReader.readLine();
			}
		} catch (SocketException e) {
			// Socket fermé : pas d'erreur
		} catch (Exception e) {
			GUI.showError("Erreur dans la lecture des messages reçus.");
		} finally {
			try {
				tcpSocket.close();
			} catch (Exception e) {
				GUI.showError("Erreur lors de la déconnexion du lecteur des messages reçus.");
			}
		}
	}
}
