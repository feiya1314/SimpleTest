package mytest.algorithm.dp;

/**
 * 小兔的叔叔从外面旅游回来给她带来了一个礼物，小兔高兴地跑回自己的房间，拆开一看是一个棋盘，小兔有所失望。不过没过几天发现了棋盘的好玩之处。
 * 从起点(0，0)走到终点(n,n)的最短路径数是C(2n,n),现在小兔又想如果不穿越对角线(但可接触对角线上的格点)，这样的路径数有多少?
 * 小兔想了很长时间都没想出来，现在想请你帮助小兔解决这个问题，对于你来说应该不难吧!
 * ————————————————
 * 版权声明：本文为CSDN博主「温姑娘」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/wyxeainn/java/article/details/61926253
 */
public class Rabbit {
    public static void main(String[] args) {

    }

    public static int num(int n) {
        int[][] dp = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            dp[0][i] = 1;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (i == j) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
                }
            }
        }
        return dp[n][n] * 2;
    }
}
