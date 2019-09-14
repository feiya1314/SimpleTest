package mytest.thread.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class MakeMoneyTask extends RecursiveTask<Integer> {
    private static final int MIN_GOAL_MONEY = 200000;
    private int goalMoney;
    private String name;
    private static final AtomicInteger employeeNo = new AtomicInteger();

    public MakeMoneyTask(int goalMoney) {
        this.goalMoney = goalMoney;
        this.name = "员工" + employeeNo.getAndIncrement() + "号";
    }

    @Override
    protected Integer compute() {
        if (goalMoney < MIN_GOAL_MONEY) {
            System.out.println("打工仔" + name + "需要赚" + goalMoney);
            return makeMoney();
        }
//        int subThreadCount = ThreadLocalRandom.current().nextInt(10) + 2;
        int subThreadCount = 5;
        System.out.println("高级打工仔" + name + ": 上级要我赚 " + goalMoney + ", 有点小多,没事让我" + subThreadCount + "个手下去完成吧," +
                "每人赚个 " + Math.ceil(goalMoney * 1.0 / subThreadCount) + "元应该没问题...");

        List<MakeMoneyTask> tasks = new ArrayList<>();
        for (int i = 0; i < subThreadCount; i++) {
            tasks.add(new MakeMoneyTask(goalMoney / subThreadCount));
        }
        // 提交全部任务
        Collection<MakeMoneyTask> makeMoneyTasks = invokeAll(tasks);

        int sum = 0;
        try {
            // 计算结果
            for (MakeMoneyTask moneyTask : makeMoneyTasks) {
                sum += moneyTask.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("高级打工仔" + name + ": 嗯,不错,效率还可以,终于赚到 " + sum + "元,赶紧邀功去....");
        return sum;
    }

    private Integer makeMoney() {
        int sum = 0;
        int day = 1;
        try {
            while (true) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(500));
                int money = ThreadLocalRandom.current().nextInt(MIN_GOAL_MONEY / 3);
                System.out.println("打工仔" + name + ": 在第 " + (day++) + " 天赚了" + money);
                sum += money;
                if (sum >= goalMoney) {
                    System.out.println("打工仔" + name + ": 终于赚到 " + sum + " 元, 可以交差了...");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sum;
    }
}
