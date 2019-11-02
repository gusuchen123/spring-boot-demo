//package com.gusuchen.mq.rocketmq.demo1;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.rocketmq.remoting.common.RemotingHelper;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.UnsupportedEncodingException;
//
///**
// * <p>
// * 消费者
// * </p>
// *
// * @author gusuchen
// * @version V1.0
// * @date Created in 2019-09-24 17:20
// */
//@Slf4j
//@Component
//public class Consumer {
//    /**
//     * 消费者的组名
//     */
//    private static final String CONSUMER_GROUP = "PushConsumer";
//
//    /**
//     * NameServer 地址
//     */
//    private static final String NAME_SERVER_ADDRESS = "192.168.2.20:9876";
//
//    @PostConstruct
//    public void defaultMqPushConsumer() {
//        // 1.创建一个消息消费者，并设置一个消费者的组名，确保名字的唯一性
//        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
//
//        // 2.指定NameServer地址，多个地址以 , 做分割;
//        pushConsumer.setNamesrvAddr(NAME_SERVER_ADDRESS);
//
//        try {
//            // 3.订阅PushTopic下Tag为push的消息，如果subExpression: *，代表订阅topic下面所有的消息
//            pushConsumer.subscribe("TopicTest", "push");
//
//            // 4.设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费，如果非第一次启动，那么按照上次消费的位置继续消费
//            pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//
//            // 5.注册消息监听器，消费消息
//            pushConsumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
//                try {
//                    for (MessageExt messageExt : list) {
//
//                        log.error("messageExt:{}", messageExt);
//
//                        String messageBody = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
//
//                        log.error("消费响应: msgId:{}, msgBody:{}", messageExt.getMsgId(), messageBody);
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//
//                    // rocketmq 重试机制
//                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
//                }
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            });
//
//            // 6.消费者对象在使用之前必须要调用 start 初始化，开始消费消息
//            pushConsumer.start();
//        } catch (MQClientException e) {
//            e.printStackTrace();
//        }
//    }
//}
