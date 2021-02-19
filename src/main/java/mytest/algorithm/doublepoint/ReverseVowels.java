package mytest.algorithm.doublepoint;

/**
 * 编写一个函数，以字符串作为输入，反转该字符串中的元音字母。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入："hello"
 * 输出："holle"
 * 示例 2：
 * <p>
 * 输入："leetcode"
 * 输出："leotcede"
 *  
 * <p>
 * 提示：
 * <p>
 * 元音字母不包含字母 "y"
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/reverse-vowels-of-a-string
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/9
 * @description :
 */
public class ReverseVowels {
    public String reverseVowels(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        char[] chars = s.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        while (left < right) {
            char leftC = chars[left];
            if (!isVowel(leftC)) {
                left++;
                continue;
            }
            char rightC = chars[right];
            if (!isVowel(rightC)) {
                right--;
                continue;
            }

            chars[left] = rightC;
            chars[right] = leftC;
            left++;
            right--;
        }
        return String.valueOf(chars);
    }

    private boolean isVowel(char alphabet) {
        if (alphabet == 'a' || alphabet == 'A' || alphabet == 'e' || alphabet == 'E' || alphabet == 'i' || alphabet == 'I'
                || alphabet == 'o' || alphabet == 'O' || alphabet == 'u' || alphabet == 'U') {
            return true;
        }
        return false;
    }
}
