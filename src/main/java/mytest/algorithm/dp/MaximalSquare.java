package mytest.algorithm.dp;

/**
 * 在一个由 '0' 和 '1' 组成的二维矩阵内，找到只包含 '1' 的最大正方形，并返回其面积。
 * 输入：matrix = [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
 * 输出：4
 * 提示：
 * <p>
 * m == matrix.length
 * n == matrix[i].length
 * 1 <= m, n <= 300
 * matrix[i][j] 为 '0' 或 '1'
 *
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/maximal-square
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/3/14
 * @description :
 */
public class MaximalSquare {
    // 动态规划，dp[i][j]中记录 以[i][j]为右下角的最大正方形的边长
    // 如果[i][j]为0，则此位置面积肯定为0，不符合全部为 1 的正方形
    // dp[i][j] 位置最大值取决于左 、上、左上对角线位置的最小值，
    // 也即是dp[i][j] = min(dp[i-1][j-1] , dp[i][j-1] ,dp[i-1][j]) + 1
    public int maximalSquare(char[][] matrix) {
        // 如果数组长度宽或者高为0，则面积为0
        if (matrix == null || matrix.length < 1 || matrix[0].length < 1) {
            return 0;
        }
        int height = matrix.length;
        int width = matrix[0].length;
        // 从 1 开始，0坐标，记录 matrix 边界外的虚拟值，都为 0 ，边界最大面积为 1
        int[][] dp = new int[height + 1][width + 1];
        int max = 0;
        // 遍历每一个位置，计算以每个位置作为正方形的右下角所能组成的正方形边长最大值
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 只有当前为1时才可能组成为全部为1的正方形
                if (matrix[i][j] == '1') {
                    // 使用 dp[i + 1][j + 1] 避免考虑边界值情况，不然需要先遍历边界，计算边界位置的长度
                    // dp[i + 1][j + 1] 值取决于左 、上、左上对角线位置的最小值，最小值才能保证组成的正方形全部为 1
                    dp[i + 1][j + 1] = Math.min(Math.min(dp[i][j], dp[i + 1][j]), dp[i][j + 1]) + 1;
                    max = Math.max(dp[i + 1][j + 1], max);
                }
            }
        }

        return max * max;
    }
}
