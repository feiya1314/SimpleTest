package mytest.algorithm.dichotomy;

/**
 * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
 * <p>
 * 请必须使用时间复杂度为 O(log n) 的算法。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * 输入: nums = [1,3,5,6], target = 5
 * 输出: 2
 * 示例 2:
 * <p>
 * 输入: nums = [1,3,5,6], target = 2
 * 输出: 1
 * 示例 3:
 * <p>
 * 输入: nums = [1,3,5,6], target = 7
 * 输出: 4
 * 示例 4:
 * <p>
 * 输入: nums = [1,3,5,6], target = 0
 * 输出: 0
 * 示例 5:
 * <p>
 * 输入: nums = [1], target = 0
 * 输出: 0
 *  
 * <p>
 * 提示:
 * <p>
 * 1 <= nums.length <= 104
 * -104 <= nums[i] <= 104
 * nums 为无重复元素的升序排列数组
 * -104 <= target <= 104
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/search-insert-position
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/8/22
 * @description :
 */
public class SearchInsert {
    public int searchInsert(int[] nums, int target) {
        if (nums == null || nums.length < 1) {
            return -1;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            // 数组的中间位置， length 如果是偶数 ，中间的两个 左一个是length/2-1  右一个是length/2
            // 如果是奇数  中间一个是 length/2
            // 数组某个区间的中间的数的坐标，
            // 偶数个，中间有两个，左一个是(r + l) / 2 = l + (r - l) / 2，右一个是(r + l) / 2 + 1
            // 如果是奇数个，中间有一个，中间位置的坐标是 (r + l) / 2 =  l + (r - l) / 2
            // !!! 遍历到最后 可能剩两个，l 和 r 紧挨着两个，或者 l和r是同一个位置 !!!
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            }

            // 遍历到最后 可能剩两个，l 和 r 紧挨着两个，或者 l和r是同一个位置，如果是两个，下一次循环后，l和r也会在同一位置
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // 最终肯定是right < left 的，前一次一定是 left = right = mid，此时
        // num[mid] > target 时，right 需要 -1，此时right 位置是小于 target 的，需要在right后面插入，也即是right + 1
        // num[mid] < target 时，left 需要加 +1.此时right还是小于 target 的，需要在right后面插入，也即是right + 1
        // right + 1的位置也是第一个大于target 的位置

        // 最后一次比较，l 和 r 还有mid 一定位于同一位置，
        // 但是下一次跳出循环，r-1或者l+1，导致r<l,
        // 如果是mid 小于target，此时是l + 1，r = mid位置不动，target应该插入mid右侧，返回 r+1
        // 如果是mid 大于target，此时是r - 1，l 位置不动，target应该插入mid的位置，即 mid 或者 l 或者 r+1
        // 所以最终是 r + 1位置
        return right + 1;
    }
}
