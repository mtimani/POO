package ChatSystem;

import java.io.*;
import java.util.*;

/**
 * Représente un message de l'application de chat
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 3L;

	private String msg;
	private User sender; 
	private Group destinationGroup;
	private Date dateMsg;
	private int function;
	
	/**
	 * Fonctions du message
	 */
	public static final int NORMAL_FUNCTION = 0;
	public static final int STOP_FUNCTION = 1;
	public static final int FILE_FUNCTION = 2;
	public static final int IMAGE_FUNCTION = 3;
	
	/**
	 * Crée un message
	 * @param msg Contenu du message
	 * @param sender ID de l'envoyeur du message
	 * @param destinationGroup ID du groupe du receveur du message
	 * @param dateMsg Date du message
	 * @param function Fonction à implémenter
	 */
	public Message(String msg, User sender, Group destinationGroup, Date dateMsg, int function) {
		this.msg = msg;
		this.sender = sender;
		this.destinationGroup = destinationGroup;
		this.dateMsg = dateMsg;
		this.function = function;
	}

	/**
	 * Retourne le contenu du message
	 * @return the content
	 */
	public String getMsg() {
		return this.msg;
	}
	
	/**
	 * Modifie le contenu du message
	 * @param content Contenu du message
	 */
	public void setMsg(String content) {
		this.msg = content;
	}

	/**
	 * Retourne l'envoyeur du message
	 * @return the sender
	 */
	public User getSender() {
		return this.sender;
	}

	/**
	 * Retourne le groupe de l'envoyeur du message
	 * @return the receiverGroup
	 */
	public Group getDestinationGroup() {
		return this.destinationGroup;
	}

	/**
	 * Retourne la date du message
	 * @return the date
	 */
	public Date getDateMsg() {
		return this.dateMsg;
	}
	
	/**
	 * Retourne la fonction du message
	 * @return the function
	 */
	public int getFunction() {
		return this.function;
	}
	
	/**
	 * Fonction qui met à jour l'envoyeur du message
	 * @param newVersionSender Username du nouvel envoyeur
	 */
	public void updateSender(User newVersionSender) {
		if (this.sender.equals(newVersionSender)) {
			this.sender = newVersionSender;
		}
		destinationGroup.updateAMember(newVersionSender);
	}
	
}
