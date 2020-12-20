package mytest.algorithm.string;

/**
 * 实现 strStr() 函数。
 *
 * 给定一个 haystack 字符串和一个 needle 字符串，在 haystack 字符串中找出 needle 字符串出现的第一个位置 (从0开始)。如果不存在，则返回  -1。
 *
 * 示例 1:
 *
 * 输入: haystack = "hello", needle = "ll"
 * 输出: 2
 * 示例 2:
 *
 * 输入: haystack = "aaaaa", needle = "bba"
 * 输出: -1
 * 说明:
 *
 * 当 needle 是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。
 *
 * 对于本题而言，当 needle 是空字符串时我们应当返回 0 。这与C语言的 strstr() 以及 Java的 indexOf() 定义相符。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/implement-strstr
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @author : yufei
 * @date : 2020/12/16 14:19
 * @description :
 */
public class IndexOfString {
    public int strStr(String haystack, String needle) {
        if (needle.length() < 1) {
            return 0;
        }
        if (haystack == null || haystack.isEmpty() || haystack.length() < needle.length()) {
            return -1;
        }
        int hp;
        int np = 0;
        for (int i = 0; i < haystack.length() - needle.length() + 1; i++) {
            if (haystack.charAt(i) == needle.charAt(0)) {
                hp = i;
                while (np < needle.length()) {
                    if (haystack.charAt(i + np) == needle.charAt(np)) {
                        if (np == needle.length() - 1) {
                            return hp;
                        }
                        np++;

                    } else {
                        np = 0;
                        break;
                    }
                }
            }
        }
        return -1;
    }

    public int strStr2(String haystack, String needle) {
        if (needle.length() < 1) {
            return 0;
        }
        if (haystack == null || haystack.isEmpty() || haystack.length() < needle.length()) {
            return -1;
        }
        // 只需要比较到haystack.length() - needle.length() + 1，后面的长度不够，肯定不对
        for (int i = 0; i < haystack.length() - needle.length() + 1; i++) {
            if (needle.equals(haystack.substring(i, i + needle.length()))) {
                return i;
            }
        }
        return -1;
    }
}
