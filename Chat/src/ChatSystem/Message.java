package ChatSystem;

import java.io.*;
import java.util.*;

/**
 * Représente un message de l'application de chat
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 3L;

	private Date date;
	private String content;
	private User sender; 
	private Group receiverGroup;
	private int function;
	
	/**
	 * Fonctions du message
	 */
	public static final int FUNCTION_NORMAL = 0;
	public static final int FUNCTION_STOP = 1;
	public static final int FUNCTION_FILE = 2;
	public static final int FUNCTION_IMAGE = 3;
	
	/**
	 * Crée un message
	 * @param date Date du message
	 * @param content Contenu du message
	 * @param sender ID de l'envoyeur du message
	 * @param receiverGroup ID du groupe du receveur du message
	 */
	public Message(Date date, String content, User sender, Group receiverGroup, int function) {
		this.date = date;
		this.content = content;
		this.sender = sender;
		this.receiverGroup = receiverGroup;
		this.function = function;
	}

	/**
	 * Retourne la date du message
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Retourne le contenu du message
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}
	
	/**
	 * Modifie le contenu du message
	 * @param content Contenu du message
	 */
	public void setContent(String content) {
		this.content = content;
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
	public Group getReceiverGroup() {
		return this.receiverGroup;
	}

	/**
	 * Retourne la fonction du message
	 * @return the function
	 */
	public int getFunction() {
		return this.function;
	}
	
	public void updateSender(User newVersionSender) {
		if (this.sender.equals(newVersionSender)) {
			this.sender = newVersionSender;
		}
		receiverGroup.updateMember(newVersionSender);
	}
	
}
