package mytest.concurrent.synchronizedtest;

public class RequestHandler {

    private static Nums nums;

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
    public void requestNumWithStaticBlockClassSyncInc() {
        synchronized(RequestHandler.class) {
            nums.requestNumWithStaticBlockClassSyncInc();
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
