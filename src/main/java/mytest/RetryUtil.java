package mytest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : feiya
 * @date : 2021/9/20
 * @description : 重试工具类
 */
public class RetryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryUtil.class);

    /**
     * 重试方法
     *
     * @param supplier   函数式接口，同Supplier ，只是可以抛出异常
     * @param retryCount 重试次数，supplier执行次数最大为 retryCount + 1
     * @param sleepTime  重试之前休眠多少毫秒
     * @param <R>        supplier获取的结果
     * @return RetryResult 重试的结果
     */
    public static <R> RetryResult<R> retry(SupplierWithException<R> supplier, int retryCount, long sleepTime) {
        RetryResult<R> result = new RetryResult<>();
        if (supplier == null) {
            result.setCode(RetryCode.SUCCESS.code);
            return result;
        }

        int retryTimes = 0;
        while (true) {
            try {
                R data = supplier.get();
                result.setData(data);
                result.setSuccess(true);
                result.setHasRetry(retryTimes > 0);
                result.setRetryTimes(retryTimes);
                result.setCode(retryTimes > 0 ? RetryCode.RETRY_ADN_SUCCESS.code : RetryCode.SUCCESS.code);

                return result;
            } catch (Exception e) {
                LOGGER.error("RetryUtil exec error ", e);
                if (retryCount <= 0) {
                    result.setSuccess(false);
                    result.setHasRetry(retryTimes > 0);
                    result.setCode(RetryCode.RETRY_AND_FAIL.code);
                    result.setRetryTimes(retryTimes);
                    return result;
                }
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        LOGGER.error("RetryUtil exec InterruptedException error ", e);
                        result.setSuccess(false);
                        result.setHasRetry(retryTimes > 0);
                        result.setRetryTimes(retryTimes);
                        result.setCode(RetryCode.INNER_ERROR.code);
                        return result;
                    }
                }
                retryCount--;
                retryTimes++;
            }
        }
    }

    public static class RetryResult<T> {
        private boolean success;
        private T data;
        private int code;
        private boolean hasRetry = false;
        private int retryTimes = 0;

        public int getRetryTimes() {
            return retryTimes;
        }

        public void setRetryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public boolean isHasRetry() {
            return hasRetry;
        }

        public void setHasRetry(boolean hasRetry) {
            this.hasRetry = hasRetry;
        }
    }

    public enum RetryCode {
        SUCCESS(0, ""),
        RETRY_ADN_SUCCESS(1, ""),
        FAIL(2, ""),
        RETRY_AND_FAIL(3, ""),
        INNER_ERROR(4, ""),
        ;
        private int code;
        private String desc;

        RetryCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
