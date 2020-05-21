package mytest.algorithm.tree;

public class BNode<T extends Comparable> {
    public BNode<T> left;
    public BNode<T> right;
    public T data;

    public BNode(BNode<T> left, BNode<T> right, T data) {
        this.left = left;
        this.right = right;
        this.data = data;
    }

    public BNode(T data) {
        this(null, null, data);
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }
}
