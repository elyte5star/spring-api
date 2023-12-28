package com.elyte.queue;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.elyte.exception.LogMessageExceptionHandler;
import com.elyte.exception.MessageExceptionHandler;

import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.auto-config.queues.queue-one.name}")
    String searchQueue;

    @Value("${spring.rabbitmq.auto-config.queues.queue-two.name}")
    String bookingQueue;

    
    @Value("${spring.rabbitmq.auto-config.queues.queue-mock.name}")
    String lostItemsQueue;

    @Value("${spring.rabbitmq.exchange}")
    String exchange;

    @Value("${spring.rabbitmq.auto-config.bindings.binding-one.routing-key}")
    private String searchRoutingkey;

    @Value("${spring.rabbitmq.auto-config.bindings.binding-two.routing-key}")
    private String bookingRoutingkey;

    @Value("${spring.rabbitmq.auto-config.bindings.binding-mock.routing-key}")
    private String lostItemsRoutingkey;



    @Bean
    Queue bookingQueue() {
        return new Queue(bookingQueue, true);
    }

    @Bean
    Queue searchQueue() {
        return new Queue(searchQueue, true);
    }

    @Bean
    Queue lostItemsQueue() {
        return new Queue(lostItemsQueue, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchange);

    }

    @Bean
    MessageExceptionHandler logMessageExceptionHandler() {
        return new LogMessageExceptionHandler();
    }

    @Bean
    Binding searchBinding() {
        return BindingBuilder.bind(searchQueue()).to(exchange()).with(searchRoutingkey);
    }

    @Bean
    Binding bookingBinding() {
        return BindingBuilder.bind(bookingQueue()).to(exchange()).with(bookingRoutingkey);
    }

    @Bean
    Binding lostItemsBinding() {
        return BindingBuilder.bind(lostItemsQueue()).to(exchange()).with(lostItemsRoutingkey);
    }

    
    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
