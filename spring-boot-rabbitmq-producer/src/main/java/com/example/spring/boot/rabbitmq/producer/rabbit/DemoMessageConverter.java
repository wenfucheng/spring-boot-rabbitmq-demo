package com.example.spring.boot.rabbitmq.producer.rabbit;

import com.example.spring.boot.rabbitmq.producer.rabbit.body.RabbitMessageBody;
import com.example.springboot.rabbitmq.common.JsonUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.UnsupportedEncodingException;

/**
 * @Description
 * @Date 2019/5/17
 * @Author wenfucheng
 */
public class DemoMessageConverter implements MessageConverter {

    /**
     * 调用RabbitTemplate.convertAndSend(Object o)会调用toMessage方法
     * @param o 和RabbitTemplate.convertAndSend()方法入参为一个
     * @param messageProperties
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        RabbitMessageBody rabbitMessageBody = (RabbitMessageBody) o;
        String object = JsonUtil.getJsonFromObject(rabbitMessageBody);
        byte[] bytes = null;
        try {
            bytes = object.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        messageProperties.setMessageId(rabbitMessageBody.getMessageId());
        // 设置contentType为application/json
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setContentLength(bytes.length);
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        System.err.println("send msg: "+object);

        Message message = new Message(bytes, messageProperties);
        return message;
    }

    /**
     * 暂时没发现有什么用
     * @param message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        System.err.println("fromMessage");
        return null;
    }
}
