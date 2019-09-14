package mytest.thread.pool;

import java.util.concurrent.*;

public class ThreadPoolTest {
    public static void main(String[] args) {
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,);
        new ForkJoinPoolTest().run();
        new MakeMoneyTest().run();

        //ExecutorService service = Executors.newCachedThreadPool();

        //ExecutorService service1 = Executors.newFixedThreadPool(10);
        //ExecutorService service2 = Executors.newSingleThreadExecutor();
        // service3 = Executors.newScheduledThreadPool(5);
        /*service3.scheduleAtFixedRate(Runnable,initialDelay,period ,TimeUnit.SECONDS)
        ScheduledExecutorService service4 = Executors.newSingleThreadScheduledExecutor();
        service4.scheduleWithFixedDelay(Runnable,initialDelay, delay,TimeUnit.SECONDS)
        ExecutorService service5 = Executors.newWorkStealingPool();
        ForkJoinPool service6=(ForkJoinPool) Executors.newWorkStealingPool();
        service5.execute(Runnable);
        service5.submit()
        service6.submit()
        service6.execute(ForkJoinTask);
        service.execute();
        service.submit()*/

        //fork pool test


    }
}
