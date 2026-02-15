package mytest.algorithm.array;

/**
 * 给定两个大小为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。
 * <p>
 * 请你找出这两个正序数组的中位数，并且要求算法的时间复杂度为 O(log(m + n))。
 * <p>
 * 你可以假设 nums1 和 nums2 不会同时为空。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * nums1 = [1, 3]
 * nums2 = [2]
 * <p>
 * 则中位数是 2.0
 * 示例 2:
 * <p>
 * nums1 = [1, 2]
 * nums2 = [3, 4]
 * <p>
 * 则中位数是 (2 + 3)/2 = 2.5
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/median-of-two-sorted-arrays
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class MedianSortedArrays {
    public static void main(String[] args) {
        System.out.println(s2(new int[]{1, 2}, new int[]{-1, 3}));
    }

    // 1 2 3 4 6 7 8    1 4  5  6  7 9 12
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int num1Length = nums1.length;
        int num2Length = nums2.length;
        int[] temp = new int[num1Length + num2Length];

        int i = 0;
        int j = 0;
        int k = 0;
        while (i < num1Length && j < num2Length) {
            if (nums1[i] < nums2[j]) {
                temp[k++] = nums1[i++];

            } else {
                temp[k++] = nums2[j++];
            }
        }
        while (i < num1Length) {
            temp[k++] = nums1[i++];
        }

        while (j < num2Length) {
            temp[k++] = nums2[j++];
        }

        if (temp.length % 2 == 0) {
            int left = temp[temp.length / 2 - 1];
            int right = temp[temp.length / 2];
            return (double) (left + right) / 2;
        } else {
            return temp[temp.length / 2];
        }
    }

    /*
        上面的方法优化  只需找到中位数的位置即可
        数组的中间位置， length 如果是偶数 ，中间的两个 左一个是length/2-1  右一个是length/2
        如果是偶数  中间一个是 length/2
     */
    public static double s2(int[] nums1, int[] nums2) {
        int num1Length = nums1.length;
        int num2Length = nums2.length;

        // 中间位置的左侧，如果为偶数，则为中间两位的左边，基数则为中间位置的左边
        int midLeft = (num1Length + num2Length) / 2 - 1;
        int mid = midLeft + 1;

        int midLeftV = 0;
        int midV = 0;

        int i = 0;
        int j = 0;
        int k = 0;
        while (i < num1Length && j < num2Length) {
            if (nums1[i] < nums2[j]) {
                if (k == midLeft) {
                    midLeftV = nums1[i];
                }
                if (k == mid) {
                    midV = nums1[i];
                    i++; // 这两行必须加上，不然下面遍历会重复统计
                    k++;
                    break;
                }
                i++;
                k++;
            } else {
                if (k == midLeft) {
                    midLeftV = nums2[j];
                }
                if (k == mid) {
                    midV = nums2[j];
                    j++; // 这两行必须加上，不然下面遍历会重复统计
                    k++;
                    break;
                }
                j++;
                k++;
            }
        }

        while (i < num1Length) {
            if (k == midLeft) {
                midLeftV = nums1[i];
            }
            if (k == mid) {
                midV = nums1[i];
                break;
            }
            i++;
            k++;
        }
        while (j < num2Length) {
            if (k == midLeft) {
                midLeftV = nums2[j];
            }
            if (k == mid) {
                midV = nums2[j];
                break;
            }
            j++;
            k++;
        }

        if ((num1Length + num2Length) % 2 == 0) {
            return (double) (midLeftV + midV) / 2;
        } else {
            return midV;
        }
    }
}
