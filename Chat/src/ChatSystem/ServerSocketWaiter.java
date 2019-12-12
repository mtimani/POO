package ChatSystem;

import java.io.*;
import java.net.*;

/**
 * Permet de se mettre en attente d'une connexion d'un utilisateur distant
 */
public class ServerSocketWaiter extends Thread {

	private ServerSocket serverSocket;
	private Controller controller;
	
	/**
	 * Crée le ServerSocketWaiter 
	 * @param serverSocket ServerSocket à utiliser
	 * @param controller Controlleur de l'application
	 */
	public ServerSocketWaiter(ServerSocket serverSocket, Controller controller) {
		super("ServerSocketWaiter");
		this.serverSocket = serverSocket;
		this.controller = controller;
	}
	
	/**
	 * Thread qui gère la connexion avec des nouveaux utilisateurs
	 */
	@Override
	public void run() {
		
		Socket socket;
		
		try {
		
			// Cette boucle permet d'être toujours en écoute même après une première connexion
			while(true) {
				// On attent que quelqu'un se connecte
				socket = serverSocket.accept();
				SocketReader socketReader = new SocketReader("ServerSocketRead", socket, controller);
				socketReader.start();
				
				while (socketReader.getGroup() == null);
				
				SocketWriter socketWriter = new SocketWriter("ServerSocketWriter", socket, controller, socketReader.getGroup());
				socketWriter.start();
			}
			
		} catch (IOException e) {
			GUI.showError("Impossible de recevoir les connexions.");
		}
		
	}
	
}
