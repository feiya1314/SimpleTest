package mytest.algorithm.sort;

import java.util.Arrays;

/**
 * 输入一个非负整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * 输入: [10,2]
 * 输出: "102"
 * 示例 2:
 * <p>
 * 输入: [3,30,34,5,9]
 * 输出: "3033459"
 *  
 * <p>
 * 提示:
 * <p>
 * 0 < nums.length <= 100
 * 说明:
 * <p>
 * 输出结果可能非常大，所以你需要返回一个字符串而不是整数
 * 拼接起来的数字可能会有前导 0，最后结果不需要去掉前导 0
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/ba-shu-zu-pai-cheng-zui-xiao-de-shu-lcof
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/9/5
 * @description :
 */
public class MinNumber {
    /**
     * 首先要判断两个数怎么才是组合成最小的
     * 3 ， 30  330 > 303 所以 30 要排在 3的前面，以此类推
     * 如 3 32 30  ， 32 需要在 3 前面 也即是 32  < 3  (即323 < 332) 同样 30  < 32 可以推断出 30 < 3 满足传递性
     * <p>
     * 即按照 一个排序规则，对数组排序即可，规则 为 x + y < y + x (这里加号是连接的意思) 则 x<y 也即是自定义排序规则即可
     *
     * @param nums
     * @return
     */
    public static String minNumber(int[] nums) {
        String[] numStr = new String[nums.length];

        for (int i = 0; i < nums.length; i++) {
            numStr[i] = String.valueOf(nums[i]);
        }

        // 自定义排序规则 (x + y) < (y + x) 则 x < y
        // Comparator 接口 如果 表达式 (a1, a2)
        Arrays.sort(numStr, (x, y) -> (x + y).compareTo(y + x));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nums.length; i++) {
            sb.append(numStr[i]);
        }

        return sb.toString();
    }
}
