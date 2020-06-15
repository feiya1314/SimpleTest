package mytest.algorithm.linkedlist;

import java.util.HashMap;
import java.util.Map;

/**
 * 请你为 最不经常使用（LFU）缓存算法设计并实现数据结构。它应该支持以下操作：get 和 put。
 * <p>
 * get(key) - 如果键存在于缓存中，则获取键的值（总是正数），否则返回 -1。
 * put(key, value) - 如果键已存在，则变更其值；如果键不存在，请插入键值对。当缓存达到其容量时，则应该在插入新项之前，
 * 使最不经常使用的项无效。在此问题中，当存在平局（即两个或更多个键具有相同使用频率）时，应该去除最久未使用的键。
 * 「项的使用次数」就是自插入该项以来对其调用 get 和 put 函数的次数之和。使用次数会在对应项被移除后置为 0 。
 * <p>
 *  
 * <p>
 * 进阶：
 * 你是否可以在 O(1) 时间复杂度内执行两项操作？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/lfu-cache
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
/*
        思路，Map 中存放 key,和 存有 value 的节点
        另外有 map 存放 节点的访问 次数 和 对应的节点，所有的相同访问次数的节点组成链表
        访问的节点每次频率加一，并放到链表头部
        如果修改操作，同样更新node之后，放入 频率链表的头部

        怎么判断哪个频率最小呢？需要保留一个最小频率的变量，记录最小频率
     */
public class LFU {
    // 记录最小频率
    private int minFreq = 1;
    private int capacity;
    // 缓存
    private Map<Integer, Node<Integer>> cache;
    // 相同频率的所有节点组成一个双向链表
    private Map<Integer, DoubleNodeList<Integer>> freqMap;

    public LFU(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        freqMap = new HashMap<>();

    }

    // 读的时候，读完之后，要将节点的访问次数加一，并把节点从原来的频率链表中，转移到+1后的频率链表
    public int get(int key) {
        Node<Integer> target = cache.get(key);
        if (target == null) {
            return -1;
        }

        // 访问的节点频率加一，并转移到下一个链表头部
        incrAndMoveToHead(target);
        return target.value;
    }

    private void incrAndMoveToHead(Node<Integer> target) {
        int curFreq = target.getFreq();
        // 找到对应频率的链表
        DoubleNodeList<Integer> list = freqMap.get(curFreq);
        list.remove(target);

        int newFreq = target.incrFreq();
        if (list.isEmpty()) {
            // 如果当前链表空了之后，那么最小频率就上升为本次target 对应的频率
            if (curFreq == minFreq) {
                minFreq = newFreq;
            }
            // 可以不移除，这样下一个频率的上升到这个频率时，就不用重新创建链表
            // freqMap.remove(curFreq);
        }

        list = freqMap.get(newFreq);
        if (list == null) {
            list = new DoubleNodeList<>();
            freqMap.put(newFreq, list);
        }

        list.moveToHead(target);
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }

        Node<Integer> target = cache.get(key);
        // 已存在的直接更新结果后，访问次数加一，并放到链表的头部
        if (target != null) {
            target.value = value;
            incrAndMoveToHead(target);

            return;
        }

        // 否则新增一个
        target = new Node<>(key, value);

        // 容量已满,
        if (cache.size() == capacity) {
            // 找到待删除节点
            DoubleNodeList<Integer> list = freqMap.get(minFreq);
            Node<Integer> tail = list.removeTail();
            cache.remove(tail.key);

            // 如果删除之后list为空了，把 list 作为新写入的节点的容器
            if (list.isEmpty()) {
                freqMap.remove(minFreq);
                minFreq = 1;
                freqMap.put(1, list);
            }

            // 如果最小频率大于 1，更新最小频率
            if (target.getFreq() < minFreq) {
                minFreq = target.getFreq();
            }

            list = freqMap.get(target.getFreq());
            list.moveToHead(target);

            // 新节点加入缓存
            cache.put(key, target);
            return;
        }

        // 容量未满，找到当前频率的节点，写入
        DoubleNodeList<Integer> list = freqMap.get(target.getFreq());
        if (list == null) {
            list = new DoubleNodeList<>();
            freqMap.put(target.getFreq(), list);
        }

        // 新节点放入对应的头节点
        list.moveToHead(target);

        // 如果最小频率大于当前的频率，则更新
        if (target.getFreq() < minFreq) {
            minFreq = target.getFreq();
        }

        // 新节点加入缓存
        cache.put(key, target);
    }

    private class DoubleNodeList<T> {
        private Node<T> head;
        private Node<T> tail;

        public void remove(Node<T> node) {
            if (head == tail) {
                head = null;
                tail = null;
                return;
            }
            // 为头节点时，next节点作为头节点
            if (node == head) {
                Node<T> next = head.next;
                next.pre = null;
                head = next;
                return;
            }

            // 为尾节点时，pre节点作为尾节点
            if (node == tail) {
                Node<T> pre = tail.pre;
                pre.next = null;
                tail = pre;
                return;
            }

            // 中间节点时 ,移除 node 节点
            Node<T> pre = node.pre;
            Node<T> next = node.next;
            pre.next = next;
            next.pre = pre;
        }

        // 移除尾部节点
        Node<T> removeTail() {
            Node<T> result = tail;
            if (head == tail) {
                head = null;
                tail = null;
                return result;
            }
            Node<T> pre = tail.pre;
            pre.next = null;
            tail = pre;

            return result;
        }

        public boolean isEmpty() {
            return head == null;
        }

        // 把 节点 放入头节点
        void moveToHead(Node<T> node) {
            if (head == null) {
                head = node;
                tail = node;
                return;
            }
            head.pre = node;
            node.next = head;
            node.pre = null;
            head = node;
        }
    }

    private class Node<T> {
        Node<T> pre;
        Node<T> next;
        private int freq = 1;
        private int key;
        T value;

        public Node() {
        }

        public Node(int key, T value) {
            this.key = key;
            this.value = value;
        }

        public int getFreq() {
            return freq;
        }

        public int incrFreq() {
            freq++;
            return freq;
        }
    }
}

