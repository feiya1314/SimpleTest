package mytest.algorithm.backtrack;

import java.util.*;

/**
 * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
 * <p>
 * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
 * <p>
 * <p>
 * <p>
 * 示例:
 * <p>
 * 输入："23"
 * 输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 * 说明:
 * 尽管上面的答案是按字典序排列的，但是你可以任意选择答案输出的顺序。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : yufei
 * @date : 2020/12/23 10:44
 * @description :
 */
public class PhoneNum {

    // 回溯实现
    public List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        List<Character> track = new ArrayList<>();
        backtrack(digits, 0, result, track);

        return result;
    }

    private void backtrack(String digits, int start, List<String> result, List<Character> track) {
        if (track.size() == digits.length()) {
            StringBuilder sb = new StringBuilder();
            track.forEach(sb::append);
            result.add(sb.toString());
            return;
        }

        // 每次递归下一层都是digits的下一个字符，例如234，每一层值要加一
        String digit = getCharMap().get(digits.charAt(start));
        // 遍历数字对应的字符串，这一层遍历完，例如 23，对应的先a 再b 再c，遍历a时，递归下一层对应3，遍历顺序 ad  ae af
        // 由于每一层值是独立的，不需要剪枝
        for (int i = 0; i < digit.length(); i++) {
            track.add(digit.charAt(i));
            backtrack(digits, start + 1, result, track);
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
