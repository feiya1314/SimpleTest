package mytest.concurrent.locktest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition reachThreeCondition = lock.newCondition();
        Condition reachSixCondition  = lock.newCondition();

        final Num num = new Num();

        Thread tha = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程A输出1-3");
                for (int i = 0; i < 3; i++) {
                    System.out.println(num.num);
                    num.num++;
                }
                reachThreeCondition.signal();
            } finally {
                lock.unlock();
            }
            lock.lock();
            try {
                reachSixCondition.await();
                System.out.println("线程A输出4-6");
                for (int i = 0; i < 3; i++) {
                    System.out.println(num.num);
                    num.num++;
                }
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
        });
        Thread threadb =new Thread(()->{
            lock.lock();
            try {
                if (num.num<3) {
                    reachThreeCondition.await();
                }

            } catch (Exception e) {
            }finally {
                lock.unlock();
            }
            lock.lock();
            try {
                System.out.println("线程B输出7-9");
                for (int i = 0; i < 3; i++) {
                    System.out.println(num.num);
                    num.num++;
                }
                reachSixCondition.signal();
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
        });
        threadb.start();
        tha.start();
    }

    static class Num {
        public int num = 1;
    }
}
