package mytest.algorithm.string;

/**
 * Given a string s consists of some words separated by spaces, return the length of the last word in the string. If the last word does not exist, return 0.
 * <p>
 * A word is a maximal substring consisting of non-space characters only.
 * <p>
 *  
 * <p>
 * Example 1:
 * <p>
 * Input: s = "Hello World"
 *
 * Output: 5
 * Example 2:
 * <p>
 * Input: s = " "
 * Output: 0
 *  
 * <p>
 * Constraints:
 * <p>
 * 1 <= s.length <= 104
 * s consists of only English letters and spaces ' '.
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/length-of-last-word
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class LengthOfLastWord {
    public int lengthOfLastWord(String s) {
        int pre = 0;
        int cur = 0;
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                // cur 大于0 时才赋值，避免连续出现空格的情况
                // pre 记录上一个单词长度，cur记录当前遇到的单词长度
                pre = cur > 0 ? cur : pre;
                // 遇到空格，cur就重新计数
                cur = 0;
                continue;
            }
            cur++;
        }
        return cur > 0 ? cur : pre;
    }

    /* 从末尾开始遍历 */
    public int lengthOfLastWord2(String s) {
        char[] chars = s.toCharArray();
        int cur = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            // 遇到空格时，如果cur大于0，说明之前末尾有字符，并且是最后一个单词，直接返回即可，
            // 否则，说明前面碰到的都是空格，继续往下判断，直到碰到字符 ，cur开始计数
            if (chars[i] == ' ') {
                if (cur > 0) {
                    return cur;
                }
                continue;
            }
            cur++;
        }
        // 可能只有开头一个单词或者没有单词，同样返回cur即可
        return cur;
    }
}
