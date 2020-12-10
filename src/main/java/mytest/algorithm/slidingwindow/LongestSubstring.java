package mytest.algorithm.slidingwindow;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : yufei
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 * <p>
 * 示例 1:
 * <p>
 * 输入: s = "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 * 示例 2:
 * <p>
 * 输入: s = "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-substring-without-repeating-characters
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @date : 2020/12/7 13:58
 * @description :
 */
public class LongestSubstring {
    public static void main(String[] args) {

    }

    /**
     * 使用滑动窗口，碰到重复的字符左窗口边界右移
     * 改进
     *
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring(String s) {
        if (s.length() < 2) {
            return s.length();
        }

        Set<Character> set = new HashSet<>();
        set.add(s.charAt(0));
        int max = 1;
        int i = 0;
        int j = 1;

        for (; i < s.length(); i++) {
            for (; j < s.length(); j++) {
                if (set.contains(s.charAt(j))) {
                    break;
                }
                set.add(s.charAt(j));
            }
            // 更新最大值
            max = Math.max(max, j - i);
            // 如果又窗口到边界了，直接结束
            if (j >= s.length()) {
                // 右窗口到边界了，说明已经遍历完成，此时窗口的大小为j - i
                return max;
            }
            // 到这说明有重复的了，右移窗口，并把最左侧的字符剔除
            set.remove(s.charAt(i));
        }
        return max;
    }

    public static int lengthOfLongestSubstring2(String s) {
        if (s.length() < 2) {
            return s.length();
        }
        Set<Character> set = new HashSet<>();
        set.add(s.charAt(0));
        int max = 1;
        //记录左边界位置
        int left = 0;
        int i = 1;
        while (i < s.length()) {
            if (set.contains(s.charAt(i))) {
                set.remove(s.charAt(left));
                // 遇到重复的字符，更新max
                max = Math.max(max, i - left);
                left++;
                continue;
            }
            set.add(s.charAt(i));
            i++;
        }
        // 完全没有重复的字符，最大值是 i - left
        return Math.max(max, i - left);
    }
}
