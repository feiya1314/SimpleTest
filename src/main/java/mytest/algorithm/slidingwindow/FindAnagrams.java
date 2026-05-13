package mytest.algorithm.slidingwindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FindAnagrams {
    public List<Integer> findAnagrams(String s, String p) {
        int sLen = s.length();
        int pLen = p.length();
        if (sLen < pLen) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>();

        int[] sArray = new int[26];
        int[] pArray = new int[26];

        for (int i = 0; i < pLen; i++) {
//            sArray[s.charAt(i) - 'a'] = sArray[s.charAt(i) - 'a'] + 1;
            ++sArray[s.charAt(i) - 'a'];
            ++pArray[p.charAt(i) - 'a'];
        }
        // 如果当前的数据相同，则符合条件，次数sArray 和 pArray中的每个字符出现的个数之和是相同的
        if (Arrays.equals(sArray, pArray)) {
            result.add(0);
        }

        // 判断到窗口前，窗口是pLen大小，
        for (int i = 0; i < sLen - pLen; i++) {
//            始终sArray 中的个数和pArray 的个数之和是相同的
            // 去掉窗口左边的字符，个数就减一了
            --sArray[s.charAt(i) - 'a'];
            // 对应的，右边的字符个数加一，窗口移动了一格
            ++sArray[s.charAt(i + pLen) - 'a'];
            if (Arrays.equals(sArray, pArray)) {
                result.add(i + 1);
            }
        }

        return result;
    }
}
