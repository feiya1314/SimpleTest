package mytest.algorithm.dichotomy;

/**
 * 实现 int sqrt(int x) 函数。
 * <p>
 * 计算并返回 x 的平方根，其中 x 是非负整数。
 * <p>
 * 由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 4
 * 输出: 2
 * 示例 2:
 * <p>
 * 输入: 8
 * 输出: 2
 * 说明: 8 的平方根是 2.82842...,
 *      由于返回类型是整数，小数部分将被舍去。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sqrtx
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/9/5
 * @description :
 */
public class MySqrt {
    public static void main(String[] args) {
        System.out.println(mySqrt(9));
    }

    public static int mySqrt(int x) {
        // 除去0和1外 一个数的平方根一定不会超过它的一半
        if (x < 2) {
            return x;
        }
        int mid = x / 2;
        int left = 1;
        // 除去0和1外 一个数的平方根一定不会超过它的一半 所以，以一半作为右边界
        int right = mid;
        while (left <= right) {
            mid = left + (right - left) / 2;
            // 考虑溢出的情况
            //long ms = (long) mid * mid;
            // 或者直接使用
             if(mid<=x/mid){
            //if (ms <= x) {
                // 考虑溢出的情况
                //long rs = (long) (mid + 1) * (mid + 1);
                // 或者直接使用
                 if(mid+1 > x/(mid+1)){
               // if (rs > x) {
                    return mid;
                }
                left = mid + 1;
                continue;
            }
            right = mid - 1;
        }

        return -1;
    }
}
