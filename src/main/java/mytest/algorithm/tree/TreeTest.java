package mytest.algorithm.tree;

import mytest.algorithm.Util;

public class TreeTest {
    public static void main(String[] args) {
        BSearchTree<Integer> tree = new BSearchTree<>();
        tree.insert(15);
        tree.insert(5);
        tree.insert(16);
        tree.insert(3);
        tree.insert(12);
        tree.insert(10);
        tree.insert(20);
        tree.insert(18);
        tree.insert(23);
        tree.insert(6);
        tree.postOrder();
        //System.out.println(tree.size());
        Util.printNode(tree.getRoot());
    }
}
