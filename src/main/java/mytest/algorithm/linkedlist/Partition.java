package mytest.algorithm.linkedlist;

/**
 * 编写程序以 x 为基准分割链表，使得所有小于 x 的节点排在大于或等于 x 的节点之前。如果链表中包含 x，
 * x 只需出现在小于 x 的元素之后(如下所示)。分割元素 x 只需处于“右半部分”即可，其不需要被置于左右两部分之间。
 * 只要把小于x的数移动到所有x的左方就行,没有顺序要求，而且等于x,大于x这些元素没有要求！
 * <p>
 * 示例:
 * <p>
 * 输入: head = 3->5->8->5->10->2->1, x = 5
 * 输出: 3->1->2->10->5->5->8
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/partition-list-lcci
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/4/10
 * @description :
 */
public class Partition {
    // 1、找到第一个 >= x的位置，记录此位置，以后所有<x的节点都放到此节点的右边
    // 2、如果是 < x 的节点，首先判断是否前面的节点都是小于 x的，如果是，表示还没有出现过 >= x的节点，继续遍历，不用交换
    // 3、如果是 < x 的节点，并且前面的节点有 >= x 的节点，那么需要把此节点交换到第一个 >= x的节点的前面
    public ListNode partition(ListNode head, int x) {
        if (head == null || head.next == null) {
            return head;
        }

        // 虚拟头节点，不用再判断头节点是<x的情况
        ListNode temp = new ListNode(0, head);
        ListNode cur = temp;
        ListNode left = null;

        while (cur.next != null) {
            // 大于等于 x的节点，先判断是不是第一个出现的大于等于 x 的节点
            if (cur.next.val >= x) {
                // 第一个 >= x的位置，记录此位置
                if (left == null) {
                    left = cur;
                }
                cur = cur.next;
                continue;
            }
            // 如果还没有出现过大于等于 x的节点，也即是前面的节点都是 < x 的节点，不用交换
            if (left == null) {
                cur = cur.next;
                continue;
            }
            // 把小于 x 的节点放到前面
            ListNode right = cur.next.next;
            ListNode leftNext = left.next;
            left.next = cur.next;
            cur.next.next = leftNext;
            cur.next = right;
        }

        return temp.next;
    }
}
