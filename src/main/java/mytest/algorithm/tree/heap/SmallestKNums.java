package mytest.algorithm.tree.heap;

import java.util.PriorityQueue;

/**
 * 设计一个算法，找出数组中最小的k个数。以任意顺序返回这k个数均可。
 *
 * 示例：
 *
 * 输入： arr = [1,3,5,7,2,4,6,8], k = 4
 * 输出： [1,2,3,4]
 * 提示：
 *
 * 0 <= len(arr) <= 100000
 * 0 <= k <= min(100000, len(arr))
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/smallest-k-lcci
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class SmallestKNums {
    public static void main(String[] args) {

    }

    // 使用 最大优先级队列，先添加 k 个元素，以后每次把堆顶的最大值 poll 出去，最终剩下的全是最小的
    public int[] getLeastNumbers(int[] arr, int k) {
        if (k == 0 || arr.length == 0) {
            return new int[0];
        }

        // PriorityQueue默认是一个小顶堆, 实现大根堆需要重写一下比较器。k只是默认大小，如果offer超过k，会扩容
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
