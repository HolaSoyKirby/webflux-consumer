package com.example.ordersconsumer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private final KafkaOrderTopicProperties kafkaOrderTopicProperties;

    public KafkaTopicConfig(KafkaOrderTopicProperties kafkaOrderTopicProperties) {
        this.kafkaOrderTopicProperties = kafkaOrderTopicProperties;
    }

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(kafkaOrderTopicProperties.getTopic())
                .build();
    }
}