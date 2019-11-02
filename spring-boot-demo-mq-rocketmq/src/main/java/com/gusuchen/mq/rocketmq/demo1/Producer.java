//package com.gusuchen.mq.rocketmq.demo1;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.exception.MQBrokerException;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.common.message.Message;
//import org.apache.rocketmq.remoting.common.RemotingHelper;
//import org.apache.rocketmq.remoting.exception.RemotingException;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StopWatch;
//
//import javax.annotation.PostConstruct;
//import java.io.UnsupportedEncodingException;
//
///**
// * <p>
// * 生产者
// * </p>
// *
// * @author gusuchen
// * @version V1.0
// * @date Created in 2019-09-24 17:20
// */
//@Slf4j
//@Component
//public class Producer {
//    private static final String PRODUCER_GROUP = "Producer";
//
//    private static final String NAME_SERVER_ADDRESS = "192.168.2.20:9876";
//
//    private static final Integer COUNT = 10000;
//
//    @PostConstruct
//    public void defaultMqProducer() {
//        // 1.生产者的组名, 确保唯一性
//        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);
//
//        // 2.指定NameServer地址，多个地址以 ; 隔开
//        producer.setNamesrvAddr(NAME_SERVER_ADDRESS);
//
//        try {
//            // 3.Producer对象在使用前必须要调用start初始化，初始化一次即可，不可以每次发送消息时都调用start方法
//            producer.start();
//
//            // 4.创建一个消息实例，包含topic tag 消息体
//            Message message = new Message("TopicTest", "push", "发送消息----hello rocket mq-----".getBytes(RemotingHelper.DEFAULT_CHARSET));
//
//            StopWatch stopWatch = new StopWatch();
//            stopWatch.start();
//
//            // 5.发送消息
//            for (int i = 0; i < COUNT; i++) {
//                SendResult sendResult = producer.send(message);
//                log.error("发送响应: MsgId:{}, 发送状态:{}, 消息的编号:{}", sendResult.getMsgId(), sendResult.getSendStatus(), i);
//            }
//
//            stopWatch.stop();
//            log.error("----------------发送一万条消息耗时：{}", stopWatch.getTotalTimeMillis());
//        } catch (MQClientException | UnsupportedEncodingException | InterruptedException | RemotingException | MQBrokerException e) {
//            e.printStackTrace();
//        } finally {
//            // 6.关闭: 一旦生产者实例不再被使用则将其关闭，包括清理资源，关闭网络连接等
//            producer.shutdown();
//        }
//    }
//}
