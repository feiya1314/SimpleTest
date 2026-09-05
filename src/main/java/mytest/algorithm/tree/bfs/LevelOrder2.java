package mytest.algorithm.tree.bfs;

import mytest.algorithm.tree.TreeNode;

import java.util.*;

/**
 * https://leetcode.cn/problems/binary-tree-level-order-traversal-ii/?envType=problem-list-v2&envId=binary-tree
 * 给你二叉树的根节点 root ，返回其节点值 自底向上的层序遍历 。 （即按从叶子节点所在层到根节点所在的层，逐层从左向右遍历）
 */
public class LevelOrder2 {
    /**
     * 使用队列，进行广度优先遍历,最后直接翻转
     */
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<List<Integer>> result = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            List<Integer> cur = new ArrayList<>();
            result.add(cur);
            // 树的当前层的节点树，把当前的层的所有节点出队
            int curLevelNum = queue.size();
            for (int i = 0; i < curLevelNum; i++) {
                TreeNode head = queue.poll();
                cur.add(head.val);
                if (head.left != null) {
                    queue.offer(head.left);
                }
                if (head.right != null) {
                    queue.offer(head.right);
                }
            }
        }
        return result.reversed();
    }
}
