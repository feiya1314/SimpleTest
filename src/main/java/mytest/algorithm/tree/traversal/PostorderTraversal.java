package mytest.algorithm.tree.traversal;

import mytest.algorithm.tree.TreeNode;

import java.util.*;

/**
 * https://leetcode.cn/problems/binary-tree-postorder-traversal/?envType=problem-list-v2&envId=depth-first-search
 * <p>
 * 给你一棵二叉树的根节点 root ，返回其节点值的 后序遍历 。
 */
public class PostorderTraversal {
    public List<Integer> postorderTraversal(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        TreeNode pre = root;
        while (cur != null || !stack.isEmpty()) {
            if (cur != null) {
                stack.push(cur);
                cur = cur.left;
            } else {
                // 这里左树已经访问过了，但是可能还有右树
                TreeNode tmp = stack.pop();
                // 这里由于是左右根的顺序，先访问右，然后退回来访问根，由于肯定有右子树，
                // 需要判断右子树是否已经访问过，访问过的不再访问右子树，不然形成死循环了
                if (tmp.right == null || pre == tmp.right) {
                    result.add(tmp.val);
                    // 右节点访问后要记录，下一次出栈的是当前节点的上级节点，肯定有右子树，需要记录已访问
                    pre = tmp;
                } else {
                    // 如果还有右节点，需要把当前节点重新放进栈中，因为当前节点要在右节点后才能读取，
                    // 所以要重新放回去，去处理右子树
                    stack.push(tmp);
                    cur = tmp.right;
                }
            }
        }
        return result;
    }

    public List<Integer> postorderTraversal2(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<>();
        recursion(root, result);

        return result;
    }

    // 递归实现，中序遍历，中序遍历，根节点是在最后遍历，先左后右，最后根节点
    // 把复杂问题转换多个小问题，每个节点的子树，和整个树的逻辑一样，每个节点和子树，都是先访问左再右最后中
    private void recursion(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        recursion(root.left, result);
        recursion(root.right, result);
        result.add(root.val);
    }
}
