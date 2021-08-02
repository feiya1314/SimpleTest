package mytest.interrupt;

import java.util.concurrent.locks.LockSupport;

public class BlockTask {
    private BlockResource resource = new BlockResource();
    public String getResource(int code) throws InterruptedException{
        if (Thread.interrupted())
            throw new InterruptedException();
        System.out.println("start get resource");

        return doGetResource(code);
    }

    private String doGetResource(int code) throws InterruptedException{
        new Thread(new BlockThread(Thread.currentThread(),resource,code)).start();
        if (parkAndCheckInterrupt()){
            throw new InterruptedException();
        }
        return resource.getResource();
    }

    private boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
}
