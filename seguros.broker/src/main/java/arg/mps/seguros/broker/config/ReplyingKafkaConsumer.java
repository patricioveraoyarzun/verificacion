package arg.mps.seguros.broker.config;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import arg.mps.seguros.broker.MPSContext;
import arg.mps.seguros.broker.api.Cotizacion;
import arg.mps.seguros.broker.api.CotizacionAutoRequest;
import arg.mps.seguros.broker.api.CotizacionRequest;
import arg.mps.seguros.broker.api.CotizacionResponse;
import arg.mps.seguros.broker.api.MPSEvent;
import arg.mps.seguros.broker.api.MPSEventHeader;
import arg.mps.seguros.broker.api.OfertaCotizacion;
import arg.mps.seguros.broker.api.VentaRequest;
import arg.mps.seguros.broker.api.VentaResponse;
import arg.mps.seguros.broker.api.integration.BrokerService;
import arg.mps.seguros.broker.api.integration.ans.adapter.AnsBrokerServiceAdapter;

@Component
public class ReplyingKafkaConsumer {
	
	private final static Logger logger = LoggerFactory.getLogger(ReplyingKafkaConsumer.class);
	
	@KafkaListener(topics = "${kafka.topic.request-topic}")
	@SendTo
	public MPSEvent listen(MPSEvent event) throws InterruptedException {
		
		Collection<BrokerService> brokerServices = MPSContext.getBrokerServices();
		logger.info("Cantidad de implementaciones de BrokerService: {}", brokerServices.size());

		Cotizacion objCotizacion = new Cotizacion();
		objCotizacion.setId(1);
		objCotizacion.setIdEntidadProveedoraSrv(1);
		
		if(event instanceof CotizacionRequest) {
			
			List<OfertaCotizacion> ofertasAutoANS = null;
			
			logger.info("Procesando cotizacion...");
			CotizacionResponse response = new CotizacionResponse();
			
			CotizacionRequest objCotizacionRequest = (CotizacionRequest) event;
			
			if(objCotizacionRequest instanceof CotizacionAutoRequest) {
				
				logger.info("Procesando cotizacion de auto...");
				logger.info("Cotizacion de auto a procesar: {}", objCotizacionRequest);
				
				AnsBrokerServiceAdapter ansAdapter = new AnsBrokerServiceAdapter();
				ofertasAutoANS = ansAdapter.cotizar(objCotizacionRequest);				
				
				objCotizacion.setOfertasCotizacion(ofertasAutoANS);				
				response.addCotizacion(objCotizacion);
				
				logger.info("Cotizacion de auto procesada...");
			}
			
			MPSEventHeader objHeader = new MPSEventHeader();
			objHeader.setCode("200");
			objHeader.setMessage("Oferta procesada correctamente");
			
			response.setHeader(objHeader);
			
			return response;
			
		}else if(event instanceof VentaRequest) {
			logger.info("Procesando venta...");
			
			MPSEventHeader header = new MPSEventHeader();
			header.setCode("MPS001");
			header.setMessage("Seguro vendido correctamente.");
			
			VentaResponse response = new VentaResponse();
			response.setHeader(header);
			
			return response;
		}
		
		return null;

	}

}
