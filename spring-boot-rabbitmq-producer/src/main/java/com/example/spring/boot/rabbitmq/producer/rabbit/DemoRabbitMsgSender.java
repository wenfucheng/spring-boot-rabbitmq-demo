package com.example.spring.boot.rabbitmq.producer.rabbit;

import com.example.spring.boot.rabbitmq.producer.rabbit.body.RabbitMessageBody;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Description Demo消息发送类
 * @Date 2019/5/17
 * @Author wenfucheng
 */
public class DemoRabbitMsgSender {

    private RabbitTemplate rabbitTemplate;

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMsg(RabbitMessageBody rabbitMessageBody){

        rabbitTemplate.convertAndSend(rabbitMessageBody);
    }

}
