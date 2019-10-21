import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class ServerThread {
    public static void main (String[] args) throws IOException {
        ServerSocket m_ServerSocket = new ServerSocket(8080);
        int id = 0;
        while(true) {
            Socket clientSocket = m_ServerSocket.accept();
            ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
            cliThread.start();
        }
    }
}

class ClientServiceThread extends Thread {

    Socket clientSocket;
    int clientID = -1;
    boolean running = true;

    ClientServiceThread(Socket s, int i) {
        clientSocket = s;
        clientID = i;
    }
    
    public void run() {
        System.out.println("Connexion acceptée avec le client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
            while(running){
                String clientCommand = in.readLine();
                System.out.println("Le client a envoyé : " + clientCommand);
                if (clientCommand.equalsIgnoreCase("quit")) {
                    running = false;
                    System.out.println("Arrêt du thread client pour le client : " + clientID);
                } else {
                    out.println(clientCommand);
                    out.flush();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}