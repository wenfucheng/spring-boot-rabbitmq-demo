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
 * 监听的方法内部必须使用channel进行消息确认，包括消费成功或消费失败
 *
 * 如果不手动确认，也不抛出异常，消息不会自动重新推送（包括其他消费者），因为对于rabbitmq来说始终没有接收到消息消费是否成功的确认，并且Channel是在消费端有缓存的，没有断开连接
 *
 * 如果rabbitmq断开，连接后会自动重新推送（不管是网络问题还是宕机）
 *
 * 如果消费端应用重启，消息会自动重新推送
 *
 * 如果消费端处理消息的时候宕机，消息会自动推给其他的消费者
 *
 * 如果监听消息的方法抛出异常，消息会按照listener.retry的配置进行重发，但是重发次数完了之后还抛出异常的话，消息不会重发（也不会重发到其他消费者），只有应用重启后会重新推送。因为retry是消费端内部处理的，包括异常也是内部处理，对于rabbitmq是不知道的（此场景解决方案后面有）
 *
 * spring.rabbitmq.listener.retry配置的重发是在消费端应用内处理的，不是rabbitqq重发
 *
 * https://blog.csdn.net/dshf_1/article/details/90241250
 *
 * @Date 2019/5/17
 * @Author wenfucheng
 */
@Component
public class DemoRabbitMsgListener implements ChannelAwareMessageListener {

    /**
     * 确认模式
     * {@link org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener}
     * @param message
     * @param channel
     * @throws Exception
     */
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
        //channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); // 应答消息, Broker收到应答从queue中移除消息
        channel.basicReject(message.getMessageProperties().getDeliveryTag(),false); // 拒绝消息, 该条消息被发送至死信队列, 由死信队列监听器消费
    }


    /**
     * 无需手动ack消息, 实现{@link org.springframework.amqp.core.MessageListener}即可, 实现此方法
     */
    @Override
    public void onMessage(Message message) {
        String content;
        try {
            content = new String(message.getBody(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            content = "";
        }

        System.err.println("consume msg: "+content);

    }
}
