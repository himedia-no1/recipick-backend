package com.no1.recipick.crawler.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String CRAWLING_START_QUEUE = "recipe.crawling.start";
    public static final String CRAWLING_STOP_QUEUE = "recipe.crawling.stop";

    @Bean
    public Queue crawlingStartQueue() {
        return new Queue(CRAWLING_START_QUEUE, true);
    }

    @Bean
    public Queue crawlingStopQueue() {
        return new Queue(CRAWLING_STOP_QUEUE, true);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}