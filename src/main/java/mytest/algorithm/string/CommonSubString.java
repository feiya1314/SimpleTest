package mytest.algorithm.string;

/**
 * 最长公共子串（Longest Common Substring）： 是指两个字符串中最长连续相同的子串长度。
 * <p>
 * 例如：str1=“1AB2345CD”,str2=”12345EF”,则str1，str2的最长公共子串为2345。
 *  https://zhuanlan.zhihu.com/p/62521862
 *
 *  https://www.nowcoder.com/questionTerminal/5bb66d2ceb3a433aa6e1c3e254554b15
 *
 * @author : feiya
 * @date : 2021/2/21
 * @description :
 */
public class CommonSubString {

    public static void main(String[] args) throws Exception {
        /*BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String str1 = bf.readLine();
        String str2 = bf.readLine();
        String longger = str1.length() > str2.length() ? str1 : str2;
        String shortter = str1.equals(longger) ? str2 : str1;
        int maxLen = 0;
        String result = "";
        for (int i = 0; i < shortter.length(); i++) {
            for (int j = i + 1; j <= shortter.length(); j++) {
                String sub = shortter.substring(i, j);
                if (longger.contains(sub) && j - i > maxLen) {
                    result = sub;
                    maxLen = j - i;
                }
            }
        }
        System.out.println(maxLen);
        System.out.println(result);
        bf.close();*/

        System.out.println(new CommonSubString().longestCommonSubsequence("abcdef", "gcdeh"));
    }

    // 暴力解法，在短的字符串中从前往后找是否在长串中
    public int longestCommonSubsequence(String text1, String text2) {
        String longger = text1.length() > text2.length() ? text1 : text2;
        String shortter = text1.equals(longger) ? text2 : text1;
        int maxLen = 0;
        String result = "";
        for (int i = 0; i < shortter.length(); i++) {
            for (int j = i + 1; j <= shortter.length(); j++) {
                String sub = shortter.substring(i, j);
                // 如果不包含 sub的，则后续的sub开头的都不必再遍历
                if (!longger.contains(sub)) {
                    break;
                }
                if (longger.contains(sub) && j - i > maxLen) {
                    result = sub;
                    maxLen = j - i;
                }
            }
        }
        System.out.println(result);
        return maxLen;
    }

    // 滑动窗口
    public int longestCommonSubsequence2(String text1, String text2) {
        // todo
        return 0;
    }
}
