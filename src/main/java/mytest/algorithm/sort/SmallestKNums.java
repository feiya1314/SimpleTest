package mytest.algorithm.sort;

import java.util.PriorityQueue;

public class SmallestKNums {
    public static void main(String[] args) {

    }

    public int[] getLeastNumbers(int[] arr, int k) {
        if (k == 0 || arr.length == 0) {
            return new int[0];
        }

        // PriorityQueue默认是一个小顶堆, 实现大根堆需要重写一下比较器。
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(k, (o1, o2) -> o2 - o1);
        for (int i = 0; i < k; i++) {
            priorityQueue.offer(arr[i]);
        }
        for (int i = k; i < arr.length; i++) {
            int biggest = priorityQueue.peek();
            if (arr[i] < biggest) {
                priorityQueue.poll();
                priorityQueue.offer(arr[i]);
            }
        }

        int[] res = new int[k];
        int index = 0;
        for (int num : priorityQueue) {
            res[index++] = num;
        }
        return res;
    }
}
