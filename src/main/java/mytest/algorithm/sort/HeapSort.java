package mytest.algorithm.sort;

import mytest.algorithm.Util;
import mytest.structures.heap.Heap;

public class HeapSort {
    public static void sort(int[] arr) {
        sort(arr, true);
    }

    public static void sort(int[] arr, boolean bigTopHeap) {
        int length = arr.length;
        if (bigTopHeap) {
            for (int i = length - 1; i > 0; i--) {
                // 调整大顶堆，使最大值放到 0 坐标处
                Heap.constructBigTopHeap(arr, i + 1);

                // 把最大值交换到末尾
                Heap.swap(arr, 0, i);
            }
            return;
        }
        for (int i = length - 1; i > 0; i--) {
            Heap.constructSmallTopHeap(arr, i + 1);
            Heap.swap(arr, 0, i);
        }
    }


    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 0, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        Util.print(arr);
        sort(arr);
        Util.print(arr);
        sort(arr, false);
        Util.print(arr);
    }
}
