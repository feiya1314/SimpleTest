package mytest.algorithm.tree.bfs;

import mytest.algorithm.tree.TreeNode;

import java.util.*;

/**
 * https://leetcode.cn/problems/binary-tree-level-order-traversal/?envType=problem-list-v2&envId=binary-tree
 * 给你二叉树的根节点 root ，返回其节点值的 层序遍历 。 （即逐层地，从左到右访问所有节点）。
 * <p>
 * 输入：root = [3,9,20,null,null,15,7]
 * 输出：[[3],[9,20],[15,7]]
 */
public class LevelOrder {
    /**
     * 使用队列，进行广度优先遍历
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
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
        return result;
    }
}
