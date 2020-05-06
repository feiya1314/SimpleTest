package mytest.algorithm.printinturns;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程交替打印奇数和偶数
 * 类似问题延申，三个线程交替打印 A B C，可以使用多个 condition，一个线程等待 BA
 */
public class PrintOddEvenInTurn implements Runnable {
    private static final byte[] myTurn = new byte[0];
    private int num = 0;
    private Lock lock = new ReentrantLock();
    private Condition run = lock.newCondition();
    private Condition a = lock.newCondition();
    private Condition b = lock.newCondition();
    private Condition c = lock.newCondition();
    private  int which = 0;

    @Override
    public void run() {
        Runnable runnable = () -> {
            while (num <= 100) {
                try {
                    synchronized (myTurn) {
                        System.out.println(num);
                        num += 1;
                        myTurn.notify();
                        if (num <= 100) {
                            myTurn.wait();
                        }
                    }
                } catch (Exception e) {

                }
            }
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
    }

    public void second() {
        Runnable runnable = () -> {
            while (num <= 100) {
                try {
                    lock.lock();
                    System.out.println(num);
                    num += 1;
                    run.signal();
                    if (num <= 100) {
                        run.await();
                    }

                } catch (Exception ignored) {

                } finally {
                    lock.unlock();
                }
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();

    }

    /* 也可以使用三个 condition 去定向指定唤醒某个线程 ，但还是需要 which 这个变量，
       不然刚开始时，A执行完之后，BC并没有处于await状态，可能C先抢占到锁。
    */
    public void third() {
        Runnable aRunnable = () -> {
            while (num <= 20) {
                try {
                    lock.lock();
                    if (which ==0) {
                        System.out.println("A");
                        which++;
                    }
                    if (which ==1) {
                        System.out.println("B");
                        which++;
                    }
                    if (which ==2) {
                        System.out.println("C");
                        which=0;
                    }

                    num++;
                    run.signalAll();
                    if (num > 20) {
                        break;
                    }
                    run.await();
                    Thread.sleep(500);
                } catch (Exception ignored) {

                } finally {
                    lock.unlock();
                }
            }
        };


        new Thread(aRunnable).start();
        new Thread(aRunnable).start();
        new Thread(aRunnable).start();
    }

    public static void main(String[] args) {
        //new PrintOddEvenInTurn().run();
        //new PrintOddEvenInTurn().second();
        new PrintOddEvenInTurn().third();
    }
}
