package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : yufei
 * @date : 2020/12/22 10:48
 * @description :
 */
public class ContainRepreatString {
    List<String> result = new ArrayList<>();
    boolean[] contain;

    public String[] permutation(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        contain = new boolean[s.length()];
        LinkedList<Character> track = new LinkedList<>();
        backtrack(chars, track);
        return result.toArray(new String[0]);
    }

    private void backtrack(char[] chars, LinkedList<Character> track) {
        if (track.size() == chars.length) {
            StringBuilder sb = new StringBuilder();
            track.forEach(sb::append);
            result.add(sb.toString());
            return;
        }

        char pre = ' ';
        for (int i = 0; i < chars.length; i++) {
            if (contain[i] || chars[i] == pre) {
                continue;
            }
            pre = chars[i];
            contain[i] = true;
            track.add(chars[i]);
            backtrack(chars, track);
            // 递归出来后，把上一次访问过的字符剔除，下一次同级递归使用
            track.removeLast();
            contain[i] = false;
        }
    }
}
