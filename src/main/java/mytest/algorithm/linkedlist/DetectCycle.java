package mytest.algorithm.linkedlist;

import java.util.HashSet;
import java.util.Set;

/**
 * 给定一个链表，返回链表开始入环的第一个节点。 如果链表无环，则返回 null。
 * <p>
 * 为了表示给定链表中的环，我们使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。 如果 pos 是 -1，则在该链表中没有环。注意，pos 仅仅是用于标识环的情况，并不会作为参数传递到函数中。
 * <p>
 * 说明：不允许修改给定的链表。
 * <p>
 * 进阶：
 * <p>
 * 你是否可以使用 O(1) 空间解决此题？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/linked-list-cycle-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/6
 * @description :
 */
public class DetectCycle {

    // 方法一 使用hash表，遍历链表，往hash表中添加，首先判断是否有重复的，
    // 如果有，则说明有环，第一个碰到的节点即是入环的第一个节点
    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }
        Set<ListNode> nodeSet = new HashSet<>();
        ListNode cur = head;
        while (cur != null) {
            if (nodeSet.contains(cur)) {
                return cur;
            }
            nodeSet.add(cur);
            cur = cur.next;
        }
        return null;
    }

    // 方法二 ，使用双指针,首先快慢指针，一个走一步，一个每次走两步，找到第一个相遇的位置，
    // 然后一个指针从头开始，每次走一步，另一个指针从相遇位置，每次一步，当两指针相遇时，即为入环的第一个节点
    public ListNode detectCycle2(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }
        ListNode pre = head.next.next;
        ListNode behind = head.next;

        boolean has = false;
        while (pre != null && behind != null && pre.next != null) {
            if (pre == behind) {
                has = true;
                break;
            }
            pre = pre.next.next;
            behind = behind.next;
        }
        if (!has) {
            return null;
        }

        pre = head;
        while (pre != behind){
            pre = pre.next;
            behind = behind.next;
        }

        return pre;
    }
}
