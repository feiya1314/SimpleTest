package mytest.algorithm.sort;

import mytest.algorithm.Util;

/**
 * 归并排序
 */
public class MergeSort {
    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 0, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        Util.print(arr);
        sort(arr, 0, arr.length - 1);
        Util.print(arr);
    }

    public static void sort(int[] arr, int low, int high) {
        // 只有一个元素时不用比较，直接返回
        if (low >= high) {
            return;
        }

        // 数组中位数
        int mid = (low + high) / 2;

        sort(arr, low, mid);
        sort(arr, mid + 1, high);
        //合并两个有序数组
        merge(arr, low, mid, high);
    }

    private static void merge(int[] arr, int low, int mid, int high) {
        // 如果左边的最大值小于 右边的最小值，则不用合并，已经是有序的了
        if (arr[mid] <= arr[mid + 1]) {
            return;
        }
        // 构造一个数组，存放合并后的数据
        int[] temp = new int[high - low + 1];
        int i = low;
        int j = mid + 1;
        int k = 0;

        // 小的数放到temp中，此时可能一个数组还没有完全放到 temp 中就已经退出了
        while (i <= mid && j <= high) {
            if (arr[i] < arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        // 将剩余的也放到剩余数组中
        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        while (j <= high) {
            temp[k++] = arr[j++];
        }

        // 将临时数据放入原始数组
        for (int v : temp) {
            arr[low++] = v;
        }
    }
}
