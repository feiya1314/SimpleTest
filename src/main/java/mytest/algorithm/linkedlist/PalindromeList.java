package mytest.algorithm.linkedlist;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 请判断一个链表是否为回文链表。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 1->2
 * 输出: false
 * 示例 2:
 * <p>
 * 输入: 1->2->2->1
 * 输出: true
 * 进阶：
 * 你能否用 O(n) 时间复杂度和 O(1) 空间复杂度解决此题？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/palindrome-linked-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/5
 * @description :
 */
public class PalindromeList {

    // 使用栈结构，压栈后出栈比较
    // 也可以先找到中间节点，然后反转链表，进行比较，再恢复
    public boolean isPalindrome(ListNode head) {
        if (head == null) {
            return false;
        }

        if (head.next == null) {
            return true;
        }

        Deque<ListNode> stack = new ArrayDeque<>();
        ListNode cur = head;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }

        int times = stack.size() / 2;
        cur = head;
        for (int i = 0; i < times; i++) {
            ListNode pop = stack.pop();
            if (pop.val!=cur.val){
                return false;
            }
            cur = cur.next;
        }

        return true;
    }
}
