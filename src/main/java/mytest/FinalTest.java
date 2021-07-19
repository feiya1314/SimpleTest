package mytest;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FinalTest {
    public static void main(String[] args) {
        /*int cont=10;
        cont = cont + (cont>>1);
        //Collections.synchronizedList()
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(null);
        linkedHashSet.add(null);
        System.out.println(linkedHashSet);
        List<String> map = new ArrayList<>();
        map.add(null);
        map.add(null);
        map.add("test2");
        System.out.println(map);
        //new FinalTest().testFinal1();
        //TreeMap
        AtomicInteger a;
        ReentrantLock l;
        LongAdder adder;*/

        int[] arr = new int[]{6, 4, -3, 0, 5, -2, -1, 0, 1, -9};
        System.out.println(CloudmallInterview1(arr));
        //LinkedHashSet
    }

    public static void quickSort(int[] arr, int low, int high) {
        int p, i, j, temp;

        if (low >= high) {
            return;
        }
        // p基准数，最左边的那个
        p = arr[low];
        i = low;
        j = high;
        while (i < j) {
            //左移，直到遇到比p大的
            while (arr[j] <= p && i < j) {
                j--;
            }
            // 右移 直到遇到比p小的
            while (arr[i] >= p && i < j) {
                i++;
            }

            temp = arr[j];
            arr[j] = arr[i];
            arr[i] = temp;
        }
        arr[low] = arr[i];
        arr[i] = p;
        quickSort(arr, low, i - 1);  //对左边快排
        quickSort(arr, i + 1, high); //对右边快排
    }

    public static int[] CloudmallInterview1(int[] Numbers) {
        int[] result = {};
        int length = Numbers.length;

        quickSort(Numbers, 0, length - 1);

        return result;
    }

    public static String toLeft(String source) {
        StringBuilder stars = new StringBuilder();
        StringBuilder nonStars = new StringBuilder();
        char[] sourceChars = source.toCharArray();
        for (char c : sourceChars) {
            if (c == '*') {
                stars.append("*");
                continue;
            }
            nonStars.append(c);
        }
        return stars.append(nonStars).toString();
    }

    public void testFinal1() {
        final Map<String, String> map = new HashMap<>();
        map.put("test11", "test11");
        map.put("test12", "test12");
        map.put("test13", "test13");
        map.put("test14", "test14");
        System.out.println("map1:");
        System.out.println(map);
        System.out.println("------------------------------------------------");
        testFinal(map);
        System.out.println("------------------------------------------------");
        System.out.println("map1:");
        System.out.println(map);
    }

    public void testFinal(Map<String, String> map) {
        map = new HashMap<>();
        map.put("test21", "test21");
        map.put("test22", "test22");
        map.put("test23", "test23");
        map.put("test24", "test24");
        System.out.println("map2:");
        System.out.println(map);
    }

    public void test() {
        ConcurrentHashMap<String,String> c=null;
        c.get("s");
        String str = new String("abc");
        ReferenceQueue queue = new ReferenceQueue();
        // 创建虚引用，要求必须与一个引用队列关联
        PhantomReference pr = new PhantomReference(str, queue);
    }

}
