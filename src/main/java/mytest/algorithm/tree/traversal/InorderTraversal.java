package mytest.algorithm.tree.traversal;

import mytest.algorithm.tree.TreeNode;

import java.util.*;

/**
 * https://leetcode.cn/problems/binary-tree-inorder-traversal/?envType=problem-list-v2&envId=depth-first-search
 * <p>
 * 给定一个二叉树的根节点 root ，返回 它的 中序 遍历 。
 */
public class InorderTraversal {
    public List<Integer> inorderTraversal(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (!stack.isEmpty() || cur != null) {
            // 先把左节点入栈
            if (cur != null) {
                stack.push(cur);
                cur = cur.left;
            } else {
                // 左侧节点已经入栈，需要处理右节点
                TreeNode c = stack.pop();
                // 由于是中序，所以先要访问根节点，再如栈右节点
                result.add(c.val);
                cur = c.right;
            }
        }
        return result;
    }

    // 使用堆栈实现
    public List<Integer> inorderTraversal3(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        TreeNode cur = root;
        while (!stack.isEmpty()) {
            // 先把左子树进栈
            while (cur != null) {
                // 已经到左子树叶子了，结束
                if (cur.left == null) {
                    break;
                }
                stack.push(cur.left);
                cur = cur.left;
            }
            cur = stack.pop();
            // 当前节点是最左的节点了（也是以该节点为根的节点，但是可能有右子树），访问值
            result.add(cur.val);
            // 节点可能还有右节点，需要将右边的节点入栈，因为是左中右的顺序，
            if (cur.right != null) {
                stack.push(cur.right);
                // 此时cur是右节点了，下次需要遍历这个节点
                cur = cur.right;
            } else {
                // 没有右子树，是叶子节点了，不需要再处理左子树，直接置空，出栈上一个节点
                cur = null;
            }
        }

        return result;
    }

    public List<Integer> inorderTraversal2(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        recursion(root, result);

        return result;
    }

    // 递归实现，中序遍历，中序遍历，根节点是在中间遍历，先左后中，最后右
    private void recursion(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }

        recursion(root.left, result);
        result.add(root.val);
        recursion(root.right, result);
    }
}
