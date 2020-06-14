package mytest.algorithm.linkedlist;

import java.util.HashMap;
import java.util.Map;

/**
 * 运用你所掌握的数据结构，设计和实现一个  LRU (最近最少使用) 缓存机制。它应该支持以下操作： 获取数据 get 和 写入数据 put 。
 * <p>
 * 获取数据 get(key) - 如果关键字 (key) 存在于缓存中，则获取关键字的值（总是正数），否则返回 -1。
 * 写入数据 put(key, value) - 如果关键字已经存在，则变更其数据值；如果关键字不存在，则插入该组「关键字/值」。当缓存容量达到上限时，
 * 它应该在写入新数据之前删除最久未使用的数据值，从而为新的数据值留出空间。
 * <p>
 *  
 * <p>
 * 进阶:
 * <p>
 * 你是否可以在 O(1) 时间复杂度内完成这两种操作？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/lru-cache
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class LRU {
    private Map<Integer, ListNode> cache;
    private ListNode<Integer> head;
    private ListNode<Integer> tail;
    private int capacity;

    public static void main(String[] args) {
        LRU cache = new LRU(3);
        int temp;
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.put(4, 4);
        temp = cache.get(4);       // 返回  1
        temp = cache.get(3);       // 返回  1

        temp = cache.get(2);       // 返回 -1 (未找到)

        temp = cache.get(1);       // 返回 -1 (未找到)
        cache.put(5, 5);
        temp = cache.get(1);       // 返回  3
        temp = cache.get(2);       // 返回  4
        temp = cache.get(3);       // 返回  4
        temp = cache.get(4);       // 返回  4
        temp = cache.get(5);       // 返回  4

        System.out.println(temp);
    }

    /*
        1、get 时，将访问的节点置顶，此时分3种情况
            -- 访问的是头节点，那么就不用重新置顶
            -- 访问的是尾部节点，尾部节点需要更新，然后把访问的节点置顶
            -- 访问的是中间节点，则需要把访问节点提出，重新链接链表，最后把访问的节点置顶

       2、put 时，如果是更新节点，则更新节点后，将节点在链表置顶，与 get 类似，新增时 分两种情况：
            -- 容量已满，则需要移除尾节点。容量为 1时，直接替换。否则移除尾节点，执行新增操作，同容量未满时一样
            -- 容量未满，把新节点放入头部，如果是第一次添加，直接放入头节点，否则，直接新节点作为头节点
     */
    public LRU(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
    }

    public int get(int key) {
        ListNode<Integer> val = cache.get(key);
        if (val == null) {
            return -1;
        }
        // 把访问的节点放入头节点
        moveToHead(val);
        return val.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }

        ListNode<Integer> val = cache.get(key);
        // 如果已存在，就更新节点的值 并且置顶
        if (val != null) {
            val.value = value;
            // 把节点置顶
            moveToHead(val);
            return;
        }

        // 如果不存在则新增
        ListNode<Integer> target = new ListNode<>(key, value);

        // 如果容量已经满了，需要把最久没访问的节点去掉
        if (cache.size() == capacity) {
            // 缓存中先 删除尾部节点
            cache.remove(tail.key);
            // 如果只有一个节点, 删掉之前的节点
            if (tail == head) {
                cache.put(key, target); // 新增的节点要放入缓存中
                head = target;
                tail = target;
                return;
            }

            // 尾部节点在链表中删掉
            ListNode<Integer> pre = tail.pre;
            pre.next = null;
            tail = pre;
        }
        cache.put(key, target); // 新增的节点要放入缓存中
        // 把新节点放到头部
        putToHead(target);
    }

    private void moveToHead(ListNode<Integer> target) {
        if (target == head) {
            return;
        }
        // 如果是尾部，则需要更新尾部，然后再更新头节点
        if (target == tail) {
            ListNode<Integer> pre = tail.pre;
            pre.next = null;
            tail = pre;

            updateHead(target);
            return;
        }

        // 将 target 摘除，并把 target 前节点 的后节点改为 target 的后节点
        ListNode<Integer> next = target.next;
        ListNode<Integer> pre = target.pre;
        pre.next = next;
        next.pre = pre;

        updateHead(target);
    }

    // 将target节点作为头节点
    private void updateHead(ListNode<Integer> target) {
        // target添加到头部
        head.pre = target;
        target.next = head;
        target.pre = null;
        head = target;
    }

    private void putToHead(ListNode<Integer> target) {
        // 第一次添加
        if (head == null) {
            head = target;
            tail = target;
            return;
        }
        // 添加到头部
        updateHead(target);
    }

    class ListNode<T> {
        private Integer key;
        private T value;
        ListNode<T> pre;
        ListNode<T> next;

        public ListNode() {
        }

        public ListNode(Integer key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
