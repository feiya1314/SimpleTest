package mytest.concurrent.synchronizedtest;

import java.util.concurrent.CountDownLatch;

public class Counter implements Runnable {

    private int requestTime;
    private RequestHandler requestHandler;
    private CountDownLatch countDownLatch;
    private boolean await = false;

    public Counter(int requestTime, RequestHandler requestHandler, CountDownLatch countDownLatch) {
        this.requestTime = requestTime;
        this.requestHandler = requestHandler;
        this.countDownLatch=countDownLatch;
    }

    @Override
    public void run() {
        for (int i = 0; i <requestTime ; i++) {
            requestHandler.requestCounter();
            requestHandler.requestCounterWithSync();
            requestHandler.requestNumWithStaticSyncInc();
            requestHandler.requestNumWithClassSyncInc();
            requestHandler.requestNumWithBlockSyncInc();
            requestHandler.requestNumWithStaticVar();
        }

        countDownLatch.countDown();
        //System.out.println(Thread.currentThread().getName() + " done");
        if (await) {
            System.out.println(Thread.currentThread().getName() + " done ,now wait to end");
            try {
                countDownLatch.await();
                Thread.sleep(2000);
                System.out.println("finish countDownLatch");
            } catch (InterruptedException e) {
            }
        }
        try {
            Thread.sleep(5000);
        }catch (Exception e){

        }
        System.out.println(Thread.currentThread().getName() + "  finally done");
    }

    public void setAwait(boolean await) {
        this.await = await;
    }
}
