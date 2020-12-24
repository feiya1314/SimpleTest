package mytest.algorithm.backtrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 输入一个不包含重复数字的数组 nums，返回这些数字的全部排列。
 * <p>
 * vector<vector<int>> permute(vector<int>& nums);
 * 比如说输入数组 [1,2,3]，输出结果应该如下，顺序无所谓，不能有重复：
 * <p>
 * [
 * [1,2,3],
 * [1,3,2],
 * [2,1,3],
 * [2,3,1],
 * [3,1,2],
 * [3,2,1]
 * ]
 *
 * @author : yufei
 * @date : 2020/12/24 19:56
 * @description :
 */
public class Permutation {
    List<List<Integer>> result = new ArrayList<>();
    boolean[] used;

    public static void main(String[] args) {
        System.out.println(new Permutation().permute(new int[]{1,2,3,4}));
    }

    public List<List<Integer>> permute(int[] nums) {
        LinkedList<Integer> track = new LinkedList<>();
        used = new boolean[nums.length];
        backtrack(nums,track);
        return result;
    }

    private void backtrack(int[] nums, LinkedList<Integer> track) {
        if (track.size() == nums.length){
            result.add(new ArrayList<>(track));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]){
                continue;
            }
            track.add(nums[i]);
            // used用于记录当前递归访问过的位置，如果访问过，递归时那个支路就不能访问，比如1 2 3
            // 访问 1后，支路有三条 1  2  3  ，显然 1 不能用，由于1 的坐标是 0 ，恰好递归时，1坐标还是0，
            // 所以可以用bool数组的对应坐标记录对应nums 坐标的字符是否被访问过
            used[i] = true;
            backtrack(nums,track);
            used[i] = false;
            track.removeLast();
        }
    }
}
