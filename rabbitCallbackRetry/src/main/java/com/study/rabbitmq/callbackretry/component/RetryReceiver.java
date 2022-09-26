package com.study.rabbitmq.callbackretry.component;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.study.rabbitmq.callbackretry.domain.NotifyMsg;
import com.study.rabbitmq.callbackretry.domain.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author Jonesw
 * @description
 * @date 2022-09-20
 */
@Component
public class RetryReceiver {
    private static Logger LOGGER = LoggerFactory.getLogger(RetryReceiver.class);

    @RabbitListener(queues = "appstore.test.retry")
    public void handler(String notifyMsg, Message message, Channel channel)  {
        LOGGER.info("RetryReceiver---接收通知->{},message-->{}",notifyMsg,JSONUtil.toJsonStr(message.getMessageProperties()));
        try {
            int retryTimes = (int) message.getMessageProperties().getHeaders().get("retry-times");
            LOGGER.info("执行第[{}]次重发...", retryTimes++);
            // 业务逻辑
            // 直接模拟失败
            if (retryTimes > 2) {
                LOGGER.error("重试次数满，进入死信队列"); // 触发告警
                channel.basicPublish(QueueEnum.QUEUE_DEAD.getExchange(), QueueEnum.QUEUE_DEAD.getRouteKey(), MessageProperties.PERSISTENT_BASIC, notifyMsg.getBytes());
            } else {
                channel.basicPublish(QueueEnum.QUEUE_READY.getExchange(), QueueEnum.QUEUE_READY.getRouteKey(), basicProperties(message, retryTimes), notifyMsg.getBytes());
            }
        } catch (IOException e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ex) {
                LOGGER.error("MQ手动确认消息失败！");
            }
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (IOException e) {
            LOGGER.error("MQ手动确认消息失败！");
        }

    }

    private AMQP.BasicProperties basicProperties(Message message, int retryTimes) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        headers.put("retry-times", retryTimes);
        return new AMQP.BasicProperties().builder()
                .deliveryMode(2) // 传送方式
                .contentEncoding("UTF-8") // 编码方式
                .contentType("application/json")
                .headers(headers) //自定义属性
                .build();
    }

}
