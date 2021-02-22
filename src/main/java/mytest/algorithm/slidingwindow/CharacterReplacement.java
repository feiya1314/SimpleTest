package mytest.algorithm.slidingwindow;

/**
 * 给你一个仅由大写英文字母组成的字符串，你可以将任意位置上的字符替换成另外的字符，总共可最多替换 k 次。在执行上述操作后，找到包含重复字母的最长子串的长度。
 * <p>
 * 注意：字符串长度 和 k 不会超过 104。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：s = "ABAB", k = 2
 * 输出：4
 * 解释：用两个'A'替换为两个'B',反之亦然。
 * 示例 2：
 * <p>
 * 输入：s = "AABABBA", k = 1
 * 输出：4
 * 解释：
 * 将中间的一个'A'替换为'B',字符串变为 "AABBBBA"。
 * 子串 "BBBB" 有最长重复字母, 答案为 4。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-repeating-character-replacement
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/12
 * @description :
 */
public class CharacterReplacement {
    // 维护一个滑动窗口，窗口中 除了出现次数最多的字符外，其他字符只能出现 k 次
    public int characterReplacement(String s, int k) {
        if (s.length() < 2) {
            return s.length();
        }
        char[] chars = s.toCharArray();
        int[] freq = new int[26];
        int result = 0;
        int maxCount = 0;
        int left = 0;
        int right = 0;
        //  [left, right) 内最多替换 k 个字符可以得到只有一种字符的子串
        while (right < chars.length) {
            freq[chars[right] - 'A'] += 1;
            // 在这里维护 maxCount，因为每一次右边界读入一个字符，字符频数增加，才会使得 maxCount 增加
            // 每次右边读入字符时，更新滑动窗口 ，更新出现频率最多的次数，用于判断是否需要移动窗口左边界
            maxCount = Math.max(maxCount, freq[chars[right] - 'A']);
            right++;

            if (right - left > maxCount + k) {
                // 说明此时 k 不够用,也就是替换的次数超过k了
                // 把其它不是最多出现的字符替换以后，都不能填满这个滑动的窗口，这个时候须要考虑左边界向右移动
                // 移出滑动窗口的时候，频数数组须要相应地做减法
                freq[chars[left] - 'A'] -= 1;
                left++;
            }

            result = Math.max(result, right - left);
        }
        return result;
    }
}
