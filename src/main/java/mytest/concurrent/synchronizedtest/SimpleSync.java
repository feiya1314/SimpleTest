package mytest.concurrent.synchronizedtest;

public class SimpleSync {
    public void syncBlock(){
        synchronized (this){
            System.out.println("hello block");
        }
    }
    public synchronized void syncMethod(){
        System.out.println("hello method");
    }

    public static void main(String[] args) {

    }
}
