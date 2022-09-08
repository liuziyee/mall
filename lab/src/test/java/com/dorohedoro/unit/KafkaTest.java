package com.dorohedoro.unit;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaTest {

    private static KafkaProducer producer;
    
    @BeforeClass
    public static void init() {
        HashMap<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "110.40.136.113:9092");
        
        config.put("key.serializer", StringSerializer.class.getName());
        config.put("value.serializer", StringSerializer.class.getName());
        
        config.put("buffer.memory", 33554432);
        config.put("batch.size", 16384);
        config.put("linger.ms", 1);
        config.put("compression.type", "snappy");

        config.put("acks", "1");

        config.put("retries", 3);
        
        producer = new KafkaProducer<>(config);
    }
    
    @Test
    public void pushAsync() {
        IntStream.range(0, 5).forEach(o -> {
            producer.send(new ProducerRecord("wiki", "pingpong"), (RecordMetadata metadata, Exception e) -> {
                Assert.assertNull(e);
                log.info("topic: {}, partition: {}", metadata.topic(), metadata.partition());
            });
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
    
    @Test
    public void pushSync() throws Exception {
        log.info("{}", producer.send(new ProducerRecord("wiki", "pingpong")).get());
    }
}
