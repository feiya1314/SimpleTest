package mytest.concurrent.synchronizedtest;

import java.util.concurrent.CountDownLatch;

public class SynchronizedTest {

    public static void main(String[] args) {
        Nums nums = new Nums();
        RequestHandler handler1 = new RequestHandler(nums);
        RequestHandler handler2 = new RequestHandler(nums);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        Counter counter1 = new Counter(10000,handler1,countDownLatch);
        Counter counter2 = new Counter(10000,handler2,countDownLatch);
        counter1.setAwait(true);

        for (int i = 0; i <5 ; i++) {
            Thread rt = new Thread(counter1);
            rt.setName("my thread count 1 - "+i);
            rt.start();
        }
        for (int i = 0; i <5 ; i++) {
            Thread rt = new Thread(counter2);
            rt.setName("my thread count 2 - "+i);
            rt.start();
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
