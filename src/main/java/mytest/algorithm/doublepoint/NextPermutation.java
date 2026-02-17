package mytest.algorithm.doublepoint;

/**
 * https://leetcode.cn/problems/next-permutation/solutions/80560/xia-yi-ge-pai-lie-suan-fa-xiang-jie-si-lu-tui-dao-
 */
public class NextPermutation {
    public static void main(String[] args) {
        nextPermutation(new int[]{1, 2, 3});
    }

    public static void nextPermutation(int[] nums) {
        if (nums == null || nums.length < 2) {
            return;
        }

        // 1、先查找从后往前第一组升序的两个值，nums[i] < nums[j]的
        int j = nums.length - 1;
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[j]) {
            i--;
            j--;
        }

        // 边界情况是 i小于0，表示从后往前一个都没有升序,i == 0时，恰好第一组就是升序的两个数
        if (i >= 0) {
            // 从j后面找到第一个大于nums[i]的，由于后面的都是降序，
            // 例如 5 7 6 4， 下一个序列的5 需要替换成6，才有可能最接近升序，
            // 如果是5 7 4 2，只能7 替换为5，不然没有比5 大的值
            int k = nums.length - 1;
            while (k < nums.length && k >= j) {
                if (nums[k] > nums[i]) {
                    break;
                }
                k--;
            }
            // 找到第一个大于j的值后，交换位置
            int temp = nums[i];
            nums[i] = nums[k];
            nums[k] = temp;
        }

        // 从j处开始升序排列，因为此前是降序的，并且i已经更换了，后续只能升序才是字典序
        int r = nums.length - 1;
        while (r > j) {
            int temp = nums[r];
            nums[r] = nums[j];
            nums[j] = temp;
            r--;
            j++;
        }
    }
}
