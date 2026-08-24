
public class Solution {
    public int[][] mergeArrays(int[][] nums1, int[][] nums2) {
        int nums1Len = nums1.length;
        int nums2Len = nums2.length;
        int[][] result = new int[nums1Len + nums2Len][];

        int maxLen = 0;

        int left = 0;
        int right = 0;
        for (int i = 0; i < nums1Len + nums2Len; i++) {
            if (left >= nums1Len && right >= nums2Len) {
                break;
            }
            int lP = left < nums1Len ? nums1[left][0] : 0;
            int lV = left < nums1Len ? nums1[left][1] : 0;

            int rP = right < nums2Len ? nums2[right][0] : 0;
            int rV = right < nums2Len ? nums2[right][1] : 0;

            if (lP == rP) {
                result[i] = new int[]{lP, lV + rV};
                maxLen++;
                left++;
                right++;
                continue;
            }
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

        int[][] realResult = new int[maxLen][];
        for (int i = 0; i < maxLen; i++) {
            realResult[i] = result[i];
        }

        return realResult;
    }
}
