package mytest.algorithm.tree.bfs;

import mytest.algorithm.Util;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 你现在手里有一份大小为 N x N 的「地图」（网格） grid，上面的每个「区域」（单元格）都用 0 和 1 标记好了。其中 0 代表海洋，1 代表陆地，
 * 请你找出一个海洋区域，这个海洋区域到离它最近的陆地区域的距离是最大的。
 * <p>
 * 我们这里说的距离是「曼哈顿距离」（ Manhattan Distance）：(x0, y0) 和 (x1, y1) 这两个区域之间的距离是 |x0 - x1| + |y0 - y1| 。
 * <p>
 * 如果我们的地图上只有陆地或者海洋，请返回 -1。
 * <p>
 * 输入：[[1,0,0],[0,0,0],[0,0,0]]
 * 输出：4
 * 解释：
 * 海洋区域 (2, 2) 和所有陆地区域之间的距离都达到最大，最大距离为 4。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/as-far-from-land-as-possible
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class MapAnalyse {
    public static void main(String[] args) {
        int[][] maze = new int[][]{{0, 1, 0, 0, 0}, {0, 1, 0, 1, 0}, {0, 0, 0, 0, 0}, {0, 1, 1, 1, 0}, {0, 0, 0, 1, 0}};
        System.out.println(maxDistance(maze));
        Util.printArr(maze);
    }

    /*
        我们只要先把所有的陆地都入队，然后从各个陆地同时开始一层一层的向海洋扩散，那么最后扩散到的海洋就是最远的海洋！
        并且这个海洋肯定是被离他最近的陆地给扩散到的！
        下面是扩散的图示，1表示陆地，0表示海洋。每次扩散的时候会标记相邻的4个位置的海洋：

        可以想象成你从每个陆地上派了很多支船去踏上伟大航道，踏遍所有的海洋。每当船到了新的海洋，就会分裂成4条新的船，
        向新的未知海洋前进（访问过的海洋就不去了）。如果船到达了某个未访问过的海洋，那他们是第一个到这片海洋的。
        很明显，这么多船最后访问到的海洋，肯定是离陆地最远的海洋。

     */
    public static int maxDistance(int[][] grid) {

        int m = grid.length;
        int n = grid[0].length;

        // 控制下一步走向的坐标，{0，1}是向下走，{1，0}是向右走
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        Queue<int[]> queue = new ArrayDeque<>();
        // 判断是否有海洋
        boolean hasOcean = false;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    queue.offer(new int[]{i, j});
                    continue;
                }
                hasOcean = true;
            }
        }
        // 没有海洋直接结束
        if (!hasOcean) {
            return -1;
        }

        // 记录当前访问的位置
        int[] point = null;
        while (!queue.isEmpty()) {
            point = queue.poll();
            int x = point[0];
            int y = point[1];
            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];
                // 判断下一个点是否越界，或者是否已经被访问过
                if (newX < 0 || newY < 0 || newX >= m || newY >= n || grid[newX][newY] != 0) {
                    continue;
                }
                // 把走到下一个点耗费的步数放入该点
                grid[newX][newY] = grid[x][y] + 1;
                // 入队，等下一轮访问
                queue.offer(new int[]{newX, newY});
            }
        }

        if (point == null) {
            return -1;
        }
        // 最后访问的点即为结果，需要-1 因为从1开始的
        return grid[point[0]][point[1]] - 1;
    }
}
