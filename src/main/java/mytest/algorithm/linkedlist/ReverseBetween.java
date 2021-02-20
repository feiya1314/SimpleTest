package mytest.algorithm.linkedlist;


/**
 * //反转从位置 m 到 n 的链表。请使用一趟扫描完成反转。
 * //
 * // 说明:
 * //1 ≤ m ≤ n ≤ 链表长度。
 * //
 * // 示例:
 * //
 * // 输入: 1->2->3->4->5->NULL, m = 2, n = 4
 * //输出: 1->4->3->2->5->NULL
 * // Related Topics 链表
 * // 👍 674 👎 0
 *
 * @author : yufei
 * @date : 2021/2/19 14:19
 * @description :
 */
public class ReverseBetween {
    // 迭代，先找到开始调整的节点，作为反转部分的最右节点，而左边的作为反转后的左节点
    // 反转后左节点 连接到反转节点的头部，右节点连接未反转部分。
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (head == null) {
            return head;
        }
        // 虚拟一个头节点，这样不用考虑当前节点是否是头节点了
        ListNode pre = new ListNode(0);
        pre.next = head;
        // 遍历的节点
        ListNode cur = pre;
        // 用于记录左边界
        ListNode left = pre;
        // 记录右边界
        ListNode right = pre;
        // 记录反转部分
        ListNode rev = null;
        int index = 0;
        while (cur != null && index <= n) {
            if (index < m) {
                // index 等于m的前的这次遍历，刚好找到 左右节点
                left = cur;
                right = cur.next;
                cur = cur.next;
                index++;
                continue;
            }
            // 把当前节点接到反转链表的头部
            ListNode next = cur.next;
            cur.next = rev;
            rev = cur;
            cur = next;
            index++;
        }
        // 拼接三段链表，左边的未反转部分、反转链表部分、右边的未反转部分
        left.next = rev;
        right.next = cur;

        // 虚节点的下个节点才是头节点
        return pre.next;
    }
}
