package mytest.algorithm.linkedlist;

import java.util.HashSet;
import java.util.Set;

/**
 * 给定一个排序链表，删除所有含有重复数字的节点，只保留原始链表中 没有重复出现 的数字。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 1->2->3->3->4->4->5
 * 输出: 1->2->5
 * 示例 2:
 * <p>
 * 输入: 1->1->1->2->3
 * 输出: 2->3
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-duplicates-from-sorted-list-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/2/13
 * @description :
 */
public class DeleteDuplicates {
    //  利用有序的特点
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode result = new ListNode(0);
        ListNode pre = null;
        ListNode cur = head;
        boolean dup = false;
        while (cur != null) {
            if (pre != null && pre.val != cur.val) {

            }
        }
        return null;
    }

    // 先遍历找出重复的数字，然后再遍历去掉重复的节点,但是没有用到排序链表的特点
    public ListNode deleteDuplicates2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        Set<Integer> current = new HashSet<>();
        Set<Integer> duplicates = new HashSet<>();
        ListNode curNode = head;
        while (curNode != null) {
            // duplicates记录重复的值
            if (current.contains(curNode.val)) {
                duplicates.add(curNode.val);
            } else {
                current.add(curNode.val);
            }
            curNode = curNode.next;
        }
        curNode = head;
        ListNode pre = null;
        while (curNode != null) {
            if (duplicates.contains(curNode.val)) {
                // 如果重复的是头节点，则更新头节点
                if (curNode == head) {
                    head = head.next;
                    curNode = head;
                } else {
                    pre.next = curNode.next;
                    curNode = curNode.next;
                }
            } else {
                pre = curNode;
                curNode = curNode.next;
            }
        }
        return head;
    }
}
