import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class ServerTime {

    ServerSocket serverSocket;
    Socket link;

    public ServerTime() throws IOException {
        this.serverSocket = new ServerSocket(1234);
        //this.link = this.serverSocket.accept();
    }

    public static void main(String[] arg){
        try {
            ServerTime S = new ServerTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            S.link = S.serverSocket.accept();
            System.out.println("Le serveur est à l'écoute !");
            BufferedReader in = new BufferedReader(new InputStreamReader(S.link.getInputStream()));
            PrintWriter out = new PrintWriter(S.link.getOutputStream(),true);
            System.out.println("Connexion établie avec " + S.link.getInetAddress());
            System.out.println("Message envoyé : " + date + "\n");
            out.println(date);
            S.link.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
}