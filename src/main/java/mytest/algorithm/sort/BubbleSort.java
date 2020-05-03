package mytest.algorithm.sort;


import static mytest.algorithm.Util.print;
import static mytest.algorithm.Util.swap;

public class BubbleSort {
    public static void sort(int[] arr) {
        // 一次遍历后是不是有交换，如果没有交换说明已经排序完成，没必要再继续了

        for (int i = arr.length - 1; i > 0; i--) {
            // 每次循环，重置
            boolean swapped = false;
            for (int j = 0; j < i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            // 没有交换说明已经排序完成，没必要再继续了
            if (!swapped) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 0, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        print(arr);
        sort(arr);
        print(arr);
    }
}
