package mytest.algorithm.backtrack;

import java.util.*;

/**
 * @author : yufei
 * @date : 2020/12/23 10:44
 * @description :
 */
public class PhoneNum {
    private List<String> result = new ArrayList<>();
    private int digLen = 0;

    // 深度优先实现
    public List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) {
            return Collections.emptyList();
        }

        /*digLen = digits.length();
        char[] chars = getChars(digits);
        Arrays.sort(chars);
        used = new boolean[digLen];

        LinkedList<Character> track = new LinkedList<>();
        backtrack(chars, track);*/

        return result;
    }

    // 回溯实现
    public List<String> letterCombination2(String digits) {
        if (digits == null || digits.isEmpty()) {
            return Collections.emptyList();
        }
        digLen = digits.length();
        Map<Character, String> maps = getCharMap();
        int index = 0;
        LinkedList<Character> track = new LinkedList<>();
        backtrack(index, maps, digits, track);

        return result;
    }

    private void backtrack(int index, Map<Character, String> maps, String digits, LinkedList<Character> track) {
        if (index == digLen) {
            StringBuilder sb = new StringBuilder();
            track.forEach(sb::append);
            result.add(sb.toString());
            return;
        }

        String mapString = maps.get(digits.charAt(index));
        int nextIndex = index + 1;
        for (int i = 0; i < mapString.length(); i++) {
            track.add(mapString.charAt(i));
            backtrack(nextIndex, maps, digits, track);
            track.removeLast();
        }
    }

    private Map<Character, String> getCharMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('2', "abc");
        map.put('3', "def");
        map.put('4', "ghi");
        map.put('5', "jkl");
        map.put('6', "mno");
        map.put('7', "pqrs");
        map.put('8', "tuv");
        map.put('9', "wxyz");

        return map;
    }
}
