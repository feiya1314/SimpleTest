package mytest.algorithm.doublepoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 给定两个数组，编写一个函数来计算它们的交集。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums1 = [1,2,2,1], nums2 = [2,2]
 * 输出：[2]
 * 示例 2：
 * <p>
 * 输入：nums1 = [4,9,5], nums2 = [9,4,9,8,4]
 * 输出：[9,4]
 *  
 * <p>
 * 说明：
 * <p>
 * 输出结果中的每个元素一定是唯一的。
 * 我们可以不考虑输出结果的顺序。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/intersection-of-two-arrays
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/9
 * @description :
 */
public class Intersection {
    // 使用 hash表
    public int[] intersection(int[] nums1, int[] nums2) {
        if (nums1.length < 1 || nums2.length < 1) {
            return new int[0];
        }
        Set<Integer> s1 = new HashSet<>();
        Set<Integer> s2 = new HashSet<>();
        for (int num : nums1) {
            s1.add(num);
        }
        for (int num : nums2) {
            s2.add(num);
        }

        List<Integer> result = new ArrayList<>();
        for (Integer n1 : s1) {
            if (s2.contains(n1)) {
                result.add(n1);
            }
        }
        int[] re = new int[result.size()];
        int index = 0;
        for (Integer n : result) {
            re[index] = n;
            index++;
        }
        return re;
    }

    // 排序+双指针
    public int[] intersection2(int[] nums1, int[] nums2) {
        if (nums1.length < 1 || nums2.length < 1) {
            return new int[0];
        }
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        int p1 = 0;
        int p2 = 0;
        int[] result = new int[nums1.length];
        int index = 0;
        while (p1 < nums1.length && p2 < nums2.length) {
            if (nums1[p1] < nums2[p2]) {
                p1++;
            } else if (nums1[p1] > nums2[p2]) {
                p2++;
            } else {
                // 和前一个不同的时候才添加
                if (p1 == 0 || nums1[p1] != nums1[p1 - 1]) {
                    result[index] = nums1[p1];
                    index++;
                }
                p1++;
                p2++;
            }
        }

        return Arrays.copyOf(result, index);
    }
}
