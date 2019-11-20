package com.example.springboot.rabbitmq.consumer.config;

import com.example.springboot.rabbitmq.consumer.rabbit.DLXRabbitMsgListener;
import com.example.springboot.rabbitmq.consumer.rabbit.DemoRabbitMsgListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 消费方mq配置
 *
 * @Date 2019/5/17
 * @Author wenfucheng
 */
@Configuration
public class RabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String host,
                                               @Value("${spring.rabbitmq.port}") Integer port,
                                               @Value("${spring.rabbitmq.username}") String username,
                                               @Value("${spring.rabbitmq.password}") String password,
                                               @Value("${spring.rabbitmq.virtual-host}") String virtualHost) {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setChannelCacheSize(25);
        return connectionFactory;
    }

    /**
     * DLX消息监听器
     * @param dlxExchangeName
     * @param dlxQueueName
     * @param routingKey
     * @param connectionFactory
     * @param dlxRabbitMsgListener
     * @return
     */
    @Bean
    public AbstractMessageListenerContainer dlxMessageListenerContainer(@Value("${spring.rabbitmq.demo.dlx.exchange}")String dlxExchangeName,
                                                                        @Value("${spring.rabbitmq.demo.dlx.queue}")String dlxQueueName,
                                                                        @Value("${spring.rabbitmq.demo.routing-key}")String routingKey,
                                                                        ConnectionFactory connectionFactory,
                                                                        DLXRabbitMsgListener dlxRabbitMsgListener){
        /**
         * 声明一个死信队列, 以下情况的消息会进入死信队列
         * 消息被拒绝(basic.reject / basic.nack)，并且requeue = false
         * 消息TTL过期
         * 队列达到最大长度
         */
        TopicExchange dlxExchange = new TopicExchange(dlxExchangeName, true, false);
        Queue dlxQueue = new Queue(dlxQueueName);
        Binding dlxBinding = BindingBuilder.bind(dlxQueue).to(dlxExchange).with(routingKey);
        // 配置Demo消息监听器
        SimpleMessageListenerContainer messageListenerContainer = getSimpleMessageListenerContainer(dlxExchange, dlxQueue, dlxBinding, connectionFactory, dlxRabbitMsgListener);
        return messageListenerContainer;
    }

    /**
     * Demo 消息监听器
     * @param exchange
     * @param queueName
     * @param routingKey
     * @param connectionFactory
     * @param demoRabbitMsgListener
     * @return
     */
    @Bean
    public AbstractMessageListenerContainer demoMessageListenerContainer(@Value("${spring.rabbitmq.demo.exchange}")String exchange,
                                                                         @Value("${spring.rabbitmq.demo.dlx.exchange}")String dlxExchangeName,
                                                                         @Value("${spring.rabbitmq.demo.queue}")String queueName,
                                                                         @Value("${spring.rabbitmq.demo.routing-key}")String routingKey,
                                                                         ConnectionFactory connectionFactory,
                                                                         DemoRabbitMsgListener demoRabbitMsgListener){
        /**
         * 声明一个TopicExchange
         * autoDelete: 是否自动解绑
         */
        TopicExchange topicExchange = new TopicExchange(exchange,true,false);

        /**
         * 声明一个Demo Queue, 并指定其死信队列 (死信队列必须在Queue的创建参数指定)
         */
        Map<String, Object> arguments = new HashMap<>();
        /**
         * 消息变成死信有以下几种情况：
         * 消息被拒绝（basic.reject/ basic.nack）并且requeue=false
         * 消息TTL过期（参考：RabbitMQ之TTL（Time-To-Live 过期时间））
         * 队列达到最大长度
         *
         * 队列属性改变需要先在rabbitmq界面删除queue
         */
        arguments.put("x-dead-letter-exchange",dlxExchangeName); // 指定死信队列
        arguments.put("x-dead-letter-routing-key",routingKey); // 指定死信队列routing-key
        //arguments.put("x-message-ttl",6000); //指定消息超时时间
        Queue queue = new Queue(queueName,true,false,false,arguments);
        // 将queue和exchange按照指定routingKey绑定起来
        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
        SimpleMessageListenerContainer messageListenerContainer = getSimpleMessageListenerContainer(topicExchange, queue, binding, connectionFactory, demoRabbitMsgListener);
        /**
         * AcknowledgeMode.NONE：自动确认
         * AcknowledgeMode.AUTO：根据情况确认
         * AcknowledgeMode.MANUAL：手动确认
         */
        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return messageListenerContainer;
    }

    private SimpleMessageListenerContainer getSimpleMessageListenerContainer(Exchange exchange,
                                                                             Queue queue,
                                                                             Binding binding,
                                                                             ConnectionFactory connectionFactory,
                                                                             MessageListener messageListener) {
        /**
         * 声明exchange,queue,binding
         */
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);

        /**
         * 配置消息监听器
         */
        SimpleMessageListenerContainer messageListenerContainer =  new SimpleMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(connectionFactory);
        messageListenerContainer.setConcurrentConsumers(1); // 并发消费消息数
        messageListenerContainer.setChannelTransacted(true);
        messageListenerContainer.setQueues(queue); // 监听队列
        messageListenerContainer.setMessageListener(messageListener); // 监听类
        //messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置手动应答模式, 默认自动应答
        return messageListenerContainer;
    }
}
