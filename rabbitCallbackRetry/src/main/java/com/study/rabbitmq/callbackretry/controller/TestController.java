package com.study.rabbitmq.callbackretry.controller;

import cn.hutool.core.date.DateUtil;
import com.study.rabbitmq.callbackretry.component.TestSender;

import com.study.rabbitmq.callbackretry.domain.NotifyMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author Jonesw
 * @description
 * @date 2022-09-22
 */
@Controller
public class TestController {
    @Autowired
    private TestSender testSender;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    @ResponseBody
    public void send() {
        NotifyMsg notifyMsg = new NotifyMsg();
        notifyMsg.setAppId(131L);
        notifyMsg.setTime(DateUtil.formatTime(new Date()));
        testSender.sendMessage(notifyMsg,1L,"TEST");
    }
}
