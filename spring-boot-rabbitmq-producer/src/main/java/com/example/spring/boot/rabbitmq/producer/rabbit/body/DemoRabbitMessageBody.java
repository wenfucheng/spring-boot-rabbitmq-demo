package com.example.spring.boot.rabbitmq.producer.rabbit.body;

import java.util.UUID;

/**
 * @Description
 * @Date 2019/5/17
 * @Author wenfucheng
 */
public class DemoRabbitMessageBody implements RabbitMessageBody {

    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String getMessageId() {
        return UUID.randomUUID().toString();
    }
}
