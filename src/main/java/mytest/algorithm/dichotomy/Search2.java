package mytest.algorithm.dichotomy;

/**
 * https://leetcode.cn/problems/binary-search/?envType=problem-list-v2&envId=array
 */
public class Search2 {
    public int search(int[] nums, int target) {
        if (nums == null || nums.length < 1) {
            return -1;
        }
        int l = 0;
        int r = nums.length - 1;
        // = 时，两个位置在同一处，也需要判断
        while (l <= r) {
            int mid = (l + r) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            // 中间值小于target，说明target只可能在右区间，否则在左区间
            if (nums[mid] < target) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return -1;
    }
}
