class MyThread extends Thread{ 
    public void run(){ 
        System.out.println("Hi there"); 
    } 
}

public class TestThread0{
    public static void main (String arg[]){ 
        MyThread t1, t2; 
        t1 =new MyThread ();
        t2 =new MyThread (); 
        t1.start();
        t2.start();
    }
}