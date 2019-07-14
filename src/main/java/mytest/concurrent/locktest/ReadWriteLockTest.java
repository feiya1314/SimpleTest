package mytest.concurrent.locktest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {
    /**
     * 缓存器,这里假设需要存储1000左右个缓存对象，按照默认的负载因子0.75，则容量=750，大概估计每一个节点链表长度为5个
     * 那么数组长度大概为：150,又有雨设置map大小一般为2的指数，则最近的数字为：128
     */
    private Map<String, Object> map = new HashMap<>(128);
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    public Object get(String id){
        Object value = null;
        rwl.readLock().lock();//首先开启读锁，从缓存中去取
        try{
            value = map.get(id);
            if(value == null){  //如果缓存中没有释放读锁，上写锁
                rwl.readLock().unlock();
                rwl.writeLock().lock();
                try{
                    value = map.get(id);
                    if(value == null){ //防止多写线程重复查询赋值,因为其他线程可能已经写成功了
                        value = "redis-value";  //此时可以去数据库中查找，这里简单的模拟一下
                    }
                    rwl.readLock().lock(); //加读锁降级写锁,不明白的可以查看上面锁降级的原理与保持读取数据原子性的讲解
                }finally{
                    rwl.writeLock().unlock(); //释放写锁
                }
            }
        }finally{
            rwl.readLock().unlock(); //最后释放读锁
        }
        return value;
    }

    class CachedData{
        Object data;
        volatile boolean cacheValid;
        final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

        public void processCachedData() {
            rwl.readLock().lock();
            if (!cacheValid) {
                // Must release read lock before acquiring write lock
                rwl.readLock().unlock();
                rwl.writeLock().lock();
                try {
                    // Recheck state because another thread might have,acquired write lock and changed state before we did.
                    if (!cacheValid) {
                        //data = ...
                        cacheValid = true;
                    }
                    // 在释放写锁之前通过获取读锁降级写锁(注意此时还没有释放写锁)
                    rwl.readLock().lock();
                } finally {
                    rwl.writeLock().unlock(); // 释放写锁而此时已经持有读锁
                }
            }

            try {
                //use(data);
            } finally {
                rwl.readLock().unlock();
            }
        }
        /*1. 多个线程同时访问该缓存对象时，都加上当前对象的读锁，之后其中某个线程优先查看data数据是否为空。【加锁顺序序号：1 】

        2. 当前查看的线程发现没有值则释放读锁立即加上写锁，准备写入缓存数据。（不明白为什么释放读锁的话可以查看上面讲解进入写锁的前提条件）【加锁顺序序号：2和3 】

        3. 为什么还会再次判断是否为空值（!cacheValid）是因为第二个、第三个线程获得读的权利时也是需要判断是否为空，否则会重复写入数据。

        4. 写入数据后先进行读锁的降级后再释放写锁。【加锁顺序序号：4和5 】

        5. 最后数据数据返回前释放最终的读锁。【加锁顺序序号：6 】

　　    如果不使用锁降级功能，如先释放写锁，然后获得读锁，在这个get过程中，可能会有其他线程竞争到写锁 或者是更新数据 则获得的数据是其他线程更新的数据，
        可能会造成数据的污染，即产生脏读的问题。*/
    }
}
