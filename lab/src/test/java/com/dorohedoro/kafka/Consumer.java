package com.dorohedoro.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class Consumer {

    private static KafkaConsumer<String, String> consumer;

    @BeforeClass
    public static void init() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "192.168.1.4:9092,192.168.1.4:9093");
        props.put("group.id", "ok");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);
        
    }

    @Test
    public void commitOffset() {
        consumer.subscribe(Arrays.asList("ok")); // 订阅主题
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
            for (ConsumerRecord<String, String> record : records) {
                log.info("主题: {}, 分区: {}, 偏移量: {}, key: {}, value: {}",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
            }
            consumer.commitAsync(); // 手动提交偏移量
        }
    }

    @Test
    public void commitPartitionOffset() {
        consumer.subscribe(Arrays.asList("ok"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
            for (TopicPartition partition : records.partitions()) {
                List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                for (ConsumerRecord<String, String> record : partitionRecords) {
                    log.info("主题: {}, 分区: {}, 偏移量: {}, key: {}, value: {}",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                Map<TopicPartition, OffsetAndMetadata> map = new HashMap<>();
                map.put(partition, new OffsetAndMetadata(lastOffset + 1));
                consumer.commitSync(map); // 手动提交单个分区的偏移量
            }
        }
    }

    @Test
    public void subscribePartitions() {
        TopicPartition p0 = new TopicPartition("ok", 0);
        consumer.assign(Arrays.asList(p0)); // 订阅分区
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
            for (ConsumerRecord<String, String> record : records) {
                log.info("主题: {}, 分区: {}, 偏移量: {}, key: {}, value: {}",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
            }
            consumer.commitAsync();
        }
    }
}
