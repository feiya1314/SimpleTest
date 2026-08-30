package mytest.algorithm.tree;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * https://leetcode.cn/problems/maximum-depth-of-binary-tree/
 * <p>
 * 给定一个二叉树 root ，返回其最大深度。
 * <p>
 * 二叉树的 最大深度 是指从根节点到最远叶子节点的最长路径上的节点数。
 */
public class MaxDepth {
    //recursion 递归实现,先递归到最左侧的树，找出左侧树的最大深度，
    public int maxDepth(TreeNode root) {
        // 叶子节点了，子树的最后了，没有节点
        if (root == null) {
            return 0;
        }
        // 左树的最大深度
        int leftMax = maxDepth(root.left);
        // 右树的最大深度
        int rightMax = maxDepth(root.right);

        // 左和右树最大值加1，就是最大深度
        return Math.max(leftMax, rightMax) + 1;
    }

    // 队列实现，核心是遍历每一层
    public int maxDepth3(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int max = 1;
        // 需要记录每一层的节点所在的深度，队列先进先出
        Queue<Pair<TreeNode, Integer>> queue = new ArrayDeque<>();
        queue.offer(Pair.of(root, 1));
        while (!queue.isEmpty()) {
            // 出队列
            Pair<TreeNode, Integer> head = queue.poll();
            if (head == null) {
                continue;
            }
            // 如果左右都没有节点，说明是叶子节点，没有更下一层了，深度就是当前节点的深度
            if (head.getLeft().left == null && head.getLeft().right == null) {
                continue;
            }
            if (head.getLeft().left != null) {
                queue.offer(Pair.of(head.getLeft().left, head.getRight() + 1));
            }
            if (head.getLeft().right != null) {
                queue.offer(Pair.of(head.getLeft().right, head.getRight() + 1));
            }
            max = Math.max(max, head.getRight() + 1);
        }
        return max;
    }

    // 堆栈实现
    public int maxDepth2(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int max = 1;
        // 需要记录每一层的节点所在的深度，堆栈先进后出
        Deque<Pair<TreeNode, Integer>> stack = new ArrayDeque<>();
        stack.push(Pair.of(root, 1));
        while (!stack.isEmpty()) {
            // 出栈
            Pair<TreeNode, Integer> top = stack.poll();
            if (top == null) {
                continue;
            }

            // 如果左右都没有节点，说明是叶子节点，没有更下一层了，深度就是当前节点的深度
            if (top.getLeft().left == null && top.getLeft().right == null) {
                continue;
            }

            // 左右节点入栈，同时下一节点的深度要加1
            if (top.getLeft().left != null) {
                stack.push(Pair.of(top.getLeft().left, top.getRight() + 1));
            }

            if (top.getLeft().right != null) {
                stack.push(Pair.of(top.getLeft().right, top.getRight() + 1));
            }
            // 因为有下一层节点，所以更新下最大的深度
            max = Math.max(max, top.getRight() + 1);
        }

        // 最终遍历完后，最大的深度也就计算完成
        return max;
    }

    static class Pair<T, R> {
        T left;
        R right;

        public static <T, R> Pair<T, R> of(T left, R right) {
            Pair<T, R> p = new Pair<>();
            p.left = left;
            p.right = right;
            return p;
        }

        public T getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }
}
