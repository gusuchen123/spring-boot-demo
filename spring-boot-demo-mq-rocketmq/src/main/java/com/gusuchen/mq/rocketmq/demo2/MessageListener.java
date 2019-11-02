package com.gusuchen.mq.rocketmq.demo2;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *     消息监听类，实现 {@link MessageListenerConcurrently}
 * </p>
 *
 * @author gusuchen
 * @date created in 2019-10-31
 */
@Slf4j
@Component
public class MessageListener implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExitList, ConsumeConcurrentlyContext context) {
        if (CollectionUtil.isNotEmpty(messageExitList)) {
            messageExitList.forEach(messageExt -> {
                try {
                    log.error("监听到消息:{}", Arrays.toString(messageExt.getBody()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
