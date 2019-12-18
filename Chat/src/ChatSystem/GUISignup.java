package ChatSystem;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;


public class GUISignup {

	private JFrame frmChatSignup;
	private JTextField textField;
	private GUI gui;

	public GUISignup(GUI gui) {
		initialize();
		this.gui = gui;
	}

	private void initialize() {
		frmChatSignup = new JFrame();
		frmChatSignup.setTitle("Chat Sign Up");
		frmChatSignup.setBounds(100, 100, 476, 131);
		frmChatSignup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatSignup.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		frmChatSignup.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblLogin = new JLabel("login (max. 20 characters)");
		lblLogin.setBounds(12, 29, 208, 15);
		panel.add(lblLogin);
		
		textField = new JTextField(20);
		textField.addKeyListener(new KeyAdapter());
		textField.setBounds(238, 27, 224, 19);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnSignup = new JButton("Sign Up");
		btnSignup.setBounds(174, 65, 114, 25);
		btnSignup.addActionListener(new CreateUserListener(this));
		panel.add(btnSignup);
		
		frmChatSignup.getRootPane().setDefaultButton(btnSignup);
	}

	public JFrame getFrame() {
		return this.frmChatSignup;
	}
	
	public GUI getGUI() {
		return this.gui;
	}
	
	public class CreateUserListener implements ActionListener {
		private GUISignup guiSignup;
		
		public CreateUserListener(GUISignup guiSignup) {
			super();
			this.guiSignup = guiSignup;
		}
		
		public void actionPerformed(ActionEvent e) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					String textFieldContent = textField.getText();
					if (textFieldContent.length()>20) {
						GUI.showError("Your login must not be longer than 20 characters.");
					}
					else if (textFieldContent.length()==0){
						GUI.showError("Please enter a username");
					}
					else {
						try {
							DataManager.createUser(textFieldContent);
							getGUI().getFrame().setVisible(true);
							guiSignup.getFrame().setVisible(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	public class KeyAdapter implements KeyListener {

		/**
		 * Pour gerer la taille de l'username
		 */
		public void keyTyped(KeyEvent e) {
			if (textField.getText().length() >= 20)
				e.consume();
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}
	
	}
}
