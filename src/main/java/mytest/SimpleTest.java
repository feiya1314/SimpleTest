package mytest;

import java.util.Random;

/**
 * @author : feiya
 * @date : 2021/9/21
 * @description :
 */
public class SimpleTest {
    public static void main(String[] args) {
        RetryUtil.RetryResult<Long> result = RetryUtil.retry(() -> {
            int times = 3;
            Random random = new Random();
            while (times > 0) {
                if (random.nextInt(3) == 1) {
                    throw new Exception("测试");
                }
                times--;
            }
            return 999L;
        }, 3, 500L);
        if (result.isSuccess()) {
            System.out.println("success result data : " + result.getData() + " code : " + result.getCode() + "  retry times " + result.getRetryTimes());
        } else {
            System.out.println("fail result data : " + result.getData() + " code : " + result.getCode() + "  retry times " + result.getRetryTimes());
        }
    }
}
