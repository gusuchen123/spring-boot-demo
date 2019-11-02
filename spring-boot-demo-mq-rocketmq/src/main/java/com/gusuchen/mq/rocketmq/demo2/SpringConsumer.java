package com.gusuchen.mq.rocketmq.demo2;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 *     消费者
 * </p>
 *
 * @author gusuchen
 * @date created in 2019-10-31
 */
@Slf4j
@Component
public class SpringConsumer {

    private DefaultMQPushConsumer consumer;

    @Autowired
    private MessageListenerConcurrently messageListener;

    public void init() throws MQClientException {
        log.error("开始启动消息消费者服务...");

        final String nameServerAddress = "192.168.2.20:9876";
        final String consumerGroupName = "spring-consumer-group";
        final String topicName = "spring-rocketmq-topic";

        // 创建一个消息消费者，并设置一个消息消费者组
        consumer = new DefaultMQPushConsumer(consumerGroupName);
        // 指定 NameServerAddress
        consumer.setNamesrvAddr(nameServerAddress);
        // 设置consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 订阅指定 Topic 下的所有消息
        consumer.subscribe(topicName, "*");
        // 注册消息监听器
        consumer.registerMessageListener(messageListener);
        // 消费者对象在使用前必须要调用 start 初始化
        consumer.start();

        log.error("消息消费者服务启动成功...");
    }

    public void destroy() {
        log.error("开始关闭消息消费者服务...");
        consumer.shutdown();
        log.error("消息消费者服务关闭成功....");
    }

    public DefaultMQPushConsumer getConsumer() {
        return this.consumer;
    }
}
