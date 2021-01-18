package mytest.algorithm.backtrack;

import java.util.LinkedList;

/**
 * 给出集合 [1,2,3,...,n]，其所有元素共有 n! 种排列。
 * <p>
 * 按大小顺序列出所有排列情况，并一一标记，当 n = 3 时, 所有排列如下：
 * <p>
 * "123"
 * "132"
 * "213"
 * "231"
 * "312"
 * "321"
 * 给定 n 和 k，返回第 k 个排列。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：n = 3, k = 3
 * 输出："213"
 * 示例 2：
 * <p>
 * 输入：n = 4, k = 9
 * 输出："2314"
 * 示例 3：
 * <p>
 * 输入：n = 3, k = 1
 * 输出："123"
 *  
 * <p>
 * 提示：
 * <p>
 * 1 <= n <= 9
 * 1 <= k <= n!
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/permutation-sequence
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Permutation3 {
    private String result;
    private int seq = 0;
    boolean[] used;

    public static void main(String[] args) {
        System.out.println(new Permutation3().getPermutation(3, 1));
    }

    // 回溯法
    public String getPermutation(int n, int k) {
        LinkedList<Integer> track = new LinkedList<>();
        used = new boolean[n];
        backtrack(n, k, track);
        return result;
    }

    private void backtrack(int n, int k, LinkedList<Integer> track) {
        if (track.size() == n) {
            seq++;
            if (seq == k) {
                StringBuilder sb = new StringBuilder();
                track.forEach(sb::append);
                result = sb.toString();
            }
            return;
        }

        for (int i = 0; i < n; i++) {
            if (used[i]) {
                continue;
            }
            track.add(i + 1);
            used[i] = true;
            backtrack(n, k, track);
            track.removeLast();
            used[i] = false;
        }
    }
}
