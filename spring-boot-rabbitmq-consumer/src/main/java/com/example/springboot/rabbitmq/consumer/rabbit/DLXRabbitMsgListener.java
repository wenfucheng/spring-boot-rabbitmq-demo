package com.example.springboot.rabbitmq.consumer.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @Description 死信队列消息监听器
 *
 * @Date 2019/5/17
 * @Author wenfucheng
 */
@Component
public class DLXRabbitMsgListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String content;
        try {
            content = new String(message.getBody(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            content = "";
        }

        System.err.println("dlx consume msg: "+content);
    }
}
