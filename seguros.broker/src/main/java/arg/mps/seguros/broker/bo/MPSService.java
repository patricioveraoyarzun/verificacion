package arg.mps.seguros.broker.bo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import arg.mps.seguros.broker.api.CotizacionRequest;
import arg.mps.seguros.broker.api.CotizacionResponse;
import arg.mps.seguros.broker.api.MPSEvent;
import arg.mps.seguros.broker.api.VentaRequest;
import arg.mps.seguros.broker.api.VentaResponse;

@Service
public class MPSService {

	private final Logger logger = LoggerFactory.getLogger(MPSService.class);
			
	@Autowired
	private ReplyingKafkaTemplate<String, MPSEvent, MPSEvent> kafkaTemplate;

	@Value("${kafka.topic.request-topic}")
	private String requestTopic;

	@Value("${kafka.topic.requestreply-topic}")
	private String requestReplyTopic;
	
	private final static String KEY_COTIZACION = "COTIZACION";
	private final static String KEY_VENTA = "VENTA";


	public CotizacionResponse cotizar(CotizacionRequest request) throws InterruptedException, ExecutionException, TimeoutException {
		String metodo = "cotizar";
		
		logger.info("[SRV][MPS_COT001] - OK: Inicio del metodo {}. Params -> request={}", metodo, request);
		
		ProducerRecord<String, MPSEvent> record = new ProducerRecord<String, MPSEvent>(requestTopic, KEY_COTIZACION, request);
		
		logger.debug("[SRV][MPS_COT002] - OK: Se creo registro para ser enviado a Kafka. Parameters -> Topic={} - Key={} - Value={} ", requestTopic, KEY_COTIZACION, request);
		
		ConsumerRecord<String, MPSEvent> consumerRecord = this.postEvent(record);
		
		logger.debug("[SRV][MPS_COT003] - OK: Se creo registro para ser enviado a Kafka. Parameters -> Topic={} - Key={} - Value={} ", requestTopic, KEY_COTIZACION, request);
		
		CotizacionResponse response = (CotizacionResponse) consumerRecord.value();
		
		logger.info("[SRV][MPS_COT004] - OK: Fin del metodo {}. Respuesta -> response={}", metodo, response);
		
		return response;
	}

	public VentaResponse vender(VentaRequest request) throws InterruptedException, ExecutionException, TimeoutException{
		ProducerRecord<String, MPSEvent> record = new ProducerRecord<String, MPSEvent>(requestTopic, KEY_VENTA, request);
		ConsumerRecord<String, MPSEvent> consumerRecord = this.postEvent(record);
		
		return (VentaResponse) consumerRecord.value();
	}

	private ConsumerRecord<String, MPSEvent> postEvent(ProducerRecord<String, MPSEvent> record) throws InterruptedException, ExecutionException, TimeoutException {
		String metodo = "postEvent";
		
		logger.info("[SRV][MPS_POS001] - OK: Inicio del metodo {}. Params -> record={}", metodo, record);
		
		// set reply topic in header
		record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
		
		logger.debug("[SRV][MPS_POS002] - OK: Se establecio el tema de respuesta en el encabezado junto con los bytes a enviar. key={} - value.length={}", KafkaHeaders.REPLY_TOPIC, requestReplyTopic.length());
		
		kafkaTemplate.setReplyTimeout(30000l);
		
		logger.debug("[SRV][MPS_POS003] - OK: Se establecio el tiempo de espera de respuesta. timeout={}", 30000l);
		
		// post in kafka topic
		RequestReplyFuture<String, MPSEvent, MPSEvent> sendAndReceive = kafkaTemplate.sendAndReceive(record);

		logger.info("[SRV][MPS_POS004] - OK: Se envio registro al topico de kafka");
		
		// confirm if producer produced successfully
		SendResult<String, MPSEvent> sendResult = sendAndReceive.getSendFuture().get(30, TimeUnit.SECONDS);

		// print all headers
		int idx = 0;
		if(logger.isTraceEnabled()) {
			sendResult.getProducerRecord().headers()
			.forEach(header -> logger.trace("[SRV][MPS_POS005-{}] - OK: Header -> key={} : value={}", (idx+1), header.key(), header.value().toString()));			
		}

		// get consumer record
		ConsumerRecord<String, MPSEvent> consumerRecord = sendAndReceive.get();
		
		logger.debug("[SRV][MPS_POS006] - OK: Respuesta desde el topico de kafka. consumerRecord={}", consumerRecord);		
		
		logger.info("[SRV][MPS_POS007] - OK: Fin del metodo {}.", metodo);
		
		return consumerRecord;
	}

}
