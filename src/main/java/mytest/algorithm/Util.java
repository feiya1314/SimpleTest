package mytest.algorithm;

public class Util {
    public static void print(int[] arr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int value : arr) {
            stringBuilder.append(value).append(",");
        }
        System.out.println(stringBuilder.toString());
    }

    public static void swap(int[] arr, int x, int y) {
        int temp = arr[x];
        arr[x] = arr[y];
        arr[y] = temp;
    }
}
