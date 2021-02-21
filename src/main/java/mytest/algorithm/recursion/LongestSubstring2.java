package mytest.algorithm.recursion;

import java.util.HashMap;
import java.util.Map;

/**
 * 找到给定字符串（由小写字符组成）中的最长子串 T ， 要求 T 中的每一字符出现次数都不少于 k 。输出 T 的长度。
 * <p>
 * 示例 1:
 * <p>
 * 输入:
 * s = "aaabb", k = 3
 * <p>
 * 输出:
 * 3
 * <p>
 * 最长子串为 "aaa" ，其中 'a' 重复了 3 次。
 * 示例 2:
 * <p>
 * 输入:
 * s = "ababbc", k = 2
 * <p>
 * 输出:
 * 5
 * <p>
 * 最长子串为 "ababb" ，其中 'a' 重复了 2 次， 'b' 重复了 3 次。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-substring-with-at-least-k-repeating-characters
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/12
 * @description :
 */
public class LongestSubstring2 {
    public int longestSubstring(String s, int k) {
       /* Map<Character, Integer> sM = new HashMap<>();
        Map<Character, Integer> cur = new HashMap<>();
        char[] chars = s.toCharArray();
        for (char c : chars) {
            sM.merge(c, 1, Integer::sum);
        }
        int max = 0;
        int curMax = 0;
        int i;
        for (i = 0; i < chars.length; i++) {
            if (sM.get(chars[i]) < k) {
                max = Math.max(max, curMax);
                curMax = 0;
                continue;
            }

        }*/
    return 0;
    }
}
