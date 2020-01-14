class MyThread extends Thread {
    public MyThread (String s) {
        super(s);
    }

    public void run() {
        System.out.println("Hello, I am " + getName());
    }
}

public class TestThreadMany {
    public static void main (String arg[]) {
        if (arg.length == 1) {

            int nb = Integer.valueOf(arg[0]);

            MyThread[] thread_t;
            thread_t = new MyThread[nb];

            for (int i=0;i<nb;i++) {
                thread_t[i] = new MyThread("Thread #" + (i+1));
                thread_t[i].start();
            }

        }
        else {
            System.out.println("Veuillez saisir un entier en argument !");
        }
    }
}