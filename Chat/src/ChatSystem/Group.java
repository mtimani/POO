package ChatSystem;

import java.io.*;
import java.util.*;

/**
 * Classe Group : Représente une conversation entre deux ou plus Users
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 2L;
	
	private int id;
	private ArrayList<User> members;
	private User origin;
	private boolean online;
	
	/**
	 * Création d'un groupe
	 * @param id ID du Groupe
	 * @param members Liste des membres du Groupe
	 * @param origin Créateur de la conversation
	 */
	public Group(int id, ArrayList<User> members, User origin) {
		this.id = id;
		this.origin = origin;
		this.online = true;
		
		this.members = new ArrayList<User>();
		for (User m : this.members) {
			this.members.add(m);
		}
	}

	/** 
	 * Retourne l'ID du groupe
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retourne la liste des membres du groupe
	 * @return the members
	 */
	public ArrayList<User> getMembers() {
		return members;
	}

	/**
	 * Retourne l'utilisateur qui initie la conversation
	 * @return the origin
	 */
	public User getOrigin() {
		return origin;
	}

	/**
	 * Spécifier l'utilisateur qui a initié la conversation
	 * @param origin the origin to set
	 */
	public void setOrigin(User origin) {
		this.origin = origin;
	}

	/**
	 * Retourne vrai si le groupe est en ligne 
	 * @return the online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * Spécifier si le groupe est en ligne ou pas
	 * @param online the online to set
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	/**
	 * Retourne Vrai si l'utilisateur est un membre du groupe, Faux sinon
	 * @param member L'utilisateur à tester
	 * @return Vrai si l'utilisateur est un membre du groupe, Faux sinon
	 */
	public boolean isMember(User member) {
		for (User m : this.members) {
			if (m.equals(member)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Permet de mettre à jour les informations sur un membre du groupe
	 * @param newVersionMember Nouvelle version de l'utilisateur
	 * @return vrai si au moins un utilisateur de la liste du groupe a été modifié
	 */
	public boolean updateMember(User newVersionMember) {
		boolean hasChanged = false;
		ArrayList<User> newMembers = new ArrayList<User>(members);
		
		for (User oldVersionMember : members) {
			if (oldVersionMember.equals(newVersionMember)
					|| !oldVersionMember.getUsername().equals(newVersionMember.getUsername())
					|| oldVersionMember.getPort() != newVersionMember.getPort()) {
				newMembers.remove(oldVersionMember);
				newMembers.add(newVersionMember);
				hasChanged = true;
			}
		}
		
		members = newMembers;
		return hasChanged;
	}
	
	/**
	 * Renvoie le nom du groupe vu par un utilisateur en particulier
	 * Utilisé dans l'interface graphique de l'application de Chat
	 * @param user L'utilisateur qui veut obtenir le nom
	 * @return Le nom du groupe vu par l'utilisateur qui demande
	 */
	public String getGroupNameForUser(User user) {
		if(this.members.get(0).equals(user)) {
			return members.get(1).getUsername();
		}
		else {
			return members.get(0).getUsername();
		}
	}
	
	/**
	 * Redéfinition de la methode .equals()
	 * @return Vrai si les deux Groupes sont égaux, Faux sinon
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group)) return false;
		Group g = (Group) obj;
		return g.id == this.id;
	}
	
}
