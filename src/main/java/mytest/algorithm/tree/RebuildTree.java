package mytest.algorithm.tree;

import mytest.algorithm.Util;

/**
 * 根据 前序/后序+中序序列可以唯一确定一棵二叉树 ，重建二叉树
 * <p>
 * 根据一棵树的前序遍历与中序遍历构造二叉树。
 * <p>
 * 注意:
 * 你可以假设树中没有重复的元素。
 * <p>
 * 例如，给出
 * <p>
 * 前序遍历 preorder = [3,9,20,15,7]
 * 中序遍历 inorder = [9,3,15,20,7]
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class RebuildTree {
    public static void main(String[] args) {
        int[] pre = new int[]{1, 2, 4, 5, 7, 8, 3, 6};
        int[] in = new int[]{4, 2, 7, 5, 8, 1, 3, 6};

        Util.printNode(buildTree(pre, in));
    }

    public static BNode<Integer> buildTree(int[] preOrder, int[] inOrder) {

        return helper(preOrder, 0, preOrder.length - 1, inOrder, 0, inOrder.length - 1);
    }

    // 整体思想是 分别构建左子树和右子树
    private static BNode<Integer> helper(int[] preOrder, int pStart, int pEnd, int[] inOrder, int iStart, int iEnd) {
        if (pStart > pEnd || iStart > iEnd) {
            return null;
        }
        // 根节点
        BNode<Integer> root = new BNode<>(preOrder[pStart]);

        // 找到中序遍历 根节点下标位置
        int i = 0;
        while (inOrder[iStart + i] != preOrder[pStart]) {
            i++;
        }
        // 构建左子树 例如第一次 是  2, 4, 5, 7, 8 为左子树前序遍历结果， 4, 2, 7, 5, 8 为中序遍历结果，不包含当前的根节点 1
        root.left = helper(preOrder, pStart + 1, pStart + i, inOrder, iStart, iStart + i - 1);

        // 构建右子树 例如第一次 是  3, 6为左子树前序遍历结果， 3, 6 为中序遍历结果，不包含当前的根节点 1
        root.right = helper(preOrder, pStart + i + 1, pEnd, inOrder, iStart + i + 1, iEnd);

        return root;
    }
}
