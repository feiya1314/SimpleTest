package mytest.algorithm.linkedlist;

/**
 * 给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。
 * <p>
 * 示例：
 * <p>
 * 给定一个链表: 1->2->3->4->5, 和 n = 2.
 * <p>
 * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
 * 说明：
 * <p>
 * 给定的 n 保证是有效的。
 * <p>
 * 进阶：
 * <p>
 * 你能尝试使用一趟扫描实现吗？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class DeleteNthFromEnd {

    // 先让一个指针右移n次，然后两个指针同时移动，直到结尾，left即为倒数第n个节点
    // 如果left 为头节点，则更新head，
    // 也可以使用栈结构
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if (head == null) {
            return head;
        }

        ListNode left = null;
        ListNode pre = head;
        ListNode cur = head;
        int index = 1;
        while (cur.next != null) {
            if (index >= n) {
                left = pre;
                pre = pre.next;
            }
            cur = cur.next;
            index++;
        }
        // 头节点
        if (pre == head) {
            return head.next;
        }
        // 尾节点
        if (pre.next == null) {
            left.next = null;
            return head;
        }
        left.next = pre.next;

        return head;
    }
}
