package mytest.concurrent.locktest;

import mytest.concurrent.volatiletest.NumIncrease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public class LockTest {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        ReentrantLock lock = new ReentrantLock();
        StampedLock stampedLock = new StampedLock();
        //readWriteLock.readLock().lock();
        //stampedLock.readLock();
        lock.lock();
        lock.unlock();

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
