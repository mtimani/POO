import java.io.*;
import java.net.*;

public class Server {

    ServerSocket serverSocket;
    Socket link;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(1234);
        //this.link = this.serverSocket.accept();
    }

    public static void main(String[] arg){
        try {
            Server S = new Server();
            String message = "";
            Integer i = 0;
            while(i<10){
                i++;
                message = "";
                S.link = S.serverSocket.accept();
                System.out.println("Le serveur est à l'écoute !");
                BufferedReader in = new BufferedReader(new InputStreamReader(S.link.getInputStream()));
                PrintWriter out = new PrintWriter(S.link.getOutputStream(),true);
                message = in.readLine();
                System.out.println("Connexion établie avec " + S.link.getInetAddress());
                System.out.println("Chaîne reçue : " + message + "\n");
                out.println(message);
            }
            S.link.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
}