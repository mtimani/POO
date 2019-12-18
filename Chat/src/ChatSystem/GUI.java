package ChatSystem;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.math.*;
import javax.swing.*;

public class GUI extends JFrame {

	private JFrame frmChatConnection;
	private static final long serialVersionUID = 1L;
	private static Controller controller;
	private JTextField loginField;
	private JComboBox<Object> comboBox;
	private ArrayList<InetAddress> ipListMachines = new ArrayList<InetAddress>(Controller.getAllIpAndBroadcast().keySet());;
	private volatile InetAddress ipSelected;
	private volatile String login = null;
	private volatile int id = -1;
	private volatile boolean statusConnexion = false;
	

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
		
		comboBox = new JComboBox<Object>(ipListMachines.toArray());
		comboBox.setBounds(12, 12, 340, 25);
		panel.add(comboBox);
		
		JLabel lblTypeYourLogin = new JLabel("Type your login");
		lblTypeYourLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblTypeYourLogin.setBounds(12, 49, 137, 15);
		panel.add(lblTypeYourLogin);
		
		loginField = new JTextField(20);
		loginField.addKeyListener(new KeyAdapter());
		loginField.setBounds(12, 89, 137, 25);
		panel.add(loginField);
		loginField.setColumns(10);
		
		JLabel lblmaxCaracters = new JLabel("(max. 20 characters)");
		lblmaxCaracters.setHorizontalAlignment(SwingConstants.CENTER);
		lblmaxCaracters.setBounds(0, 62, 154, 15);
		panel.add(lblmaxCaracters);
		
		frmChatConnection.getRootPane().setDefaultButton(btnConnexion);
	}
	
	public JFrame getFrame() {
		return this.frmChatConnection;
	}
	
	/**
	 * @return the ipSelected
	 */
	public InetAddress getIpSelected() {
		return ipSelected;
	}

	/**
	 * @param ipSelected the ipSelected to set
	 */
	public void setIpSelected(InetAddress ipSelected) {
		this.ipSelected = ipSelected;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the statusConnexion
	 */
	public boolean isStatusConnexion() {
		return statusConnexion;
	}

	/**
	 * @param statusConnexion the statusConnexion to set
	 */
	public void setStatusConnexion(boolean statusConnexion) {
		this.statusConnexion = statusConnexion;
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
					String loginFieldContent = loginField.getText();
					if (loginFieldContent.length()>0) {
						try {
							int id = -1;
							id = DataManager.checkUser(loginFieldContent);
							if (id!=-1) {
								setId(id);
								setLogin(loginFieldContent);
								setIpSelected((InetAddress) comboBox.getSelectedItem());
								setStatusConnexion(true);
								setVisible(false);
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
						gui.frmChatConnection.setVisible(false);
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
	
	/**
	 * Permet de limiter le nombre de caracteres des zones de texte
	 */
	public class KeyAdapter implements KeyListener {

		/**
		 * Pour gerer la taille de l'username
		 */
		public void keyTyped(KeyEvent e) {
			if (loginField.getText().length() >= 20)
				e.consume();
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}
	
	}
	
}
