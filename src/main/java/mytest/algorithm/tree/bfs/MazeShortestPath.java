package mytest.algorithm.tree.bfs;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 给定一个n*m的二维整数数组，用来表示一个迷宫，数组中只包含0或1，其中0表示可以走的路，1表示不可通过的墙壁。
 * <p>
 * 最初，有一个人位于左上角(1, 1)处，已知该人每次可以向上、下、左、右任意一个方向移动一个位置。
 * <p>
 * 请问，该人从左上角移动至右下角(n, m)处，至少需要移动多少次。
 * <p>
 * 数据保证(1, 1)处和(n, m)处的数字为0，且一定至少存在一条通路。
 * <p>
 * 数据范围
 * <p>
 * 1≤n,m≤100 1≤n,m≤100
 * <p>
 * 输入样例：
 * <p>
 * 5 5
 * 0 1 0 0 0
 * 0 1 0 1 0
 * 0 0 0 0 0
 * 0 1 1 1 0
 * 0 0 0 1 0
 * 输出样例：
 * <p>
 * 8
 * ————————————————
 * 版权声明：本文为CSDN博主「ccluqh」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/qq_28468707/java/article/details/102786710
 */
public class MazeShortestPath {
    public static void main(String[] args) {
        int[][] maze = new int[][]{{0, -1, 0, 0, 0}, {0, -1, 0, -1, 0}, {0, 0, 0, 0, 0}, {0, -1, -1, -1, 0}, {0, 0, 0, -1, 0}};
        int re = walk(5, 5, maze);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                sb.append(maze[i][j]);
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        System.out.println(sb.toString());
        System.out.println(re);

        /* 最终数组中的结果，里面是每一条路径（每个位置下一步可能向上下左右）中所需要的步数
            2 -1  6  7 8
            1 -1  5 -1 7
            2  3  4  5 6
            3 -1 -1 -1 7
            4  5  6 -1 8
         */
    }

    public static int walk(int m, int n, int[][] maze) {
        // 控制下一步走向的坐标，{0，1}是向下走，{1，0}是向右走
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        // 广度优先遍历需要使用队列来实现
        Queue<int[]> queue = new ArrayDeque<>();
        // 把起点放入队列，如果有多个起点，则需要都加入队列
        queue.offer(new int[]{0, 0});

        int[] cur = null;
        while (!queue.isEmpty()) {
            cur = queue.poll();
            int x = cur[0];
            int y = cur[1];
            // 走到了终点，肯定是最短的路线先到达，取到了结果，退出
            if (x == (m - 1) && y == (n - 1)) {
                break;
            }
            // 把四个方向的下一步都压入队列，（排除边界和走不通的）
            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                // 下一步如果超出边界，或者 =1 （碰到墙壁），则这条路不通
                if (newX < 0 || newY < 0 || newX >= m || newY >= n || maze[newX][newY] != 0) {
                    continue;
                }
                maze[newX][newY] = maze[x][y] + 1; // 当前已访问的不允许访问了，并且把到 newX newY 位置走的步数放入此处，原始数组没用了，不再新搞一个数组存放走的步数。
                queue.offer(new int[]{newX, newY}); // 下一步加入到队列
            }
        }

        if (cur == null) {
            return 0;
        }
        return maze[cur[0]][cur[1]];
    }
}
