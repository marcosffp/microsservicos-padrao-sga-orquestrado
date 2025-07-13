package br.com.microservices.orchestrated.orchestratorservice.config.kafka;

import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;

@EnableKafka
@Configuration
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;
  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;
  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String autoOffsetReset;

  private static final int PARTITIONS = 1;
  private static final int REPLICATION = 1;

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerProps());
  }

  private Map<String, Object> consumerProps() {
    return Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        ConsumerConfig.GROUP_ID_CONFIG, groupId,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerProps());
  }

  private Map<String, Object> producerProps() {
    return Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
    return new KafkaTemplate<>(pf);
  }

  private NewTopic buildTopic(String topicName) {
    return TopicBuilder
        .name(topicName)
        .replicas(REPLICATION)
        .partitions(PARTITIONS)
        .build();
  }

  @Bean
  public NewTopic startSagaTopic() {
    return buildTopic(ETopics.START_SAGA.getTopic());
  }

  @Bean
  public NewTopic baseOrchestratorTopic() {
    return buildTopic(ETopics.BASE_ORCHESTRATOR.getTopic());
  }

  @Bean
  public NewTopic finishSuccessTopic() {
    return buildTopic(ETopics.FINISH_SUCCESS.getTopic());
  }

  @Bean
  public NewTopic finishFailTopic() {
    return buildTopic(ETopics.FINISH_FAIL.getTopic());
  }

  @Bean
  public NewTopic productValidationSuccessTopic() {
    return buildTopic(ETopics.PRODUCT_VALIDATITON_SUCCESS.getTopic());
  }

  @Bean
  public NewTopic productValidationFailTopic() {
    return buildTopic(ETopics.PRODUCT_VALIDATITON_FAIL.getTopic());
  }

  @Bean
  public NewTopic inventorySuccessTopic() {
    return buildTopic(ETopics.INVENTORY_SUCCESS.getTopic());
  }

  @Bean
  public NewTopic inventoryFailTopic() {
    return buildTopic(ETopics.INVENTORY_FAIL.getTopic());
  }

  @Bean
  public NewTopic paymentSuccessTopic() {
    return buildTopic(ETopics.PAYMENT_SUCCESS.getTopic());
  }

  @Bean
  public NewTopic paymentFailTopic() {
    return buildTopic(ETopics.PAYMENT_FAIL.getTopic());
  }

  @Bean
  public NewTopic notifyEndingTopic() {
    return buildTopic(ETopics.NOTIFY_ENDING.getTopic());
  }

}
