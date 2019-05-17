package com.example.springboot.rabbitmq.consumer.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @Description rabbitmq消费方监听ChannelAwareMessageListener.onMessage()方法比MessageListener.onMessage()方法多一个Channel参数,
 *              可以手动ack
 *
 * @Date 2019/5/17
 * @Author wenfucheng
 */
@Component
public class DemoRabbitMsgListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String content;
        try {
            content = new String(message.getBody(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            content = "";
        }

        System.err.println("demo consume msg: "+content);

        //参数1: 消息标识id,
        //参数2: 是否应答所有小于此标识id的消息
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); // 应答消息, Broker收到应答从queue中移除消息
        channel.basicReject(message.getMessageProperties().getDeliveryTag(),false); // 拒绝消息, 该条消息被发送至死信队列, 又死信队列监听器消费
    }


    /**
     * 无需手动ack消息, 实现MessageListener即可, 实现此方法
     */
//    @Override
//    public void onMessage(Message message) {
//        String content;
//        try {
//            content = new String(message.getBody(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            content = "";
//        }
//
//        System.err.println("consume msg: "+content);
//
//    }
}
