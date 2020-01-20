package ChatSystem;

import java.io.*;
import java.util.*;

/**
 * Represente une conversation entre deux utilisateurs ou plus
 *
 */
public class Group implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private ArrayList<User> members;
	private User origin;
	private boolean online;
	
	/**
	 * Creation d'un groupe
	 * @param id ID du groupe
	 * @param members Liste des membres du groupe
	 */
	public Group(int id, ArrayList<User> members, User starter) {
		this.id = id;
		this.origin = starter;
		this.online = true;
		
		this.members = new ArrayList<User>();
		for(User m : members)
			this.members.add(m);
		
	}
	
	/**
	 * Retourne l'ID du groupe
	 * @return L'ID du groupe
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Permet de changer l'id du groupe
	 * @param id Nouveau id du groupe
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Retourne les membres du groupe
	 * @return Les membres du groupe
	 */
	public ArrayList<User> getMembers() {
		return members;
	}
	
	/**
	 * Retourne l'utilisateur qui a initie la conversation
	 * @return l'utilisateur qui a initie la conversation
	 */
	public User getOrigin() {
		return origin;
	}
	
	/**
	 * Indique l'utilisateur qui a initie la conversation
	 * @param origin L'utilisateur qui a initie la conversation
	 */
	public void setOrigin(User origin) {
		this.origin = origin;
	}
	
	/**
	 * Retourne True si le groupe est en ligne
	 * @return True si le groupe est en ligne
	 */
	public boolean isOnline() {
		return online;
	}
	
	/**
	 * Indique que le groupe est en ligne ou non
	 * @param online Si le groupe est en ligne ou non
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	/**
	 * Retourne True si un utilisateur est membre de ce groupe
	 * @param member L'utilisateur a tester
	 * @return True si l'utilisateur est dans le groupe, False sinon
	 */
	public boolean isMember(User member) {
		for(User m : members) {
			if(m.getId() == member.getId())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Permet de mettre a jour les informations sur un membre du groupe
	 * @param newVersionMember La nouvelle version de l'utilisateur
	 */
	public boolean updateMember(User newVersionMember) {
		boolean hasChanged = false;
		ArrayList<User> newMembers = new ArrayList<User>(members);
		
		for(User oldVersionMember : members) {
			
			if(oldVersionMember.equals(newVersionMember)) {
				
				if(oldVersionMember.getPort() != newVersionMember.getPort()
						|| !oldVersionMember.getUsername().equals(newVersionMember.getUsername())) {
					newMembers.remove(oldVersionMember);
					newMembers.add(newVersionMember);
					hasChanged = true;
				}
				
			}
		}
		
		members = newMembers;
		
		return hasChanged;
	}
	
	/**
	 * Renvoie le nom d'un groupe vu par un utilisateur en particulier
	 * Dans une conversation � deux, le nom correspond au nom du contact distant
	 * Utilise pour faire le lien avec ce qui est affiche dans le GUI
	 * @param user L'utilisateur qui veut obtenir le nom
	 * @return Le nom du groupe vu par l'utilisateur demandeur
	 */
	public String getGroupNameForUser(User user) {
	
		if(members.get(0).equals(user))
			return members.get(1).getUsername();
		else
			return members.get(0).getUsername();
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group))
			return false;
		
		Group g = (Group) obj;
		return g.id == id;
	}

}
