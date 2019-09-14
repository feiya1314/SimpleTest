package mytest.thread.pool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

public class MakeMoneyTest implements Runnable {
    @Override
    public void run() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MakeMoneyTask task = new MakeMoneyTask(1000000);
        ForkJoinTask<Integer> result = forkJoinPool.submit(task);
        try {
            do {
                TimeUnit.MILLISECONDS.sleep(5);
                // System.out.println("sleep to waiting for finish");
            } while (!task.isDone());
            forkJoinPool.shutdown();
            System.out.println(result.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
