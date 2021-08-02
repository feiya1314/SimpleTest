package mytest.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalSessionManager {
    private static final ThreadLocal<ThreadLocalSession> threadLocal = new ThreadLocal<>();
    private static final ThreadLocal<ThreadLocalSession> threadLocalWithInit = ThreadLocal.withInitial(ThreadLocalSession::new);
    private static final ThreadLocal<String> threadLocal2 = new ThreadLocal<>();
    private static AtomicInteger id = new AtomicInteger(1);

    public static ThreadLocalSession getSession() {
        ThreadLocalSession session = threadLocal.get();
        String name = threadLocal2.get();
        if (session == null || session.isDestroy()) {
            session = new ThreadLocalSession();
            int id = ThreadLocalSessionManager.id.getAndIncrement();
            session.setId(id);
            session.setName("jack - " + id);
            session.setStatus("Running");
            threadLocal.set(session);
            threadLocal2.set("threadLocal2 "+"jack - " + id);
        }
        return session;
    }

    public static ThreadLocalSession getInitSession() {
        ThreadLocalSession session = threadLocalWithInit.get();
        String name = threadLocal2.get();
        int id = ThreadLocalSessionManager.id.getAndIncrement();
        session.setId(id);
        session.setName("jack - " + id);
        session.setStatus("Running");
        threadLocal2.set("threadLocal2 "+"jack - " + id);

        /*if (session.isDestroy()) {
            session = new ThreadLocalSession();
            int id = ThreadLocalSessionManager.id.getAndIncrement();
            session.setId(id);
            session.setName("jack - " + id);
            session.setStatus("Running");
            threadLocal.set(session);
            threadLocal2.set("threadLocal2 "+"jack - " + id);
        }*/
        return session;
    }
    public static void close() {
        ThreadLocalSession session = threadLocal.get();
        if (session != null && !session.isDestroy()) {
            session.destroy();
            threadLocal.remove();
        }
    }
}
