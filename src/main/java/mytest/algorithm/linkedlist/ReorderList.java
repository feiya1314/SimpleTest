package mytest.algorithm.linkedlist;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 给定一个单链表 L：L0→L1→…→Ln-1→Ln ，
 * 将其重新排列后变为： L0→Ln→L1→Ln-1→L2→Ln-2→…
 * <p>
 * 你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。
 * <p>
 * 示例 1:
 * <p>
 * 给定链表 1->2->3->4, 重新排列为 1->4->2->3.
 * 示例 2:
 * <p>
 * 给定链表 1->2->3->4->5, 重新排列为 1->5->2->4->3.
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/reorder-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/4
 * @description :
 */
public class ReorderList {
    public static void main(String[] args) {
        System.out.println(5 / 2);
    }

    // 使用栈结构（双端队列），先将节点都压栈，然后出栈，按规则插入
    // 方法二，1、找到链表中间节点 2、右半段链表反转 3、合并两个链表
    public void reorderList(ListNode head) {
        if (head == null || head.next == null || head.next.next == null) {
            return;
        }
        Deque<ListNode> stack = new ArrayDeque<>();
        // 元素入栈
        ListNode cur = head;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }
        // 出栈次数，只需要遍历一半长度即可
        int endIndex = stack.size() / 2;
        cur = head;
        for (int i = 0; i < endIndex; i++) {
            ListNode pop = stack.pop();
            ListNode next = cur.next;
            cur.next = pop;
            pop.next = next;
            cur = next;
        }
        // 最终的cur节点，就是尾节点，next 置 null
        cur.next = null;
    }
}
