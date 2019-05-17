package com.example.spring.boot.rabbitmq.producer.config;

import com.example.spring.boot.rabbitmq.producer.rabbit.DemoMessageConverter;
import com.example.spring.boot.rabbitmq.producer.rabbit.DemoRabbitMsgSender;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description 生产方mq配置
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
     * 注入Demo消息发送类, Demo可以是不同的业务, 对应多个发送类
     * @param exchange
     * @param routingKeyPrefix
     * @param connectionFactory
     * @return
     */
    @Bean
    public DemoRabbitMsgSender demoRabbitMsgSender(@Value("${spring.rabbitmq.demo.exchange}")String exchange,
                                                   @Value("${spring.rabbitmq.demo.routing-key-prefix}")String routingKeyPrefix,
                                                   ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchange);
        //设置生产方的routingKey(bindingKey)
        rabbitTemplate.setRoutingKey(routingKeyPrefix+"demo1");
        // 设置消息转换器(调用RabbitTemplate.convertAndSend()方法会调用消息转换器)
        rabbitTemplate.setMessageConverter(new DemoMessageConverter());

        DemoRabbitMsgSender demoRabbitMsgSender = new DemoRabbitMsgSender();
        demoRabbitMsgSender.setRabbitTemplate(rabbitTemplate);
        return demoRabbitMsgSender;
    }
}
