package mytest.algorithm.dichotomy;

/**
 * https://leetcode.cn/problems/search-in-rotated-sorted-array/description/
 */
public class Search {
    public int search(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        int l = 0;
        int r = nums.length - 1;
        while (r >= l) {
            int mid = (l + r) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            // 4,5,6,7,0,1,2
            // 4,5,6,7,-6 -5 -4 -3 -2 -1 0,1,2
            // 如果中间值大于最左侧值，说明左边是有序的，否则右侧是有序的
            // 判断是在左段还是右段
            // 1、如果中间值大于左侧值，说明左侧是连续的，升序的
            // 等于值的话，说明 l mid r在同一个位置了,等下次循环跳出就行
            if (nums[mid] >= nums[l]) {
                if (nums[l] == target) {
                    return l;
                }
                // target位于左侧区间中
                if (nums[l] < target && target < nums[mid]) {
                    r = mid - 1;
                    continue;
                }
                // 不在左侧区间，则去右侧区间遍历
                l = mid + 1;
                continue;
            }
            // 说明右侧是连续升序的
            if (nums[mid] < nums[l]) {
                if (nums[r] == target) {
                    return r;
                }
                // 只可能位于右侧区间
                if (nums[mid] < target && target < nums[r]) {
                    l = mid + 1;
                    continue;
                }
                // 不在右侧区间，则在左侧区间
                r = mid - 1;
            }
        }

        return -1;
    }
}
