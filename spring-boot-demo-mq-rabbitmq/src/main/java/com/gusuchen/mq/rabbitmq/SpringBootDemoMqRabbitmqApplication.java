package com.gusuchen.mq.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * spring-boot 集成 RabbitMQ 实现基于直接队列模式、分列模式、主题模式、延迟队列的消息发送和接收
 *
 * @author gusuchen
 * @since 2019-09-04
 */
@SpringBootApplication
public class SpringBootDemoMqRabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoMqRabbitmqApplication.class, args);
    }

}
