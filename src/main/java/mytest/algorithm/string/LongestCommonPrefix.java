package mytest.algorithm.string;

/**
 * 编写一个函数来查找字符串数组中的最长公共前缀。
 *
 * 如果不存在公共前缀，返回空字符串 ""。
 *
 * 示例 1:
 *
 * 输入: ["flower","flow","flight"]
 * 输出: "fl"
 * 示例 2:
 *
 * 输入: ["dog","racecar","car"]
 * 输出: ""
 * 解释: 输入不存在公共前缀。
 * 说明:
 *
 * 所有输入只包含小写字母 a-z 。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-common-prefix
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @author : yufei
 * @date : 2020/12/14 14:02
 * @description :
 */
public class LongestCommonPrefix {
    public static void main(String[] args) {
        String[] strings = new String[]{"flower", "flow", "flight"};
        System.out.println(longestCommonPrefix(strings));
    }

    public static String longestCommonPrefix(String[] strs) {
        if (strs.length < 1) {
            return "";
        }
        if (strs.length == 1) {
            return strs[0];
        }

        StringBuilder result = new StringBuilder();
        boolean end = false; //是否结束
        int index = 0; //前缀索引
        while (!end && index < strs[0].length()) {
            char c = strs[0].charAt(index); // 以
            for (String str : strs) {
                if (index > str.length() - 1) {
                    end = true;
                    break;
                }
                if (str.charAt(index) != c) {
                    end = true;
                    break;
                }
            }
            index++;
            if (!end) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
