package com.dorohedoro.kafka;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer {

    private static KafkaProducer<String, String> producer;

    private static Map<String, Integer> chainMap;

    @BeforeClass
    public static void init() {
        chainMap = new HashMap<>();
        chainMap.put("ETH", 0);
        chainMap.put("BSC", 1);
        chainMap.put("POLYGON", 2);
        chainMap.put("ARBITRUM", 3);
        chainMap.put("OPTIMISM", 4);

        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "192.168.1.4:9092,192.168.1.4:9093");
        props.put("buffer.memory", 33554432);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("compression.type", "snappy");
        props.put("acks", "1");
        props.put("retries", 3);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }

    @Test
    public void sendAsync() {
        IntStream.range(0, 100).forEach(o -> {
            OKTX oktx = OKTX.builder().chainShortName("POLYGON").build();
            String chain = oktx.getChainShortName();
            Integer partitionNo = chainMap.get(chain);
            producer.send(new ProducerRecord<>("ok", partitionNo, chain, JSON.toJSONString(oktx)),
                    (RecordMetadata metadata, Exception e) -> log.info("主题: {}, 分区: {}, 偏移量: {}", metadata.topic(), metadata.partition(), metadata.offset()));
        });
    }

    @Test
    public void sendSync() throws Exception {
        OKTX oktx = OKTX.builder().chainShortName("ETH").build();
        String chain = oktx.getChainShortName();
        Integer partitionNo = chainMap.get(chain);
        Future<RecordMetadata> future = producer.send(new ProducerRecord<>("ok", partitionNo, chain, JSON.toJSONString(oktx)));
        RecordMetadata metadata = future.get();
        log.info("主题: {}, 分区: {}, 偏移量: {}", metadata.topic(), metadata.partition(), metadata.offset());
    }
}
