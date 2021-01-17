package mytest.algorithm.slidingwindow;

/**
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 * 上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。 感谢 Marcos 贡献此图。
 * <p>
 * 示例:
 * <p>
 * 输入: [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出: 6
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/trapping-rain-water
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class HoldRainWater {
    public static void main(String[] args) {
        int[] arr = new int[]{5, 2, 6, 12, 34, 3, 9, 1, 7, 4, 8, 11, 23, 7, 12};
        System.out.println(trap(arr));
    }

    public static int trap(int[] arr) {
        if (arr.length <= 0) {
            return 0;
        }

        // 记录左右最大值
        int leftMax = arr[0];
        int rightMax = arr[arr.length - 1];

        int total = 0;

        // 双指针遍历，如果左边最大值小于右边最大值，则可以确定 该位置蓄水量和 leftMax 相关，因为右侧实际的最大值一定是 大于等于 rightMax ，
        // 处理左循环即可，否则处理右循环
        for (int i = 1, j = arr.length - 2; i <= j; ) {
            // 左边最大值 小于右边最大值，计算左边的 i 位置的蓄水量
            if (leftMax < rightMax) {
                // i 位置比左边最大值还大或者相同，此时是无法蓄水的，替换leftMax，否则可以蓄水，蓄水量为 leftMax - arr[i]
                if (arr[i] >= leftMax) {
                    leftMax = arr[i];
                } else {
                    total += (leftMax - arr[i]);
                }
                i++;
            } else {
                if (arr[j] > rightMax) {
                    rightMax = arr[j];
                } else {
                    total += (rightMax - arr[j]);
                }
                j--;
            }
        }
        return total;
    }
}
