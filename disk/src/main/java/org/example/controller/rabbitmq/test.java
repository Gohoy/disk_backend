package org.example.controller.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping("mq/")
@RestController
public class test {
    @Autowired
    private AmqpTemplate rabbitmqTemplate;

    @PostMapping("/send/")
    public void send(){
        String context = "hello"+new Date();
        System.out.println("Sender"+ context);
        this.rabbitmqTemplate.convertAndSend("hello",context);
    }

    @Component
    @RabbitListener(queues = "hello")
    public class HelloReceiver {

        @RabbitHandler
        public void process(String hello) {
            System.out.println("Receiver  : " + hello);
        }

    }

}
