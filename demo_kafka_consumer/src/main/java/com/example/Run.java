package com.example;

import com.example.kafka.Consumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Run {

    public static void main(String[] args) {
        SpringApplication.run(Run.class, args);

        String brokers = "192.168.188.156:32400, 192.168.188.156:32401, 192.168.188.156:32402";
        String topicName = "spring_demo";
        String groupId = "spring_group";

        Consumer.consume(brokers, groupId, topicName);
    }
}
