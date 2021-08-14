package mytest.algorithm.doublepoint;

/**
 * //给定一个链表，旋转链表，将链表每个节点向右移动 k 个位置，其中 k 是非负数。
 * //
 * // 示例 1:
 * //
 * // 输入: 1->2->3->4->5->NULL, k = 2
 * //输出: 4->5->1->2->3->NULL
 * //解释:
 * //向右旋转 1 步: 5->1->2->3->4->NULL
 * //向右旋转 2 步: 4->5->1->2->3->NULL
 * //
 * //
 * // 示例 2:
 * //
 * // 输入: 0->1->2->NULL, k = 4
 * //输出: 2->0->1->NULL
 * //解释:
 * //向右旋转 1 步: 2->0->1->NULL
 * //向右旋转 2 步: 1->2->0->NULL
 * //向右旋转 3 步: 0->1->2->NULL
 * //向右旋转 4 步: 2->0->1->NULL
 * // Related Topics 链表 双指针
 * https://leetcode-cn.com/problems/rotate-list/
 *
 * @author : yufei
 * @date : 2021/1/18 13:58
 * @description :
 */
public class RotateRight {
    // 1、先计算链表长度 2、将链表成环 3、遍历到 len-k/len 次数后，到达新的尾节点，断开环
    public ListNode rotateRight2(ListNode head, int k) {
        if (k == 0 || head == null || head.next == null) {
            return head;
        }
        int len = 1;
        ListNode cur = head;
        while (cur.next != null) {
            cur = cur.next;
            len++;
        }
        // 移动的次数，此实cur是在链表尾部
        int num = len - k % len;
        if (num == len) {
            return head;
        }
        // 链表成环
        cur.next = head;
        // 移动num次 cur 到达新的链表的尾部，断开链表
        while (num > 0) {
            cur = cur.next;
            num--;
        }
        // 不能使用 head = cur.next;
        ListNode ret = cur.next;
        cur.next = null;

        return ret;
    }

    // 1、双指针先找到倒数第k个节点，此时可以得到联表的长度
    // 2、如果链表长度>= k，说明不用重复循环移动
    // 3、如果链表长度<k，说明至少重复循环移动一次，最终的结果等同于移动 k % length 个位置
    public ListNode rotateRight(ListNode head, int k) {
        // 不移动或者只有一个元素，直接返回head
        if (k == 0 || head == null || head.next == null) {
            return head;
        }
        ListNode left = head;
        ListNode leftPre = null;
        ListNode right = head;
        int curIndex = 1;
        while (right.next != null) {
            right = right.next;
            if (curIndex >= k) {
                leftPre = left;
                left = left.next;
            }
            curIndex++;
        }
        int length = curIndex;
        if (length >= k) {
            if (left == head) {
                return head;
            }
            if (left.next == null) {
                leftPre.next = null;
                left.next = head;
                return left;
            }
            leftPre.next = null;
            right.next = head;
            return left;
        }
        k = k % length;
        if (k == 0) {
            return head;
        }

        left = head;
        leftPre = null;
        right = head;
        curIndex = 1;
        while (right.next != null) {
            right = right.next;
            if (curIndex >= k) {
                leftPre = left;
                left = left.next;
            }
            curIndex++;
        }
        if (left == head) {
            return head;
        }
        if (left.next == null) {
            leftPre.next = null;
            left.next = head;
            return left;
        }
        leftPre.next = null;
        right.next = head;

        return left;
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
