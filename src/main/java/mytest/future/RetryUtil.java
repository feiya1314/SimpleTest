package mytest.future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class RetryUtil {
    public static <R> CompletableFuture<R> retry(Supplier<CompletableFuture<R>> supplier, int retry) {
        CompletableFuture<R> future = supplier.get();
        for (int i = 0; i < retry; i++) {
            future = future.thenApply(CompletableFuture::completedFuture).exceptionally(throwable -> {
                System.out.println("error occurs ,尝试重试"+ throwable.getMessage());
                return supplier.get();
            }).thenCompose(Function.identity());
            // }).thenCompose(t->t);
        }
        return future;
    }
}
