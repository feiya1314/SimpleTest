package mytest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : feiya
 * @date : 2021/3/20
 * @description :
 */
public class Solution {
    public static void main(String[] args) {
        String result = new Solution().desc("tree");
        System.out.println(result);
    }

    public String desc(String input) {
        if (input == null || input.length() <= 1) {
            return input;
        }
        char[] chars = input.toCharArray();
        List<Character> cList = new ArrayList<>();
        Map<Character, Integer> charsMap = new HashMap<>();
        for (char c : chars) {
            charsMap.merge(c, 1, Integer::sum);
        }
        Map<Integer, List<Character>> freq = new HashMap<>();
        charsMap.forEach((k, v) -> {
            List<Character> charsList = freq.get(v);
            if (charsList == null) {
                charsList = new ArrayList<>();
                freq.put(v, charsList);
            }
            charsList.add(k);
        });



        int[] cFreq = new int[freq.keySet().size()];
        int i = 0;
        for (int f : freq.keySet()) {
            cFreq[i] = f;
            i++;
        }
        Arrays.sort(cFreq);

    /*    Set<Character> cSet = new HashSet<>();
        for (int i = 0; i < chars.length; i++) {
            if (cSet.contains(chars[i])) {
                continue;
            }
            cSet.add(chars[i]);
            cList.add(chars[i]);
        }
        StringBuilder sb = new StringBuilder();
        for (Character c : cList) {
            int num = charsMap.get(c);
            for (int i = 0; i < num; i++) {
                sb.append(c);
            }
        }*/

        return null;
    }
}
