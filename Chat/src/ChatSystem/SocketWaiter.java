package ChatSystem;

import java.io.*;
import java.net.*;

/**
 * Permet de se mettre en attente d'une connexion d'un utilisateur distant
 */
public class SocketWaiter extends Thread {

	private ServerSocket tcpServerSocket;
	private Controller controller;
	
	/**
	 * Crée un ServerSocketWaiter 
	 * @param tcpServerSocket ServerSocket à utiliser
	 * @param controller Controlleur de l'application
	 */
	public SocketWaiter(ServerSocket tcpServerSocket, Controller controller) {
		super("SocketWaiter");
		this.tcpServerSocket = tcpServerSocket;
		this.controller = controller;
	}
	
	/**
	 * Thread qui gère la connexion avec des nouveaux utilisateurs
	 */
	@Override
	public void run() {
		Socket tcpSocket;
		try {
			// Cette boucle permet d'être toujours en écoute même après une première connexion
			while(true) {
				// On attent que quelqu'un se connecte
				tcpSocket = tcpServerSocket.accept();
				SocketReader socketReaderInput = new SocketReader("ServerSocketRead", tcpSocket, controller);
				socketReaderInput.start();
				while (socketReaderInput.getGroup() == null);
				SocketWriter socketWriterOutput = new SocketWriter("ServerSocketWriter", tcpSocket, controller, socketReaderInput.getGroup());
				socketWriterOutput.start();
			}
		} catch (IOException e) {
			GUI.showError("Impossible de recevoir les connexions.");
		}
	}
	
}
