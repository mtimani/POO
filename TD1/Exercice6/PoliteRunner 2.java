import java.util.Timer;
import java.lang.*;

public class PoliteRunner extends Thread { 
    
    private int tick = 1;
    private int num;

    public PoliteRunner(int num) {
        this.num = num;
    }

    public void run() {
        while (tick < 400000) {
            tick++;
            if ((tick % 50000) == 0) {
                System.out.println("Thread #" + num + ", tick = " + tick);
            }
            Thread.yield();
        }
    }
}
