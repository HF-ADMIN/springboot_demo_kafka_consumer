package com.example.kafka;

import com.example.dao.KafkaDAO;
import com.example.repository.KafKaRepository;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;

import java.time.Duration;
import java.util.Properties;
import java.util.Arrays;

/**
 * @className   Consumer
 * @mehtod      consume
 * @description consumer properties 설정 후 MSG consume
 */
@Component
public class Consumer {

    // repo static autuwired
    private static KafKaRepository Repository;
    @Autowired
    private KafKaRepository autoRepository;
    @PostConstruct
    private void initStaticRepo() {
        Repository = this.autoRepository;
    }

    static KafkaDAO kafkaDAO = new KafkaDAO();


    public static int consume(String brokers, String groupId, String topicName) {

        // kafka consumer prop
        Properties properties = new Properties();
        // broker server list
        properties.setProperty("bootstrap.servers", brokers);
        // consumer group
        properties.setProperty("group.id", groupId);
        // ByteArray -> String
        properties.setProperty("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        // topic의 오래된 offset부터 read
        properties.setProperty("auto.offset.reset","earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // 구독할 topic list
        consumer.subscribe(Arrays.asList(topicName));

        // consumer loop
        String strSplit[];
        String tmp;
        int count = 0;
        while(true) {
            // msg pool
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
            if (records.count() == 0) {

            } else {
                for(ConsumerRecord<String, String> record: records) {
                    // Display record and count
                    count += 1;
                    System.out.println( count + ": " + record.value());

                    tmp = record.value().replaceAll("\\[","");
                    tmp = tmp.replaceAll("\\]","");
                    strSplit = tmp.split("\\|");

                    kafkaDAO.setTimestamp(strSplit[0]);
                    kafkaDAO.setCode(strSplit[1]);
                    kafkaDAO.setGuid(strSplit[2]);
                    kafkaDAO.setMsg(strSplit[3]);

                    Repository.insert(kafkaDAO);
                }
            }
        }
    }

}
