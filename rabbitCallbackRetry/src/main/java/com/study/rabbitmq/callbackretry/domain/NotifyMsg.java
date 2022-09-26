package com.study.rabbitmq.callbackretry.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jonesw
 * @description
 * @date 2022-09-26
 */
@Getter
@Setter
public class NotifyMsg {
    private Long appId;
    private String time;
}
