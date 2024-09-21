package com.ahicode.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibraryRabbitMqConfig {

    @Bean
    public Queue queue() {
        return new Queue("authors_queue", true);
    }

    @Bean
    public DirectExchange authorExchange() {
        return new DirectExchange("author_exchange");
    }

    @Bean
    public Binding authorBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("author.routing.key");
    }
}
