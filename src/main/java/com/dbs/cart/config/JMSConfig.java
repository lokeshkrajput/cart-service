package com.dbs.cart.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.Queue;
import java.util.Arrays;

@Configuration
@EnableJms
public class JMSConfig {

    @Value("${activemq.broker-url}")
    private String brokerUrl;

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        factory.setTrustedPackages(Arrays.asList("com.dbs.cart", "java.math"));
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(activeMQConnectionFactory());
    }

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("cart.item.queue");
    }

    @Bean("stageItemQ")
    public Queue stageItemQueue() {
        return new ActiveMQQueue("stage.cart.item.queue");
    }

    @Bean("retryItemQ")
    public Queue itemRetryQueue() {
        return new ActiveMQQueue("retry.cart.item.queue");
    }

    @Bean("errorItemQ")
    public Queue erroredItemQueue() {
        return new ActiveMQQueue("error.cart.item.queue");
    }

}
