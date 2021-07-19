package mytest.thread;

/* 阅读链接 http://www.jasongj.com/java/threadlocal/
 *  https://zhuanlan.zhihu.com/p/26713362
 *    适用场景
 *    如上文所述，ThreadLocal 适用于如下两种场景
 *        每个线程需要有自己单独的实例
 *        实例需要在多个方法中共享，但不希望被多线程共享
 *    session 实现
 * */
public class ThreadLocalTest {
    public static void main(String[] args) {
        for (int i =0 ;i<5;i++){
            Thread thread = new Thread(()->{
                ThreadLocalSession session = ThreadLocalSessionManager.getInitSession();
                System.out.println(Thread.currentThread().getName());
                System.out.println(session.toString());
                session.destroy();
                System.out.println(session.toString());

            });
            thread.setName("thread - "+(i+1));
            thread.start();
            try {
                Thread.sleep(500);
                ThreadLocalSessionManager.close();
            }catch (Exception e){

            }
        }
    }
}
