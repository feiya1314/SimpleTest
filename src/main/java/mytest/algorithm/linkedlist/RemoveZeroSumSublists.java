package mytest.algorithm.linkedlist;

import java.util.HashMap;
import java.util.Map;

/**
 * 给你一个链表的头节点 head，请你编写代码，反复删去链表中由 总和 值为 0 的连续节点组成的序列，直到不存在这样的序列为止。
 * <p>
 * 删除完毕后，请你返回最终结果链表的头节点。
 * <p>
 *  
 * <p>
 * 你可以返回任何满足题目要求的答案。
 * <p>
 * （注意，下面示例中的所有序列，都是对 ListNode 对象序列化的表示。）
 * <p>
 * 示例 1：
 * <p>
 * 输入：head = [1,2,-3,3,1]
 * 输出：[3,1]
 * 提示：答案 [1,2,1] 也是正确的。
 * 示例 2：
 * <p>
 * 输入：head = [1,2,3,-3,4]
 * 输出：[1,2,4]
 * 示例 3：
 * <p>
 * 输入：head = [1,2,3,-3,-2]
 * 输出：[1]
 * <p>
 * 给你的链表中可能有 1 到 1000 个节点。
 * 对于链表中的每个节点，节点的值：-1000 <= node.val <= 1000.
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-zero-sum-consecutive-nodes-from-linked-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/10
 * @description :
 */
public class RemoveZeroSumSublists {
    // 第一次遍历 计算每个节点加上所有前面节点的值，保存到 map 中
    // 第二次遍历，计算当前节点加上所有前面节点的值，并从map中找出和当前值一样的节点，那么节点中间的所有值之和为0
    // 例如       1 2 3 -4 -1 -1 4 2 5
    // 节点和     1 3 6  2  1  0 4 6 11    两个和为 6 的节点，中间节点肯定有几个节点之和一定为 -6 ，才能保证  从第一个 6 + -6  + 6=6
    // 那么第一个 6 后面的节点，直到第二个6所在节点之和一定为 0 也就是 6 + （-6  + 6）= 6，删除括号中的节点即可
    public ListNode removeZeroSumSublists(ListNode head) {
        // 虚拟头节点，避免出现需要删除头节点的情形
        ListNode vir = new ListNode(0, head);
        Map<Integer, ListNode> map = new HashMap<>();
        int sum = 0;
        ListNode cur = vir;
        while (cur != null) {
            // 计算当前节点和前面所有节点值的和，并保存当前节点，后面如果出现值相同的，则覆盖之前的节点
            sum += cur.val;
            map.put(sum, cur);
            cur = cur.next;
        }
        sum = 0;
        cur = vir;
        while (cur != null) {
            // 第二次遍历，找到和当前节点累加的值一样的节点，那么当前节点后面的所有节点，直到同值的节点，之和肯定为0，需要删除
            // 对于没有第二个相同的值，那么map中获取的节点就是当前节点， cur.next = map.get(sum).next 等同 cur.next = cur.next不会出差
            sum += cur.val;
            cur.next = map.get(sum).next;
            cur = cur.next;
        }
        // 返回更新后的头节点
        return vir.next;
    }
}
