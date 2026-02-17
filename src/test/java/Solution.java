import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Solution {
    public int search(int[] nums, int target) {

    }

    public int searchInsert(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int l = 0;
        int r = nums.length - 1;
        while (r >= l) {
            // 数组的中间位置， length 如果是偶数 ，中间的两个 左一个是length/2-1  右一个是length/2
            // 如果是奇数  中间一个是 length/2
            // 数组某个区间的中间的数的坐标，
            // 偶数个，中间有两个，左一个是(r + l) / 2 = l + (r - l) / 2，右一个是(r + l) / 2 + 1
            // 如果是奇数个，中间有一个，中间位置的坐标是 (r + l) / 2 =  l + (r - l) / 2
            // !!! 遍历到最后 可能剩两个，l 和 r 紧挨着两个，或者 l和r是同一个位置 !!!
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            // 遍历到最后 可能剩两个，l 和 r 紧挨着两个，或者 l和r是同一个位置，如果是两个，下一次循环后，l和r也会在同一位置
            if (nums[mid] > target) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        // 最后一次比较，l 和 r 还有mid 一定位于同一位置，
        // 但是下一次跳出循环，r-1或者l+1，导致r<l,
        // 如果是mid 小于target，此时是l + 1，r = mid位置不动，target应该插入mid右侧，返回 r+1
        // 如果是mid 大于target，此时是r - 1，l 位置不动，target应该插入mid的位置，即 mid 或者 l 或者 r+1
        // 所以最终是 r + 1位置
        return r + 1;
    }
}
