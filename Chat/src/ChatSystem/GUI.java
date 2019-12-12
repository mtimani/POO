package ChatSystem;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*; 

public class GUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel panel; 
	
	private JTextField textArea;   
	
	private JButton send; 
	private Icon icon = null; // A modifier

	
	private JButton userProfile;  
	
	
	public GUI(String title, GraphicsConfiguration gc) {
		super(title, gc); 
		panel = new JPanel(); 
		
		/* Ecrire un message */ 
		textArea = new JTextField("ecrire un message..."); 
		textArea.addActionListener(this);
		panel.add(textArea); 
		
		/* Envoyer un message */ 
		send = new JButton("Envoyer", icon);
		send.addActionListener(this);
		panel.add(send); 
		
		/* Editer le profil */
		userProfile = new JButton("Profil");
		userProfile.addActionListener(this);
		
		
		JFrame frame = new JFrame("ChatSystem");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(panel, BorderLayout.CENTER);
	    
	    
        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
	}
	
	public void addGroup(Group g) {	
	}
	
	public void replaceUsernameInList(String oldUsername, String newUsername) {
	}
	
	public void updateConnectedUsers() {	
	}
	
	public void selectGroupInList(Group g) {
	}
	
	public void setGroupNoRead(Group g) {
	}
	
	public static void showError(String error) {	
	}
	
	
	
	
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	GraphicsConfiguration gc = null; // A modifier 
            	GUI gui1 = new GUI("bidule", gc); 
            }
        });
    }

}
