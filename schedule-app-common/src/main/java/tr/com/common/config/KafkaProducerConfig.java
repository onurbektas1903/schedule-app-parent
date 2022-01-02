package tr.com.common.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.stereotype.Component;
import tr.com.common.dto.DomainMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProducerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.application.name}")
    private String applicationName;

    public ProducerFactory<String, DomainMessage> producerFactory() {
        return producerFactory(Collections.emptyMap());
    }

    public ProducerFactory<String, DomainMessage> transactionalProducerFactory() {
        return transactionalProducerFactory(Collections.emptyMap());
    }

    public ProducerFactory<String, DomainMessage> producerFactory(
            Map<String, Object> configMap) {
        Map<String, Object> configProps = createDefaultProps(configMap);
        DefaultKafkaProducerFactory<String, DomainMessage> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        return  producerFactory;
    }

    private Map<String, Object> createDefaultProps(Map<String, Object> configMap) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.putAll(configMap);
        return configProps;
    }

    public ProducerFactory<String, DomainMessage> transactionalProducerFactory(
            Map<String, Object> configMap) {
        Map<String, Object> configProps = createDefaultProps(configMap);
        DefaultKafkaProducerFactory<String, DomainMessage> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        producerFactory.setTransactionIdPrefix("txId_"+applicationName);
        boolean b = producerFactory.transactionCapable();
    System.out.println("Transaction capable: "+ b);
        return  producerFactory;
    }

    @Bean
    public KafkaTransactionManager transactionManager() {
        KafkaTransactionManager manager = new KafkaTransactionManager(transactionalProducerFactory());
        return manager;
    }
}
