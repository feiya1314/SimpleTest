package mytest.concurrent.synchronizedtest;

public class RequestHandler {
    private int requestNum=0;
    private int requestNumWithSync=0;
    public void requestCounter(){
        requestNum ++;
    }

    public synchronized void requestCounterWithSync() {
        requestNumWithSync ++;
    }

    public void getRequestNum(){
        System.out.println("requestnum: "+requestNum);
    }

    public void getRequestNumWithSync() {
        System.out.println("requestNumWithSync: "+requestNumWithSync);
    }
}
