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
	 * Sauvegarde sur la machine de tous les messages de l'utilisateur
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
	
	/**
	 * Sauvegarde sur la machine de tous les groupes de l'utilisateur
	 * @param groups La liste de tous les Groupes a sauvegarder
	 */
	public static void writeAllGroups(ArrayList<Group> groups) throws FileNotFoundException, IOException {
		
		// Verifie que le dossier "data" existe, sinon le cree
	    File directory = new File(PATH_DATA);
	    if(!directory.exists()) directory.mkdir();

	    FileOutputStream file = new FileOutputStream(PATH_GROUPS);
		ObjectOutputStream out = new ObjectOutputStream(file);
		
		// Ecriture de chaque groupe
		for(Group g : groups) {
			out.writeObject(g);
		}
		
		out.close();
		file.close();
	}
	
	/**
	 * Retourne la liste de tous les groupes sauvegardés sur la machine
	 * @return La liste de tous les groupes sauvegardés sur la machine
	 */
	public static ArrayList<Group> readAllGroups() throws FileNotFoundException, IOException, ClassNotFoundException{
		
		ArrayList<Group> groups = new ArrayList<Group>();
		
		File groupsFile = new File(PATH_GROUPS);
		if(groupsFile.exists()) {
			
			FileInputStream file = new FileInputStream(PATH_GROUPS);
			ObjectInputStream in = new ObjectInputStream(file);
			
			// Lecture de tous les groupes
			while(true) {
				try {
					Group group = (Group) in.readObject();
					groups.add(group);
				}
				catch (EOFException e) {
					break;
				}
			}
			
			in.close();
			file.close();
			
		}
		
		return groups;
	}
	
	/**
	 * Permet de stocker un utilisateur sur la machine
	 * @param username Username de l'utilisateur
	 * @param password Password de l'utilisateur
	 * @throws IOException Si une erreur survient
	 * @throws NoSuchAlgorithmException Si une erreur survient
	 */
	public static void createUser(String username, char[] password) throws IOException, NoSuchAlgorithmException {		
		// Verifie que le dossier "data" existe, sinon le cree
		File directory = new File(PATH_DATA);
	    if(!directory.exists())
	        directory.mkdir();
		    
	    FileOutputStream file = new FileOutputStream(PATH_USER);
		ObjectOutputStream out = new ObjectOutputStream(file);   
		
		// ID de l'utilisateur random
		Random rand = new Random();
		int id = rand.nextInt(999999999);
		
		out.writeInt(id);
		out.writeObject(username);
		
		// Chiffrement du mot de passe avec MD5
		byte[] passwordHashed = hashPassword(password);
		out.writeObject(passwordHashed);
		
		out.close();
		file.close();
	}
	
	/**
	 * Permet de voir si les donnees de connexion sont correctes
	 * @param username Username de l'utilisateur
	 * @param password Password de l'utilisateur
	 * @return L'ID de l'utilisateur si les donnees sont correctes, -1 sinon
	 * @throws IOException Si une erreur survient
	 * @throws ClassNotFoundException Si une erreur survient
	 * @throws NoSuchAlgorithmException Si une erreur survient
	 */
	public static int checkUser(String username, char[] passwordEnter) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

		File usersFile = new File(PATH_USER);

		if (usersFile.exists()) {
			
			FileInputStream file = new FileInputStream(PATH_USER);
			ObjectInputStream in = new ObjectInputStream(file);

			int id = (int) in.readInt();
			String usernameFile = (String) in.readObject();
			byte[] passwordFileHashed = (byte[]) in.readObject();
			
			byte[] passwordEnterHashed = hashPassword(passwordEnter);

			if (usernameFile.equals(username) && Arrays.equals(passwordFileHashed, passwordEnterHashed)) {
				in.close();
				return id;
			}			
			in.close();
		}		
		return -1;
	}
	
	/**
	 * Permet de changer le username de l'utilisateur (y compris dans le fichier)
	 * @param newUsername Le nouveau username
	 * @throws IOException Si une erreur survient
	 * @throws ClassNotFoundException Si une erreur survient
	 */
	public static void changeUsername(String newUsername) throws IOException, ClassNotFoundException {
		File usersFile = new File(PATH_USER);
		
		if (usersFile.exists()) {
			FileInputStream file_read = new FileInputStream(PATH_USER);			

			ObjectInputStream in = new ObjectInputStream(file_read);			
			int id = (int) in.readInt();
			@SuppressWarnings("unused")
			String oldUsername = (String) in.readObject();

			byte[] password = (byte[]) in.readObject();
			in.close();
			file_read.close();
			
			FileOutputStream file_write = new FileOutputStream(PATH_USER, false);
			ObjectOutputStream out = new ObjectOutputStream(file_write);
			out.writeInt(id);
			out.writeObject(newUsername);
			out.writeObject(password);	
			out.close();
			file_write.close();
		}		
	}
	
}
