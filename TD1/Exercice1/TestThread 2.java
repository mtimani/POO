class MyThread extends Thread {
    public MyThread (String s) {
        super(s);
    }

    public void run() {
        System.out.println("Hello, I am " + getName());
    }
}

public class TestThread {
    public static void main (String arg[]) {
        MyThread t1, t2;

        t1 = new MyThread("Thread #1");
        t2 = new MyThread("Thread #2");

        t2.start();
        t1.start();
    }
}