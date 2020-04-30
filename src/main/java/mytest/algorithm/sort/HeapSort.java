package mytest.algorithm.sort;

import mytest.structures.heap.Heap;

public class HeapSort {
    public static void sort(int[] arr) {
        sort(arr, true);
    }

    public static void sort(int[] arr, boolean bigTopHeap) {
        int length = arr.length;
        if (bigTopHeap) {
            for (int i = length - 1; i > 0; i--) {
                Heap.constructBigTopHeap(arr, i + 1);
                Heap.swap(arr, 0, i);
            }
            return;
        }
        for (int i = length - 1; i > 0; i--) {
            Heap.constructSmallTopHeap(arr, i + 1);
            Heap.swap(arr, 0, i);
        }
    }

    public static void print(int[] arr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int value : arr) {
            stringBuilder.append(value).append(",");
        }
        System.out.println(stringBuilder.toString());
    }

    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 0, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        print(arr);
        sort(arr);
        print(arr);
        sort(arr, false);
        print(arr);
    }
}
