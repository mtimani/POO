package ChatSystem;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Représente un utilisateur de l'application de chat
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String username;
	private InetAddress ip;
	private int port;
	private Date lastVisit;
	
	/**
	 * Crée un Utilisateur
	 * @param id ID de l'Utilisateur
	 * @param username Username de l'Utilisateur
	 * @param ip IP de l'Utilisateur
	 */
	public User(int id, String username, InetAddress ip) {
		this.id = id;
		this.username = username;
		this.ip = ip;
	}

	/**
	 * Retourne l'ID de l'Utilisateur
	 * @return l'ID de l'Utilisateur
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Retourne le Username de l'Utilisateur
	 * @return le Username de l'Utilisateur
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Modifie le Username de l'Utilisateur
	 * @param username Le nouveau Username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Retourne l'adresse IP de l'Utilisateur
	 * @return l'adresse IP de l'Utilisateur
	 */
	public InetAddress getIp() {
		return this.ip;
	}

	/**
	 * Retourne le numéro de Port de l'Utilisateur
	 * @return le numéro de Port de l'Utilisateur
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Modifie le numéro de Port de l'Utilisateur
	 * @param port Le nouveau numéro de Port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Retourne la Date de la dernière connexion de l'Utilisateur
	 * @return La Date de la dernière connexion de l'Utilisateur
	 */
	public Date getLastVisit() {
		return this.lastVisit;
	}

	/**
	 * Modifie la Dare de la dernière connexion de l'Utilisateur
	 * @param lastVisit La Date de la dernière connexion de l'Utilisateur
	 */
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}
	
	/**
	 * Test pour vérifier si deux Utilisateurs sont égaux (En utilisant comme critère l'ID)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof User)) return false;
		User u = (User) obj;
		return u.id == this.id;
	}
	
	/**
	 * Redéfinit la méthode d'affichage de l'objet User
	 * @return l'id de l'Utilisateur et son Username
	 */
	@Override
	public String toString() {
		return "[" + Integer.toString(this.id) + this.username + "]";
	}
	
}
