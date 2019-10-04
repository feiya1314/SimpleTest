package mytest.concurrent.semaphore;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Student implements Runnable {
    private String name;
    private Semaphore semaphore;
    private int type;

    public Student(String name, Semaphore semaphore, int type) {
        this.name = name;
        this.semaphore = semaphore;
        this.type = type;
    }

    @Override
    public void run() {
        switch (type){
            case 0:
                semaphore.acquireUninterruptibly();
                try {
                    Thread.sleep(RandomUtils.nextLong(1000, 3000));
                    System.out.println(name + " get the meat");
                }catch (Exception e){

                }
                semaphore.release();
                break;
            case 1:
                try {
                    if(semaphore.tryAcquire(RandomUtils.nextInt(6000, 16000), TimeUnit.MILLISECONDS)) {
                        Thread.sleep(RandomUtils.nextLong(1000, 3000));
                        semaphore.release();
                        System.out.println(name + " get the meat");
                    }else {
                        System.out.println(name + " didn't get the meat, go back to eat other");
                    }
                }catch (Exception e){

                }
                break;
            case 2:
                try {
                    semaphore.acquire();
                    //进行打饭
                    try {
                        Thread.sleep(RandomUtils.nextLong(1000, 3000));
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    //将打饭机会让后后面的同学
                    semaphore.release();
                    //打到了饭
                    System.out.println(name + " 终于打到了饭.");
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    //被叫去聚餐，不再打饭
                    System.out.println(name + " 全部聚餐，不再打饭.");
                }
                break;
            default:break;

        }
    }
}
