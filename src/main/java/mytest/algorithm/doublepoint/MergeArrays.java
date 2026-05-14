package mytest.algorithm.doublepoint;

/**
 * https://leetcode.cn/problems/merge-two-2d-arrays-by-summing-values/description/
 * 示例 1：
 *
 * 输入：nums1 = [[1,2],[2,3],[4,5]], nums2 = [[1,4],[3,2],[4,1]]
 * 输出：[[1,6],[2,3],[3,2],[4,6]]
 * 解释：结果数组中包含以下元素：
 * - id = 1 ，对应的值等于 2 + 4 = 6 。
 * - id = 2 ，对应的值等于 3 。
 * - id = 3 ，对应的值等于 2 。
 * - id = 4 ，对应的值等于 5 + 1 = 6 。
 * 示例 2：
 *
 * 输入：nums1 = [[2,4],[3,6],[5,5]], nums2 = [[1,3],[4,3]]
 * 输出：[[1,3],[2,4],[3,6],[4,3],[5,5]]
 * 解释：不存在共同 id ，在结果数组中只需要包含每个 id 和其对应的值。
 */
public class MergeArrays {
    public int[][] mergeArrays(int[][] nums1, int[][] nums2) {
        // 使用双指针，一个指向左边的数组，一个指向右边的数组，当两个值相同时，处理后指针同时移动，否则移动id小的指针，进行下一次比较
        int nums1Len = nums1.length;
        int nums2Len = nums2.length;
        int[][] result = new int[nums1Len + nums2Len][];

        int maxLen = 0;

        int left = 0;
        int right = 0;
        for (int i = 0; i < nums1Len + nums2Len; i++) {
            // 两个指针都超出了各自长度，说明处理完了，直接返回
            if (left >= nums1Len && right >= nums2Len) {
                break;
            }
            // 左右两个数组的当前位置的值，遍历完的数组默认值是0
            int lP = left < nums1Len ? nums1[left][0] : 0;
            int lV = left < nums1Len ? nums1[left][1] : 0;

            int rP = right < nums2Len ? nums2[right][0] : 0;
            int rV = right < nums2Len ? nums2[right][1] : 0;

            // id相同，两个指针同时移动，进行下一次
            if (lP == rP) {
                result[i] = new int[]{lP, lV + rV};
                maxLen++;
                left++;
                right++;
                continue;
            }
            // 右边的已经遍历完了，直接使用左边的就行
            if (rP == 0) {
                result[i] = nums1[left];
                maxLen++;
                left++;
                continue;
            }
            if (lP == 0) {
                result[i] = nums2[right];
                maxLen++;
                right++;
                continue;
            }
            // 左边的小于右边的，结果使用左边的，左边指针移动
            if (lP < rP) {
                result[i] = nums1[left];
                maxLen++;
                left++;
                continue;
            }
            if (lP > rP) {
                result[i] = nums2[right];
                maxLen++;
                right++;
            }
        }

        // 把结果复制到实际的长度的数组中
        int[][] realResult = new int[maxLen][];
        System.arraycopy(result, 0, realResult, 0, maxLen);

        return realResult;
    }
}
