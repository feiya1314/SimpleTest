package mytest.algorithm.doublepoint;

/**
 * 给定一个增序排列数组 nums ，你需要在 原地 删除重复出现的元素，使得每个元素最多出现两次，返回移除后数组的新长度。
 * 示例 1：
 * //
 * //
 * //输入：nums = [1,1,1,2,2,3]
 * //输出：5, nums = [1,1,2,2,3]
 * //解释：函数应返回新长度 length = 5, 并且原数组的前五个元素被修改为 1, 1, 2, 2, 3 。 你不需要考虑数组中超出新长度后面的元素。
 * //
 * //
 * // 示例 2：
 * //
 * //
 * //输入：nums = [0,0,1,1,1,1,2,3,3]
 * //输出：7, nums = [0,0,1,1,2,3,3]
 * //解释：函数应返回新长度 length = 7, 并且原数组的前五个元素被修改为 0, 0, 1, 1, 2, 3, 3 。 你不需要考虑数组中超出新长度后面
 * //的元素。
 * https://leetcode-cn.com/problems/remove-duplicates-from-sorted-array-ii/
 * @author : yufei
 * @date : 2021/1/20 10:56
 * @description :
 */
public class RemoveDuplicates2 {
    // 一个指针遍历，一个指针记录下一次转移的位置，另外一个count统计重复出现的次数，
    // 当count>2时，是多余的重复项，不需要移动，直接忽略
    public int removeDuplicates(int[] nums) {
        if (nums == null) {
            return 0;
        }
        if (nums.length <= 2) {
            return nums.length;
        }
        // 下一次移动的所在位置
        int index = 1;
        int count = 1;
        for (int i = 1; i < nums.length; i++) {
            // 统计重复的次数，如果不同，则意味着新的数字开始，count重置为1
            if (nums[i] == nums[i - 1]) {
                count++;
            } else {
                count = 1;
            }
            // 小于等于两次的数字需要移动
            if (count <= 2) {
                nums[index] = nums[i];
                index++;
            }
        }
        return index;
    }
}
