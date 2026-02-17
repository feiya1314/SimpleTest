package mytest.algorithm.doublepoint;

/**
 * 给定一个排序数组，你需要在 原地 删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。
 * <p>
 * 不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * 给定数组 nums = [1,1,2],
 * <p>
 * 函数应该返回新的长度 2, 并且原数组 nums 的前两个元素被修改为 1, 2。
 * <p>
 * 你不需要考虑数组中超出新长度后面的元素。
 * 示例 2:
 * <p>
 * 给定 nums = [0,0,1,1,1,2,2,3,3,4],
 * <p>
 * 函数应该返回新的长度 5, 并且原数组 nums 的前五个元素被修改为 0, 1, 2, 3, 4。
 * <p>
 * 你不需要考虑数组中超出新长度后面的元素。
 *  
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-duplicates-from-sorted-array
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/1/17
 * @description :
 */
public class RemoveDuplicates {
    // 一个指针记录不重复的需要移动的元素，遇到不重复的元素时，指针向前移动，重复的元素，指针不动
    // 遇到不重复的元素，添加到指针位置
    public int removeDuplicates(int[] nums) {
        if (nums == null) {
            return 0;
        }
        if (nums.length < 2) {
            return nums.length;
        }
        int l = 0;
        int r = 1;
        // 左指针记录不重复的元素最后的位置
        while (r < nums.length) {
            // 如果值不同，则移动左指针，表示最新的和之前不同的值，把当前右指针的不同的值，保存到该位置
            if (nums[r] != nums[l]) {
                l++;
                nums[l] = nums[r];
            }
            // 如果值相等，则右指针继续往下走，直到找到不同的
            r++;
        }

        return l + 1;
    }

    public int removeDuplicates2(int[] nums) {
        if (nums == null) {
            return 0;
        }
        if (nums.length < 2) {
            return nums.length;
        }
        // 记录需要移动的位置
        int nextIndex = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) {
                continue;
            }
            // 不重复的元素，指针向前移动
            nextIndex++;
            // 如果当前元素索引，比指针位置大，说明之前有重复，则把当前元素移到指针位置
            if (i > nextIndex) {
                nums[nextIndex] = nums[i];
            }
        }
        return nextIndex + 1;
    }
}
