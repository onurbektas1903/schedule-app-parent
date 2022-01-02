package tr.com.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchLoggingErrorHandler;
import org.springframework.kafka.listener.ListenerUtils;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import tr.com.common.dto.DomainMessage;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomKafkaConsumerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.application.name}")
    private String appName;


    public ConsumerFactory<String, DomainMessage> consumerFactory(Map<String, Object> configMap) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, appName+"-group");
        props.putAll(configMap);
        JsonDeserializer<DomainMessage> jsonDeserializer = new JsonDeserializer<>(DomainMessage.class);
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(
                        props, new StringDeserializer(), jsonDeserializer);
    }

    public ConcurrentKafkaListenerContainerFactory<String, DomainMessage> batchKafkaListenerContainerFactory() {
        Map<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,"350000");
        return batchKafkaListenerContainerFactory(props);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainMessage> customKafkaListenerContainerFactory(
            Map<String, Object> configMap) {
        ConcurrentKafkaListenerContainerFactory<String, DomainMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(configMap));
        factory.setErrorHandler(
                new SeekToCurrentErrorHandler(
                        (rec, ex) -> log.error(ListenerUtils.recordToString(rec) + "\n" + ex.getMessage()),
                        new FixedBackOff(0L, 1L)));
        return factory;
    }
    public ConcurrentKafkaListenerContainerFactory<String, DomainMessage> batchKafkaListenerContainerFactory(
            Map<String, Object> configMap) {
        ConcurrentKafkaListenerContainerFactory<String, DomainMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(configMap));
        factory.setBatchListener(true);
        factory.setBatchErrorHandler(new BatchLoggingErrorHandler());
        return factory;
    }
}
