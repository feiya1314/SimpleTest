package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * https://leetcode.cn/problems/combinations/
 */
public class Combine {
    public List<List<Integer>> combine(int n, int k) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> track = new ArrayList<>();

        backtrack(n, k, 1, result, track);
        return result;
    }

    private void backtrack(int n, int k, int start, List<List<Integer>> result, List<Integer> track) {
        if (track.size() == k) {
            result.add(new ArrayList<>(track));
            return;
        }

        // 排列有顺序区分，i需要从 0 开始，但是需要记录已使用过的值，已使用的值不能再用
        // 组合无顺序区分，仅仅是取出元素，所以下一层的从大于当前值的后一个值开始，小于的前面已经处理过
        for (int i = start; i <= n; i++) {
            track.add(i);
            backtrack(n, k, i + 1, result, track);
            track.removeLast();
        }
    }
}
