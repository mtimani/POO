package ChatSystem;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.prefs.*;
import org.ini4j.*;

/**
 * Classe permettant de se charger de la sauvegarde et récupération de données dans la mémoire
 */
public class DataManager {
	
	/**
	 * Constantes
	 */
	
	private static final String PATH_DATA = "data/";
	private static final String PATH_USER = "data/user.bin";
	private static final String PATH_MESSAGES = "data/messages.bin";
	private static final String PATH_GROUPS = "data/groups.bin";
	private static final String PATH_CONFIG = "settings/settings.ini";
	
	/**
	 * Erreurs
	 */
	@SuppressWarnings("serial")
	public static class PasswordError extends Exception {};
	
	/**
	 * Permet de sauvegarder un utilisateur sur la machine
	 * @param username Username de l'utilisateur
	 * @param password Mot de passe de l'utilisateur
	 */
	public static void createUser(String username, char[] password) throws IOException, NoSuchAlgorithmException {		
		// Verifie que le dossier "data" existe, sinon le cree
		File directory = new File(PATH_DATA);
	    if(!directory.exists()) directory.mkdir();
		    
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
	 * @param password Mot de passe de l'utilisateur
	 * @return L'ID de l'utilisateur si les donnees sont correctes, -1 sinon
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static int checkUser(String username, char[] password) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

		File usersFile = new File(PATH_USER);

		if (usersFile.exists()) {
			
			FileInputStream file = new FileInputStream(PATH_USER);
			ObjectInputStream in = new ObjectInputStream(file);

			int id = (int) in.readInt();
			String usernameFile = (String) in.readObject();
			byte[] passwordFileHashed = (byte[]) in.readObject();
			
			byte[] passwordEnterHashed = hashPassword(password);

			if (usernameFile.equals(username) && Arrays.equals(passwordFileHashed, passwordEnterHashed)) {
				in.close();
				return id;
			}			
			in.close();
		}		
		return -1;
	}
	
	/**
	 * Permet de changer le username de l'utilisateur dans la mémoire
	 * @param newUsername Le nouveau username
	 * @throws IOException
	 * @throws ClassNotFoundException
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
	
	/**
	 * Permet de changer le mot de passe de l'utilisateur
	 * @param oldPassword L'ancien mot de passe
	 * @param newPassword Le nouveau mot de passe
	 * @throws IOException Si une erreur survient
	 * @throws ClassNotFoundException Si une erreur survient
	 * @throws NoSuchAlgorithmException Si une erreur survient
	 * @throws PasswordError Si l'ancien mot de passe fourni est incorrect
	 */
	public static void changePassword(char[] oldPassword, char[] newPassword) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, PasswordError {
		File usersFile = new File(PATH_USER);
		
		if (usersFile.exists()) {
			FileInputStream file_read = new FileInputStream(PATH_USER);			
			ObjectInputStream in = new ObjectInputStream(file_read);	
			byte[] oldPasswordHashed = hashPassword(oldPassword);
			
			int id = (int) in.readInt();
			String username = (String) in.readObject();
			byte[] passwordFileHashed = (byte[]) in.readObject();
			in.close();
			file_read.close();
			
			if(Arrays.equals(passwordFileHashed, oldPasswordHashed)) {
				FileOutputStream file_write = new FileOutputStream(PATH_USER);
				ObjectOutputStream out = new ObjectOutputStream(file_write);
				
				byte[] newPasswordHashed = hashPassword(newPassword);
				
				out.writeInt(id);
				out.writeObject(username);
				out.writeObject(newPasswordHashed);			
				out.close();
				file_write.close();
			}
			else {
				throw new PasswordError();
			}
		} 
	}
	
	/**
	 * Permet de chiffrer le mot de passe
	 * @param password Le mot de passe a chiffrer
	 * @return Le mot de passe chiffre
	 * @throws NoSuchAlgorithmException Si une erreur survient
	 */
	private static byte[] hashPassword (char[] password) throws NoSuchAlgorithmException {
		byte[] passwordBytes = new byte[password.length];
		for (int i = 0; i < passwordBytes.length; i++)
			passwordBytes[i] = (byte) password[i];
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] passwordHashed = md.digest(passwordBytes);
		
		return passwordHashed;
	}
	
	/**
	 * Sauvegarde sur la machine de tous les messages de l'utilisateur
	 * @param messages Messages à stocker
	 * @throws IOException
	 * @throws FileNotFoundException
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
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
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
	 * @throws IOException
	 * @throws FileNotFoundException
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
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
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
	 * Permet de lire un paramètre du fichier ini
	 * @param node Section du fichier
	 * @param setting Nom du paramètre
	 * @param defaultValue Valeur par défault à retourner si paramètre non existant
	 * @return Valeur du paramètre
	 */
	public static String getSetting(String node, String setting, String defaultValue) {
		File iniFile = new File(PATH_CONFIG);
		if(!iniFile.exists() || iniFile.isDirectory()) return defaultValue;
		
		try {
			Ini ini = new Ini(iniFile);
			Preferences prefs = new IniPreferences(ini);
			return prefs.node(node).get(setting, defaultValue);
		}
		catch (Exception e) {
			return defaultValue;
		}
	}
	
}
