package mytest.concurrent.synchronizedtest;

public class RequestHandler {

    private static Nums nums;
    private static Byte[] lock = new Byte[0];

    public RequestHandler(Nums nums) {
        this.nums = nums;
    }

    public void requestCounter(){
        nums.requestNumInc();
    }

    public synchronized void requestCounterWithSync() {
        nums.requestNumWithSyncInc();
    }

    public static synchronized  void requestNumWithStaticSyncInc() {
        nums.requestNumWithStaticSyncInc();
    }
    public void requestNumWithBlockSyncInc() {
        synchronized(this) {
            nums.requestNumWithStaticBlockSyncInc();
        }
    }
    public void requestNumWithStaticVar() {
        synchronized(RequestHandler.class) {
            nums.requestNumWithStaticVar();
        }
    }

    public void requestNumWithClassSyncInc(){
        synchronized (RequestHandler.class) {
            nums.requestNumWithClassSyncInc();
        }
    }

    public void getRequestNum(){
        nums.printNum();
    }

}
