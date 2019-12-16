package ChatSystem;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class GUI extends JFrame {

	private JFrame frmChatConnection;
	private static final long serialVersionUID = 1L;
	private static Controller controller;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmChatConnection.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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

	/**
	 * Create the application.
	 */
	public GUI() throws SocketException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws SocketException {
		frmChatConnection = new JFrame();
		frmChatConnection.setTitle("Chat Connection");
		frmChatConnection.setBounds(100, 100, 389, 175);
		frmChatConnection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatConnection.getContentPane().setLayout(null);
		frmChatConnection.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 0, 364, 140);
		frmChatConnection.getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnConnexion = new JButton("Log in");
		btnConnexion.setBounds(12, 103, 137, 25);
		panel.add(btnConnexion);
		
		JButton btnNewButton = new JButton("Sign up");
		btnNewButton.setBounds(206, 103, 146, 25);
		panel.add(btnNewButton);
		
		ArrayList<InetAddress> allIP = new ArrayList<InetAddress>(controller.getAllIpAndBroadcast().keySet());
		JComboBox comboBox = new JComboBox(allIP.toArray());
		comboBox.setBounds(12, 12, 340, 25);
		panel.add(comboBox);
		
		JLabel lblTypeYourLogin = new JLabel("Type your login");
		lblTypeYourLogin.setBounds(25, 49, 106, 15);
		panel.add(lblTypeYourLogin);
		
		textField = new JTextField();
		textField.setBounds(12, 72, 137, 25);
		panel.add(textField);
		textField.setColumns(10);
	}
}


/**package ChatSystem;

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
		
		/* Ecrire un message *
		textArea = new JTextField("ecrire un message..."); 
		textArea.addActionListener(this);
		panel.add(textArea); 
		
		/* Envoyer un message * 
		send = new JButton("Envoyer", icon);
		send.addActionListener(this);
		panel.add(send); 
		
		/* Editer le profil *
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
**/

