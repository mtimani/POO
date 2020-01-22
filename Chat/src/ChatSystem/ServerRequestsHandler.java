package ChatSystem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ChatSystem.Controller.ConnectionError;
import ChatSystem.Controller.SendConnectionError;
import ChatSystemServer.ChatServer;
import ChatSystemServer.ChatServer.ServerResponse;

/**
 * Classe chargee de lancer des requetes au serveur
 */
public class ServerRequestsHandler extends TimerTask {
	
	private Controller controller;
	
	public ServerRequestsHandler(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		Gson gsonCommand = new GsonBuilder().setDateFormat("dd/MM/yyyy-hh:mm:ss").create();
		// Creation des donnees utilisateur
		String paramValueUsed = "userdata=" + gsonCommand.toJson(controller.getUser());
		try {
			// Test de la connexion
			if(!Controller.testConnectionServer())
				throw new ConnectionError();
			// Connexion au serveur et traitement de la reponse
			HttpURLConnection httpCon = Controller.sendRequestToServer(ChatSystemServer.ChatServer.USER_CONNECTION_ACTION, paramValueUsed);		
			// On verifie la reponse
			int httpConStatus = httpCon.getResponseCode();
			if(httpConStatus != HttpURLConnection.HTTP_OK) {
				throw new SendConnectionError();
			}
			String jsonResponse = Controller.getResponseContent(httpCon);
			ServerResponse serverResponse = gsonCommand.fromJson(jsonResponse, ServerResponse.class);
			if(serverResponse.getCode() != ChatServer.NO_ERROR) {
				throw new SendConnectionError();
			}
			// On recupere la liste des utilisateurs connectes
			User[] responseUsers = gsonCommand.fromJson(serverResponse.getData(), User[].class);
			ArrayList<User> connectedUsersList = new ArrayList<User>(Arrays.asList(responseUsers));
			controller.receiveConnectedUsersFromServer(connectedUsersList);
		} 
		catch (IOException e) {
			GUI.showError("Une erreur s'est produite dans la decouverte du reseau.");
			System.exit(Controller.EXIT_GET_CONNECTED_USERS);
		} 
		catch (ConnectionError | NumberFormatException e) {
			GUI.showError("Impossible de se connecter au serveur.\nVerifiez la configuration de la connexion ou utilisez le protocole UDP.");
			System.exit(Controller.EXIT_ERROR_SERVER_UNAVAILABLE);
		} 
		catch (JsonSyntaxException e) {
			GUI.showError("Erreur lors de la reception des donnees du serveur.");
			System.exit(Controller.EXIT_GET_CONNECTED_USERS);
		} 
		catch (SendConnectionError e) {
			GUI.showError("Impossible de se connecter au chat.");
			System.exit(Controller.EXIT_ERROR_SEND_CONNECTION);
		}
	}

}
