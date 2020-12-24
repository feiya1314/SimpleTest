package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 不重复数组的所有组合
 * 输入两个数字 n, k，算法输出 [1..n] 中 k 个数字的所有组合。
 * <p>
 * vector<vector<int>> combine(int n, int k);
 * 比如输入 n = 4, k = 2，输出如下结果，顺序无所谓，但是不能包含重复（按照组合的定义，[1,2] 和 [2,1] 也算重复）：
 * <p>
 * [
 * [1,2],
 * [1,3],
 * [1,4],
 * [2,3],
 * [2,4],
 * [3,4]
 * ]
 *
 * @author : yufei
 * @date : 2020/12/24 11:37
 * @description :
 */
public class Combination {
    private List<List<Integer>> result = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(new Combination().combination(new int[]{2, 3, 4, 5}, 3));
    }

    public List<List<Integer>> combination(int[] nums, int size) {
        LinkedList<Integer> track = new LinkedList<>();
        backtrack(nums, 0, track, size);
        return result;
    }

    private void backtrack(int[] nums, int start, LinkedList<Integer> track, int size) {
        // 跳出条件是track记录的字符数量等于要求的 k 个数字组合
        if (track.size() == size) {
            result.add(new ArrayList<>(track));
            return;
        }
        for (int i = start; i < nums.length; i++) {
            // 余下的字符总长度不够 k 个数了，肯定组不成，直接跳出
            if ((track.size() + nums.length - i) < size) {
                break;
            }
            track.add(nums[i]);
            // 从 i+1 开始，因为i+1之前的字符已经在上层的递归中使用了，再用就重复了
            backtrack(nums, i + 1, track, size);
            track.removeLast();
        }
    }
}
