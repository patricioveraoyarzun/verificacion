package arg.mps.seguros.broker.bo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import arg.mps.seguros.broker.api.Comuna;
import arg.mps.seguros.broker.api.DatoParametricoResponse;
import arg.mps.seguros.broker.api.MPSEventHeader;
import arg.mps.seguros.broker.api.MarcaAuto;
import arg.mps.seguros.broker.api.ModeloAuto;
import arg.mps.seguros.broker.api.Region;
import arg.mps.seguros.broker.controllers.MpsController;
import arg.mps.seguros.broker.dao.DatoParametricoDao;

@Service
public class MpsDatosParametricosService {

	private final Logger logger = LoggerFactory.getLogger(MpsController.class);
	
	@Autowired
	private DatoParametricoDao datoParametricoDao;
	
	public DatoParametricoResponse<Region> getRegiones() {
		List<Region> regiones = datoParametricoDao.getRegiones();
		
		MPSEventHeader objHeader = new MPSEventHeader();
		objHeader.setCode("200");
		objHeader.setMessage("Servicio ejecutado correctamente");

		DatoParametricoResponse<Region> response = new DatoParametricoResponse<Region>();
		response.setHeader(objHeader);		
		response.setDatos(regiones);
				
		return response;		
	}
	
	public DatoParametricoResponse<Comuna> getComunasPorRegion(int idRegion) {
		List<Comuna> comunas = datoParametricoDao.getComunasPorRegion(idRegion);
		
		MPSEventHeader objHeader = new MPSEventHeader();
		objHeader.setCode("200");
		objHeader.setMessage("Servicio ejecutado correctamente");

		DatoParametricoResponse<Comuna> response = new DatoParametricoResponse<Comuna>();
		response.setHeader(objHeader);		
		response.setDatos(comunas);
				
		return response;		
	}
	
	public DatoParametricoResponse<MarcaAuto> getMarcasAuto() {
		List<MarcaAuto> marcas = datoParametricoDao.getMarcasAuto();
		
		MPSEventHeader objHeader = new MPSEventHeader();
		objHeader.setCode("200");
		objHeader.setMessage("Servicio ejecutado correctamente");

		DatoParametricoResponse<MarcaAuto> response = new DatoParametricoResponse<MarcaAuto>();
		response.setHeader(objHeader);		
		response.setDatos(marcas);
				
		return response;	
	}
	
	public DatoParametricoResponse<ModeloAuto> getModelosAutoPorMarca(int idMarcaAuto) {
		List<ModeloAuto> modelo = datoParametricoDao.getModelosAutoPorMarca(idMarcaAuto);
		logger.info("Cantidad de modelos de autos: {}",  modelo.size());
		
		MPSEventHeader objHeader = new MPSEventHeader();
		objHeader.setCode("200");
		objHeader.setMessage("Servicio ejecutado correctamente");

		DatoParametricoResponse<ModeloAuto> response = new DatoParametricoResponse<ModeloAuto>();
		response.setHeader(objHeader);		
		response.setDatos(modelo);
				
		return response;	
	}	
}
