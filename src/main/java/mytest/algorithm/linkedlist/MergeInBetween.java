package mytest.algorithm.linkedlist;

/**
 * 1669. 合并两个链表
 * 给你两个链表 list1 和 list2 ，它们包含的元素分别为 n 个和 m 个。
 * <p>
 * 请你将 list1 中第 a 个节点到第 b 个节点删除，并将list2 接在被删除节点的位置。
 * <p>
 * 下图中蓝色边和节点展示了操作后的结果：
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/merge-in-between-linked-lists
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/10
 * @description :
 */
public class MergeInBetween {
    // 首先找到 a 的前一个节点，b 的后一个节点，然后找到，list2的尾部节点，拼接即可
    public ListNode mergeInBetween(ListNode list1, int a, int b, ListNode list2) {
        // 这里使用一个虚拟节点，作为list1的头部，不用判断a为头节点的情况
        ListNode vir = new ListNode(0, list1);
        ListNode aNode = null;
        ListNode bNode = null;
        int index = -1;
        ListNode cur = vir;

        while (cur != null) {
            // 如果下一个节点索引为a，那么当前节点即为 a 的前一个的节点
            if (index + 1 == a) {
                aNode = cur;
            }
            // 当前节点索引为b，记录 b 节点的下一个节点，结束循环
            if (index == b) {
                bNode = cur.next;
                break;
            }
            cur = cur.next;
            index++;
        }

        // 找list2 的尾部节点
        cur = list2;
        while (cur.next != null) {
            cur = cur.next;
        }

        // 拼接
        aNode.next = list2;
        cur.next = bNode;

        return vir.next;
    }
}
