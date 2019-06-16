package mytest.concurrent.volatiletest;

import java.util.concurrent.CountDownLatch;

public class VolatileTest {
    public static void main(String[] args) {

        CountDownLatch countDownLatch = new CountDownLatch(10);
        NumIncrease numIncrease = new NumIncrease(countDownLatch);
        for (int i = 0; i < 10; i++) {
            new Thread(numIncrease).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) { }

        numIncrease.getResult();
    }
}
