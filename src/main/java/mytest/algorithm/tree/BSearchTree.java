package mytest.algorithm.tree;

public class BSearchTree<T extends Comparable<? super T>> implements Tree<T> {
    private BNode<T> root;

    private int size;

    public BSearchTree() {
        this.root = null;
        this.size = 0;
    }

    public BNode<T> getRoot() {
        return root;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int height() {
        return 0;
    }

    // 前根遍历 前根遍历实际上就是深度优先遍历
    // 首先访问根，再先序遍历左（右）子树，最后先序遍历右（左）子树。
    @Override
    public String preOrder() {
        preOrder(root);
        return null;
    }

    private void preOrder(BNode<T> node){
        if (node == null){
            return ;
        }
        System.out.println(node.data);
        preOrder(node.left);
        preOrder(node.right);
    }
    // 首先中序遍历左（右）子树，再访问根，最后中序遍历右（左）子树。
    @Override
    public String inOrder() {
        inOrder(root);
        return null;
    }

    private void inOrder(BNode<T> node){
        if (node == null){
            return ;
        }
        inOrder(node.left);
        System.out.println(node.data);
        inOrder(node.right);
    }
    // 首先后序遍历左（右）子树，再后序遍历右（左）子树，最后访问根。
    @Override
    public String postOrder() {
        postOrder(root);
        return null;
    }

    private void postOrder(BNode<T> node){
        if (node == null){
            return ;
        }
        postOrder(node.left);
        postOrder(node.right);
        System.out.println(node.data);
    }
    /**
     * 层次遍历
     */
    @Override
    public String levelOrder() {
        return null;
    }

    // 最终一定是在 null 位置插入的
    @Override
    public void insert(T data) {
        if (data == null)
            throw new RuntimeException("data can\'Comparable be null !");

        insertNoneRecursion(data);
        //root = insertRecursion(data, root);
    }

    // 使用非递归插入
    private void insertNoneRecursion(T data) {
        if (root == null) {
            root = new BNode<>(data);
            size++;
            return;
        }
        BNode<T> newNode = new BNode<>(data);
        BNode<T> tempRoot = root;

        while (true) {
            int compare = data.compareTo(tempRoot.data);
            // 已经有了改元素，没必要重新插入
            if (compare == 0) {
                return;
            }

            // 在左子树中插入
            if (compare < 0) {
                // 如果左树为空，直接插入
                if (tempRoot.left == null) {
                    tempRoot.left = newNode;
                    size++;
                    return;
                }
                // 不为空，则直接进行下一轮比较
                tempRoot = tempRoot.left;
                continue;
            }

            // 在右子树中插入
            // 如果右树为空，直接插入
            if (tempRoot.right == null) {
                tempRoot.right = newNode;
                size++;
                return;
            }
            // 不为空，则直接进行下一轮比较
            tempRoot = tempRoot.right;
        }
    }

    // 使用递归插入
    private BNode<T> insertRecursion(T data, BNode<T> fa) {
        if (fa == null) {
            fa = new BNode<>(data);
            size++;
            return fa;
        }
        int compare = data.compareTo(fa.data);

        // 在左侧插入
        if (compare < 0) {
            fa.left = insertRecursion(data, fa.left);
            return fa;
        }

        // 在右侧插入
        if (compare > 0) {
            fa.right = insertRecursion(data, fa.right);
        }
        return fa;
    }

    @Override
    public void remove(T data) {
        if (data == null)
            throw new RuntimeException("data can\'Comparable be null !");

        removeNoneRecursion(data);
    }

    private void removeNoneRecursion(T data) {
        if (isEmpty()) {
            return;
        }
        BNode<T> fa = root;
        BNode<T> cur = root;
        boolean isLeft = false;

        while (cur != null) {
            int com = data.compareTo(cur.data);

            if (com < 0) {
                fa = cur;
                cur = cur.left;
                isLeft = true;
                continue;
            }

            if (com > 0) {
                fa = cur;
                cur = cur.right;
                isLeft = false;
                continue;
            }

            break;
        }
        if (cur == null) {
            return;
        }

        if (cur.isLeaf()) {
            if (cur == root) {
                root = null;
                return;
            }
            if (isLeft) {
                fa.left = null;
            } else {
                fa.right = null;
            }
            return;
        }

        if (cur.left != null && cur.right != null) {
            if (cur == root){
                // todo
            }
        }
    }

    @Override
    public T findMin() {
        return findMin(root).data;
    }

    private BNode<T> findMin(BNode<T> node) {
        if (node == null) {
            return null;
        }

        if (node.left == null) {
            return node;
        } else {
            return findMin(node.left);
        }
    }

    @Override
    public T findMax() {
        return null;
    }

    @Override
    public BNode findNode(T data) {
        return null;
    }

    @Override
    public boolean contains(T data) {
        return false;
    }

    @Override
    public void clear() {

    }
}
