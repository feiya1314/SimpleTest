import java.util.*;

public class Solution {
    public List<List<Integer>> combine(int n, int k) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> track = new ArrayList<>();

        backtrack(n, k, 1, result, track);
        return result;
    }

    private void backtrack(int n, int k, int start, List<List<Integer>> result, List<Integer> track) {
        if (track.size() == k) {
            result.add(new ArrayList<>(track));
            return;
        }

        for (int i = start; i <= n; i++) {
            track.add(i);
            backtrack(n, k, i + 1, result, track);
            track.removeLast();
        }
    }
}
