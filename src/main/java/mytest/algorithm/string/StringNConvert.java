package mytest.algorithm.string;

import java.util.ArrayList;
import java.util.List;

/**
 * 将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。
 * <p>
 * 比如输入字符串为 "LEETCODEISHIRING" 行数为 3 时，排列如下：
 * <p>
 * L   C   I   R
 * E T O E S I I G
 * E   D   H   N
 * 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："LCIRETOESIIGEDHN"。
 * <p>
 * 请你实现这个将字符串进行指定行数变换的函数：
 * <p>
 * string convert(string s, int numRows);
 * 示例 1:
 * <p>
 * 输入: s = "LEETCODEISHIRING", numRows = 3
 * 输出: "LCIRETOESIIGEDHN"
 * 示例 2:
 * <p>
 * 输入: s = "LEETCODEISHIRING", numRows = 4
 * 输出: "LDREOEIIECIHNTSG"
 * 解释:
 * <p>
 * L     D     R
 * E   O E   I I
 * E C   I H   N
 * T     S     G
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/zigzag-conversion
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : yufei
 * @date : 2020/12/11 14:55
 * @description :
 */
public class StringNConvert {
    public static void main(String[] args) {

    }

    /**
     * 整体思路是遍历字符串，用numRows 行存储，字符存到对应行
     */
    public static String convert(String s, int numRows) {
        if (s.length() < 3 || numRows < 2) {
            return s;
        }
        List<StringBuilder> sbl = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            sbl.add(new StringBuilder());
        }
        sbl.get(0).append(s.charAt(0));
        boolean down = true;

        int curIndex = 1;
        int maxIndex = numRows - 1; // list索引最大值
        for (int i = 1; i < s.length(); i++) {
            // curIndex 记录当前位于第几行
            sbl.get(curIndex).append(s.charAt(i));
            // 处理完当前的字符后，计算下一次的字符所在的行的索引 curIndex
            int index = curIndex % maxIndex;
            // 每次 index == 0 时，说明到转折点了，每numRows转折一次，并且更新下次需要存储的行的位置curIndex
            // 如果是垂直向下的情况
            if (down) {
                // 转折点的时候
                if (index == 0) {
                    down = false;
                    curIndex--;
                } else {
                    curIndex++;
                }
            } else {
                if (index == 0) {
                    down = true;
                    curIndex++;
                } else {
                    curIndex--;
                }
            }
        }
        StringBuilder resultString = new StringBuilder();
        for (StringBuilder sb : sbl) {
            resultString.append(sb);
        }
        return resultString.toString();
    }
}
