package com.jlf.music.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

/**
 * Rabbit配置类
 */
@Configuration
public class RabbitMQConfig {

    // 私信交换机
    public static final String PRIVATE_MESSAGE_EXCHANGE = "private.message.exchange";
    // 私信队列
    public static final String PRIVATE_MESSAGE_QUEUE = "private.message.queue";

    // 用户状态交换机
    public static final String USER_STATUS_EXCHANGE = "user.status.exchange";
    // 用户状态队列
    public static final String USER_STATUS_QUEUE = "user.status.queue";

    /**
     * 声明私信交换机
     * 该方法创建一个Topic类型的交换机，用于处理私信相关的消息
     * Topic Exchange(主题交换机)：根据消息的路由键和绑定键的模式匹配来分发消息
     *
     * @return TopicExchange 私信交换机实例
     */
    @Bean
    public TopicExchange privateMessageExchange() {
        return new TopicExchange(PRIVATE_MESSAGE_EXCHANGE);
    }

    /**
     * 声明私信队列
     * 该方法创建一个队列，用于存储私信相关的消息
     *
     * @return Queue 私信队列实例
     */
    @Bean
    public Queue privateMessageQueue() {
        return new Queue(PRIVATE_MESSAGE_QUEUE);
    }

    /**
     * 绑定私信队列到私信交换机
     * 该方法将私信队列与私信交换机绑定，并指定路由键为"message.#"
     *
     * @return Binding 绑定关系实例
     */
    @Bean
    public Binding privateMessageBinding() {
        return BindingBuilder.bind(privateMessageQueue()) // 绑定队列
                .to(privateMessageExchange()) // 绑定交换机
                .with("message.#"); // 绑定键
    }

    /**
     * 声明用户状态交换机
     * 该方法创建一个Topic类型的交换机，用于处理用户状态相关的消息
     *
     * @return TopicExchange 用户状态交换机实例
     */
    @Bean
    public TopicExchange userStatusExchange() {
        return new TopicExchange(USER_STATUS_EXCHANGE);
    }

    /**
     * 声明用户状态队列
     * 该方法创建一个队列，用于存储用户状态相关的消息
     *
     * @return Queue 用户状态队列实例
     */
    @Bean
    public Queue userStatusQueue() {
        return new Queue(USER_STATUS_QUEUE);
    }

    /**
     * 绑定用户状态队列到用户状态交换机
     * 该方法将用户状态队列与用户状态交换机绑定，并指定路由键为"status.#"
     *
     * @return Binding 绑定关系实例
     */
    @Bean
    public Binding userStatusBinding() {
        return BindingBuilder.bind(userStatusQueue())
                .to(userStatusExchange())
                .with("status.#");
    }

    /**
     * 配置消息转换器
     * 该方法创建一个Jackson2JsonMessageConverter实例，用于将消息转换为JSON格式
     *
     * @return MessageConverter JSON消息转换器实例
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate
     * 该方法创建一个RabbitTemplate实例，并设置消息转换器为JSON格式
     *
     * @param connectionFactory RabbitMQ连接工厂
     * @return RabbitTemplate RabbitTemplate实例
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}