package mytest.algorithm.tree;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * https://juejin.im/post/5b8d64346fb9a01a1d4f99fa
 * <p>
 * https://leetcode.cn/problems/invert-binary-tree/description/?envType=problem-list-v2&envId=binary-tree
 */
public class TraverseTree {
    public TreeNode invertTree(TreeNode root) {
        exchange(root);
        return root;
    }

    // 使用递归实现，对每个节点进行反转
    private void exchange(TreeNode root) {
        if (root == null) {
            return;
        }
        exchangeNode(root);
        exchange(root.left);
        exchange(root.right);
    }

    // 队列实现，对每一层，每个节点进行反转
    public TreeNode invertTree2(TreeNode root) {
        if (root == null) {
            return root;
        }
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode head = queue.poll();
            exchangeNode(head);

            if (head.left != null) {
                queue.offer(head.left);
            }
            if (head.right != null) {
                queue.offer(head.right);
            }
        }
        return root;
    }

    private static void exchangeNode(TreeNode head) {
        TreeNode temp = head.left;
        head.left = head.right;
        head.right = temp;
    }
}
