package mytest.concurrent.locktest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockNumIncrease implements Runnable {
    private int num = 0;
    private AtomicInteger atomicNum = new AtomicInteger(0);
    private CountDownLatch countDownLatch;
    Lock lock = new ReentrantLock();

    public LockNumIncrease(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            try {
                lock.lock();
                num++;
            }finally {
                lock.unlock();
            }
            atomicNum.incrementAndGet();
        }
        countDownLatch.countDown();
    }

    void getResult() {
        System.out.println("num : " + num);
        System.out.println("atomicNum : " + atomicNum.get());
    }
}
