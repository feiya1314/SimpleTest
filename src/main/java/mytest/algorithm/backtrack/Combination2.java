package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * //给定两个整数 n 和 k，返回 1 ... n 中所有可能的 k 个数的组合。
 * //
 * // 示例:
 * //
 * // 输入: n = 4, k = 2
 * //输出:
 * //[
 * //  [2,4],
 * //  [3,4],
 * //  [2,3],
 * //  [1,2],
 * //  [1,3],
 * //  [1,4],
 * //]
 * // Related Topics 回溯算法
 * https://leetcode-cn.com/problems/combinations/
 *
 * @author : yufei
 * @date : 2020/12/26 10:38
 * @description :
 */
public class Combination2 {
    List<List<Integer>> result = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(new Combination2().combine(4, 2));
    }

    public List<List<Integer>> combine(int n, int k) {
        LinkedList<Integer> track = new LinkedList<>();
        backtrack(n, 0, k, track);
        return result;
    }

    private void backtrack(int n, int start, int k, LinkedList<Integer> track) {
        if (track.size() == k) {
            result.add(new ArrayList<>(track));
            return;
        }

        for (int i = start; i < n; i++) {
            // track 每次add 是 i+1因为，是从0开始，实际是从1开始
            track.add(i + 1);
            backtrack(n, i + 1, k, track);
            track.removeLast();
        }
    }
}
