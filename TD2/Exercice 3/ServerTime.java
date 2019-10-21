import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.time.*;
import java.text.*;

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
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            S.link = S.serverSocket.accept();
            System.out.println("Le serveur est à l'écoute !");
            BufferedReader in = new BufferedReader(new InputStreamReader(S.link.getInputStream()));
            PrintWriter out = new PrintWriter(S.link.getOutputStream(),true);
            System.out.println("Connexion établie avec " + S.link.getInetAddress());
            System.out.println("Message envoyé : " + timeStamp + "\n");
            out.println(timeStamp);
            S.link.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
}