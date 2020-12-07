package mytest.algorithm.linkedlist;

public class ReverseList {
    public static void main(String[] args) {

    }

    /**
     * 迭代方式
     *
     * @param head
     * @return
     */
    public static ListNode reverseList(ListNode head) {
        // 每次把 cur.next 指向前一个，然后把原始的 next 当作 cur，进行下一步循环
        ListNode prev = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode temp = cur.next;
            cur.next = prev;
            prev = cur;
            cur = temp;
        }

        return prev;
    }

    /**
     * 递归方式  1 → 2 → 3 → 4 → null
     * @param head
     * @return
     */
    public static ListNode reverseListRecursion(ListNode head) {
        if (head == null || head.next == null){
            return head;  // 不能返回 null ，必须返回 head
        }
        ListNode tmp = reverseListRecursion(head.next);  // tmp 结果为 最后一个节点，也即是 4 那个节点，由于 tmp 始终没有修改，所以为反转后的 head
        head.next.next = head;
        head.next = null;

        return tmp;
    }

}
