package mytest.algorithm.tree;


import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 给定一个二叉树，找出其最小深度。
 * <p>
 * 最小深度是从根节点到最近叶子节点的最短路径上的节点数量。
 * <p>
 * 说明: 叶子节点是指没有子节点的节点。
 * <p>
 * 示例:
 * <p>
 * 给定二叉树 [3,9,20,null,null,15,7],
 * <p>
 * 3
 * / \
 * 9  20
 * /  \
 * 15   7
 * 返回它的最小深度  2.
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/minimum-depth-of-binary-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class MinDepth {

    // 使用递归
    public int minDepthRev(TreeNode root) {
        return 0;
    }

    // 使用广度优先
    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int count = 0;

        while (!queue.isEmpty()) {
            count++;
            int curSize = queue.size();

            for (int i = 0; i < curSize; i++) {
                TreeNode curNode = queue.poll();
                // 左右子节点都没有时，就到达最小深度了
                if (curNode.left == null && curNode.right == null) {
                    return count;
                }

                if (curNode.left != null) {
                    queue.offer(curNode.left);
                }

                if (curNode.right != null) {
                    queue.offer(curNode.right);
                }
            }
        }

        return count;
    }
}
