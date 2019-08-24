package mytest.concurrent.volatiletest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class NumIncrease implements Runnable{
    private volatile int num=0;
    private AtomicInteger atomicNum = new AtomicInteger(0);
    private CountDownLatch countDownLatch;

    public NumIncrease(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        for (int i = 0; i <10000 ; i++) {
            num++;
            atomicNum.incrementAndGet();
        }
        countDownLatch.countDown();
    }

    void getResult (){
        System.out.println("volatile num : "+num);
        System.out.println("atomicNum : "+atomicNum.get());
    }
}
