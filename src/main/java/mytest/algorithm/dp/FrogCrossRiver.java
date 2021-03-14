package mytest.algorithm.dp;

import java.util.Arrays;

/**
 * 题目描述
 * 在河上有一座独木桥，一只青蛙想沿着独木桥从河的一侧跳到另一侧。在桥上有一些石子，青蛙很讨厌踩在这些石子上。由于桥的长度和青蛙一次跳过的距离都是正整数，
 * 我们可以把独木桥上青蛙可能到达的点看成数轴上的一串整点：0,1,…,L（其中L是桥的长度）。坐标为0的点表示桥的起点，坐标为L的点表示桥的终点。青蛙从桥的起点开始，
 * 不停的向终点方向跳跃。一次跳跃的距离是S到T之间的任意正整数（包括S,T）。当青蛙跳到或跳过坐标为LL的点时，就算青蛙已经跳出了独木桥。
 * <p>
 * 题目给出独木桥的长度L，青蛙跳跃的距离范围S,T，桥上石子的位置。你的任务是确定青蛙要想过河，最少需要踩到的石子数。
 * <p>
 * 输入格式
 * 第一行有1个正整数L(1 ≤ L ≤10^9)，表示独木桥的长度。
 * <p>
 * 第二行有3个正整数S,T,M，分别表示青蛙一次跳跃的最小距离，最大距离及桥上石子的个数，其中1≤S≤T≤10,1≤M≤100。
 * <p>
 * 第三行有M个不同的正整数分别表示这M个石子在数轴上的位置（数据保证桥的起点和终点处没有石子）。所有相邻的整数之间用一个空格隔开。
 * <p>
 * 输出格式
 * 一个整数，表示青蛙过河最少需要踩到的石子数。
 * <p>
 * 输入输出样例
 * 输入 #1
 * <p>
 * 10
 * 2 3 5
 * 2 3 5 6 7
 * 输出 #1
 * <p>
 * 2
 * 说明/提示
 * 对于30%的数据，1≤L≤10000；
 * <p>
 * 对于全部的数据，1≤L≤10^9。
 * ————————————————
 * 版权声明：本文为CSDN博主「漫步星云」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/weixin_44062302/article/details/104109135
 * https://ac.nowcoder.com/acm/problem/16655
 *
 * @author : feiya
 * @date : 2021/3/14
 * @description :
 */
public class FrogCrossRiver {
    /**
     * 独木桥上青蛙可能到达的点看成数轴上的一串整点：0,1,…,L（其中L是桥的长度）。坐标为0的点表示桥的起点，坐标为L的点表示桥的终点。
     * 一次跳跃的距离是S到T之间的任意正整数（包括S,T）
     * 题解 https://ac.nowcoder.com/acm/problem/blogs/16655
     * @param length    桥的长度
     * @param jumpS     跳跃的最短距离
     * @param jumpT     跳跃的最大距离
     * @param stonesNum 桥上石子的个数
     * @param stones    桥上石子在桥上的位置
     * @return 青蛙过河最少需要踩到的石子数
     */
    public int crossRiver(int length, int jumpS, int jumpT, int stonesNum, int[] stones) {
        // 如果每次跳的距离一样,则跳的位置固定，每次跳的位置为 jumpS、2jumpS、3jumpS 。。。
        // 如果石头的位置恰好是 jumpS 整数倍，那么刚好跳到这个石头上
        if (jumpS == jumpT) {
            int min = 0;
            for (int stone : stones) {
                if (stone % jumpS == 0) {
                    min++;
                }
            }
            return min;
        }

        // 如果每次跳的位置不同，用dp[i] 表示走到 i 位置时，踩的最少的石子数
        // i这个位置没有石头：dp[i]=min(dp[i],dp[k]) 从上一次位置跳到 i 时，由于从 k 跳最小是 dp[k] 次，到 i 要么是dp[k] 要么是dp[k]+1次
        // i这个位置是石头：dp[i]=min(dp[i],dp[k]+1)
        // (k是上一步的位置，S <= i-k <=T) 因为跳的范围是 S -> T
        // 如果以桥长来记录每个位置，由于桥太长，dp数组放不下每个位置
        // 由于 M <= 100, 石头的数目小于等于100,但是从起点到终点一共的举例是10^9.所以路中的很大一段是没有石头的
        // 每次行走的距离T,S <= 10,由于T S不相等，
        Arrays.sort(stones, 0, stonesNum + 1);
        int[] f = new int[10020];
        int[] w = new int[10020];
        int last = 0, k = 0;
        for (int i = 1; i <= stonesNum; i++) {
            for (int j = 1; j <= Math.min(stones[i] - last, 100); j++) {
                w[++k] = 0;
            }
            w[k] = 1;
            last = stones[i];
        }
        for (int i = 1; i <= k + 10; i++) {
            f[i] = 110000;
            for (int j = jumpS; j <= jumpT; j++) {
                if (i - j >= 0) {
                    f[i] = Math.min(f[i], f[i - j] + w[i]);
                }
            }
        }

        int result = 110000;
        for (int i = k; i <= k + 10; i++) {
            result = Math.min(result, f[i]);
        }

        return result;
    }
}
