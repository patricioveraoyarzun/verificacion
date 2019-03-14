package arg.mps.seguros.broker.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import arg.mps.seguros.broker.api.MPSEvent;

@Configuration
public class KafkaConfiguration {

	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${kafka.topic.request-reply-topic}")
	private String requestReplyTopic;

	@Value("${kafka.consumer-group}")
	private String consumerGroup;
	
	@Value("${kafka.producer.max-block-ms}")
	private Long producerMaxBlock;

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		// list of host:port pairs used for establishing the initial connections to the
		// Kakfa cluster
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, producerMaxBlock);
		return props;
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "mps-consumer-group");
		return props;
	}

	@Bean
	public ProducerFactory<String, MPSEvent> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, MPSEvent> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public ReplyingKafkaTemplate<String, MPSEvent, MPSEvent> replyKafkaTemplate(ProducerFactory<String, MPSEvent> pf,
			KafkaMessageListenerContainer<String, MPSEvent> container) {
		return new ReplyingKafkaTemplate<>(pf, container);

	}

	@Bean
	public KafkaMessageListenerContainer<String, MPSEvent> replyContainer(ConsumerFactory<String, MPSEvent> cf) {
		ContainerProperties containerProperties = new ContainerProperties(requestReplyTopic);
		return new KafkaMessageListenerContainer<>(cf, containerProperties);
	}

	@Bean
	public ConsumerFactory<String, MPSEvent> consumerFactory() {
		JsonDeserializer<MPSEvent> domainEventJsonDeserializer = new JsonDeserializer<>(MPSEvent.class);
		domainEventJsonDeserializer.addTrustedPackages("*");
		
		return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),domainEventJsonDeserializer);
	}

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MPSEvent>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, MPSEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setReplyTemplate(kafkaTemplate());
		return factory;
	}

}
