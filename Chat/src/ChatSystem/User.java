package ChatSystem;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Représente un utilisateur de l'application de chat
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int userId;
	private String username;
	private Date lastVisitDate;
	private int portNum;
	private InetAddress ipAddr;
	
	/**
	 * Crée un Utilisateur
	 * @param userId ID de l'Utilisateur
	 * @param username Username de l'Utilisateur
	 * @param ipAddr IP de l'Utilisateur
	 */
	public User(int userId, String username, InetAddress ipAddr) {
		this.userId = userId;
		this.username = username;
		this.ipAddr = ipAddr;
	}

	/**
	 * Retourne l'ID de l'Utilisateur
	 * @return l'ID de l'Utilisateur
	 */
	public int getUserId() {
		return this.userId;
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
	 * Retourne la Date de la dernière connexion de l'Utilisateur
	 * @return La Date de la dernière connexion de l'Utilisateur
	 */
	public Date getLastVisitDate() {
		return this.lastVisitDate;
	}

	/**
	 * Modifie la Dare de la dernière connexion de l'Utilisateur
	 * @param lastVisitDate La Date de la dernière connexion de l'Utilisateur
	 */
	public void setLastVisitDate(Date lastVisitDate) {
		this.lastVisitDate = lastVisitDate;
	}

	/**
	 * Retourne le numéro de Port de l'Utilisateur
	 * @return le numéro de Port de l'Utilisateur
	 */
	public int getPortNum() {
		return this.portNum;
	}

	/**
	 * Modifie le numéro de Port de l'Utilisateur
	 * @param portNum Le nouveau numéro de Port
	 */
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}
	
	/**
	 * Retourne l'adresse IP de l'Utilisateur
	 * @return l'adresse IP de l'Utilisateur
	 */
	public InetAddress getIpAddr() {
		return this.ipAddr;
	}
	
	/**
	 * Test pour vérifier si deux Utilisateurs sont égaux (En utilisant comme critère l'ID)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof User)) return false;
		User u = (User) obj;
		return u.userId == this.userId;
	}
	
	/**
	 * Redéfinit la méthode d'affichage de l'objet User
	 * @return l'id de l'Utilisateur et son Username
	 */
	@Override
	public String toString() {
		return "[" + Integer.toString(this.userId) + this.username + "]";
	}
	
}
