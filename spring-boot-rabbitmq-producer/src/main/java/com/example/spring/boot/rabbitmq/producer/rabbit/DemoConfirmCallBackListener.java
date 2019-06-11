package com.example.spring.boot.rabbitmq.producer.rabbit;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @Description
 * @Date 2019/5/20
 * @Author wenfucheng
 */
@Component
public class DemoConfirmCallBackListener implements ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        try {
            System.err.println("confirm callback: " + correlationData.getId() +","+ new String(correlationData.getReturnedMessage().getBody(),"UTF-8")+", causer: " + cause);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
