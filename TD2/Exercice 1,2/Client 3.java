import java.io.*;
import java.net.*;

public class Client {

    Socket socket;

    public Client() throws IOException {
        this.socket = new Socket("127.0.0.1",1234);
    }

    public static void main(String[] arg){
        try {
            Client C = new Client();
            BufferedReader in = new BufferedReader(new InputStreamReader(C.socket.getInputStream()));
            PrintWriter out = new PrintWriter(C.socket.getOutputStream(),true);
            out.println(arg[0]);
            System.out.println(in.readLine());
            C.socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}