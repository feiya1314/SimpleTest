package mytest.algorithm.tree.traversal;

import mytest.algorithm.tree.TreeNode;

import java.util.*;

/**
 * https://leetcode.cn/problems/binary-tree-preorder-traversal/
 * 给你二叉树的根节点 root ，返回它节点值的 前序 遍历。
 */
public class PreorderTraversal {
    // 堆栈方式实现
    public List<Integer> preorderTraversal2(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode top = stack.pop();
            // 栈先进后出，所以先访问当前值，再填右节点，最后左节点，那么左侧的先访问
            result.add(top.val);
            if (top.right != null) {
                stack.push(top.right);
            }
            if (top.left != null) {
                stack.push(top.left);
            }
        }
        return result;
    }

    public List<Integer> preorderTraversal(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        recursion(root, result);
        return result;
    }

    // 递归方式实现，先访问根节点，再左再右
    private void recursion(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        // 先访问根节点
        result.add(root.val);
        // 遍历左树和右树
        recursion(root.left, result);
        recursion(root.right, result);
    }
}
