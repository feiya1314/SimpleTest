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
        if (root == null) {
            return 0;
        }
        // 当前节点已经是叶子节点，则这个节点的深度是 1
        if (root.left == null && root.right == null) {
            return 1;
        }
        // 左子节点的深度
        int left = minDepthRev(root.left);
        // 右子节点的深度
        int right = minDepthRev(root.right);

        // 左节点是空，则当前的节点的深度是右子节点的深度加上当前节点的深度1
        if (root.left == null) {
            return right + 1;
        }
        if (root.right == null) {
            return left + 1;
        }

        // 左右两节点都不为空，则当前节点的最小深度是左右两个的最小深度加上当前节点
        return Math.min(left, right) + 1;
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
            // queue的当前size，就是当前层的节点数量
            int curSize = queue.size();

            // 遍历当前的层的所有节点
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

    // 使用广度优先，记录每个节点所在的深度
    public int minDepth2(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<Pair<TreeNode, Integer>> queue = new ArrayDeque<>();
        queue.offer(Pair.of(root, 1));
        while (!queue.isEmpty()) {
            Pair<TreeNode, Integer> head = queue.poll();
            // 碰到的第一个叶子节点，深度则就是最小的深度
            if (head.getLeft().left == null && head.getLeft().right == null) {
                return head.getRight();
            }

            if (head.getLeft().left != null) {
                queue.offer(Pair.of(head.getLeft().left, head.getRight() + 1));
            }

            if (head.getLeft().right != null) {
                queue.offer(Pair.of(head.getLeft().right, head.getRight() + 1));
            }
        }
        return -1;
    }

    static class Pair<T, R> {
        private T left;
        private R right;

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
