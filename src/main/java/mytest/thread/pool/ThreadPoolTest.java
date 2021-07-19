package mytest.thread.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.UncaughtExceptionHandlers;

import java.util.concurrent.*;

public class ThreadPoolTest {
    public static void main(String[] args) {
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,);
      /*  new ForkJoinPoolTest().run();
        new MakeMoneyTest().run();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("calculate task thread-%s")
                .setUncaughtExceptionHandler(UncaughtExceptionHandlers.systemExit())
                .build();*/

        //ExecutorService service = Executors.newCachedThreadPool();

        ExecutorService service1 = Executors.newFixedThreadPool(10);
        service1.execute(()->{
            System.out.println(11111);
        });
        //ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,);
        //ExecutorService service2 = Executors.newSingleThreadExecutor();
        ExecutorService service3 = Executors.newScheduledThreadPool(5);
        service3.execute(()->{

        });
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
