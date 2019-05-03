package mytest.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalSessionManager {
    private static final ThreadLocal<ThreadLocalSession> threadLocal = new ThreadLocal<>();
    private static AtomicInteger id = new AtomicInteger(1);

    public static ThreadLocalSession getSession() {
        ThreadLocalSession session = threadLocal.get();
        if (session == null || session.isDestroy()) {
            session = new ThreadLocalSession();
            int id = ThreadLocalSessionManager.id.getAndIncrement();
            session.setId(id);
            session.setName("jack - " + id);
            session.setStatus("Running");
        }
        return session;
    }

    public static void close() {
        ThreadLocalSession session = threadLocal.get();
        if (session != null && !session.isDestroy()) {
            session.destroy();
        }
    }
}
