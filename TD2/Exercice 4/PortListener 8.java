import java.net.*;

public class PortListener {

    public static void main (String[] args) {
        Socket sock;
        for (int i=0;i<1025;i++){
            try {
                sock = new Socket("localhost",(i+1));
                System.out.println("Port numéro " + (i+1) + " est utilisé \n");
            }
            catch (Exception e){}
        }
    }

}