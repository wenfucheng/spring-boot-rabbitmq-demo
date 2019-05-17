package com.example.spring.boot.rabbitmq.producer.controller;

import com.example.spring.boot.rabbitmq.producer.rabbit.DemoRabbitMsgSender;
import com.example.spring.boot.rabbitmq.producer.rabbit.body.DemoRabbitMessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description
 * @Date 2019/5/17
 * @Author wenfucheng
 */
@Controller
public class PushController {

    @Autowired
    private DemoRabbitMsgSender demoRabbitMsgSender;

    @RequestMapping("/pushMsg")
    @ResponseBody
    public String pushMsg(DemoRabbitMessageBody messageBody){
        demoRabbitMsgSender.sendMsg(messageBody);
        return "ok";
    }
}
