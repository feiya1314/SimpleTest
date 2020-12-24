package mytest.algorithm.backtrack;

import java.util.*;

/**
 * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
 *
 * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
 *
 *
 *
 * 示例:
 *
 * 输入："23"
 * 输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 * 说明:
 * 尽管上面的答案是按字典序排列的，但是你可以任意选择答案输出的顺序。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
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
