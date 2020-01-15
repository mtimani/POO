import java.io.*;
import java.net.*;

public class ClientTime {

    Socket socket;

    public ClientTime() throws IOException {
        this.socket = new Socket("127.0.0.1",1234);
    }

    public static void main(String[] arg){
        try {
            ClientTime C = new ClientTime();
            BufferedReader in = new BufferedReader(new InputStreamReader(C.socket.getInputStream()));
            PrintWriter out = new PrintWriter(C.socket.getOutputStream(),true);
            System.out.println(in.readLine());
            C.socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}