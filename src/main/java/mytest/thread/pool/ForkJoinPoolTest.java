package mytest.thread.pool;

import com.google.common.util.concurrent.Uninterruptibles;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ForkJoinPoolTest implements Runnable{

    @Override
    public void run(){
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Random random = new Random();
        int[] source = new int[200];
        int sum = 0;
        for (int i = 0, len = source.length; i < len; i++) {
            source[i] = random.nextInt(20);
            sum += source[i];
        }
        System.out.println("total : " + sum);

        ForkJoinTask<Integer> result = forkJoinPool.submit(new AddTask(source, 0, source.length));
        try {
            System.out.println("total : " + Uninterruptibles.getUninterruptibly(result));
        }catch (ExecutionException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Random random = new Random();
        int[] source = new int[200];
        int sum = 0;
        for (int i = 0, len = source.length; i < len; i++) {
            source[i] = random.nextInt(20);
            sum += source[i];
        }
        System.out.println("total : " + sum);

        ForkJoinTask<Integer> result = forkJoinPool.submit(new AddTask(source, 0, source.length));
        System.out.println("total : " + result.get());
    }
}
