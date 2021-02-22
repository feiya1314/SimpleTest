package mytest.algorithm.linkedlist;

/**
 * 给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。
 * <p>
 * 你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * <p>
 * 输入：head = [1,2,3,4]
 * 输出：[2,1,4,3]
 * 示例 2：
 * <p>
 * 输入：head = []
 * 输出：[]
 * 示例 3：
 * <p>
 * 输入：head = [1]
 * 输出：[1]
 *  
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/swap-nodes-in-pairs
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/13
 * @description :
 */
public class SwapPairs {
    public ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        // 新节点用来记录更换后的链表，不能直接在head上交换，会形成环形链表
        ListNode result = new ListNode(0);

        ListNode cur = head;
        ListNode pre = result;

        while (cur != null && cur.next != null) {
            // 交换两个节点
            pre.next = cur.next;
            ListNode next = cur.next;
            cur.next = cur.next.next;
            next.next = cur;

            //更新前一个节点和当前要交换的起始节点
            pre = cur;
            cur = cur.next;
        }
        // 头节点为 result 的next
        return result.next;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }
}
