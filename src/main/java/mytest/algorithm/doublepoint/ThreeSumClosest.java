package mytest.algorithm.doublepoint;

import java.util.Arrays;

/**
 * 给定一个包括 n 个整数的数组 nums 和 一个目标值 target。找出 nums 中的三个整数，使得它们的和与 target 最接近。返回这三个数的和。假定每组输入只存在唯一答案。
 * <p>
 *  
 * <p>
 * 示例：
 * <p>
 * 输入：nums = [-1,2,1,-4], target = 1
 * 输出：2
 * 解释：与 target 最接近的和是 2 (-1 + 2 + 1 = 2) 。
 *  
 * <p>
 * 提示：
 * <p>
 * 3 <= nums.length <= 10^3
 * -10^3 <= nums[i] <= 10^3
 * -10^4 <= target <= 10^4
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/3sum-closest
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/1/17
 * @description :
 */
public class ThreeSumClosest {
    public static void main(String[] args) {
        System.out.println(new ThreeSumClosest().threeSumClosest(new int[]{-1, 2, 1, -4}, 1));
    }

    // 排序后，固定第一个数字，然后双指针从头和尾开始遍历，
    // 对于等于target 的，则直接返回,否则，更新最接近target的值
    // 如果sum < target 则左指针右移，否则右指针左移
    public int threeSumClosest(int[] nums, int target) {
        int closest = Integer.MAX_VALUE;
        int closestSum = 0;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            // 剔除重复的
            if (i > 0 && nums[i - 1] == nums[i]) {
                continue;
            }
            int check = nums[i] + nums[i + 1] + nums[i + 2];
            // 后面肯定比check更大
            if (check >= target) {
                if (Math.abs(check - target) < closest) {
                    return check;
                }
                return closestSum;
            }

            int left = i + 1;
            int right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == target) {
                    return target;
                }
                int cur = Math.abs(sum - target);
                if (cur < closest) {
                    closest = cur;
                    closestSum = sum;
                }
                // 移动指针到和上次不同的位置
                if (sum < target) {
                    left++;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                } else {
                    right--;
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                }
            }
        }
        return closestSum;
    }
}
