package org.example.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class rabbitmqConfig {
    @Bean
    public Queue Queue(){
        return new Queue("hello");
    }
}
