package mytest.algorithm.doublepoint;

import java.util.Arrays;

/**
 * //给定一个包含红色、白色和蓝色，一共 n 个元素的数组，原地对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。
 * //
 * // 此题中，我们使用整数 0、 1 和 2 分别表示红色、白色和蓝色。
 * // 示例 1：
 * //输入：nums = [2,0,2,1,1,0]
 * //输出：[0,0,1,1,2,2]
 * // 示例 2：
 * //输入：nums = [2,0,1]
 * //输出：[0,1,2]
 * // 示例 3：
 * //输入：nums = [0]
 * //输出：[0]
 * // 示例 4：
 * //输入：nums = [1]
 * //输出：[1]
 * // 提示：
 * // n == nums.length
 * // 1 <= n <= 300
 * // nums[i] 为 0、1 或 2
 * <p>
 * // 进阶：
 * // 你可以不使用代码库中的排序函数来解决这道题吗？
 * // 你能想出一个仅使用常数空间的一趟扫描算法吗？
 * https://leetcode-cn.com/problems/sort-colors/
 *
 * @author : yufei
 * @date : 2021/1/19 12:32
 * @description :
 */
public class SortColors {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 0};
        new SortColors().sortColors(nums);
        Arrays.stream(nums).forEach(System.out::print);
    }

    // 先遍历一次，把0的放在最前面，然后再遍历一次，把1放在0后面
    public void sortColors(int[] nums) {
        if (nums == null || nums.length < 2) {
            return;
        }
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                swap(nums, index, i);
                index++;
            }
        }

        for (int i = index; i < nums.length; i++) {
            if (nums[i] == 1) {
                swap(nums, index, i);
                index++;
            }
        }
    }

    // 双指针 分别指向头尾
    public void sortColors2(int[] nums) {
        if (nums == null || nums.length < 2) {
            return;
        }

        int left = 0;
        int right = nums.length - 1;
        for (int i = 0; i < right; i++) {
            // 先确保所有的2包括交换过来的2，都放到尾部
            while (i <= right && nums[i] == 2) {
                swap(nums, i, right);
                right--;
            }
            // 如果i为0，交换，因为前面已经判断2，所以交换过来的不会是2
            if (nums[i] == 0) {
                swap(nums, i, left);
                left++;
            }
        }
    }

    private void swap(int[] nums, int left, int right) {
        int temp = nums[left];
        nums[left] = nums[right];
        nums[right] = temp;
    }
}
