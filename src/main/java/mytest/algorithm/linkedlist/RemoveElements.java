package mytest.algorithm.linkedlist;

/**
 * 给你一个链表的头节点 head 和一个整数 val ，请你删除链表中所有满足 Node.val == val 的节点，并返回 新的头节点 。
 * 输入：head = [1,2,6,3,4,5,6], val = 6
 * 输出：[1,2,3,4,5]
 * 示例 2：
 * <p>
 * 输入：head = [], val = 1
 * 输出：[]
 * 示例 3：
 * <p>
 * 输入：head = [7,7,7,7], val = 7
 * 输出：[]
 *  
 * <p>
 * 提示：
 * <p>
 * 列表中的节点在范围 [0, 104] 内
 * 1 <= Node.val <= 50
 * 0 <= k <= 50
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-linked-list-elements
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/5
 * @description :
 */
public class RemoveElements {
    // 遍历节点，把与val相等的节点删去
    public ListNode removeElements(ListNode head, int val) {
        // 由于头节点也可能是相同，需要判断是否是头节点，所以，使用一个虚节点作为头节点，这样不用处理是否为头节点的情况
        ListNode tempHead = new ListNode(-1, head);
        ListNode cur = tempHead;
        // 这里因为头节点肯定不相等，所以直接判断next节点
        while (cur.next != null) {
            // 使用next节点判断，在删除时可以不用记录前一个节点，使用cur即可
            // 指向下一个节点后，cur 节点不能变更，需要判断 新的next节点是否需要删除
            if (cur.next.val == val) {
                cur.next = cur.next.next;
                continue;
            }
            // cur的next节点不符合时，才更新cur位置
            cur = cur.next;
        }
        // 头节点为 tempHead.next
        return tempHead.next;
    }
}
