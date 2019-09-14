package mytest.thread.pool;

import java.util.concurrent.RecursiveTask;

public class AddTask extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 20;
    private int[] source;
    private int start;
    private int end;

    public AddTask(int[] source, int start, int end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        if (end - start < THRESHOLD) {
            for (int i = start; i < end; i++) {
                sum += source[i];
            }
            return sum;
        }
        int middle = (start + end) / 2;
        AddTask left = new AddTask(source, start, middle);
        AddTask right = new AddTask(source, middle, end);

        left.fork();
        right.fork();
        sum = left.join() + right.join();

        return sum;
    }
}
