package ChatSystem;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.math.*;
import javax.swing.*;

public class GUI extends JFrame {

	private JFrame frmChatConnection;
	private static final long serialVersionUID = 1L;
	private static Controller controller;
	private JTextField textField;
	private JComboBox comboBox;

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

	/**
	 * Create the application.
	 */
	public GUI() throws SocketException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("unchecked")
	private void initialize() throws SocketException {
		frmChatConnection = new JFrame();
		frmChatConnection.setTitle("Chat Connection");
		frmChatConnection.setBounds(100, 100, 390, 187);
		frmChatConnection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatConnection.getContentPane().setLayout(null);
		frmChatConnection.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 0, 364, 156);
		frmChatConnection.getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnConnexion = new JButton("Log in");
		btnConnexion.setBounds(12, 123, 137, 25);
		btnConnexion.addActionListener(new ConnectListener(this));
		panel.add(btnConnexion);
		
		JButton btnNewButton = new JButton("Sign up");
		btnNewButton.setBounds(206, 123, 146, 25);
		btnNewButton.addActionListener(new CreateUserListener(this));
		panel.add(btnNewButton);
		
		ArrayList<InetAddress> allIP = new ArrayList<InetAddress>(Controller.getAllIpAndBroadcast().keySet());
		@SuppressWarnings("rawtypes")
		JComboBox comboBox = new JComboBox(allIP.toArray());
		comboBox.setBounds(12, 12, 340, 25);
		this.comboBox = comboBox;
		panel.add(comboBox);
		
		JLabel lblTypeYourLogin = new JLabel("Type your login");
		lblTypeYourLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblTypeYourLogin.setBounds(12, 49, 137, 15);
		panel.add(lblTypeYourLogin);
		
		textField = new JTextField(20);
		textField.setBounds(12, 89, 137, 25);
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel lblmaxCaracters = new JLabel("(max. 20 caracters)");
		lblmaxCaracters.setHorizontalAlignment(SwingConstants.CENTER);
		lblmaxCaracters.setBounds(12, 62, 137, 15);
		panel.add(lblmaxCaracters);
		
		frmChatConnection.getRootPane().setDefaultButton(btnConnexion);
	}
	
	public JFrame getFrame() {
		return this.frmChatConnection;
	}
	
	public JComboBox getComboBox() {
		return this.comboBox;
	}
	
	public class ConnectListener implements ActionListener {
		private GUI gui;
		
		public ConnectListener(GUI gui) {
			super();
			this.gui = gui;
		}
		
		public void actionPerformed(ActionEvent e) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					String textFieldContent = textField.getText();
					if (textFieldContent.length()>0) {
						try {
							int id = -1;
							id = DataManager.checkUser(textFieldContent);
							if (id!=-1) {
								byte[] bytes = BigInteger.valueOf(gui.comboBox.getSelectedIndex()).toByteArray();
								InetAddress address = InetAddress.getByAddress(bytes);
								controller.connection(id, textFieldContent, address);
								gui.getFrame().setVisible(false);
							}
							else {
								GUI.showError("The login does not exist. Please Sign Up if you do not have an account.");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						GUI.showError("Please enter a login");
					}
				}
			});
		}
	}
	
	public class CreateUserListener implements ActionListener {
		private GUI gui;
		
		public CreateUserListener(GUI gui) {
			super();
			this.gui = gui;
		}
		
		public void actionPerformed(ActionEvent e) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						GUISignup signupWindow = new GUISignup(gui);
						signupWindow.getFrame().setVisible(true);
						gui.getFrame().setVisible(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
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
		JOptionPane.showMessageDialog(null, error, "Erreur", JOptionPane.ERROR_MESSAGE);
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

