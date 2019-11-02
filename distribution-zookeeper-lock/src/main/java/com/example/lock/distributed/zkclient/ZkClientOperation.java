package com.example.lock.distributed.zkclient;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

/**
 * <p>
 * {@link org.I0Itec.zkclient.ZkClient} 简单使用
 * {@link ZkClient#create(String, Object, CreateMode)} zookeeper不支持递归创建目录
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-17 15:34
 */
@Slf4j
public class ZkClientOperation {

    public static void main(String[] args) {
        // 1.创建一个zkClient客户端
        ZkClient zkClient = new ZkClient("192.168.2.20:2181");

        // 2.实现自定义序列化接口
        zkClient.setZkSerializer(new CustomZkSerializer());

        // 3.创建一个zknode,不能递归创建目录
        final String zkPath = "/locks";
        if (!zkClient.exists(zkPath)) {
            zkClient.createEphemeral(zkPath);
        }
        // 4.创建一个watch, 订阅zknode的变化
        zkClient.subscribeChildChanges(zkPath, (parentPath, currentChildren) -> log.error("【订阅子节点的变化】parentPath:{}, 子节点发生变化:{}", parentPath, currentChildren));

        // 5.创建一个watch, 监视zknode数据的变化
        zkClient.subscribeDataChanges(zkPath, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {
                log.error("【订阅数据的变化】dataPath:{}, 节点的数据发生变化, data:{}", dataPath, data);
            }

            @Override
            public void handleDataDeleted(String dataPath) {
                log.error("【订阅数据的变化】dataPath:{}, 节点被删除了", dataPath);
            }
        });

        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) {
                log.error("【订阅状态发生变化】state:{}", state.toString());
            }

            @Override
            public void handleNewSession() {

            }

            @Override
            public void handleSessionEstablishmentError(Throwable error) {

            }
        });

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
