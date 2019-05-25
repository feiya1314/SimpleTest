package mytest.algorithm;

/*
 * 找出数组中出现奇数次的元素
 * 1、给定一个含有n个元素的整型数组array，其中只有一个元素出现奇数次，找出这个元素
 * 2、由 n 个元素组成的数组，n - 2 个数出现了偶数次，两个数出现了奇数次（这两个数不相等），如何用 o(1)的空间复杂度，找出这两个数
 * */
public class SingleNumber {
    /*
     * 1 问解
     *  原理：一个数异或自己结果为 0 ，任意数 异或 0，结果是其自身
     *  @param origin 原始数据
     *   length 长度
     * */
    public static int oneSingleNum(int[] origin, int length) {
        int result = origin[0];
        for (int i = 1; i < length; i++) {
            result ^= origin[i];
        }
        return result;
    }

    /*
     * 2 问解
     *  原理 ： 假设这两个数分别为a、b，将数组中所有元素异或之后的结果为x , x即为 a b 异或之后的结果，并且因为 a!= b
     *  所以 x 中至少有一位 是 1 ，假如是第 k 位 为1 ，例如 1001010 ，k = 1,3,6 ,如取，k=1 ,此时 数组中 第k 位 = 1 的数字与 x 异或，
     *  x ^ arr[1] ^ arr[2] ^ arr[4] ^ arr[9] ... 得到的结果必然为a 和 b 之一，因为 a 和 b 两个必然是 一个第K 位为 1另一个第K 位为0
     *  这样另一个 即可通过 x 异或这个得到 例如 b=a^x
     *  @param origin 原始数据
     *   length 长度
     * */
    public static int[] twoSingleNum(int[] origin, int length) {
        int[] finalResult = new int[2];
        int firstResult = oneSingleNum(origin, length);
        int key = 0;
        int temp = firstResult;
        // 找到firstResult 第一个为1 的位置 key
        while ((temp & 1) != 1) {
            temp = temp >> 1;
            key++;
        }

        int a = firstResult;
        for (int i = 0; i < length; i++) {
            if (((origin[i] >> key) & 1) == 1) {
                a = a ^ origin[i];
            }
        }
        finalResult[0] = a;
        finalResult[1] = a ^ firstResult;
        return finalResult;
    }

    public static void main(String[] args) {
        System.out.println(0xff>>>7);
        int[] arr = new int[]{1, 2, 4, 2, 4,5,6,5,6,1,1};
        System.out.println(oneSingleNum(arr, 11));
        int[] arr2 = new int[]{1, 2, 4, 2, 4,5,6,5,6,1,1,3};
        int[] finalResult = twoSingleNum(arr2, 12);
        System.out.println("a : " + finalResult[0] + " b : " + finalResult[1]);
    }
}
