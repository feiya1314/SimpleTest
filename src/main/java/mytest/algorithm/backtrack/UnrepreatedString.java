package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : yufei
 * @date : 2020/12/21 19:46
 * @description :
 */
public class UnrepreatedString {
    List<String> result = new ArrayList<>();
    boolean[] contain;

    public String[] permutation(String s) {
        char[] chars = s.toCharArray();
        contain = new boolean[s.length()];
        LinkedList<Character> track = new LinkedList<>();
        backtrack(chars, track);
        return result.toArray(new String[0]);
    }

    // track用来记录一次组合，track长度达到数组长度后，说明组合完成
    private void backtrack(char[] chars, LinkedList<Character> track) {
        if (track.size() == chars.length) {
            StringBuilder sb = new StringBuilder();
            track.forEach(sb::append);
            result.add(sb.toString());
            return;
        }
        for (char c : chars) {
            // 由于每次递归的chars都是同一个，会有重复的char，如果已经包含了，则跳过这条线
            if (track.contains(c)) {
                continue;
            }
            track.add(c);
            backtrack(chars, track);
            // 将遍历过的字符剔除，例如q w e,遍历q w e,之后，剔除last的，
            // 会先剔除e,之后剔除 w，然后添加e，w,找到qew这个组合
            track.removeLast();
        }
    }

    // 优化判断是否包含了字符的步骤
    private void backtrack2(char[] chars, LinkedList<Character> track) {
        if (track.size() == chars.length) {
            StringBuilder sb = new StringBuilder();
            track.forEach(sb::append);
            result.add(sb.toString());
            return;
        }
        // 由于每次往下遍历的时候，都是从0开始，0后，递归的第一个仍然是0，
        // 但是这个是需要略过的，track记录后把此处的字符标记为true
        for (int i = 0; i < chars.length; i++) {
            if (contain[i]) {
                continue;
            }
            contain[i] = true;
            track.add(chars[i]);
            backtrack2(chars, track);
            // 递归出来后需要剔除最后一次的字符
            track.removeLast();
            contain[i] = false;
        }
    }
}
