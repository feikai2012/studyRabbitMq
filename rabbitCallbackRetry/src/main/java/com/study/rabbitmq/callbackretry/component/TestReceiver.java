package com.study.rabbitmq.callbackretry.component;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.study.rabbitmq.callbackretry.domain.NotifyMsg;
import com.study.rabbitmq.callbackretry.domain.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jonesw
 * @description
 * @date 2022-09-20
 */
@Component
@RabbitListener(queues = "appstore.test.test")
public class TestReceiver {
    private static Logger LOGGER = LoggerFactory.getLogger(TestReceiver.class);
    private Map<Long, Integer> counterMap=new ConcurrentHashMap<>();
    @RabbitHandler
    public void handler(String  notifyMsg, Message message, Channel channel) throws IOException {
        LOGGER.info("InsertTranslateReceiver---接收通知->{}",notifyMsg);


        try {
//            if(appId>2L){
//                LOGGER.error("00000");
                channel.basicPublish(QueueEnum.QUEUE_READY.getExchange(),QueueEnum.QUEUE_READY.getRouteKey(),basicProperties(message,0),notifyMsg.getBytes());
//            }
        }catch (IOException ioe) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }catch (IOException ex){
                LOGGER.error("MQ手动确认消息失败");
            }

        }
            try {
                //手动确认消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }catch (IOException e2){
                LOGGER.error("MQ手动确认消息失败");
            }

    }

    private AMQP.BasicProperties basicProperties(Message message, int retryTimes) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        headers.put("retry-times", retryTimes);
        return new AMQP.BasicProperties().builder()
                .deliveryMode(2) // 传送方式
                .contentEncoding("UTF-8") // 编码方式
                .contentType("application/json")
//                .contentType("text/plain")
                .headers(headers) //自定义属性
                .build();
    }

}
