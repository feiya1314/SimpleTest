package mytest.interrupt;

import java.util.concurrent.locks.LockSupport;

public class BlockThread implements Runnable{
    private Thread orignalThread;
    private BlockResource resource;
    private int code;

    public BlockThread(Thread orignalThread,BlockResource resource,int code) {
        this.orignalThread = orignalThread;
        this.resource = resource;
        this.code =code;
    }

    @Override
    public void run() {
        if (code ==0){
            getResouceInterrupt();
        }else {
            getResourceSuccess();
        }

        LockSupport.unpark(orignalThread);
    }

    private void getResouceInterrupt()
    {
        try {
            Thread.sleep(10000);
        }catch (InterruptedException e)
        {

        }
        resource.setResource("failed");
        orignalThread.interrupt();
    }
    private void getResourceSuccess(){
        try {
            Thread.sleep(20000);
        }catch (InterruptedException e)
        {

        }
        resource.setResource("success");
    }
}
