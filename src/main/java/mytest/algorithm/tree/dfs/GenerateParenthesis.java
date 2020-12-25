package mytest.algorithm.tree.dfs;

import java.util.ArrayList;
import java.util.List;

/**
 * //数字 n 代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且 有效的 括号组合。
 * //
 * //
 * //
 * // 示例：
 * //
 * // 输入：n = 3
 * //输出：[
 * //       "((()))",
 * //       "(()())",
 * //       "(())()",
 * //       "()(())",
 * //       "()()()"
 * //     ]
 * //
 * // Related Topics 字符串 回溯算法
 * <p>
 * https://leetcode-cn.com/problems/generate-parentheses/
 *
 * @author : yufei
 * @date : 2020/12/25 10:18
 * @description :
 */
public class GenerateParenthesis {
    List<String> result = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(new GenerateParenthesis().generateParenthesis(3));
    }

    // todo 理解原理
    public List<String> generateParenthesis(int n) {
        dfs(n, n, "");
        return result;
    }

    // 递归过程 ((( ))) -> (( 此时 left还剩一个，跳出之后进入right > left分支
    // -> (()()) 之后 类似
    private void dfs(int left, int right, String str) {
        if (left == 0 && right == 0) {
            result.add(str);
            return;
        }

        // 保证 ( 在第一位
        if (left > 0) { // 如果左括号还剩余的话，可以拼接左括号
            dfs(left - 1, right, str + "(");
        }
        // 保证 ( 数量要比 ) 多，因为如果 ）数量多于 （ 则一定不合法了
        // 比如 (()))这个时候无论如何在后面追加都不会合法
        if (right > left) { // 如果右括号剩余多于左括号剩余的话，可以拼接右括号
            dfs(left, right - 1, str + ")");
        }
    }
}
