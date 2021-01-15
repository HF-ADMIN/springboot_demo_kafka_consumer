package com.example.kafka;

import com.example.dao.KafkaDAO;
import com.example.repository.KafKaRepository;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import org.apache.commons.lang3.time.StopWatch;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.time.Duration;
import java.util.*;

import static org.apache.kafka.common.utils.Utils.sleep;

/**
 * @className   Consumer
 * @mehtod      consume
 * @description consumer properties 설정 후 MSG consume
 */
@Component
public class Consumer {

    // repo static autowired
    private static KafKaRepository Repository;
    @Autowired
    private KafKaRepository autoRepository;
    @PostConstruct
    private void initStaticRepo() {
        Repository = this.autoRepository;
    }

//    static KafkaDAO kafkaDAO = new KafkaDAO();


    public static int consume(String brokers, String groupId, String topicName) {
        System.out.println("consumer start@@@");
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
        //consumer transaction 처리
//        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_committed");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // subcribe 방식 partition 자동 할당
        consumer.subscribe(Arrays.asList(topicName));

//      partition 지정해서 특정 partition만 소비 할 수 있음
//        Collection<TopicPartition> partitions = new ArrayList<>();
//        토픽의 모든 파티션 정보 긁어옴
//        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
//        for (PartitionInfo partitionInfo : partitionInfos) {
//            partitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
//        }

//        partitions.add(new TopicPartition(topicName, 1));
//        System.out.println(partitions);
//
//        consumer.assign(partitions);

        String strSplit[];
        String tmp;

        int count = 0;

        StopWatch stopWatch = new StopWatch();
        ArrayList<KafkaDAO> list = new ArrayList<>();
        // consumer loop
        while(true) {
            // topic list msg polling

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
            count += records.count();


//            System.out.println("record count ++ : " + records.count());
//            System.out.println("total count ++ : " + count);
//            System.out.println("time : " + stopWatch.getTime());

            // consumer pooling records가 없을 때
            // 1. 이전에 pooling 한 record가 있다면 stopWatch 시작
            // 2. stopWatch가 시작되어 있다면 최초 시작 이후 10초가 경과했으면 DB push
            if ( records.count() == 0 ) {

                if( count > 0 && stopWatch.getTime() == 0 ){
                    System.out.println("records count = 0 && count > 0 time = 0");
                    System.out.println("no data stopwatch start");
                    System.out.println("@@@@@@@@@여긴 돌면 안되는데@@@@@@@@@");
                    System.out.println("@@@@@@@@@111111111111@@@@@@@@@");
                    System.out.println("@@@@@@@@@111111111111@@@@@@@@@");
                    stopWatch.start();
                }else if(count > 0 && stopWatch.getTime() > 10000){
                    System.out.println("records count = 0 && count > 0 time > 10000");
                    System.out.println("no more data , timeout, insert");
                    Repository.saveAll(list);
                    list.clear();
                    stopWatch.stop();
                    stopWatch.reset();
                    System.out.println(stopWatch.getTime());
                    count = 0;
                }else{

                }
            }
            // cousumer pooling record가 있을 때
            // stopWatch가 시작되어 있지 않다면 시작하고 pooling한 record를 list에 add하고
            // pooling한 총 데이터가 100개 이상이거나 10초가 경과했다면 DB push
            else if ( records.count() > 0 ) {

                if( stopWatch.getTime() == 0 ){
                    System.out.println("stopwatch start");
                    stopWatch.start();
                }
                System.out.println("polling data : " + count);
                System.out.println("add list");
                for(ConsumerRecord<String, String> record: records) {
                    // Display record and count
                    KafkaDAO kafkaDAO = new KafkaDAO();

                    tmp = record.value().replaceAll("\\[","");
                    tmp = tmp.replaceAll("\\]","");
                    strSplit = tmp.split("\\|");
                    System.out.println(strSplit.length);

                    for(int i=0; i < strSplit.length; i ++){
                        if(strSplit[i].equals("") || strSplit[i].equals(null)){
                            strSplit[i]= "@@not null@@";
                        }
                    }

                    kafkaDAO.setTimestamp(strSplit[0]);
                    kafkaDAO.setService(strSplit[1]);
                    kafkaDAO.setMethod(strSplit[2]);
                    kafkaDAO.setGuid(strSplit[3]);
                    kafkaDAO.setMsg(strSplit[4]);

                    list.add(kafkaDAO);
//                    Repository.insert(kafkaDAO);
//                    consumer.commitSync();
                }

                if(count > 100 || stopWatch.getTime() > 10000){
                    System.out.println("records count > 0 && count > 0 time > 10000");
                    System.out.println("timeout, insert");
                    Repository.saveAll(list);
                    list.clear();
                    stopWatch.stop();
                    stopWatch.reset();
                    System.out.println(stopWatch.getTime());
                    count = 0;
                }

            }
            // 그 외 에러
            else{
                System.out.println("record count < 0 .. ERR");
            }
            sleep(10);
        }
    }

}
