package mytest.algorithm.linkedlist;

/**
 * 给定一个单链表，把所有的奇数节点和偶数节点分别排在一起。请注意，这里的奇数节点和偶数节点指的是节点编号的奇偶性，而不是节点的值的奇偶性。
 * <p>
 * 请尝试使用原地算法完成。你的算法的空间复杂度应为 O(1)，时间复杂度应为 O(nodes)，nodes 为节点总数。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 1->2->3->4->5->NULL
 * 输出: 1->3->5->2->4->NULL
 * 示例 2:
 * <p>
 * 输入: 2->1->3->5->6->4->7->NULL
 * 输出: 2->3->6->7->1->5->4->NULL
 * 说明:
 * <p>
 * 应当保持奇数节点和偶数节点的相对顺序。
 * 链表的第一个节点视为奇数节点，第二个节点视为偶数节点，以此类推。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/odd-even-linked-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/5
 * @description :
 */
public class OddEvenList {
    // 使用两个新节点，分别作为奇数和偶数两个链表 ，遍历原始链表，奇数的放到奇数链表，偶数的放到偶数链表
    // 遍历完后，把偶数链表连接到奇数链表尾部，返回奇数的头节点
    public ListNode oddEvenList(ListNode head) {
        if (head == null || head.next == null || head.next.next == null) {
            return head;
        }

        // 新建两个空节点作为奇数 偶数链表的头部
        ListNode even = new ListNode();
        ListNode odd = new ListNode();

        ListNode oddCur = odd;
        ListNode evenCur = even;
        int index = 1;
        while (head != null) {
            // 如果当前为奇数位的节点，则拼接到奇数链表的尾部
            if (index % 2 == 1) {
                oddCur.next = head;
                oddCur = head;
            } else {
                evenCur.next = head;
                evenCur = head;
            }
            head = head.next;
            index++;
        }
        // 更新两个链表的尾部节点（遍历后，尾节点的next可能还保留之前的节点，需要置为 null）
        evenCur.next = null;
        oddCur.next = null;
        // 如果偶数链表有实际节点，则拼接到奇数链表尾部
        if (even.next != null) {
            oddCur.next = even.next;
        }

        // odd.next为实际的头节点
        return odd.next;
    }
}
