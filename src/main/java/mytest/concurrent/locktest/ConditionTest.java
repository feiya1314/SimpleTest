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
                reachThreeCondition.signal();//此时reachThreeCondition 条件已经满足，通知等待reachThreeCondition条件的线程继续执行
            } finally {
                lock.unlock();
            }
            lock.lock();
            try {
                reachSixCondition.await();//await时会释放锁，并阻塞在此,等到reachSixCondition 此条件满足时，使用signal 来取消等待，往下执行
                System.out.println("线程A输出7-9");
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
                while (num.num<3) {   //使用while是因为接收到signal的时候，条件有可能不满足,本例即使使用if也是可以满足的，但是最好用while
                    reachThreeCondition.await();//同样，等待reachThreeCondition 条件满足
                }

            } catch (Exception e) {
            }finally {
                lock.unlock();
            }
            lock.lock();
            try {
                System.out.println("线程B输出4-6");
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
