package mytest.concurrent.synchronizedtest;

import java.util.concurrent.CountDownLatch;

public class Counter implements Runnable {

    private int requestTime;
    private RequestHandler requestHandler;
    private CountDownLatch countDownLatch;

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
        }
        countDownLatch.countDown();
    }
}
