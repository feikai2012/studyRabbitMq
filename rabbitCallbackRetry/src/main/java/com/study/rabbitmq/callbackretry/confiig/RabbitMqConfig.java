package com.study.rabbitmq.callbackretry.confiig;

import com.study.rabbitmq.callbackretry.domain.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonesw
 * @description 消息队列相关配置
 * @date 2022-09-26
 */

@Configuration
public class RabbitMqConfig {


    // 声明重试队列
    @Bean
    public Queue callbackRetryQueue() {
        return new Queue(QueueEnum.QUEUE_RETRY.getName());
    }
    // 声明交换机
    @Bean
    public DirectExchange callbackRetryExchange() {
        return new DirectExchange(QueueEnum.QUEUE_RETRY.getExchange());
    }
    // 绑定预备队列到交换机，路由Key为ready
    @Bean
    public Binding bindReTryQueue() {
        return BindingBuilder.bind(callbackRetryQueue()).to(callbackRetryExchange()).with(QueueEnum.QUEUE_RETRY.getRouteKey());
    }
    // 声明交换机
    @Bean
    public DirectExchange notifyCallbackExchange() {
        return new DirectExchange(QueueEnum.QUEUE_READY.getExchange());
    }

    // 声明预备队列
    @Bean
    public Queue notifyCallbackReadyQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 10*1000); // 10秒队列消息超时时间
        args.put("x-dead-letter-exchange", QueueEnum.QUEUE_READY.getExchange()); // 死信交换机
        args.put("x-dead-letter-routing-key", QueueEnum.QUEUE_RETRY.getRouteKey()); // 死信路由
        return new Queue(QueueEnum.QUEUE_READY.getName(), true, false, false, args);
    }

    // 绑定预备队列到交换机，路由Key为ready
    @Bean
    public Binding bindNotifyCallbackReadyQueue() {
        return BindingBuilder.bind(notifyCallbackReadyQueue()).to(notifyCallbackExchange()).with(QueueEnum.QUEUE_READY.getRouteKey());
    }
    // 声明死信队列
    @Bean
    public Queue notifyCallbackDeadQueue() {
        return new Queue(QueueEnum.QUEUE_DEAD.getName());
    }
    // 绑定死信队列到交换机，路由Key为dead
    @Bean
    public Binding bindNotifyCallbackDeadQueue() {
        return BindingBuilder.bind(notifyCallbackDeadQueue()).to(notifyCallbackExchange()).with(QueueEnum.QUEUE_DEAD.getRouteKey());
    }




    /**
     * TEST
     * 消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange testDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TEST.getExchange())
                .durable(true)
                .build();
    }
    /**
     * 实际消费队列
     */
    @Bean
    public Queue testQueue() {
        return new Queue(QueueEnum.QUEUE_TEST.getName());
    }


    /**
     * 延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange testTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_TEST.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 延迟队列（死信队列）
     */
    @Bean
    public Queue testTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_TEST.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_TEST.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_TEST.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 将队列绑定到交换机
     */
    @Bean
    Binding translateTestBinding(DirectExchange testDirect,Queue testQueue){
        return BindingBuilder
                .bind(testQueue)
                .to(testDirect)
                .with(QueueEnum.QUEUE_TEST.getRouteKey());
    }

    /**
     * 将延迟队列绑定到交换机
     */
    @Bean
    Binding translateTestTtlBinding(DirectExchange testTtlDirect, Queue testTtlQueue){
        return BindingBuilder
                .bind(testTtlQueue)
                .to(testTtlDirect)
                .with(QueueEnum.QUEUE_TTL_TEST.getRouteKey());
    }
}
