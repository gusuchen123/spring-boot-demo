package com.gusuchen.mq.rocketmq.demo2;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * <p>
 * 生产者
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-25 18:25
 */
@Slf4j
@Component
public class SpringProducer {

    private DefaultMQProducer producer;

    /**
     * <p>
     *     启动消息生产者服务
     * </p>
     * @throws MQClientException MQ异常 {@link MQClientException}
     */
    @PostConstruct
    public void init() throws MQClientException {
        log.error("开始启动消息生产服务....");

        String producerGroupName = "spring-producer-group";
        producer = new DefaultMQProducer(producerGroupName);

        String nameServerAddress = "192.168.2.20:9876";
        producer.setNamesrvAddr(nameServerAddress);

        producer.start();

        log.error("消息生产服务启动成功....");
    }

    /**
     * 关闭消息生产服务
     */
    @PreDestroy
    public void destroy() {
        log.error("开始关闭消息生产服务....");

        producer.shutdown();

        log.error("消息生产服务关闭成功...");
    }

    public DefaultMQProducer getProducer() {
        return this.producer;
    }
}
