package ChatSystem;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class GUIMain {

	private JFrame frmChatSystem;
	private GUI gui;
	
	/**
	 * Create the application.
	 */
	public GUIMain(GUI gui) {
		initialize();
		this.gui = gui;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChatSystem = new JFrame();
		frmChatSystem.setTitle("Chat System");
		frmChatSystem.setBounds(100, 100, 800, 500);
		frmChatSystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatSystem.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		frmChatSystem.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
	}
	
	public JFrame getFrame() {
		return this.frmChatSystem;
	}

}
