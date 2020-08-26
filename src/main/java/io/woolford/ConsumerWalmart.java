package io.woolford;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Properties;

public class ConsumerWalmart {

    public static void main(String[] args) throws UnknownHostException {

        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("group.id", "foo");
        config.put("bootstrap.servers", "pkc-4nym6.us-east-1.aws.confluent.cloud:9092");
        config.put("security.protocol", "SASL_SSL");
        config.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule   required username=\"HZL2D2L2BX2CXBHO\"   password=\"5DWQaDBfC8xzj0qYSBq20xtdmdKBpBOZkU766YE1VBjw4qrUCI6TtdWxdS9qyGGA\";");
        config.put("sssl.endpoint.identification.algorithm", "https");
        config.put("sasl.mechanism", "PLAIN");
        config.put("key.deserializer", StringDeserializer.class);
        config.put("value.deserializer", StringDeserializer.class);
        KafkaConsumer consumer = new KafkaConsumer<String, String>(config);

        consumer.subscribe(Collections.singleton("walmart"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
            for (ConsumerRecord record: records){
                System.out.println(record.value());
            }
            consumer.commitSync();
        }
    }
}
