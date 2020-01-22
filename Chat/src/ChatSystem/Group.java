package ChatSystem;

import java.io.*;
import java.util.*;

/**
 * Represente une conversation entre deux utilisateurs ou plus
 *
 */
public class Group implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int groupId;
	private User origin;
	private ArrayList<User> groupMembers;
	private boolean online;
	
	/**
	 * Creation d'un groupe
	 * @param groupId ID du groupe
	 * @param origin Origine de la conversation
	 * @param groupMembers Liste des membres du groupe
	 */
	public Group(int groupId, User origin, ArrayList<User> groupMembers) {
		this.groupId = groupId;
		this.origin = origin;
		this.online = true;
		this.groupMembers = new ArrayList<User>();
		for(User m : groupMembers) {
			this.groupMembers.add(m);
		}
	}
	
	/**
	 * Retourne l'ID du groupe
	 * @return L'ID du groupe
	 */
	public int getGroupId() {
		return groupId;
	}
	
	/**
	 * Permet de changer l'id du groupe
	 * @param groupId Nouveau id du groupe
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
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
	 * Retourne les membres du groupe
	 * @return Les membres du groupe
	 */
	public ArrayList<User> getGroupMembers() {
		return groupMembers;
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
	public boolean isAMember(User member) {
		for(User m : groupMembers) {
			if(m.getUserId() == member.getUserId())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Permet de mettre a jour les informations sur un membre du groupe
	 * @param newVersionMember La nouvelle version de l'utilisateur
	 * @return booleen pour indiquer si la mise a jour s'est bien deroulee
	 */
	public boolean updateAMember(User newVersionMember) {
		boolean hasChanged = false;
		ArrayList<User> newGroupMembers = new ArrayList<User>(groupMembers);
		
		for(User oldVersionMember : groupMembers) {
			
			if(oldVersionMember.equals(newVersionMember)) {
				
				if(oldVersionMember.getPortNum() != newVersionMember.getPortNum()
						|| !oldVersionMember.getUsername().equals(newVersionMember.getUsername())) {
					newGroupMembers.remove(oldVersionMember);
					newGroupMembers.add(newVersionMember);
					hasChanged = true;
				}
				
			}
		}
		
		groupMembers = newGroupMembers;
		
		return hasChanged;
	}
	
	/**
	 * Renvoie le nom d'un groupe vu par un utilisateur en particulier
	 * Dans une conversation � deux, le nom correspond au nom du contact distant
	 * Utilise pour faire le lien avec ce qui est affiche dans le GUI
	 * @param user L'utilisateur qui veut obtenir le nom
	 * @return Le nom du groupe vu par l'utilisateur demandeur
	 */
	public String getGroupNameForAUser(User user) {
	
		if(groupMembers.get(0).equals(user))
			return groupMembers.get(1).getUsername();
		else
			return groupMembers.get(0).getUsername();
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group))
			return false;
		
		Group g = (Group) obj;
		return g.groupId == groupId;
	}

}
