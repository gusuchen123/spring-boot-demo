package com.gusuchen.mq.rocketmq;

import com.gusuchen.mq.rocketmq.demo2.SpringProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

/**
 * <p></p>
 *
 * @author gusuchen
 * @date created in 2019-10-31
 */
@Slf4j
public class SpringProducerTest extends RocketMqApplicationTests {

    @Autowired
    private SpringProducer producer;

    @Test
    public void sendMessage() throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        for (int i = 0; i < 20; i ++) {
            Message message = new Message("spring-rocketmq-topic", null, ("Spring RocketMq Demo" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

            SendResult sendResult = producer.getProducer().send(message);

            log.error("sendResult:{}", sendResult);
        }
    }
}
