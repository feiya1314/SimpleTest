package mytest.algorithm.linkedlist;

/**
 * 给定一个链表，判断链表中是否有环。
 * <p>
 * 为了表示给定链表中的环，我们使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。 如果 pos 是 -1，则在该链表中没有环。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/linked-list-cycle
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class CycleLink {
    // 快慢指针，一个指针每次往右移动一次，一个指针每次右移两次，如果有环，一定会相遇
    // 方法二 使用hash表，依次添加进hash表中，如果有重复的，则说明有环
    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        ListNode pre = head.next.next;
        ListNode behind = head.next;

        while (pre != null && behind != null && pre.next != null) {
            if (pre == behind) {
                return true;
            }
            pre = pre.next.next;
            behind = behind.next;
        }
        return false;
    }
}
