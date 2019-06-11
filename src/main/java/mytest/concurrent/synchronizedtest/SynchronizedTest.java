package mytest.concurrent.synchronizedtest;

import java.util.concurrent.CountDownLatch;

public class SynchronizedTest {

    public static void main(String[] args) {
        RequestHandler handler = new RequestHandler();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        Runnable counter = new Counter(10000,handler,countDownLatch);

        for (int i = 0; i <10 ; i++) {
            new Thread(counter).start();
        }
        try {
            countDownLatch.await();
        }catch (InterruptedException e)
        {

        }
        handler.getRequestNum();
        handler.getRequestNumWithSync();
    }
}
