package com.study.rabbitmq.callbackretry.component;

import cn.hutool.json.JSONUtil;
import com.study.rabbitmq.callbackretry.domain.NotifyMsg;
import com.study.rabbitmq.callbackretry.domain.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Jonesw
 * @description
 * @date 2022-09-20
 */
@Component
public class TestSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(NotifyMsg notifyMsg, final long delayTimes, String language) {
        LOGGER.info("send appId:{},language:{},当前时间：{},delayTimes:{}", notifyMsg.getAppId(), language, new Date(), delayTimes);
        String exchange = QueueEnum.QUEUE_TTL_TEST.getExchange();
        String routeKey = QueueEnum.QUEUE_TTL_TEST.getRouteKey();

        amqpTemplate.convertAndSend(exchange, routeKey, JSONUtil.toJsonStr(notifyMsg), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //给消息设置延迟毫秒值
                message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                return message;
            }
        });
    }
}
