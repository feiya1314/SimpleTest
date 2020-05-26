package mytest.algorithm.dp;

/**
 * 给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。
 * <p>
 * 示例 1：
 * <p>
 * 输入: "babad"
 * 输出: "bab"
 * 注意: "aba" 也是一个有效答案。
 * 示例 2：
 * <p>
 * 输入: "cbbd"
 * 输出: "bb"
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-palindromic-substring
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Palindrome {
    public static void main(String[] args) {
        System.out.println(find2("abcdedcba"));
    }

    // 暴力法需要遍历所有大于 1 的字符串，还需要判断子串是不是回文 复杂度 N^3

    // dp 实现 时间复杂度：O(N^{2}）

    //空间复杂度：O(N^{2})

    // )，二维 dp 问题，一个状态得用二维有序数对表示，因此空间复杂度是 O(N^{2})O(N

    public static String find(String ori) {
        if (ori.length() < 2) {
            return ori;
        }
        // dp[i][j] 是以i开头，j结尾的字符串，如果是回文，则为 true 否则为 false
        boolean[][] dp = new boolean[ori.length()][ori.length()];

        // i 和 j相同时，此时是同一个字符，一定是回文
        for (int i = 0; i < ori.length(); i++) {
            dp[i][i] = true;
        }

        //dp[i][j] = dp[i+1]dp[j-1] && s[i]==s[j]
        // 注意遍历的顺序，需要先确定 j ，再改变 i ，因为用到了 i+1, 如果从 i 固定，因为 i+1 还没有判断过，不知道是否是回文，
        // 而 j - 1 是已经判断过的
        int length = 1;
        int start = 0;
        for (int j = 1; j < ori.length(); j++) {
            for (int i = 0; j > i; i++) {
                // abcdedcba
                // i 和 j 处的字符不同，一定不是回文
                if (ori.charAt(i) != ori.charAt(j)) {
                    dp[i][j] = false;

                } else if (j - i < 3) { // 相等的话，那么就是 bab 或者bb 一定是回文
                    dp[i][j] = true;

                } else {
                    dp[i][j] = dp[i + 1][j - 1];  // dp[i + 1][j - 1] 是回文的话，那么 i j 也是回文
                }

                // 如果是回文的话，如果比之前的回文长，就记录一下
                if (dp[i][j] && (j - i + 1) > length) {
                    length = j - i + 1;
                    start = i;
                }
            }
        }

        return ori.substring(start, start + length);
    }

    // 中心扩散法  遍历每一个索引，以这个索引为中心，利用“回文串”中心对称的特点，往两边扩散，看最多能扩散多远。
    public static String find2(String ori) {
        if (ori.length() < 2) {
            return ori;
        }

        int maxLength = 0;
        String result = null;
        for (int i = 0; i < ori.length(); i++) {
            // i 有可能是回文字符串中间的值，例如 aba中的b，也可能是位于中间的其中一个，例如abba 中的 b
            String odd = centerSpread(ori, i, i); // 回文串为奇数的情况，从 i开始扩散
            String even = centerSpread(ori, i, i + 1);// 回文串为偶数的情况，从 i 和 i+1 开始扩散
            String le = odd.length() > even.length() ? odd : even;
            if (le.length() > maxLength) {
                maxLength = le.length();
                result = le;
            }
        }
        return result;
    }

    public static String centerSpread(String ori, int left, int right) {
        while (left >= 0 && right < ori.length()) {
            if (ori.charAt(left) == ori.charAt(right)) {  // 如果目前是回文，就左右扩散，直到不是回文结束
                left--;
                right++;
            } else {
                break;
            }
        }
        return ori.substring(left + 1, right); // 返回的一定是回文字符串
    }
}
