package mytest.algorithm.sort;

import mytest.algorithm.Util;

public class FastSort {

    /**
     * 快速排序 主要思想是，找到一个基准值，将小于该值的放到左边，大于该值的放到右边，然后对左右两个全部再执行相同的方式
     *
     * @param arr   原数组
     * @param left  数组开始下标（0）
     * @param right 数组结束下标（length - 1）
     */
    public static void sort(int[] arr, int left, int right) {
        // 相等时，无需在比较了
        if (left >= right) {
            return;
        }
        int base = arr[left];
        int i = left + 1, j = right;
        // 流程：找到左侧比base值大，同时右侧比base值小的，交换位置 注意相遇时 i==j 时
        // 如果该值小于等于 base，则将该位置和base交换，如果大于等于base时，将左侧的和base交换位置
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


    public static void sort2(int[] nums, int left, int right) {
        if (left >= right) {
            return;
        }
        int base = nums[left];
        int i = left;
        int j = right;
        // 整体思路，使用填坑法，左侧第一个为基准值，需要从右侧找到小于基准值的值，填入坑位，
        // 此时右侧的位置成为新的坑位，需要从左侧找大于基准值的值，填入新的坑位，
        // 当i = j 时，剩最后一个位置，这时这里需要填入base值，此时左侧都是<=base 右侧都是>=base
        while (i < j) {
            while (i < j && nums[j] >= base) {
                j--;
            }
            if (i < j) {
                nums[i] = nums[j];
                i++;
            }

            while (i < j && nums[i] <= base) {
                i++;
            }
            if (i < j) {
                nums[j] = nums[i];
                j--;
            }

        }
        nums[j] = base;
        sort2(nums, left, i - 1);
        sort2(nums, i + 1, right);
    }

    public static void main(String[] args) {
        //int[] arr = new int[]{5, 2, 6, 0, 34, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};  //3  5  1  6
        int[] arr = new int[]{5,  6, 1, 3};
        Util.print(arr);

        sort(arr, 0, arr.length - 1);
        Util.print(arr);
    }
}
