package mytest.algorithm.sort;


/**
 * https://leetcode.cn/problems/sort-an-array/
 */
public class SortArray {
    public int[] sortArray(int[] nums) {
        sort(nums, 0, nums.length - 1);
        return nums;
    }


    public static void sort2(int[] nums, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = (left + right) / 2;
        sort2(nums, left, mid);
        sort2(nums, mid + 1, right);
        merge(nums, left, right, mid);

    }

    private static void merge(int[] nums, int left, int right, int mid) {
        if (nums[mid] <= nums[mid + 1]) {
            return;
        }

        // 1378  23489
        int[] temp = new int[right - left + 1];
        int i = left;
        int j = mid + 1;
        int k = 0;
        while (i <= mid && j <= right) {
            if (nums[i] < nums[j]) {
                temp[k] = nums[i];
                i++;
            } else {
                temp[k] = nums[j];
                j++;
            }
            k++;
        }
        while (i <= mid) {
            temp[k] = nums[i];
            k++;
            i++;
        }
        while (j <= right) {
            temp[k] = nums[j];
            k++;
            j++;
        }

        for (int v : temp) {
            nums[left] = v;
            left++;
        }
    }

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

}
