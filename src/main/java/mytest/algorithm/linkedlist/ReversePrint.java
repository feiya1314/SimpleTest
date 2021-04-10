package mytest.algorithm.linkedlist;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。
 * <p>
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：head = [1,3,2]
 * 输出：[2,3,1]
 *
 * @author : feiya
 * @date : 2021/4/8
 * @description :
 */
public class ReversePrint {
    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(3);
        head.next.next = new ListNode(2);
        System.out.println(Arrays.toString(reversePrint(head)));
    }
    // 使用堆栈
    public static int[] reversePrint(ListNode head) {
        if (head == null) {
            return new int[0];
        }
        Deque<ListNode> deque = new ArrayDeque<>();
        while (head != null) {
            deque.push(head);
            head = head.next;
        }

        int size = deque.size();
        int[] result = new int[size];
        // 这里用size，不能用deque.size()，每次弹出后，deque.size()会减小
        for (int i = 0; i < size; i++) {
            result[i] = deque.pop().val;
        }
        return result;
    }
}
