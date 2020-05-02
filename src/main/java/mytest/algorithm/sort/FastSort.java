package mytest.algorithm.sort;

import mytest.algorithm.Util;

public class FastSort {

    /**
     * 快速排序
     * @param arr   原数组
     * @param left  数组开始下标（0）
     * @param right 数组结束下标（length - 1）
     */
    public static void sort(int[] arr, int left, int right) {
        if (right - left < 1) {
            return;
        }
        int base = arr[left];
        int i = left + 1, j = right;
        while (i <= j) {
            // 如果最左边的比最右边的大，就交换，继续下一次比较
            if (arr[i] > base && arr[j] < base) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
                continue;
            }
            // 左边的小于或等于基数时，往后移，比较下一个数字
            if (arr[i] <= base) {
                i++;
            }
            // 右边的是大于等于基数时，往前移
            if (arr[j] >= base) {
                j--;
            }
        }
        // 排序完成后，由于上面循环中 j--，此时 j 位置左数组最后一个，将基数和 j 的交换，
        // 此时 base左边都小于等于它，右边都大于等于它
        arr[left] = arr[j];
        arr[j] = base;
        // 继续快速排序 base 左侧的
        sort(arr, left, j - 1);
        // 继续快速排序 base 右侧的
        sort(arr, j + 1, right);
    }

    public static void main(String[] args) {
        //int[] arr = new int[]{5, 2, 6, 0, 34, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};  //3  5  1  6
        int[] arr = new int[]{5,  6, 1, 3};
        Util.print(arr);

        sort(arr, 0, arr.length - 1);
        Util.print(arr);
    }
}
