package arg.mps.seguros.broker.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import arg.mps.seguros.broker.api.Comuna;
import arg.mps.seguros.broker.api.DatoParametricoResponse;
import arg.mps.seguros.broker.api.MarcaAuto;
import arg.mps.seguros.broker.api.ModeloAuto;
import arg.mps.seguros.broker.api.Region;
import arg.mps.seguros.broker.bo.MpsDatosParametricosService;

@RestController
@RequestMapping("/api/core/paramdata")
public class MpsDatosParametricosController {

	private final static Logger logger = LoggerFactory.getLogger(MpsDatosParametricosController.class);
	
	@Autowired
	private MpsDatosParametricosService datosParametricosSrv;
		
	@RequestMapping(value="/regiones", 
            method=RequestMethod.GET, 
            produces=MediaType.APPLICATION_JSON_VALUE)
	public DatoParametricoResponse<Region> getRegiones() {
		logger.info("Datos parametricos -> Regiones");
		DatoParametricoResponse<Region> response = null;
		try {
			response = datosParametricosSrv.getRegiones();			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return response;
	}
	
	@RequestMapping(value="/regiones/{idRegion}/comunas", 
            method=RequestMethod.GET, 
            produces=MediaType.APPLICATION_JSON_VALUE)
	public DatoParametricoResponse<Comuna> getComunas(@PathVariable("idRegion") int idRegion) {
		logger.info("Datos parametricos -> Comunas: idRegion={}", idRegion);
		DatoParametricoResponse<Comuna> response = null;
		try {
			response = datosParametricosSrv.getComunasPorRegion(idRegion);			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return response;
	}
	
	@RequestMapping(value="/marcas/autos", 
            method=RequestMethod.GET, 
            produces=MediaType.APPLICATION_JSON_VALUE)
	public DatoParametricoResponse<MarcaAuto> getMarcasAuto() {
		logger.info("Datos parametricos -> Marcas auto");		
		DatoParametricoResponse<MarcaAuto> response = null;
		try {
			response = datosParametricosSrv.getMarcasAuto();			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return response;
	}
	
	@RequestMapping(value="marcas/{idMarcaAuto}/autos/modelos", 
            method=RequestMethod.GET, 
            produces=MediaType.APPLICATION_JSON_VALUE)
	public DatoParametricoResponse<ModeloAuto> getModelosAuto(@PathVariable("idMarcaAuto") int idMarcaAuto) {
		logger.info("Datos parametricos -> Modelos auto: idMarcaAuto={}", idMarcaAuto);		
		DatoParametricoResponse<ModeloAuto> response = null;
		try {
			response = datosParametricosSrv.getModelosAutoPorMarca(idMarcaAuto);			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return response;
	}	
}
