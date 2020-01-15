import java.io.*;
import java.net.*;

import java.util.*;

public class ClientTime {

    Socket socket;

    public ClientTime() throws IOException {
        this.socket = new Socket("127.0.0.1",8080);
    }

    public static void main(String[] arg){
        try {
            String message = "";
            Scanner scanIn = new Scanner(System.in);;
            ClientTime C = new ClientTime();
            BufferedReader in = new BufferedReader(new InputStreamReader(C.socket.getInputStream()));
            PrintWriter out = new PrintWriter(C.socket.getOutputStream(),true);
            while (!message.equals("quit")){
                message = scanIn.nextLine();
                out.println(message);
            }
            scanIn.close();
            C.socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}