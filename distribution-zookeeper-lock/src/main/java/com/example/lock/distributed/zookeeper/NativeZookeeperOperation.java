package com.example.lock.distributed.zookeeper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * {@link org.apache.zookeeper.ZooKeeper} 简单使用
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-18 17:10
 */
@Slf4j
public class NativeZookeeperOperation {

    public static void main(String[] args) {
        final String lockRoot = "/lock";
        ZooKeeper zooKeeper = ZooKeeperWatcher.conn().zooKeeper;

        /*
         * 根节点如果不存在，则创建根结点；
         * 并发问题，如果两个线程同时检测不存在，两个同时去创建必须有一个会失败
         */
        try {
            if (zooKeeper.exists(lockRoot, true) == null) {
                zooKeeper.create(lockRoot, StrUtil.EMPTY.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException ex) {
            log.error("【ZooKeeperWatcher#conn()】线程名称:{}, 创建默认的根节点:{} 失败",
                    Thread.currentThread().getId(), lockRoot);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class ZooKeeperWatcher implements Watcher {
        private static final String CONNECTION_ADDR = "192.168.2.20:2181";
        private static final int TIMEOUT = 30000;

        ZooKeeper zooKeeper;
        Thread thread;

        static ZooKeeperWatcher conn() {
            ZooKeeperWatcher watcher = new ZooKeeperWatcher();
            try {
                watcher.zooKeeper = new ZooKeeper(CONNECTION_ADDR, TIMEOUT, watcher);
                watcher.thread = Thread.currentThread();

                // 阻塞等待连接建立完毕
                LockSupport.park();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return watcher;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (ObjectUtil.isNotNull(this.thread)) {
                /*
                 * 唤醒阻塞线程，这是监听线程，跟获取锁的线程不是同一个线程
                 */
                LockSupport.unpark(this.thread);
                this.thread = null;
            }
        }
    }
}
