package com.ridehub.paymentservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentCreatedTopic() {
        return new NewTopic("payment-created", 1, (short) 1);
    }

    @Bean
    public NewTopic paymentSucceededTopic() {
        return new NewTopic("payment-succeeded", 1, (short) 1);
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return new NewTopic("payment-failed", 1, (short) 1);
    }

    @Bean
    public NewTopic paymentTimeoutTopic() {
        return new NewTopic("payment-timeout", 1, (short) 1);
    }

    @Bean
    public NewTopic paymentRefundedTopic() {
        return new NewTopic("payment-refunded", 1, (short) 1);
    }

}
