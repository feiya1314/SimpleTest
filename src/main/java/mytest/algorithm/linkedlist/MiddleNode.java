package mytest.algorithm.linkedlist;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个头结点为 head 的非空单链表，返回链表的中间结点。
 * <p>
 * 如果有两个中间结点，则返回第二个中间结点。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：[1,2,3,4,5]
 * 输出：此列表中的结点 3 (序列化形式：[3,4,5])
 * 返回的结点值为 3 。 (测评系统对该结点序列化表述是 [3,4,5])。
 * 注意，我们返回了一个 ListNode 类型的对象 ans，这样：
 * ans.val = 3, ans.next.val = 4, ans.next.next.val = 5, 以及 ans.next.next.next = NULL.
 * 示例 2：
 * <p>
 * 输入：[1,2,3,4,5,6]
 * 输出：此列表中的结点 4 (序列化形式：[4,5,6])
 * 由于该列表有两个中间结点，值分别为 3 和 4，我们返回第二个结点。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/middle-of-the-linked-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/6
 * @description :
 */
public class MiddleNode {
    // 方法一，使用数组，根据坐标，找到中间节点
    public ListNode middleNode(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        List<ListNode> nodes = new ArrayList<>();
        ListNode cur = head;
        while (cur != null) {
            nodes.add(cur);
            cur = cur.next;
        }

        return nodes.get(nodes.size() / 2);
    }

    // 方法二 遍历两次，第一次找到总长，第二次找到中间的节点
    public ListNode middleNode2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        int size = 0;
        ListNode cur = head;
        while (cur != null) {
            size++;
            cur = cur.next;
        }

        int mid = size / 2;
        cur = head;
        while (mid > 0) {
            cur = cur.next;
            mid--;
        }
        return cur;
    }

    // 方法三 快慢指针，一个走一步，一个每次走两步，当到头时另一个即为中间节点
    public ListNode middleNode3(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode slow = head;
        ListNode fast = head;

        while (slow != null && fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }
}
