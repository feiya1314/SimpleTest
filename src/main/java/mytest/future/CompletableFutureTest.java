package mytest.future;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {
    public static void main(String[] args) throws Exception {
        //CompletableFuture
        //https://www.ibm.com/developerworks/cn/java/j-cf-of-jdk8/index.html
        //Callable
        //FutureTask
        System.out.println("同步泡面");
        long start = System.currentTimeMillis();
        CompletableFutureTest.noodles();
        System.out.println("耗时 ： " + (System.currentTimeMillis() - start));

        System.out.println();
        System.out.println();

        System.out.println("异步泡面1");
        start = System.currentTimeMillis();
        CompletableFutureTest.noodlesAsyncOne();
        System.out.println("耗时 ： " + (System.currentTimeMillis() - start));

        System.out.println();
        System.out.println();

        System.out.println("异步泡面2");
        start = System.currentTimeMillis();
        CompletableFutureTest.noodlesAsyncTwo();
        System.out.println("耗时 ： " + (System.currentTimeMillis() - start));

    }

    public static void noodles() throws Exception {
        InstantNoodles instantNoodles = new InstantNoodles();
        instantNoodles.prepare();
        Water water = new Water();
        water.heating();
        System.out.println("水煮好了，温度" + water.getTemperature());
        System.out.println("获取开水");
        System.out.println("倒开水 water : " + water.getWater());
        instantNoodles.cook(water.getWater());
        System.out.println("泡面好了");
        System.out.println(instantNoodles.getNoodles());
    }

    public static void noodlesAsyncOne() throws Exception {
        //System.out.println("主线程 ： " + Thread.currentThread().getName());
        CompletableFuture<Water> waterFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("烧水线程 ： " + Thread.currentThread().getName());
            Water water = new Water();
            water.heating();
            return water;
        }).whenComplete((w, e) -> {
            //System.out.println("whenComplete线程 ： " + Thread.currentThread().getName());
            System.out.println("水煮好了，温度" + w.getTemperature());
        });

        CompletableFuture<InstantNoodles> noodlesFuture = CompletableFuture.supplyAsync(() -> {
            InstantNoodles instantNoodles = new InstantNoodles();
            instantNoodles.prepare();

            System.out.println("获取开水");
            try {
                Optional<String> waterStr = Optional.ofNullable(waterFuture.get().getWater());
                waterStr.ifPresent((wa) -> {
                    System.out.println("倒开水 water : " + wa);
                    instantNoodles.cook(wa);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return instantNoodles;
        });

        InstantNoodles noodles = noodlesFuture.get();

        System.out.println("泡面好了");
        System.out.println(noodles.getNoodles());
    }

    public static void noodlesAsyncTwo() throws Exception {
        CompletableFuture<Water> waterFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("烧水线程 ： " + Thread.currentThread().getName());
            Water water = new Water();
            water.heating();
            return water;
        }).whenComplete((w, e) -> {
            //System.out.println("whenComplete线程 ： " + Thread.currentThread().getName());
            System.out.println("水煮好了，温度" + w.getTemperature());
        });

        /*CompletableFuture<InstantNoodles> noodlesFuture = */

        CompletableFuture<InstantNoodles> noodlesFuture = waterFuture.thenCombine(CompletableFuture.supplyAsync(() -> {
            InstantNoodles instantNoodles = new InstantNoodles();
            instantNoodles.prepare();
            return instantNoodles;
        }), (w, n) -> {
            System.out.println("获取开水");
            Optional<String> waterStr = Optional.ofNullable(w.getWater());
            waterStr.ifPresent((wa) -> {
                System.out.println("倒开水 water : " + wa);
                n.cook(wa);
            });
            return n;
        });

        InstantNoodles noodles = noodlesFuture.get();

        System.out.println("泡面好了");
        System.out.println(noodles.getNoodles());
    }
}
