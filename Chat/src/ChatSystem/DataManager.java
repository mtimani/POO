package ChatSystem;

import java.io.*;
import java.util.*;
import java.security.*;


public class DataManager {
	
	/**
	 * Constantes
	 */
	private static final String PATH_DATA = "data/";
	private static final String PATH_USER = "data/user.bin";
	private static final String PATH_MESSAGES = "data/messages.bin";
	private static final String PATH_GROUPS = "data/groups.bin";
	
	//Password code here if it appears afterwards in the project
	
	/**
	 * Stockage sur la machine de tous les messages de l'utilisateur
	 * @param messages Messages à stocker
	 */
	public static void writeAllMessages(ArrayList<Message> messages) throws FileNotFoundException, IOException {
		// Verfication de l'existance du dossier "data", création du dossier dans le cas où il n'existe pas
		File directory = new File(PATH_DATA);
		if(!directory.exists()) directory.mkdir();
		
		FileOutputStream file = new FileOutputStream(PATH_MESSAGES);
		ObjectOutputStream out = new ObjectOutputStream(file);
		
		//Ecriture de chaque message
		for (Message m : messages) {
			out.writeObject(m);
		}
		
		out.close();
		file.close();
	}
	
	/**
	 * Retourne l'ensemble des messages sauvegardés sur la machine de l'utilisateur
	 * @return La liste de tous les messages sauvegardés sur la machine
	 */
	public static ArrayList<Message> readAllMessages() throws FileNotFoundException, IOException, ClassNotFoundException {
		ArrayList<Message> messages = new ArrayList<Message>();
		
		File messagesFile = new File(PATH_MESSAGES);
		if(messagesFile.exists()) {
			FileInputStream file = new FileInputStream(PATH_MESSAGES);
			ObjectInputStream in = new ObjectInputStream(file);
			
			// Lecture de tous les messages
			while(true) {
				try {
					Message m = (Message) in.readObject();
					messages.add(m);
				}
				catch (EOFException e) {
					break;
				}
			}
			
			in.close();
			file.close();
		}
		
		return messages;
	}
	
	
	
}
