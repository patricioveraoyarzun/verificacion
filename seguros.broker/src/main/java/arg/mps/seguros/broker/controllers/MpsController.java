package arg.mps.seguros.broker.controllers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import arg.mps.seguros.broker.api.CotizacionAutoRequest;
import arg.mps.seguros.broker.api.CotizacionRequest;
import arg.mps.seguros.broker.api.CotizacionResponse;
import arg.mps.seguros.broker.api.MPSEventHeader;
import arg.mps.seguros.broker.api.VentaRequest;
import arg.mps.seguros.broker.api.VentaResponse;
import arg.mps.seguros.broker.bo.MPSService;
import arg.mps.seguros.broker.utils.Constantes;

@RestController
@RequestMapping("/api")
public class MpsController {

	private static final Logger logger = LoggerFactory.getLogger(MpsController.class);
	
	@Autowired
	private MPSService mpsService;
	
	@RequestMapping(value="/cotizacion", 
            method=RequestMethod.GET, 
            produces=MediaType.APPLICATION_JSON_VALUE)
	public CotizacionResponse cotizar(@RequestParam("msg") String message) {
		
		CotizacionRequest cotizacionRequest = new CotizacionRequest(message);
		
		CotizacionResponse response = null;
		try {
			response = mpsService.cotizar(cotizacionRequest);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	@RequestMapping(value="/cotizacion/auto", 
            method=RequestMethod.POST, 
            produces=MediaType.APPLICATION_JSON_VALUE)
	public CotizacionResponse cotizarAutoPost(@RequestBody CotizacionAutoRequest request) {
		String metodo = "cotizarAutoPost";
		
		logger.info("[CTL][MPS_CAU001] - OK: Inicio del metodo {}. Params -> request={}", metodo, request);
			
		CotizacionResponse response = null;
		try {
			request.setMessage(Constantes.TIPO_AGRUPACION_AUTO);
			response = mpsService.cotizar(request);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.error("[CTL][MPS_CAU-1] - ERROR: Error en metodo {} - Error[{e}].", metodo, e);			
		}
		
		logger.info("[CTL][MPS_CAU] - Fin del metodo {}.", metodo);
		
		return response;
	}	
	
	@RequestMapping(value="/venta", 
            method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> venta(@RequestBody VentaRequest ventaRequest) {
		
		VentaResponse response = null;
		try {
			response = mpsService.vender(ventaRequest);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		
		MPSEventHeader headerResponse = response.getHeader();
		
		return new ResponseEntity<>(
				  headerResponse.getCode()+":"+headerResponse.getMessage(), 
			      HttpStatus.OK);
	}
	
}
