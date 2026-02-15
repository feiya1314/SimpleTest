package mytest.algorithm.doublepoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * /给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有和为 0 且不重
 * //复的三元组。
 * //
 * // 注意：答案中不可以包含重复的三元组。
 * // 示例 1：
 * //输入：nums = [-1,0,1,2,-1,-4]
 * //输出：[[-1,-1,2],[-1,0,1]]
 * //
 * //
 * // 示例 2：
 * //
 * //
 * //输入：nums = []
 * //输出：[]
 * //
 * //
 * // 示例 3：
 * //
 * //
 * //输入：nums = [0]
 * //输出：[]
 * <p>
 * // 0 <= nums.length <= 3000
 * // -105 <= nums[i] <= 105
 * //
 * // Related Topics 数组 双指针
 * <p>
 * https://leetcode-cn.com/problems/3sum/
 *
 * @author : yufei
 * @date : 2021/1/15 14:11
 * @description :
 */
public class ThreeSum {
    public static void main(String[] args) {
        System.out.println(threeSum(new int[]{-4, -4 ,-1 ,1 ,1 ,2 ,2 ,5 ,8 ,10}));
    }
    // 先固定首位，排序，然后后面两位双指针遍历
    // 由于题中说，不重复，利用排序避免重复答案，排序后，循环找后面两个值和当前位置的和为0的两个，后面两个值寻找时，
    // 只需要找之和等于特定值的两个数（-num[i]），遍历时，如果暴力遍历，复杂度会比较高，
    // 由于已经排序，可以从首尾处开始找，如果两数之和大于目标值，则尾指针前移，否则头指针后移。
    // 由于有可能存在重复，1、最外层的遍历时，值相同的需要跳过 2、内层双指针遍历时，左指针或者右指针值相同的也是重复的需要跳过
    public static List<List<Integer>> threeSum(int[] nums) {
        if (nums == null || nums.length < 3) {
            return Collections.emptyList();
        }
        List<List<Integer>> results = new ArrayList<>();
        Arrays.sort(nums);

        // 只需要遍历到 -2 处，后面的内存循环已经不够双指针了，不够两个元素了
        for (int i = 0; i < nums.length - 2; i++) {
            // 另外如果当前值大于0，由于已经排序，表示后面的值肯定会大于0，是不可能有解的,直接结束
            // 如果当前值和后面两值和大于0，后面只会更大，不可能有解==0了，也不用遍历，直接结束了
            if (nums[i] > 0 || nums[i] + nums[i + 1] + nums[i + 2] > 0) {
                break;
            }
            // 外层遍历时，和前值相同，表示重复 例如
            // -4 -4 -1 1 1 2 2 5 8 10 第二的 -4  遍历的集合是 -1 1 1 2 2 5 8 10，
            // 是属于第一个-4 的子集，所以可以跳过第二个 -4
            if (i > 0 && (nums[i] == nums[i - 1])) {
                continue;
            }
            // 当前值加最后两位值最大的值，也小于0，后续遍历肯定更小，不可能有值
            if (nums[i] + nums[nums.length - 1] + nums[nums.length - 2] < 0) {
                continue;
            }
            // 开始内层循环
            int l = i + 1;
            int r = nums.length - 1;
            int target = -nums[i];
            while (l < r) {
                // 和左边上一个值相同，重复了，跳过，左指针后移
                if (l > (i + 1) && nums[l] == nums[l - 1]) {
                    l++;
                    continue;
                }
                // 和右边上一个值相同，重复了，跳过，右指针前移
                if (r < nums.length - 1 && nums[r] == nums[r + 1]) {
                    r--;
                    continue;
                }

                int sum = nums[l] + nums[r];
                // 只相等，则找到解，左右指针移动，同时移动，因为只移动一个，另一个值的解必然重复了
                if (sum == target) {
                    List<Integer> result = new ArrayList<>();
                    result.add(nums[i]);
                    result.add(nums[l]);
                    result.add(nums[r]);

                    results.add(result);
                    l++;
                    r--;
                    continue;
                }
                // 值小，左边指针移动，否则右边指针移动
                if (sum < target) {
                    l++;
                } else {
                    r--;
                }
            }
        }
        return results;
    }

    public List<List<Integer>> threeSum2(int[] nums) {
        if (nums == null || nums.length < 3) {
            return Collections.emptyList();
        }
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            if (nums[i] > 0 || nums[i] + nums[i + 1] + nums[i + 2] > 0) {
                break;
            }
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            // 当前值加最后两位值最大的值，也小于0，后续遍历肯定更小，不可能有值
            if (nums[i] + nums[nums.length - 1] + nums[nums.length - 2] < 0) {
                continue;
            }
            int left = i + 1;
            int right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    while (left < right && nums[left - 1] == nums[left]) {
                        left++;
                    }
                    right--;
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else if (sum > 0) {
                    right--;
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else {
                    left++;
                    while (left < right && nums[left - 1] == nums[left]) {
                        left++;
                    }
                }
            }
        }
        return result;
    }
}
