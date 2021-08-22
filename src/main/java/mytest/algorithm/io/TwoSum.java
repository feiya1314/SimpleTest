package mytest.algorithm.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * 链接：https://ac.nowcoder.com/acm/contest/5652/A?&headNav=acm
 * 来源：牛客网
 *
 * 计算a+b
 * 打开以下链接可以查看正确的代码
 * 1
 * https://ac.nowcoder.com/acm/contest/5657#question
 * 输入描述:
 * 输入包括两个正整数a,b(1 <= a, b <= 10^9),输入数据包括多组。
 * 输出描述:
 * 输出a+b的结果
 * 示例1
 * 输入
 * 复制
 * 1 5
 * 10 20
 * 输出
 * 复制
 * 6
 * 30
 *
 * @author : feiya
 * @date : 2021/8/6
 * @description :
 */
public class TwoSum {
    public static void main(String[] args)throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line =null;
        while((line = br.readLine())!=null){
            String[] s = line.split(" ");
            System.out.println(Integer.parseInt(s[0])+Integer.parseInt(s[1]));
        }
        br.close();
    }

    public void s2(){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println(a+b);
        }
    }
}
