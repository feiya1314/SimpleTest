package mytest;

import java.util.Scanner;
import java.io.*;

/**
 * https://ac.nowcoder.com/acm/contest/5652#question
 */
public class NewcoderIO {
    public static void main(String[] args)throws Exception {
        //Scanner in = new Scanner(System.in);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // 矩阵高度
        int n = Integer.parseInt(br.readLine());
        // 每次读一行
        String line = br.readLine();
        // 以空格分割
        String[] chars=line.split(" ");
        // 矩阵宽度
        int w = chars.length;
        String[][] matrix = new String[n][w];
        for(int i=0;i<w;i++){
            matrix[0][i]=chars[i];
        }

        for(int i=1;i<n;i++){
            line = br.readLine();
            chars=line.split(" ");
            for(int j=0;j<w;j++){
                matrix[i][j]=chars[j];
            }
        }
        int[][] dp = new int[n+1][w+1];
        int max = 0;
        for(int i=0;i<n;i++){
            for(int j=0;j<w;j++){
                if("1".equals(matrix[i][j])){
                    dp[i+1][j+1] = Math.min(Math.min(dp[i][j],dp[i][j+1]),dp[i+1][j])+1;
                    max = Math.max(max,dp[i+1][j+1]);
                }
            }
        }
        System.out.println(max*max);
    }
}
