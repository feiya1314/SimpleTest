package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * //n 皇后问题研究的是如何将 n 个皇后放置在 n×n 的棋盘上，并且使皇后彼此之间不能相互攻击。
 * //
 * //
 * //
 * // 上图为 8 皇后问题的一种解法。
 * //
 * // 给定一个整数 n，返回所有不同的 n 皇后问题的解决方案。
 * //
 * // 每一种解法包含一个明确的 n 皇后问题的棋子放置方案，该方案中 'Q' 和 '.' 分别代表了皇后和空位。
 * //
 * //
 * //
 * // 示例：
 * //
 * // 输入：4
 * //输出：[
 * // [".Q..",  // 解法 1
 * //  "...Q",
 * //  "Q...",
 * //  "..Q."],
 * //
 * // ["..Q.",  // 解法 2
 * //  "Q...",
 * //  "...Q",
 * //  ".Q.."]
 * //]
 * //解释: 4 皇后问题存在两个不同的解法。
 * //
 * //
 * //
 * //
 * // 提示：
 * //
 * //
 * // 皇后彼此不能相互攻击，也就是说：任何两个皇后都不能处于同一条横行、纵行或斜线上。
 * //
 * // Related Topics 回溯算法
 * https://leetcode-cn.com/problems/n-queens/
 *
 * @author : yufei
 * @date : 2020/12/29 13:12
 * @description :
 */
public class NQueens {
    private List<List<String>> result = new ArrayList<>();
    private boolean[] used;
    private int[] usedPosition;

    public static void main(String[] args) {
        System.out.println(new NQueens().solveNQueens(4));
    }

    // 用char[][]数组作为棋盘，Q所在位置作为在棋盘上的位置
    // 每一层只能放一个Q，递归每层Q可以放的位置，并判断放的位置是或合法，不合法则剪枝
    public List<List<String>> solveNQueens(int n) {
        // board 用于跟踪每次递归,每层在指定位置写入 Q，类似排列中的 track
        char[][] board = new char[n][n];
        // used记录已经写入Q的位置
        used = new boolean[n];
        // usedPosition 记录每一层写入 Q 的所在坐标
        usedPosition = new int[n];
        for (int i = 0; i < n; i++) {
            usedPosition[i] = Integer.MIN_VALUE;
        }
        for (int i = 0; i < n; i++) {
            Arrays.fill(board[i], '.');
        }
        backtrack(board, n, 0);
        return result;
    }

    private void backtrack(char[][] board, int n, int level) {
        // 能到达最后一层，说明前面校验都合适，满足要求
        if (level >= n) {
            List<String> strings = new ArrayList<>();
            for (char[] line : board) {
                strings.add(new String(line));
            }

            result.add(strings);
            return;
        }

        for (int i = 0; i < n; i++) {
            // 校验 i 这个位置是否满足NQ的要求，不能和之前的同行、同列、斜对面
            if (!isInvalid(i, level)) {
                continue;
            }
            // 在这一层i的位置处写入Q
            board[level][i] = 'Q';
            // i位置 标记为已使用
            used[i] = true;
            // 记录此层写入Q的位置
            usedPosition[level] = i;
            backtrack(board, n, level + 1);
            // 递归后恢复，用于下一次循环
            used[i] = false;
            usedPosition[level] = Integer.MIN_VALUE;
            board[level][i] = '.';
        }
    }

    private boolean isInvalid(int i, int level) {
        // 在i位置已经写入过，则此列不能在写入
        if (used[i]) {
            return false;
        }

        // 遍历此层之前的写入的位置，后面的没必要遍历
        // 如果位于之前的位置的斜线上，则不合法
        for (int j = 0; j < level; j++) {
            // 根据每一层Q的写入位置，判断 i 是否合法，例如第0层写入位置如果是2，
            // 当前层是第三层，那么如果i是2+3-0=5的话，正好是斜线上，不合法
            if (usedPosition[j] - level + j == i || usedPosition[j] + level - j == i) {
                return false;
            }
        }
        return true;
    }
}
