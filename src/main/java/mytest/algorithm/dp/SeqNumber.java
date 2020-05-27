package mytest.algorithm.dp;

import java.util.HashSet;

/**
 * 给定一个未排序的整数数组，找出最长连续序列的长度。
 * <p>
 * 要求算法的时间复杂度为 O(n)。
 * <p>
 * 示例:
 * <p>
 * 输入: [100, 4, 200, 1, 3, 2]
 * 输出: 4
 * 解释: 最长连续序列是 [1, 2, 3, 4]。它的长度为 4。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-consecutive-sequence
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * 找出最长的连续序列
 * 本题也可以使用动态规划去做
 */
public class SeqNumber {
    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 12, 34, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        System.out.println(process(arr));
    }

    public static int process(int[] arr) {
        HashSet<Integer> set = new HashSet<>();
        for (int v : arr) {
            set.add(v);
        }
        int count = 0;
        int start = 0;
        int countTmp;
        for (int v : arr) {
            // 每次开始前先清空，从头计数
            countTmp = 0;
            // 如果包含 v - 1，跳过，从连续序列的最小值开始
            if (set.contains(v - 1)) {
                continue;
            }

            int i = v;
            countTmp++; // 找到了最小值，计数加一
            // 循环直到找不到连续的数
            while (set.contains(i + 1)) {
                i++;
                countTmp++;
            }
            // 找到最长的
            if (countTmp > count) {
                count = countTmp;
                start = v;
            }
        }
        for (int i = 0; i < count; i++) {
            System.out.println(start + i);
        }
        System.out.println();
        return count;
    }

    public static int dp(int arr) {

        return 0;
    }
}
