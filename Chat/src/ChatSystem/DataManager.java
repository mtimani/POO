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
	private static final String DATA_PATH = "data/";
	private static final String USER_PATH = "data/user.bin";
	private static final String MESSAGES_PATH = "data/messages.bin";
	private static final String GROUPS_PATH = "data/groups.bin";
	private static final String CONFIG_PATH = "settings/settings.ini";
	
	/**
	 * Erreurs
	 */
	@SuppressWarnings("serial")
	public static class PasswordError extends Exception {};
	
	/**
	 * Permet de chiffrer le mot de passe
	 * @param password Le mot de passe a chiffrer
	 * @return Le mot de passe chiffre
	 * @throws NoSuchAlgorithmException Si une erreur survient
	 */
	private static byte[] hashAPassword (char[] password) throws NoSuchAlgorithmException {
		byte[] passwordBytes = new byte[password.length];
		for (int i = 0; i < passwordBytes.length; i++)
			passwordBytes[i] = (byte) password[i];
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] passwordHashed = md.digest(passwordBytes);
		
		return passwordHashed;
	}
	
	/**
	 * Permet de lire un paramètre du fichier ini
	 * @param node Section du fichier
	 * @param setting Nom du paramètre
	 * @param defaultValue Valeur par défault à retourner si paramètre non existant
	 * @return Valeur du paramètre
	 */
	public static String getASetting(String node, String setting, String defaultValue) {
		File iniFile = new File(CONFIG_PATH);
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
	
	/**
	 * Sauvegarde sur la machine de tous les messages de l'utilisateur
	 * @param messages Messages à stocker
	 * @throws IOException Exception d'entrees-sorties
	 * @throws FileNotFoundException Exception de disponibilite de fichier
	 */
	public static void writeAllMessages(ArrayList<Message> messages) throws FileNotFoundException, IOException {
		// Verfication de l'existance du dossier "data", création du dossier dans le cas où il n'existe pas
		File directory = new File(DATA_PATH);
		if(!directory.exists()) directory.mkdir();
		
		FileOutputStream file = new FileOutputStream(MESSAGES_PATH);
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
	 * @throws IOException Exception d'entrees-sorties
	 * @throws FileNotFoundException Exception de disponibilite de fichier
	 * @throws ClassNotFoundException Exception de classe inexistante
	 */
	public static ArrayList<Message> readAllMessages() throws FileNotFoundException, IOException, ClassNotFoundException {
		ArrayList<Message> messages = new ArrayList<Message>();
		
		File messagesFile = new File(MESSAGES_PATH);
		if(messagesFile.exists()) {
			FileInputStream file = new FileInputStream(MESSAGES_PATH);
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
	 * @throws IOException Exception d'entrees-sorties
	 * @throws FileNotFoundException Exception de disponibilite de fichier
	 */
	public static void writeAllGroups(ArrayList<Group> groups) throws FileNotFoundException, IOException {
		
		// Verifie que le dossier "data" existe, sinon le cree
	    File directory = new File(DATA_PATH);
	    if(!directory.exists()) directory.mkdir();

	    FileOutputStream file = new FileOutputStream(GROUPS_PATH);
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
	 * @throws IOException Exception d'entrees-sorties
	 * @throws FileNotFoundException Exception de disponibilite de fichier
	 * @throws ClassNotFoundException Exception de classe inexistante
	 */
	public static ArrayList<Group> readAllGroups() throws FileNotFoundException, IOException, ClassNotFoundException{
		
		ArrayList<Group> groups = new ArrayList<Group>();
		
		File groupsFile = new File(GROUPS_PATH);
		if(groupsFile.exists()) {
			
			FileInputStream file = new FileInputStream(GROUPS_PATH);
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
	 * Permet de sauvegarder un utilisateur sur la machine
	 * @param username Username de l'utilisateur
	 * @param password Mot de passe de l'utilisateur
	 * @throws IOException Exception d'entrees-sorties
	 * @throws NoSuchAlgorithmException Exception d'algorithme inexistant
	 */
	public static void createAUser(String username, char[] password) throws IOException, NoSuchAlgorithmException {		
		// Verifie que le dossier "data" existe, sinon le cree
		File directory = new File(DATA_PATH);
	    if(!directory.exists()) directory.mkdir();
		    
	    FileOutputStream file = new FileOutputStream(USER_PATH);
		ObjectOutputStream out = new ObjectOutputStream(file);   
		
		// ID de l'utilisateur random
		Random rand = new Random();
		int id = rand.nextInt(999999999);
		
		out.writeInt(id);
		out.writeObject(username);
		
		// Chiffrement du mot de passe avec MD5
		byte[] passwordHashed = hashAPassword(password);
		out.writeObject(passwordHashed);
		
		out.close();
		file.close();
	}
	
	/**
	 * Permet de voir si les donnees de connexion sont correctes
	 * @param username Username de l'utilisateur
	 * @param password Mot de passe de l'utilisateur
	 * @return L'ID de l'utilisateur si les donnees sont correctes, -1 sinon
	 * @throws IOException Exception d'entrees-sorties
	 * @throws ClassNotFoundException Exception de classe inexistante
	 * @throws NoSuchAlgorithmException Exception d'algorithme inexistant
	 */
	public static int checkAUser(String username, char[] password) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

		File usersFile = new File(USER_PATH);

		if (usersFile.exists()) {
			
			FileInputStream file = new FileInputStream(USER_PATH);
			ObjectInputStream in = new ObjectInputStream(file);

			int id = (int) in.readInt();
			String usernameFile = (String) in.readObject();
			byte[] passwordFileHashed = (byte[]) in.readObject();
			
			byte[] passwordEnterHashed = hashAPassword(password);

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
	 * @throws IOException Exception d'entrees-sorties
	 * @throws ClassNotFoundException Exception de classe inexistante
	 */
	public static void changeAUsername(String newUsername) throws IOException, ClassNotFoundException {
		File usersFile = new File(USER_PATH);
		
		if (usersFile.exists()) {
			FileInputStream file_read = new FileInputStream(USER_PATH);			

			ObjectInputStream in = new ObjectInputStream(file_read);			
			int id = (int) in.readInt();
			@SuppressWarnings("unused")
			String oldUsername = (String) in.readObject();

			byte[] password = (byte[]) in.readObject();
			in.close();
			file_read.close();
			
			FileOutputStream file_write = new FileOutputStream(USER_PATH, false);
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
	 * @throws IOException Exception d'entrees-sorties
	 * @throws ClassNotFoundException Exception de classes inexistante
	 * @throws NoSuchAlgorithmException Exception d'algorithme inexistant
	 * @throws PasswordError Exception de mot de passe incorrect
	 */
	public static void changeAPassword(char[] oldPassword, char[] newPassword) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, PasswordError {
		File usersFile = new File(USER_PATH);
		
		if (usersFile.exists()) {
			FileInputStream file_read = new FileInputStream(USER_PATH);			
			ObjectInputStream in = new ObjectInputStream(file_read);	
			byte[] oldPasswordHashed = hashAPassword(oldPassword);
			
			int id = (int) in.readInt();
			String username = (String) in.readObject();
			byte[] passwordFileHashed = (byte[]) in.readObject();
			in.close();
			file_read.close();
			
			if(Arrays.equals(passwordFileHashed, oldPasswordHashed)) {
				FileOutputStream file_write = new FileOutputStream(USER_PATH);
				ObjectOutputStream out = new ObjectOutputStream(file_write);
				
				byte[] newPasswordHashed = hashAPassword(newPassword);
				
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
	
}
