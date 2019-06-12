package mytest.concurrent.synchronizedtest;

import java.util.concurrent.CountDownLatch;

public class SynchronizedTest {

    public static void main(String[] args) {
        Nums nums = new Nums();
        RequestHandler handler1 = new RequestHandler(nums);
        RequestHandler handler2 = new RequestHandler(nums);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        Runnable counter1 = new Counter(10000,handler1,countDownLatch);
        Runnable counter2 = new Counter(10000,handler2,countDownLatch);

        for (int i = 0; i <5 ; i++) {
            new Thread(counter1).start();
        }
        for (int i = 0; i <5 ; i++) {
            new Thread(counter2).start();
        }
        try {
            countDownLatch.await();
        }catch (InterruptedException e)
        {

        }
        handler1.getRequestNum();
        System.out.println("-----------------------------------------");
        handler2.getRequestNum();
    }
}
