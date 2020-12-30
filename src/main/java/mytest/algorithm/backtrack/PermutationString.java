package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * //输入一个字符串，打印出该字符串中字符的所有排列。
 * //
 * //
 * //
 * // 你可以以任意顺序返回这个字符串数组，但里面不能有重复元素。
 * //
 * //
 * //
 * // 示例:
 * //
 * // 输入：s = "abc"
 * //输出：["abc","acb","bac","bca","cab","cba"]
 * //
 * //
 * //
 * //
 * // 限制：
 * //
 * // 1 <= s 的长度 <= 8
 * // Related Topics 回溯算法
 * https://leetcode-cn.com/problems/zi-fu-chuan-de-pai-lie-lcof/
 *
 * @author : yufei
 * @date : 2020/12/30 13:13
 * @description :
 */
public class PermutationString {
    private List<String> result = new ArrayList<>();
    boolean[] used;

    public String[] permutation(String s) {
        if (s == null || s.isEmpty()) {
            return new String[0];
        }
        LinkedList<Character> track = new LinkedList<>();
        char[] source = s.toCharArray();
        used = new boolean[source.length];
        Arrays.sort(source);
        backtrack(source,track);

        return result.toArray(new String[0]);
    }

    private void backtrack(char[] chars, LinkedList<Character> track) {
        if (track.size() == chars.length) {
            StringBuilder sb = new StringBuilder();
            track.forEach(sb::append);
            result.add(sb.toString());
        }

        char pre = ' ';
        for (int i = 0; i < chars.length; i++) {
            if (pre == chars[i] || used[i]) {
                continue;
            }
            pre = chars[i];
            track.add(chars[i]);
            used[i] = true;
            backtrack(chars, track);
            used[i] = false;
            track.removeLast();
        }
    }
}
