package mytest.algorithm.dichotomy;

import java.util.Arrays;

/**
 * 给定一个 n 个元素有序的（升序）整型数组 nums 和一个目标值 target  ，写一个函数搜索 nums 中的 target，如果目标值存在返回下标，否则返回 -1。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: nums = [-1,0,3,5,9,12], target = 9
 * 输出: 4
 * 解释: 9 出现在 nums 中并且下标为 4
 * 示例 2:
 * <p>
 * 输入: nums = [-1,0,3,5,9,12], target = 2
 * 输出: -1
 * 解释: 2 不存在 nums 中因此返回 -1
 *  
 * <p>
 * 提示：
 * <p>
 * 你可以假设 nums 中的所有元素是不重复的。
 * n 将在 [1, 10000]之间。
 * nums 的每个元素都将在 [-9999, 9999]之间。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/binary-search
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/7/28
 * @description :
 */
public class FindTargetIndex {
    public static void main(String[] args) {
        System.out.println(getIndex(new int[]{-1, 0, 3, 5, 9, 12}, 3));
    }

    private static int getIndex(int[] source, int target) {
        if (source == null || source.length < 1) {
            return -1;
        }
        Arrays.sort(source);
        int right = source.length;
        int left = 0;
        int mid = source.length / 2;
        int lastIndex = -1;
        while (mid < source.length && mid >= 0) {
            mid = (right + left) / 2;
            if (source[mid] == target) {
                return mid;
            }
            if (lastIndex == mid) {
                return -1;
            }
            if (source[mid] > target) {
                lastIndex = mid;
                right = mid;
                continue;
            }
            if (source[mid] < target) {
                lastIndex = mid;
                left = mid;
            }
        }
        return -1;
    }

    private static int getIndex2(int[] source, int target) {
        if (source == null || source.length < 1) {
            return -1;
        }
        Arrays.sort(source);
        int right = source.length - 1;
        int left = 0;
        while (left <= right) {
            // 可以一定程度上防溢出
            // pivot = (right + left) / 2，为啥就是中间值索引？有些人好好思考过吗？其实，这不过是一个运算的结果，真正原始的等式就是 pivot = left + (right - left) / 2;
            // 这个等式才能从逻辑上说明中间值的获取过程，只是这个公式也刚好防范了溢出问题。
            int mid = left + (right - left) / 2;
            if (source[mid] == target) {
                return mid;
            }

            if (source[mid] > target) {
                right = mid - 1;
                continue;
            }
            if (source[mid] < target) {
                left = mid + 1;
            }
        }
        return -1;
    }
}
