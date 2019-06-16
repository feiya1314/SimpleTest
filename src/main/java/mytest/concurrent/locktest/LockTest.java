package mytest.concurrent.locktest;

import mytest.concurrent.volatiletest.NumIncrease;

import java.util.concurrent.CountDownLatch;

public class LockTest {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        LockNumIncrease numIncrease = new LockNumIncrease(countDownLatch);
        for (int i = 0; i < 10; i++) {
            new Thread(numIncrease).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) { }

        numIncrease.getResult();
    }
}
