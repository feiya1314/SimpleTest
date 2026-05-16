package mytest.algorithm.slidingwindow;

/**
 * https://leetcode.cn/problems/minimum-common-value/description/?envType=problem-list-v2&envId=two-pointers
 */
public class GetCommon {
    public int getCommon(int[] nums1, int[] nums2) {
        int slow = 0;
        int fast = 0;

        while (slow < nums1.length && fast < nums2.length) {
            if (nums1[slow] == nums2[fast]) {
                return nums1[slow];
            }
            if (nums1[slow] < nums2[fast]) {
                slow++;
            } else {
                fast++;
            }
        }
        return -1;
    }
}
