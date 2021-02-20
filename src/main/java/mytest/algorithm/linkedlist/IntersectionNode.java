package mytest.algorithm.linkedlist;

import java.util.List;

/**
 * 编写一个程序，找到两个单链表相交的起始节点。
 * 注意：
 * <p>
 * 如果两个链表没有交点，返回 null.
 * 在返回结果后，两个链表仍须保持原有的结构。
 * 可假定整个链表结构中没有循环。
 * 程序尽量满足 O(n) 时间复杂度，且仅用 O(1) 内存。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/intersection-of-two-linked-lists
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class IntersectionNode {
    public static void main(String[] args) {

    }

    //方法1 hash表法，先把A节点都放入hash中，然后遍历b，如果bi在hash表中，则bi为相交节点
    // 方法2，双指针，同时遍历 ，当pa遍历完后，把pa指向 b头节点，当pb遍历到尾部时，将pb指向
    // a头节点，继续遍历，直到有相同节点时，则为相交节点
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }
        ListNode pA = headA;
        ListNode pB = headB;
        while (pA != pB) {
            pA = pA == null ? headB : pA.next;
            pB = pB == null ? headA : pB.next;

        }

        return pA;
    }
}
