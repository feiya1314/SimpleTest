package mytest.algorithm.doublepoint;

import java.util.Arrays;

/**
 * //给你两个有序整数数组 nums1 和 nums2，请你将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组。
 * //
 * // 初始化 nums1 和 nums2 的元素数量分别为 m 和 n 。你可以假设 nums1 的空间大小等于 m + n，这样它就有足够的空间保存来自 nu
 * //ms2 的元素。
 * //
 * //
 * //
 * // 示例 1：
 * //
 * //
 * //输入：nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
 * //输出：[1,2,2,3,5,6]
 * //
 * //
 * // 示例 2：
 * //
 * //
 * //输入：nums1 = [1], m = 1, nums2 = [], n = 0
 * //输出：[1]
 * //
 * //
 * //
 * //
 * // 提示：
 * //
 * //
 * // nums1.length == m + n
 * // nums2.length == n
 * // 0 <= m, n <= 200
 * // 1 <= m + n <= 200
 * // -109 <= nums1[i], nums2[i] <= 109
 * //
 * // Related Topics 数组 双指针
 * https://leetcode-cn.com/problems/merge-sorted-array/
 *
 * @author : yufei
 * @date : 2021/1/21 11:12
 * @description :
 */
public class MergeSortedArray {
    // 从头到尾，双指针，把小的值放入数组
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int[] nums = Arrays.copyOf(nums1, nums1.length);
        if (n < 1) {
            return;
        }
        int n1 = 0;
        int n2 = 0;
        int i = 0;
        // 有一个比较完就结束，剩下的全部是大的值，添加到末尾即可
        while (n1 < m && n2 < n) {
            if (nums[n1] < nums2[n2]) {
                nums1[i] = nums[n1];
                n1++;
            } else {
                nums1[i] = nums2[n2];
                n2++;
            }
            i++;
        }
        // 如果n1数组有剩余
        // 或者用System.arraycopy()
        while (n1 < m) {
            nums1[i] = nums[n1];
            n1++;
            i++;
        }
        // 如果n2数组有剩余
        while (n2 < n) {
            nums1[i] = nums2[n2];
            n2++;
            i++;
        }
    }

    // 方法2，从尾部向前遍历，直接写到nums1尾部，节省空间，空间复杂度为1
    public void merge2(int[] nums1, int m, int[] nums2, int n) {
        if (n < 1) {
            return;
        }
        int i = m + n - 1;
        int n1 = m - 1;
        int n2 = n - 1;
        while (n1 >= 0 && n2 >= 0) {
            if (nums1[n1] > nums2[n2]) {
                nums1[i] = nums1[n1];
                n1--;
            } else {
                nums1[i] = nums2[n2];
                n2--;
            }
            i--;
        }

        while (n1 >= 0) {
            nums1[i] = nums1[n1];
            n1--;
            i--;
        }
        while (n2 >= 0) {
            nums1[i] = nums2[n2];
            n2--;
            i--;
        }
    }
}
