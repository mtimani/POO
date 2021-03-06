package ChatSystemServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ChatSystem.User;

/**
 * Servlet implementation class ChatServer
 */
public class ChatServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int TEST_SERVER_ACTION = 0;
	public static final int USER_CONNECTION_ACTION = 1;
	public static final int USER_DISCONNECTION_ACTION = 2;

	// Delai de deconnexion automatique : 5 secondes
	public static final int AUTO_DECONNECTION_DELAY = 5000;
	
	public static final int NO_ERROR = 0;
	public static final int NO_ACTION_ERROR = 1;
	public static final int NO_USER_DATA_ERROR = 2;
	public static final int JSON_FORMAT_ERROR = 10;

	// Liste des utilisateurs connectes sur le serveur
	private ArrayList<User> connectedUsers;
	
	/**
	 * Sous-classe utilisee pour envoyer une reponse du serveur
	 */
	public class ServerResponse {
		private int code;
		private String dataFormat;
		private String data;
		
		public ServerResponse(int code, String dataFormat) {
			this.code = code;
			this.data = null;
			this.dataFormat = dataFormat;
		}
		
		public int getCode() {
			return code;
		}
		
		public void setCode(int code) {
			this.code = code;
		}
		
		public String getDataFormat() {
			return dataFormat;
		}
		
		public void setDataFormat(String dataFormat) {
			this.dataFormat = dataFormat;
		}
		
		public String getData() {
			return data;
		}
		
		public void setData(String data) {
			this.data = data;
		}
	}
	
	/**
	 * Demarrage du serveur
	 */
    public ChatServer() {
        super();
        
        connectedUsers = new ArrayList<User>();
    }

    /**
     * Action a faire lorsqu'on recoit une requete (GET) sur le serveur
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// On renvoie des donnees au format JSON
		response.setContentType("application/json");
		
		ServerResponse serverResponse = new ServerResponse(NO_ERROR, "json");
		
		PrintWriter out = response.getWriter();
		HashMap<String, String> parameters = getParametersMap(request);

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy-hh:mm:ss").create();
		String jsonData;
		
		// Requete de test
		if(parameters.containsKey("test")) {
			serverResponse.setData("Le serveur fonctionnement correctement.");
			jsonData = gson.toJson(serverResponse);
			out.write(jsonData);
		}
		
		// Sinon, la requete doit contenir une action
		else if(parameters.containsKey("action")) {
			
			try {
				int action = Integer.parseInt(parameters.get("action"));
				
				/**
				 * Recoit une indication de presence d'un utilisateur
				 * Met a jour les donnees sur cet utilisateur
				 * et renvoie la liste des autres utilisateurs connectes
				 */
				if(action == USER_CONNECTION_ACTION) {
					
					if(parameters.containsKey("userdata")) {
						
						// Lecture des donnees de l'utilisateur
						User newUser = gson.fromJson(parameters.get("userdata"), User.class);
						
						// Gestion de la deconnexion automatique
						deconnectOldUsers();
						newUser.setLastVisitDate(new Date());
						
						// On se met a la fin (et on n'apparait pas a soi-meme)
						if(connectedUsers.contains(newUser))
							connectedUsers.remove(newUser);
						
						// On renvoie la liste des utilisateurs
						serverResponse.setData(gson.toJson(connectedUsers));

						// Ajout du nouvel utilisateur en dernier
						connectedUsers.add(newUser);
						
					}
					else {
						serverResponse.setCode(NO_USER_DATA_ERROR);
					}

				}
				
				/**
				 * Supprime l'utilisateur de la liste des utilisateurs connectes
				 */
				else if(action == USER_DISCONNECTION_ACTION) {
					
					if(parameters.containsKey("userdata")) {
						
						// Lecture des donnees de l'utilisateur
						User removedUser = gson.fromJson(parameters.get("userdata"), User.class);
						
						// Suppression de l'utilisateur
						if(connectedUsers.contains(removedUser))
							connectedUsers.remove(removedUser);
						
					}
					else {
						serverResponse.setCode(NO_USER_DATA_ERROR);
					}
					
				}
				
			}
			catch (JsonSyntaxException e) {
				serverResponse.setCode(JSON_FORMAT_ERROR);
			}
			finally {
				jsonData = gson.toJson(serverResponse);
				out.write(jsonData);
			}
			
		}
		else {
			serverResponse.setCode(NO_ACTION_ERROR);
			jsonData = gson.toJson(serverResponse);
			out.write(jsonData);
		}
		
	}
	
	/**
	 * Retourne une map des parametres envoyes dans la requete
	 * @param request La requete envoyee au serveur
	 * @return Une map des parametres envoyes dans la requete
	 */
	private HashMap<String, String> getParametersMap(HttpServletRequest request){
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		Enumeration<String> parameterNames = request.getParameterNames();
		
		while(parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String paramValue = "";
			
			for(String paramVal : request.getParameterValues(paramName))
				paramValue += paramVal;
			
			map.put(paramName, paramValue);
		}
		
		return map;
	}
	
	/**
	 * Permet de supprimer les utilisateurs qui n'ont pas envoye d'informations depuis trop longtemps
	 */
	private void deconnectOldUsers() {
	
		Date currentTime = new Date();
		ArrayList<User> stillConnectedUsers = new ArrayList<User>();
		
		for(User u : connectedUsers) {
			if(currentTime.getTime() - u.getLastVisitDate().getTime() < AUTO_DECONNECTION_DELAY)
				stillConnectedUsers.add(u);
		}
		
		connectedUsers = stillConnectedUsers;
	}

	/**
	 * Permet de traiter une requete POST
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// On renvoie sur une requete GET
		doGet(request, response);
	}

}
