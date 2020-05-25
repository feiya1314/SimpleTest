package mytest.algorithm.slidingwindow;

import java.util.HashMap;
import java.util.Map;

/**
 * 给你一个字符串 S、一个字符串 T，请在字符串 S 里面找出：包含 T 所有字符的最小子串。
 * <p>
 * 示例：
 * <p>
 * 输入: S = "ADOBECODEBANC", T = "ABC"
 * 输出: "BANC"
 * 说明：
 * <p>
 * 如果 S 中不存这样的子串，则返回空字符串 ""。
 * 如果 S 中存在这样的子串，我们保证它是唯一的答案。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/minimum-window-substring
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class MinSubString {
    public static void main(String[] args) {
        System.out.println(sub("a", "aa"));
    }

    /**
     * 思路使用滑动窗口
     *
     * @param s
     * @param t
     * @return
     */
    public static String sub(String s, String t) {
        if (s == null || s.length() == 0 || t == null || t.length() == 0 || s.length() < t.length()) {
            return "";
        }
        Map<Character, Integer> tMap = new HashMap<>();
        Map<Character, Integer> curMap = new HashMap<>();
        for (int i = 0; i < t.length(); i++) {
            tMap.put(t.charAt(i), tMap.getOrDefault(t.charAt(i), 0) + 1); // 如果不要求字符数和 t 保持一致，例如 a，aa 可以看作a，a的话，则可以不用记录总数
        }

        int headIndex = 0; //保存字串的初始坐标
        int subLength = Integer.MAX_VALUE; // 保存字串的长度
        int left = 0;
        int right = 0;

        //  S = "ADOBECODEBANC", T = "ABC"
        while (right < s.length()) {
            // 右移窗口
            char c = s.charAt(right);
            if (tMap.containsKey(c)) {
                curMap.put(c, curMap.getOrDefault(c, 0) + 1);
            }
            right++;
            // 判断窗口是否已经包含 t 中所有的字符串，如果包含，则left右移
            while (allInclude(tMap, curMap) && left < right) {
                char le = s.charAt(left);
                // 如果目标字符串不包含 le，直接 left 右移
                if (!curMap.containsKey(le)) {
                    left++;
                } else if (curMap.get(le) > tMap.get(le)) { // 如果当前 map 中le的数量比目标所需的还多，left 可以右移，并剔除一个
                    left++;                                  // 如果不要求字符数和 t 保持一致，例如 a，aa 可以看作a，a的话，tMap.get(le)可以换成 1，也即是只要多于1个即可优化
                    curMap.put(le, curMap.get(le) - 1);
                } else {
                    // 此时当前 窗口中字符 刚好满足 要求，检查一下，如果当前的窗口长度比之前小，就更新一下窗口的初始坐标和窗口长度
                    if (right - left < subLength) {
                        headIndex = left;
                        subLength = right - left;
                    }
                    break;
                }
            }

        }

        return subLength == Integer.MAX_VALUE ? "" : s.substring(headIndex, headIndex + subLength);
    }

    private static boolean allInclude(Map<Character, Integer> tMap, Map<Character, Integer> curMap) {
        for (Map.Entry<Character, Integer> entry : tMap.entrySet()) {
            if (!curMap.containsKey(entry.getKey()) || curMap.get(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
}
