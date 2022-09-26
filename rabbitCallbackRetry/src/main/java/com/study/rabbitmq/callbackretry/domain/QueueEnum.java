package com.study.rabbitmq.callbackretry.domain;

import lombok.Getter;

/**
 * @author Jonesw
 * @description 息队列枚举配置
 * @date 2022-08-17
 */
@Getter
public enum QueueEnum {
    /**
     * 消息通知队列
     */
    QUEUE_RETRY("appstore.test.direct", "appstore.test.retry", "appstore.test.retry"),//重试队列
    QUEUE_DEAD("appstore.test.direct", "appstore.test.dead", "appstore.test.dead"),//死信队列
    QUEUE_TEST("appstore.test.direct", "appstore.test.test", "appstore.test.test"),//延时队列
    QUEUE_READY("appstore.test.direct", "appstore.test.ready", "appstore.test.ready"),//准备队列
    /**
     * 消息通知ttl队列
     */
    QUEUE_TTL_TEST("appstore.test.direct.ttl", "appstore.test.test.ttl", "appstore.test.test.ttl");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
