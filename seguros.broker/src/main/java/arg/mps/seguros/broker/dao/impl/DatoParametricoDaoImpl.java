package arg.mps.seguros.broker.dao.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import arg.mps.seguros.broker.api.Ciudad;
import arg.mps.seguros.broker.api.Comuna;
import arg.mps.seguros.broker.api.MarcaAuto;
import arg.mps.seguros.broker.api.ModeloAuto;
import arg.mps.seguros.broker.api.Region;
import arg.mps.seguros.broker.api.integration.ans.client.dto.ComboBoxList;
import arg.mps.seguros.broker.api.integration.ans.client.dto.DatoParametricoResponse;
import arg.mps.seguros.broker.api.integration.ans.client.dto.ResultadoComboBox;
import arg.mps.seguros.broker.api.integration.ans.rest.service.core.client.ComboBoxClient;
import arg.mps.seguros.broker.controllers.MpsController;
import arg.mps.seguros.broker.dao.DatoParametricoDao;
import arg.mps.seguros.broker.utils.StringUtil;

@Repository
public class DatoParametricoDaoImpl implements DatoParametricoDao {

	private final Logger logger = LoggerFactory.getLogger(MpsController.class);
	
	@Autowired
	ComboBoxClient ansComboBoxClient;
	
	//private static final List<Comuna> comunasDummy = getComunasDummy();
	//private static final List<MarcaAuto> marcasDummy = getMarcasAutoDummy();
	// private static final List<ModeloAuto> modelosDummy = getModeloAutoDummy();
	
	@Override
	public List<Region> getRegiones() {
		String metodo = "getRegiones";
		
		logger.info("[DAO][PAR_GRE01] - OK: Inicio del metodo {}.", metodo);
		
		List<Region> result = new ArrayList<Region>();
		try {
			DatoParametricoResponse response = ansComboBoxClient.getRegiones();
			
			logger.info("[DAO][PAR_GRE02] - OK: Regiones retornadas por servicio ANS -> response={}.", response);
			
			if(response.getEstado().equals("0")) {
				ResultadoComboBox resultadoParam = response.getResultado()[0];
				ComboBoxList[] comboBoxListResul = resultadoParam.getComboBoxList();
				
				Region region;
				for(int i=1; i<comboBoxListResul.length; i++) {
					ComboBoxList item = comboBoxListResul[i];
					
					region = new Region();
					region.setId(Integer.parseInt(item.getValue()));
					region.setNombre(item.getText());
					
					result.add(region);
				}
			}				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();		
		}
		
		logger.info("[DAO][PAR_GRE03] - OK: Cantidad de regiones retornadas: {}.", result.size());
		
		logger.info("[DAO][PAR_GRE04] - OK: Fin del metodo {}.", metodo);
		
		return result;
	}

	@Override
	public List<Comuna> getComunasPorRegion(int idRegion){
		String metodo = "getComunasPorRegion";		
		List<Comuna> result = new ArrayList<Comuna>();
		
		logger.info("[DAO][PAR_GCR01] - OK: Inicio del metodo {}. Param -> idRegion={}", metodo, idRegion);
		
		List<Ciudad> ciudades = this.getCiudades();
		List<Comuna> comunas = this.getComunas();
		
		logger.info("[DAO][[PAR_GCR02] - OK: Se obtuvieron las listas de ciudades y comunas provitas por ANS.");
		
		List<Ciudad> ciudadesRegion = ciudades
				.stream()
				.filter(c -> c.getIdRegion() == idRegion)
				.collect(Collectors.toList());
		
		for (Ciudad ciudad : ciudadesRegion) {
			List<Comuna> comunasCiudad = comunas.stream()
			.filter(c -> c.getIdCiudad() == ciudad.getId())
			.collect(Collectors.toList());
			
			result.addAll(comunasCiudad);
		}
		
		logger.info("[DAO][PAR_GCO03] - OK: Cantidad de comunas retornadas correspondiente a la region {}: {}.", idRegion, result.size());
		
		logger.info("[DAO][PAR_GCO04] - OK: Fin del metodo {}.", metodo);
		
		return result;
	}
	
	@Override
	public List<MarcaAuto> getMarcasAuto() {
		String metodo = "getMarcasAuto";
		
		logger.info("[DAO][PAR_GMA01] - OK: Inicio del metodo {}.", metodo);
		
		List<MarcaAuto> result = new ArrayList<MarcaAuto>();
		try {
			DatoParametricoResponse response = ansComboBoxClient.getMarcasAuto();
			
			logger.info("[DAO][PAR_GMA02] - OK: Marcas retornadas por servicio ANS: response={}.", response);
			
			if(response.getEstado().equals("0")) {
				ResultadoComboBox resultadoParam = response.getResultado()[0];
				ComboBoxList[] comboBoxListResul = resultadoParam.getComboBoxList();
				
				MarcaAuto marcaAuto;
				for(int i=1; i<comboBoxListResul.length; i++) {
					ComboBoxList item = comboBoxListResul[i];
					
					marcaAuto = new MarcaAuto();
					marcaAuto.setId(Integer.parseInt(item.getValue()));
					marcaAuto.setNombre(item.getText());
					
					result.add(marcaAuto);
				}
			}				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		

		logger.info("[DAO][PAR_GMA03] - OK: Cantidad de marcas retornadas: {}.", result.size());
		
		logger.info("[DAO][PAR_GMA04] - OK: Fin del metodo {}.", metodo);
		
		return result;
	}

	@Override
	public List<ModeloAuto> getModelosAutoPorMarca(int idMarcaAuto) {
		String metodo = "getModelosAutoPorMarca";
		
		logger.info("[DAO][PAR_GMO01] - OK: Inicio del metodo {}. Parametros -> idMarcaAuto={} .", metodo, idMarcaAuto);
		
		List<ModeloAuto> result = new ArrayList<ModeloAuto>();
		try {
			DatoParametricoResponse response = ansComboBoxClient.getModelosAuto();
			
			logger.info("[DAO][PAR_GMO02] - OK: Modelos retornados por servicio ANS: response={} .", response);
			
			if(response.getEstado().equals("0")) {
				ResultadoComboBox resultadoParam = response.getResultado()[0];
				ComboBoxList[] comboBoxListResul = resultadoParam.getComboBoxList();
				List<ComboBoxList> objComboBoxList = Arrays.asList(comboBoxListResul);
				
				result = objComboBoxList.stream()
				.filter(m -> m.getParentId().equals(String.valueOf(idMarcaAuto)))
				.map(a -> new ModeloAuto(Integer.parseInt(a.getValue()), idMarcaAuto, a.getText()))
				.collect(Collectors.toList());
			}				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		logger.info("[DAO][PAR_GMO03] - OK: Cantidad de modelos retornados correspondiente a la marca {}: {}.", idMarcaAuto, result.size());
		
		logger.info("[DAO][PAR_GMO04] - OK: Fin del metodo {}.", metodo);
		
		return result;
	}

	private List<Ciudad> getCiudades() {
		String metodo = "getCiudades";
		
		logger.info("[DAO][PAR_GCI01] - OK: Inicio del metodo {}.", metodo);
		
		List<Ciudad> result = new ArrayList<Ciudad>();
		try {
			DatoParametricoResponse response = ansComboBoxClient.getCiudades();
			
			logger.info("[DAO][PAR_GCI02] - OK: Ciudades retornadas por servicio ANS -> response={}.", response);
			
			if(response.getEstado().equals("0")) {
				ResultadoComboBox resultadoParam = response.getResultado()[0];
				ComboBoxList[] comboBoxListResul = resultadoParam.getComboBoxList();
				
				Ciudad ciudad;
				for(int i=0; i<comboBoxListResul.length; i++) {
					ComboBoxList item = comboBoxListResul[i];
					
					if(!item.getValue().equals("-1") && !StringUtil.isNullOrEmpty(item.getParentId())) {
						ciudad = new Ciudad();
						ciudad.setId(Integer.parseInt(item.getValue()));
						ciudad.setNombre(item.getText());
						ciudad.setIdRegion(Integer.parseInt(item.getParentId()));	
						
						result.add(ciudad);
					}			
				}
			}				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();		
		}
		
		logger.info("[DAO][PAR_GCI03] - OK: Cantidad de ciudades retornadas: {}.", result.size());
		
		logger.info("[DAO][PAR_GCI04] - OK: Fin del metodo {}.", metodo);
		
		return result;
	}
	
	private List<Comuna> getComunas() {
		String metodo = "getComunas";
		
		logger.info("[DAO][PAR_GCO01] - OK: Inicio del metodo {}.", metodo);
		
		List<Comuna> result = new ArrayList<Comuna>();
		try {
			DatoParametricoResponse response = ansComboBoxClient.getComunas();
			
			logger.info("[DAO][PAR_GCO02] - OK: Comunas retornadas por servicio ANS -> response={}.", response);
			
			if(response.getEstado().equals("0")) {
				ResultadoComboBox resultadoParam = response.getResultado()[0];
				ComboBoxList[] comboBoxListResul = resultadoParam.getComboBoxList();
				
				Comuna comuna;
				for(int i=0; i<comboBoxListResul.length; i++) {
					ComboBoxList item = comboBoxListResul[i];
					
					if(!item.getValue().equals("-1") && !StringUtil.isNullOrEmpty(item.getParentId())){
						comuna = new Comuna();
						comuna.setId(Integer.parseInt(item.getValue()));
						comuna.setNombre(item.getText());											
						comuna.setIdCiudad(Integer.parseInt(item.getParentId()));
						
						result.add(comuna);						
					}
				}
			}				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();		
		}
		
		logger.info("[DAO][PAR_GCO03] - OK: Cantidad de comunas retornadas: {}.", result.size());
		
		logger.info("[DAO][PAR_GCO04] - OK: Fin del metodo {}.", metodo);
		
		return result;
	}
	
	private static List<Comuna> getComunasDummy(){
		List<Comuna> comunas = new ArrayList<Comuna>();
		
		Comuna comuna = new Comuna();
		comuna.setId(1);
		comuna.setNombre("Arica");		
		comunas.add(comuna);
		
		comuna = new Comuna();
		comuna.setId(2);
		comuna.setNombre("Camarones");
		comunas.add(comuna);
		
		comuna = new Comuna();
		comuna.setId(3);
		comuna.setNombre("Putre");
		comunas.add(comuna);
		
		comuna = new Comuna();
		comuna.setId(4);
		comuna.setNombre("General Lagos");
		comunas.add(comuna);		
		
		comuna = new Comuna();
		comuna.setId(5);
		comuna.setNombre("Iquique");
		comunas.add(comuna);
		
		comuna = new Comuna();
		comuna.setId(6);
		comuna.setNombre("Alto Hospicio");
		comunas.add(comuna);		
		
		comuna = new Comuna();
		comuna.setId(7);
		comuna.setNombre("Pozo Almonte");
		comunas.add(comuna);
		
		comuna = new Comuna();
		comuna.setId(8);
		comuna.setNombre("Camiña");
		comunas.add(comuna);
		
		comuna = new Comuna();
		comuna.setId(9);
		comuna.setNombre("Colchane");
		comunas.add(comuna);
		
		comuna = new Comuna();
		comuna.setId(10);
		comuna.setNombre("Huara");
		comunas.add(comuna);
		
		return comunas;
	}
	
	private static List<MarcaAuto> getMarcasAutoDummy() {
		List<String> marcasDummy = new ArrayList<String>();
		marcasDummy.add("Acura");
		marcasDummy.add("Alfa Romeo");
		marcasDummy.add("Aptera");
		marcasDummy.add("Aston Martin");
		marcasDummy.add("Audi");
		marcasDummy.add("Austin");
		marcasDummy.add("Bentley");
		marcasDummy.add("BMW");
		marcasDummy.add("Bugatti");
		marcasDummy.add("Buick");
		marcasDummy.add("Cadillac");
		marcasDummy.add("Chevrolet");
		marcasDummy.add("Chrysler");
		marcasDummy.add("CitroÃ«n");
		marcasDummy.add("Corbin");
		marcasDummy.add("Daewoo");
		marcasDummy.add("Daihatsu");
		marcasDummy.add("Dodge");
		marcasDummy.add("Eagle");
		marcasDummy.add("Fairthorpe");
		marcasDummy.add("Ferrari");
		marcasDummy.add("FIAT");
		marcasDummy.add("Fillmore");
		marcasDummy.add("Foose");
		marcasDummy.add("Ford");
		marcasDummy.add("Geo");
		marcasDummy.add("GMC");
		marcasDummy.add("Hillman");
		marcasDummy.add("Holden");
		marcasDummy.add("Honda");
		marcasDummy.add("HUMMER");
		marcasDummy.add("Hyundai");
		marcasDummy.add("Infiniti");
		marcasDummy.add("Isuzu");
		marcasDummy.add("Jaguar");
		marcasDummy.add("Jeep");
		marcasDummy.add("Jensen");
		marcasDummy.add("Kia");
		marcasDummy.add("Lamborghini");
		marcasDummy.add("Land Rover");
		marcasDummy.add("Lexus");
		marcasDummy.add("Lincoln");
		marcasDummy.add("Lotus");
		marcasDummy.add("Maserati");
		marcasDummy.add("Maybach");
		marcasDummy.add("Mazda");
		marcasDummy.add("McLaren");
		marcasDummy.add("Mercedes-Benz");
		marcasDummy.add("Mercury");
		marcasDummy.add("Merkur");
		marcasDummy.add("MG");
		marcasDummy.add("MINI");
		marcasDummy.add("Mitsubishi");
		marcasDummy.add("Morgan");
		marcasDummy.add("Nissan");
		marcasDummy.add("Oldsmobile");
		marcasDummy.add("Panoz");
		marcasDummy.add("Peugeot");
		marcasDummy.add("Plymouth");
		marcasDummy.add("Pontiac");
		marcasDummy.add("Porsche");
		marcasDummy.add("Ram");
		marcasDummy.add("Rambler");
		marcasDummy.add("Renault");
		marcasDummy.add("Rolls-Royce");
		marcasDummy.add("Saab");
		marcasDummy.add("Saturn");
		marcasDummy.add("Scion");
		marcasDummy.add("Shelby");
		marcasDummy.add("Smart");
		marcasDummy.add("Spyker");
		marcasDummy.add("Spyker Cars");
		marcasDummy.add("Studebaker");
		marcasDummy.add("Subaru");
		marcasDummy.add("Suzuki");
		marcasDummy.add("Tesla");
		marcasDummy.add("Toyota");
		marcasDummy.add("Volkswagen");
		marcasDummy.add("Volvo");
		
		List<MarcaAuto> marcasResult = new ArrayList<MarcaAuto>();
		MarcaAuto marca = null;
		
		for(int i=0; i < marcasDummy.size(); i++) {
			marca = new MarcaAuto();
			marca.setId((i+1));
			marca.setNombre(marcasDummy.get(i));
			
			marcasResult.add(marca);
		}
		
		return marcasResult;
	}
	
	private static List<ModeloAuto> getModeloAutoDummy(){
		List<String> modelosDummy = new ArrayList<String>();
		modelosDummy.add("CL,1");
		modelosDummy.add("Integra,1");
		modelosDummy.add("Legend,1");
		modelosDummy.add("MDX,1");
		modelosDummy.add("NSX,1");
		modelosDummy.add("RDX,1");
		modelosDummy.add("RL,1");
		modelosDummy.add("RSX,1");
		modelosDummy.add("SLX,1");
		modelosDummy.add("TL,1");
		modelosDummy.add("TSX,1");
		modelosDummy.add("Vigor,1");
		modelosDummy.add("ZDX,1");
		modelosDummy.add("164,2");
		modelosDummy.add("Spider,2");
		modelosDummy.add("2e,3");
		modelosDummy.add("Typ-1,3");
		modelosDummy.add("Type-1h,3");
		modelosDummy.add("DB9,4");
		modelosDummy.add("DB9 Volante,4");
		modelosDummy.add("DBS,4");
		modelosDummy.add("Rapide,4");
		modelosDummy.add("V12 Vantage,4");
		modelosDummy.add("V8 Vantage,4");
		modelosDummy.add("V8 Vantage S,4");
		modelosDummy.add("Vanquish S,4");
		modelosDummy.add("Vantage,4");
		modelosDummy.add("Virage,4");
		modelosDummy.add("100,5");
		modelosDummy.add("200,5");
		modelosDummy.add("4000,5");
		modelosDummy.add("4000CS Quattro,5");
		modelosDummy.add("4000s,5");
		modelosDummy.add("4000s Quattro,5");
		modelosDummy.add("5000CS,5");
		modelosDummy.add("5000CS Quattro,5");
		modelosDummy.add("5000S,5");
		modelosDummy.add("80,5");
		modelosDummy.add("80/90,5");
		modelosDummy.add("90,5");
		modelosDummy.add("A3,5");
		modelosDummy.add("A4,5");
		modelosDummy.add("A5,5");
		modelosDummy.add("A6,5");
		modelosDummy.add("A7,5");
		modelosDummy.add("A8,5");
		modelosDummy.add("Allroad,5");
		modelosDummy.add("Cabriolet,5");
		modelosDummy.add("Coupe GT,5");
		modelosDummy.add("Coupe Quattro,5");
		modelosDummy.add("Q5,5");
		modelosDummy.add("Q7,5");
		modelosDummy.add("Quattro,5");
		modelosDummy.add("R8,5");
		modelosDummy.add("riolet,5");
		modelosDummy.add("RS 4,5");
		modelosDummy.add("RS 6,5");
		modelosDummy.add("RS4,5");
		modelosDummy.add("RS6,5");
		modelosDummy.add("S4,5");
		modelosDummy.add("S5,5");
		modelosDummy.add("S6,5");
		modelosDummy.add("S8,5");
		modelosDummy.add("TT,5");
		modelosDummy.add("V8,5");
		modelosDummy.add("Mini,6");
		modelosDummy.add("Mini Cooper,6");
		modelosDummy.add("Mini Cooper S,6");
		modelosDummy.add("Arnage,7");
		modelosDummy.add("Azure,7");
		modelosDummy.add("Azure T,7");
		modelosDummy.add("Brooklands,7");
		modelosDummy.add("Continental,7");
		modelosDummy.add("Continental Flying S,7");
		modelosDummy.add("Continental GT,7");
		modelosDummy.add("Continental GTC,7");
		modelosDummy.add("Continental Super,7");
		modelosDummy.add("Mulsanne,7");
		modelosDummy.add("1 Series,8");
		modelosDummy.add("3 Series,8");
		modelosDummy.add("325,8");
		modelosDummy.add("330,8");
		modelosDummy.add("5 Series,8");
		modelosDummy.add("525,8");
		modelosDummy.add("530,8");
		modelosDummy.add("545,8");
		modelosDummy.add("550,8");
		modelosDummy.add("6 Series,8");
		modelosDummy.add("600,8");
		modelosDummy.add("645,8");
		modelosDummy.add("650,8");
		modelosDummy.add("7 Series,8");
		modelosDummy.add("745,8");
		modelosDummy.add("750,8");
		modelosDummy.add("760,8");
		modelosDummy.add("8 Series,8");
		modelosDummy.add("Alpina B7,8");
		modelosDummy.add("M,8");
		modelosDummy.add("M Roadster,8");
		modelosDummy.add("M3,8");
		modelosDummy.add("M5,8");
		modelosDummy.add("M6,8");
		modelosDummy.add("X3,8");
		modelosDummy.add("X5,8");
		modelosDummy.add("X5 M,8");
		modelosDummy.add("X6,8");
		modelosDummy.add("X6 M,8");
		modelosDummy.add("Z3,8");
		modelosDummy.add("Z4,8");
		modelosDummy.add("Z4 M,8");
		modelosDummy.add("Z4 M Roadster,8");
		modelosDummy.add("Z8,8");
		modelosDummy.add("Veyron,9");
		modelosDummy.add("Century,10");
		modelosDummy.add("Coachbuilder,10");
		modelosDummy.add("Electra,10");
		modelosDummy.add("Enclave,10");
		modelosDummy.add("Estate,10");
		modelosDummy.add("Hearse,10");
		modelosDummy.add("LaCrosse,10");
		modelosDummy.add("LeSabre,10");
		modelosDummy.add("Lucerne,10");
		modelosDummy.add("Park Avenue,10");
		modelosDummy.add("Rainier,10");
		modelosDummy.add("Reatta,10");
		modelosDummy.add("Regal,10");
		modelosDummy.add("Rendezvous,10");
		modelosDummy.add("Riviera,10");
		modelosDummy.add("Roadmaster,10");
		modelosDummy.add("Skyhawk,10");
		modelosDummy.add("Skylark,10");
		modelosDummy.add("Somerset,10");
		modelosDummy.add("Special,10");
		modelosDummy.add("Terraza,10");
		modelosDummy.add("Verano,10");
		modelosDummy.add("Allante,11");
		modelosDummy.add("Brougham,11");
		modelosDummy.add("Catera,11");
		modelosDummy.add("CTS,11");
		modelosDummy.add("CTS-V,11");
		modelosDummy.add("DeVille,11");
		modelosDummy.add("DTS,11");
		modelosDummy.add("Eldorado,11");
		modelosDummy.add("Escalade,11");
		modelosDummy.add("Escalade ESV,11");
		modelosDummy.add("Escalade EXT,11");
		modelosDummy.add("Fleetwood,11");
		modelosDummy.add("Seville,11");
		modelosDummy.add("Sixty Special,11");
		modelosDummy.add("SRX,11");
		modelosDummy.add("STS,11");
		modelosDummy.add("STS-V,11");
		modelosDummy.add("XLR,11");
		modelosDummy.add("XLR-V,11");
		modelosDummy.add("1500,12");
		modelosDummy.add("2500,12");
		modelosDummy.add("3500,12");
		modelosDummy.add("APV,12");
		modelosDummy.add("Astro,12");
		modelosDummy.add("Avalanche,12");
		modelosDummy.add("Avalanche 1500,12");
		modelosDummy.add("Avalanche 2500,12");
		modelosDummy.add("Aveo,12");
		modelosDummy.add("Bel Air,12");
		modelosDummy.add("Beretta,12");
		modelosDummy.add("Blazer,12");
		modelosDummy.add("Camaro,12");
		modelosDummy.add("Caprice,12");
		modelosDummy.add("Caprice Classic,12");
		modelosDummy.add("Cavalier,12");
		modelosDummy.add("Citation,12");
		modelosDummy.add("Classic,12");
		modelosDummy.add("Cobalt,12");
		modelosDummy.add("Cobalt SS,12");
		modelosDummy.add("Colorado,12");
		modelosDummy.add("Corsica,12");
		modelosDummy.add("Corvair,12");
		modelosDummy.add("Corvair 500,12");
		modelosDummy.add("Corvette,12");
		modelosDummy.add("Cruze,12");
		modelosDummy.add("Equinox,12");
		modelosDummy.add("Express,12");
		modelosDummy.add("Express 1500,12");
		modelosDummy.add("Express 2500,12");
		modelosDummy.add("Express 3500,12");
		modelosDummy.add("G-Series 1500,12");
		modelosDummy.add("G-Series 2500,12");
		modelosDummy.add("G-Series 3500,12");
		modelosDummy.add("G-Series G10,12");
		modelosDummy.add("G-Series G20,12");
		modelosDummy.add("G-Series G30,12");
		modelosDummy.add("HHR,12");
		modelosDummy.add("HHR Panel,12");
		modelosDummy.add("Impala,12");
		modelosDummy.add("Impala SS,12");
		modelosDummy.add("K5 Blazer,12");
		modelosDummy.add("Lumina,12");
		modelosDummy.add("Lumina APV,12");
		modelosDummy.add("LUV,12");
		modelosDummy.add("Malibu,12");
		modelosDummy.add("Malibu Maxx,12");
		modelosDummy.add("Metro,12");
		modelosDummy.add("Monte Carlo,12");
		modelosDummy.add("Monza,12");
		modelosDummy.add("Prizm,12");
		modelosDummy.add("S10,12");
		modelosDummy.add("S10 Blazer,12");
		modelosDummy.add("Silverado,12");
		modelosDummy.add("Silverado 1500,12");
		modelosDummy.add("Silverado 2500,12");
		modelosDummy.add("Silverado 3500,12");
		modelosDummy.add("Silverado 3500HD,12");
		modelosDummy.add("Silverado Hybrid,12");
		modelosDummy.add("Sonic,12");
		modelosDummy.add("Sportvan G10,12");
		modelosDummy.add("Sportvan G20,12");
		modelosDummy.add("Sportvan G30,12");
		modelosDummy.add("SSR,12");
		modelosDummy.add("Suburban,12");
		modelosDummy.add("Suburban 1500,12");
		modelosDummy.add("Suburban 2500,12");
		modelosDummy.add("Tahoe,12");
		modelosDummy.add("Tracker,12");
		modelosDummy.add("Trailblazer,12");
		modelosDummy.add("Traverse,12");
		modelosDummy.add("Uplander,12");
		modelosDummy.add("Vega,12");
		modelosDummy.add("Venture,12");
		modelosDummy.add("Volt,12");
		modelosDummy.add("200,13");
		modelosDummy.add("300,13");
		modelosDummy.add("300C,13");
		modelosDummy.add("300M,13");
		modelosDummy.add("Aspen,13");
		modelosDummy.add("Cirrus,13");
		modelosDummy.add("Concorde,13");
		modelosDummy.add("Crossfire,13");
		modelosDummy.add("Crossfire Roadster,13");
		modelosDummy.add("Fifth Ave,13");
		modelosDummy.add("Grand Voyager,13");
		modelosDummy.add("Imperial,13");
		modelosDummy.add("LeBaron,13");
		modelosDummy.add("LHS,13");
		modelosDummy.add("New Yorker,13");
		modelosDummy.add("Pacifica,13");
		modelosDummy.add("Prowler,13");
		modelosDummy.add("PT Cruiser,13");
		modelosDummy.add("Sebring,13");
		modelosDummy.add("Town & Country,13");
		modelosDummy.add("Voyager,13");
		modelosDummy.add("2CV,14");
		modelosDummy.add("CX,14");
		modelosDummy.add("SM,14");
		modelosDummy.add("Sparrow,15");
		modelosDummy.add("Lanos,16");
		modelosDummy.add("Leganza,16");
		modelosDummy.add("Nubira,16");
		modelosDummy.add("Charade,17");
		modelosDummy.add("Rocky,17");
		modelosDummy.add("Aries,18");
		modelosDummy.add("Aspen,18");
		modelosDummy.add("Avenger,18");
		modelosDummy.add("Caliber,18");
		modelosDummy.add("Caravan,18");
		modelosDummy.add("Challenger,18");
		modelosDummy.add("Charger,18");
		modelosDummy.add("Colt,18");
		modelosDummy.add("D150,18");
		modelosDummy.add("D150 Club,18");
		modelosDummy.add("D250,18");
		modelosDummy.add("D250 Club,18");
		modelosDummy.add("D350,18");
		modelosDummy.add("D350 Club,18");
		modelosDummy.add("Dakota,18");
		modelosDummy.add("Dakota Club,18");
		modelosDummy.add("Daytona,18");
		modelosDummy.add("Durango,18");
		modelosDummy.add("Dynasty,18");
		modelosDummy.add("Grand Caravan,18");
		modelosDummy.add("Intrepid,18");
		modelosDummy.add("Journey,18");
		modelosDummy.add("Magnum,18");
		modelosDummy.add("Monaco,18");
		modelosDummy.add("Neon,18");
		modelosDummy.add("Nitro,18");
		modelosDummy.add("Omni,18");
		modelosDummy.add("Ram,18");
		modelosDummy.add("Ram 1500,18");
		modelosDummy.add("Ram 1500 Club,18");
		modelosDummy.add("Ram 2500,18");
		modelosDummy.add("Ram 2500 Club,18");
		modelosDummy.add("Ram 3500,18");
		modelosDummy.add("Ram 3500 Club,18");
		modelosDummy.add("Ram 50,18");
		modelosDummy.add("Ram Van 1500,18");
		modelosDummy.add("Ram Van 2500,18");
		modelosDummy.add("Ram Van 3500,18");
		modelosDummy.add("Ram Van B150,18");
		modelosDummy.add("Ram Van B250,18");
		modelosDummy.add("Ram Van B350,18");
		modelosDummy.add("Ram Wagon B150,18");
		modelosDummy.add("Ram Wagon B250,18");
		modelosDummy.add("Ram Wagon B350,18");
		modelosDummy.add("Ramcharger,18");
		modelosDummy.add("Shadow,18");
		modelosDummy.add("Spirit,18");
		modelosDummy.add("Sprinter,18");
		modelosDummy.add("Stealth,18");
		modelosDummy.add("Stratus,18");
		modelosDummy.add("Viper,18");
		modelosDummy.add("Viper RT/10,18");
		modelosDummy.add("Premier,19");
		modelosDummy.add("Summit,19");
		modelosDummy.add("Talon,19");
		modelosDummy.add("Vision,19");
		modelosDummy.add("Rockette,20");
		modelosDummy.add("430 Scuderia,21");
		modelosDummy.add("458 Italia,21");
		modelosDummy.add("599 GTB Fiorano,21");
		modelosDummy.add("612 Scaglietti,21");
		modelosDummy.add("California,21");
		modelosDummy.add("F430,21");
		modelosDummy.add("F430 Spider,21");
		modelosDummy.add("FF,21");
		modelosDummy.add("500,22");
		modelosDummy.add("Nuova 500,22");
		modelosDummy.add("Fillmore,23");
		modelosDummy.add("Hemisfear,24");
		modelosDummy.add("Aerostar,25");
		modelosDummy.add("Aspire,25");
		modelosDummy.add("Bronco,25");
		modelosDummy.add("Bronco II,25");
		modelosDummy.add("C-MAX Hybrid,25");
		modelosDummy.add("Club Wagon,25");
		modelosDummy.add("Contour,25");
		modelosDummy.add("Country,25");
		modelosDummy.add("Courier,25");
		modelosDummy.add("Crown Victoria,25");
		modelosDummy.add("E-350 Super Duty,25");
		modelosDummy.add("E-350 Super Duty Van,25");
		modelosDummy.add("E-Series,25");
		modelosDummy.add("E150,25");
		modelosDummy.add("E250,25");
		modelosDummy.add("E350,25");
		modelosDummy.add("Econoline E150,25");
		modelosDummy.add("Econoline E250,25");
		modelosDummy.add("Econoline E350,25");
		modelosDummy.add("Edge,25");
		modelosDummy.add("Escape,25");
		modelosDummy.add("Escort,25");
		modelosDummy.add("Escort ZX2,25");
		modelosDummy.add("Excursion,25");
		modelosDummy.add("EXP,25");
		modelosDummy.add("Expedition,25");
		modelosDummy.add("Expedition EL,25");
		modelosDummy.add("Explorer,25");
		modelosDummy.add("Explorer Sport,25");
		modelosDummy.add("Explorer Sport Trac,25");
		modelosDummy.add("F-250 Super Duty,25");
		modelosDummy.add("F-350 Super Duty,25");
		modelosDummy.add("F-Series,25");
		modelosDummy.add("F-Series Super Duty,25");
		modelosDummy.add("F150,25");
		modelosDummy.add("F250,25");
		modelosDummy.add("F350,25");
		modelosDummy.add("F450,25");
		modelosDummy.add("Fairlane,25");
		modelosDummy.add("Falcon,25");
		modelosDummy.add("Festiva,25");
		modelosDummy.add("Fiesta,25");
		modelosDummy.add("Five Hundred,25");
		modelosDummy.add("Flex,25");
		modelosDummy.add("Focus,25");
		modelosDummy.add("Focus ST,25");
		modelosDummy.add("Freestar,25");
		modelosDummy.add("Freestyle,25");
		modelosDummy.add("Fusion,25");
		modelosDummy.add("Galaxie,25");
		modelosDummy.add("GT,25");
		modelosDummy.add("GT500,25");
		modelosDummy.add("Laser,25");
		modelosDummy.add("Lightning,25");
		modelosDummy.add("LTD,25");
		modelosDummy.add("LTD Crown Victoria,25");
		modelosDummy.add("Model T,25");
		modelosDummy.add("Mustang,25");
		modelosDummy.add("Probe,25");
		modelosDummy.add("Ranger,25");
		modelosDummy.add("Taurus,25");
		modelosDummy.add("Taurus X,25");
		modelosDummy.add("Tempo,25");
		modelosDummy.add("Th!nk,25");
		modelosDummy.add("Thunderbird,25");
		modelosDummy.add("Torino,25");
		modelosDummy.add("Transit Connect,25");
		modelosDummy.add("Windstar,25");
		modelosDummy.add("ZX2,25");
		modelosDummy.add("Metro,26");
		modelosDummy.add("Prizm,26");
		modelosDummy.add("Storm,26");
		modelosDummy.add("Tracker,26");
		modelosDummy.add("1500,27");
		modelosDummy.add("1500 Club Coupe,27");
		modelosDummy.add("2500,27");
		modelosDummy.add("2500 Club Coupe,27");
		modelosDummy.add("3500,27");
		modelosDummy.add("3500 Club Coupe,27");
		modelosDummy.add("Acadia,27");
		modelosDummy.add("Canyon,27");
		modelosDummy.add("Envoy,27");
		modelosDummy.add("Envoy XL,27");
		modelosDummy.add("Envoy XUV,27");
		modelosDummy.add("EV1,27");
		modelosDummy.add("Jimmy,27");
		modelosDummy.add("Rally Wagon 1500,27");
		modelosDummy.add("Rally Wagon 2500,27");
		modelosDummy.add("Rally Wagon 3500,27");
		modelosDummy.add("Rally Wagon G2500,27");
		modelosDummy.add("Rally Wagon G3500,27");
		modelosDummy.add("Safari,27");
		modelosDummy.add("Savana,27");
		modelosDummy.add("Savana 1500,27");
		modelosDummy.add("Savana 2500,27");
		modelosDummy.add("Savana 3500,27");
		modelosDummy.add("Savana Cargo Van,27");
		modelosDummy.add("Sierra,27");
		modelosDummy.add("Sierra 1500,27");
		modelosDummy.add("Sierra 2500,27");
		modelosDummy.add("Sierra 2500HD,27");
		modelosDummy.add("Sierra 3500,27");
		modelosDummy.add("Sierra 3500HD,27");
		modelosDummy.add("Sierra Denali,27");
		modelosDummy.add("Sierra Hybrid,27");
		modelosDummy.add("Sonoma,27");
		modelosDummy.add("Sonoma Club,27");
		modelosDummy.add("Sonoma Club Coupe,27");
		modelosDummy.add("Suburban 1500,27");
		modelosDummy.add("Suburban 2500,27");
		modelosDummy.add("Terrain,27");
		modelosDummy.add("Vandura 1500,27");
		modelosDummy.add("Vandura 2500,27");
		modelosDummy.add("Vandura 3500,27");
		modelosDummy.add("Vandura G1500,27");
		modelosDummy.add("Vandura G2500,27");
		modelosDummy.add("Vandura G3500,27");
		modelosDummy.add("Yukon,27");
		modelosDummy.add("Yukon Denali,27");
		modelosDummy.add("Yukon XL,27");
		modelosDummy.add("Yukon XL 1500,27");
		modelosDummy.add("Yukon XL 2500,27");
		modelosDummy.add("Minx Magnificent,28");
		modelosDummy.add("Monaro,29");
		modelosDummy.add("VS Commodore,29");
		modelosDummy.add("Accord,30");
		modelosDummy.add("Accord Crosstour,30");
		modelosDummy.add("Civic,30");
		modelosDummy.add("Civic GX,30");
		modelosDummy.add("Civic Si,30");
		modelosDummy.add("CR-V,30");
		modelosDummy.add("CR-X,30");
		modelosDummy.add("CR-Z,30");
		modelosDummy.add("Crosstour,30");
		modelosDummy.add("del Sol,30");
		modelosDummy.add("Element,30");
		modelosDummy.add("FCX Clarity,30");
		modelosDummy.add("Fit,30");
		modelosDummy.add("Insight,30");
		modelosDummy.add("Odyssey,30");
		modelosDummy.add("Passport,30");
		modelosDummy.add("Pilot,30");
		modelosDummy.add("Prelude,30");
		modelosDummy.add("Ridgeline,30");
		modelosDummy.add("S2000,30");
		modelosDummy.add("H1,31");
		modelosDummy.add("H2,31");
		modelosDummy.add("H2 SUT,31");
		modelosDummy.add("H2 SUV,31");
		modelosDummy.add("H3,31");
		modelosDummy.add("H3T,31");
		modelosDummy.add("Accent,32");
		modelosDummy.add("Azera,32");
		modelosDummy.add("Elantra,32");
		modelosDummy.add("Entourage,32");
		modelosDummy.add("Equus,32");
		modelosDummy.add("Excel,32");
		modelosDummy.add("Genesis,32");
		modelosDummy.add("Genesis Coupe,32");
		modelosDummy.add("HED-5,32");
		modelosDummy.add("Santa Fe,32");
		modelosDummy.add("Scoupe,32");
		modelosDummy.add("Sonata,32");
		modelosDummy.add("Tiburon,32");
		modelosDummy.add("Tucson,32");
		modelosDummy.add("Veloster,32");
		modelosDummy.add("Veracruz,32");
		modelosDummy.add("XG300,32");
		modelosDummy.add("XG350,32");
		modelosDummy.add("EX,33");
		modelosDummy.add("FX,33");
		modelosDummy.add("G,33");
		modelosDummy.add("G25,33");
		modelosDummy.add("G35,33");
		modelosDummy.add("G37,33");
		modelosDummy.add("I,33");
		modelosDummy.add("IPL G,33");
		modelosDummy.add("J,33");
		modelosDummy.add("JX,33");
		modelosDummy.add("M,33");
		modelosDummy.add("Q,33");
		modelosDummy.add("QX,33");
		modelosDummy.add("QX56,33");
		modelosDummy.add("Amigo,34");
		modelosDummy.add("Ascender,34");
		modelosDummy.add("Axiom,34");
		modelosDummy.add("Hombre,34");
		modelosDummy.add("Hombre Space,34");
		modelosDummy.add("i-280,34");
		modelosDummy.add("i-290,34");
		modelosDummy.add("i-350,34");
		modelosDummy.add("i-370,34");
		modelosDummy.add("i-Series,34");
		modelosDummy.add("Impulse,34");
		modelosDummy.add("Oasis,34");
		modelosDummy.add("Rodeo,34");
		modelosDummy.add("Rodeo Sport,34");
		modelosDummy.add("Space,34");
		modelosDummy.add("Stylus,34");
		modelosDummy.add("Trooper,34");
		modelosDummy.add("VehiCROSS,34");
		modelosDummy.add("S-Type,35");
		modelosDummy.add("X-Type,35");
		modelosDummy.add("XF,35");
		modelosDummy.add("XJ,35");
		modelosDummy.add("XJ Series,35");
		modelosDummy.add("XK,35");
		modelosDummy.add("XK Series,35");
		modelosDummy.add("Cherokee,36");
		modelosDummy.add("Comanche,36");
		modelosDummy.add("Commander,36");
		modelosDummy.add("Compass,36");
		modelosDummy.add("Grand Cherokee,36");
		modelosDummy.add("Liberty,36");
		modelosDummy.add("Patriot,36");
		modelosDummy.add("Wrangler,36");
		modelosDummy.add("Interceptor,37");
		modelosDummy.add("Amanti,38");
		modelosDummy.add("Borrego,38");
		modelosDummy.add("Carens,38");
		modelosDummy.add("Forte,38");
		modelosDummy.add("Mentor,38");
		modelosDummy.add("Mohave/Borrego,38");
		modelosDummy.add("Optima,38");
		modelosDummy.add("Rio,38");
		modelosDummy.add("Rio5,38");
		modelosDummy.add("Rondo,38");
		modelosDummy.add("Sedona,38");
		modelosDummy.add("Sephia,38");
		modelosDummy.add("Sorento,38");
		modelosDummy.add("Soul,38");
		modelosDummy.add("Spectra,38");
		modelosDummy.add("Spectra5,38");
		modelosDummy.add("Sportage,38");
		modelosDummy.add("Aventador,39");
		modelosDummy.add("Countach,39");
		modelosDummy.add("Diablo,39");
		modelosDummy.add("Gallardo,39");
		modelosDummy.add("MurciÃ©lago,39");
		modelosDummy.add("MurciÃ©lago LP640,39");
		modelosDummy.add("ReventÃ³n,39");
		modelosDummy.add("Defender,40");
		modelosDummy.add("Defender 110,40");
		modelosDummy.add("Defender 90,40");
		modelosDummy.add("Defender Ice Edition,40");
		modelosDummy.add("Discovery,40");
		modelosDummy.add("Discovery Series II,40");
		modelosDummy.add("Freelander,40");
		modelosDummy.add("LR2,40");
		modelosDummy.add("LR3,40");
		modelosDummy.add("LR4,40");
		modelosDummy.add("Range Rover,40");
		modelosDummy.add("Range Rover Classic,40");
		modelosDummy.add("Range Rover Evoque,40");
		modelosDummy.add("Range Rover Sport,40");
		modelosDummy.add("Sterling,40");
		modelosDummy.add("CT,41");
		modelosDummy.add("ES,41");
		modelosDummy.add("GS,41");
		modelosDummy.add("GX,41");
		modelosDummy.add("HS,41");
		modelosDummy.add("IS,41");
		modelosDummy.add("IS F,41");
		modelosDummy.add("IS-F,41");
		modelosDummy.add("LFA,41");
		modelosDummy.add("LS,41");
		modelosDummy.add("LS Hybrid,41");
		modelosDummy.add("LX,41");
		modelosDummy.add("RX,41");
		modelosDummy.add("RX Hybrid,41");
		modelosDummy.add("SC,41");
		modelosDummy.add("Aviator,42");
		modelosDummy.add("Blackwood,42");
		modelosDummy.add("Continental,42");
		modelosDummy.add("Continental Mark VII,42");
		modelosDummy.add("LS,42");
		modelosDummy.add("Mark LT,42");
		modelosDummy.add("Mark VII,42");
		modelosDummy.add("Mark VIII,42");
		modelosDummy.add("MKS,42");
		modelosDummy.add("MKT,42");
		modelosDummy.add("MKX,42");
		modelosDummy.add("MKZ,42");
		modelosDummy.add("Navigator,42");
		modelosDummy.add("Navigator L,42");
		modelosDummy.add("Town Car,42");
		modelosDummy.add("Zephyr,42");
		modelosDummy.add("Elan,43");
		modelosDummy.add("Elise,43");
		modelosDummy.add("Esprit,43");
		modelosDummy.add("Esprit Turbo,43");
		modelosDummy.add("Evora,43");
		modelosDummy.add("Exige,43");
		modelosDummy.add("228,44");
		modelosDummy.add("430,44");
		modelosDummy.add("Biturbo,44");
		modelosDummy.add("Coupe,44");
		modelosDummy.add("Gran Sport,44");
		modelosDummy.add("GranSport,44");
		modelosDummy.add("GranTurismo,44");
		modelosDummy.add("Karif,44");
		modelosDummy.add("Quattroporte,44");
		modelosDummy.add("Spyder,44");
		modelosDummy.add("57,45");
		modelosDummy.add("57S,45");
		modelosDummy.add("62,45");
		modelosDummy.add("Landaulet,45");
		modelosDummy.add("323,46");
		modelosDummy.add("626,46");
		modelosDummy.add("929,46");
		modelosDummy.add("B-Series,46");
		modelosDummy.add("B-Series Plus,46");
		modelosDummy.add("B2000,46");
		modelosDummy.add("B2500,46");
		modelosDummy.add("B2600,46");
		modelosDummy.add("CX-5,46");
		modelosDummy.add("CX-7,46");
		modelosDummy.add("CX-9,46");
		modelosDummy.add("Familia,46");
		modelosDummy.add("GLC,46");
		modelosDummy.add("Mazda2,46");
		modelosDummy.add("Mazda3,46");
		modelosDummy.add("Mazda5,46");
		modelosDummy.add("Mazda6,46");
		modelosDummy.add("Mazda6 5-Door,46");
		modelosDummy.add("Mazda6 Sport,46");
		modelosDummy.add("Mazdaspeed 3,46");
		modelosDummy.add("Mazdaspeed6,46");
		modelosDummy.add("Miata MX-5,46");
		modelosDummy.add("Millenia,46");
		modelosDummy.add("MPV,46");
		modelosDummy.add("MX-3,46");
		modelosDummy.add("MX-5,46");
		modelosDummy.add("MX-6,46");
		modelosDummy.add("Navajo,46");
		modelosDummy.add("Protege,46");
		modelosDummy.add("Protege5,46");
		modelosDummy.add("RX-7,46");
		modelosDummy.add("RX-8,46");
		modelosDummy.add("Tribute,46");
		modelosDummy.add("MP4-12C,47");
		modelosDummy.add("190E,48");
		modelosDummy.add("300CE,48");
		modelosDummy.add("300D,48");
		modelosDummy.add("300E,48");
		modelosDummy.add("300SD,48");
		modelosDummy.add("300SE,48");
		modelosDummy.add("300SL,48");
		modelosDummy.add("300TE,48");
		modelosDummy.add("400E,48");
		modelosDummy.add("400SE,48");
		modelosDummy.add("400SEL,48");
		modelosDummy.add("500E,48");
		modelosDummy.add("500SEC,48");
		modelosDummy.add("500SEL,48");
		modelosDummy.add("500SL,48");
		modelosDummy.add("600SEC,48");
		modelosDummy.add("600SEL,48");
		modelosDummy.add("600SL,48");
		modelosDummy.add("C-Class,48");
		modelosDummy.add("CL-Class,48");
		modelosDummy.add("CL65 AMG,48");
		modelosDummy.add("CLK-Class,48");
		modelosDummy.add("CLS-Class,48");
		modelosDummy.add("E-Class,48");
		modelosDummy.add("G-Class,48");
		modelosDummy.add("G55 AMG,48");
		modelosDummy.add("GL-Class,48");
		modelosDummy.add("GLK-Class,48");
		modelosDummy.add("M-Class,48");
		modelosDummy.add("R-Class,48");
		modelosDummy.add("S-Class,48");
		modelosDummy.add("SL-Class,48");
		modelosDummy.add("SL65 AMG,48");
		modelosDummy.add("SLK-Class,48");
		modelosDummy.add("SLK55 AMG,48");
		modelosDummy.add("SLR McLaren,48");
		modelosDummy.add("SLS AMG,48");
		modelosDummy.add("SLS-Class,48");
		modelosDummy.add("Sprinter,48");
		modelosDummy.add("Sprinter 2500,48");
		modelosDummy.add("Sprinter 3500,48");
		modelosDummy.add("W123,48");
		modelosDummy.add("W126,48");
		modelosDummy.add("W201,48");
		modelosDummy.add("Capri,49");
		modelosDummy.add("Cougar,49");
		modelosDummy.add("Grand Marquis,49");
		modelosDummy.add("Lynx,49");
		modelosDummy.add("Marauder,49");
		modelosDummy.add("Mariner,49");
		modelosDummy.add("Marquis,49");
		modelosDummy.add("Milan,49");
		modelosDummy.add("Montego,49");
		modelosDummy.add("Monterey,49");
		modelosDummy.add("Mountaineer,49");
		modelosDummy.add("Mystique,49");
		modelosDummy.add("Sable,49");
		modelosDummy.add("Topaz,49");
		modelosDummy.add("Tracer,49");
		modelosDummy.add("Villager,49");
		modelosDummy.add("XR4Ti,50");
		modelosDummy.add("MGB,51");
		modelosDummy.add("Clubman,52");
		modelosDummy.add("Cooper,52");
		modelosDummy.add("Cooper Clubman,52");
		modelosDummy.add("Cooper Countryman,52");
		modelosDummy.add("Countryman,52");
		modelosDummy.add("MINI,52");
		modelosDummy.add("3000GT,53");
		modelosDummy.add("Challenger,53");
		modelosDummy.add("Chariot,53");
		modelosDummy.add("Cordia,53");
		modelosDummy.add("Diamante,53");
		modelosDummy.add("Eclipse,53");
		modelosDummy.add("Endeavor,53");
		modelosDummy.add("Excel,53");
		modelosDummy.add("Expo,53");
		modelosDummy.add("Expo LRV,53");
		modelosDummy.add("Galant,53");
		modelosDummy.add("GTO,53");
		modelosDummy.add("i-MiEV,53");
		modelosDummy.add("L300,53");
		modelosDummy.add("Lancer,53");
		modelosDummy.add("Lancer Evolution,53");
		modelosDummy.add("Mighty Max,53");
		modelosDummy.add("Mighty Max Macro,53");
		modelosDummy.add("Mirage,53");
		modelosDummy.add("Montero,53");
		modelosDummy.add("Montero Sport,53");
		modelosDummy.add("Outlander,53");
		modelosDummy.add("Outlander Sport,53");
		modelosDummy.add("Pajero,53");
		modelosDummy.add("Precis,53");
		modelosDummy.add("Raider,53");
		modelosDummy.add("RVR,53");
		modelosDummy.add("Sigma,53");
		modelosDummy.add("Space,53");
		modelosDummy.add("Starion,53");
		modelosDummy.add("Tredia,53");
		modelosDummy.add("Truck,53");
		modelosDummy.add("Tundra,53");
		modelosDummy.add("Aero 8,54");
		modelosDummy.add("200SX,55");
		modelosDummy.add("240SX,55");
		modelosDummy.add("280ZX,55");
		modelosDummy.add("300ZX,55");
		modelosDummy.add("350Z,55");
		modelosDummy.add("350Z Roadster,55");
		modelosDummy.add("370Z,55");
		modelosDummy.add("Altima,55");
		modelosDummy.add("Armada,55");
		modelosDummy.add("Cube,55");
		modelosDummy.add("Datsun/Nissan Z-car,55");
		modelosDummy.add("Frontier,55");
		modelosDummy.add("GT-R,55");
		modelosDummy.add("JUKE,55");
		modelosDummy.add("Leaf,55");
		modelosDummy.add("Maxima,55");
		modelosDummy.add("Murano,55");
		modelosDummy.add("NV1500,55");
		modelosDummy.add("NV2500,55");
		modelosDummy.add("NV3500,55");
		modelosDummy.add("NX,55");
		modelosDummy.add("Pathfinder,55");
		modelosDummy.add("Pathfinder Armada,55");
		modelosDummy.add("Quest,55");
		modelosDummy.add("Rogue,55");
		modelosDummy.add("Sentra,55");
		modelosDummy.add("Stanza,55");
		modelosDummy.add("Titan,55");
		modelosDummy.add("Versa,55");
		modelosDummy.add("Xterra,55");
		modelosDummy.add("88,56");
		modelosDummy.add("98,56");
		modelosDummy.add("Achieva,56");
		modelosDummy.add("Alero,56");
		modelosDummy.add("Aurora,56");
		modelosDummy.add("Bravada,56");
		modelosDummy.add("Ciera,56");
		modelosDummy.add("Custom Cruiser,56");
		modelosDummy.add("Cutlass,56");
		modelosDummy.add("Cutlass Cruiser,56");
		modelosDummy.add("Cutlass Supreme,56");
		modelosDummy.add("Intrigue,56");
		modelosDummy.add("LSS,56");
		modelosDummy.add("Regency,56");
		modelosDummy.add("Silhouette,56");
		modelosDummy.add("Toronado,56");
		modelosDummy.add("Esperante,57");
		modelosDummy.add("207,58");
		modelosDummy.add("Acclaim,59");
		modelosDummy.add("Breeze,59");
		modelosDummy.add("Colt,59");
		modelosDummy.add("Colt Vista,59");
		modelosDummy.add("Fury,59");
		modelosDummy.add("Grand Voyager,59");
		modelosDummy.add("Horizon,59");
		modelosDummy.add("Laser,59");
		modelosDummy.add("Neon,59");
		modelosDummy.add("Prowler,59");
		modelosDummy.add("Reliant,59");
		modelosDummy.add("Roadrunner,59");
		modelosDummy.add("Sundance,59");
		modelosDummy.add("Volare,59");
		modelosDummy.add("Voyager,59");
		modelosDummy.add("1000,60");
		modelosDummy.add("6000,60");
		modelosDummy.add("Aztek,60");
		modelosDummy.add("Bonneville,60");
		modelosDummy.add("Chevette,60");
		modelosDummy.add("Daewoo Kalos,60");
		modelosDummy.add("Fiero,60");
		modelosDummy.add("Firebird,60");
		modelosDummy.add("Firebird Formula,60");
		modelosDummy.add("Firebird Trans Am,60");
		modelosDummy.add("Firefly,60");
		modelosDummy.add("G3,60");
		modelosDummy.add("G5,60");
		modelosDummy.add("G6,60");
		modelosDummy.add("G8,60");
		modelosDummy.add("Gemini,60");
		modelosDummy.add("Grand Am,60");
		modelosDummy.add("Grand Prix,60");
		modelosDummy.add("Grand Prix Turbo,60");
		modelosDummy.add("GTO,60");
		modelosDummy.add("LeMans,60");
		modelosDummy.add("Montana,60");
		modelosDummy.add("Montana SV6,60");
		modelosDummy.add("Monterey,60");
		modelosDummy.add("Parisienne,60");
		modelosDummy.add("Safari,60");
		modelosDummy.add("Solstice,60");
		modelosDummy.add("Sunbird,60");
		modelosDummy.add("Sunfire,60");
		modelosDummy.add("Tempest,60");
		modelosDummy.add("Torrent,60");
		modelosDummy.add("Trans Sport,60");
		modelosDummy.add("Turbo Firefly,60");
		modelosDummy.add("Vibe,60");
		modelosDummy.add("911,61");
		modelosDummy.add("914,61");
		modelosDummy.add("924,61");
		modelosDummy.add("924 S,61");
		modelosDummy.add("928,61");
		modelosDummy.add("944,61");
		modelosDummy.add("968,61");
		modelosDummy.add("Boxster,61");
		modelosDummy.add("Carrera GT,61");
		modelosDummy.add("Cayenne,61");
		modelosDummy.add("Cayman,61");
		modelosDummy.add("Panamera,61");
		modelosDummy.add("1500,62");
		modelosDummy.add("2500,62");
		modelosDummy.add("3500,62");
		modelosDummy.add("C/V,62");
		modelosDummy.add("Dakota,62");
		modelosDummy.add("Classic,63");
		modelosDummy.add("Alliance,64");
		modelosDummy.add("Ghost,65");
		modelosDummy.add("Phantom,65");
		modelosDummy.add("9-2X,66");
		modelosDummy.add("09-mar,66");
		modelosDummy.add("9-4X,66");
		modelosDummy.add("09-may,66");
		modelosDummy.add("9-7X,66");
		modelosDummy.add("900,66");
		modelosDummy.add("9000,66");
		modelosDummy.add("Astra,67");
		modelosDummy.add("Aura,67");
		modelosDummy.add("Ion,67");
		modelosDummy.add("L-Series,67");
		modelosDummy.add("Outlook,67");
		modelosDummy.add("Relay,67");
		modelosDummy.add("S-Series,67");
		modelosDummy.add("Sky,67");
		modelosDummy.add("VUE,67");
		modelosDummy.add("FR-S,68");
		modelosDummy.add("iQ,68");
		modelosDummy.add("tC,68");
		modelosDummy.add("xA,68");
		modelosDummy.add("xB,68");
		modelosDummy.add("xD,68");
		modelosDummy.add("GT350,69");
		modelosDummy.add("GT500,69");
		modelosDummy.add("Fortwo,70");
		modelosDummy.add("C8 Double 12 S,71");
		modelosDummy.add("C8 Laviolette,71");
		modelosDummy.add("C8 Spyder,71");
		modelosDummy.add("C8 Spyder Wide Body,71");
		modelosDummy.add("C8,72");
		modelosDummy.add("Avanti,73");
		modelosDummy.add("Alcyone SVX,74");
		modelosDummy.add("B9 Tribeca,74");
		modelosDummy.add("Baja,74");
		modelosDummy.add("Brat,74");
		modelosDummy.add("BRZ,74");
		modelosDummy.add("Forester,74");
		modelosDummy.add("Impreza,74");
		modelosDummy.add("Impreza WRX,74");
		modelosDummy.add("Justy,74");
		modelosDummy.add("Legacy,74");
		modelosDummy.add("Leone,74");
		modelosDummy.add("Loyale,74");
		modelosDummy.add("Outback,74");
		modelosDummy.add("Outback Sport,74");
		modelosDummy.add("SVX,74");
		modelosDummy.add("Tribeca,74");
		modelosDummy.add("XT,74");
		modelosDummy.add("Aerio,75");
		modelosDummy.add("Cultus,75");
		modelosDummy.add("Daewoo Lacetti,75");
		modelosDummy.add("Daewoo Magnus,75");
		modelosDummy.add("Equator,75");
		modelosDummy.add("Esteem,75");
		modelosDummy.add("Forenza,75");
		modelosDummy.add("Grand Vitara,75");
		modelosDummy.add("Kizashi,75");
		modelosDummy.add("Reno,75");
		modelosDummy.add("Samurai,75");
		modelosDummy.add("Sidekick,75");
		modelosDummy.add("SJ,75");
		modelosDummy.add("SJ 410,75");
		modelosDummy.add("Swift,75");
		modelosDummy.add("SX4,75");
		modelosDummy.add("Verona,75");
		modelosDummy.add("Vitara,75");
		modelosDummy.add("X-90,75");
		modelosDummy.add("XL-7,75");
		modelosDummy.add("XL7,75");
		modelosDummy.add("Model S,76");
		modelosDummy.add("Roadster,76");
		modelosDummy.add("4Runner,77");
		modelosDummy.add("Avalon,77");
		modelosDummy.add("Camry,77");
		modelosDummy.add("Camry Hybrid,77");
		modelosDummy.add("Camry Solara,77");
		modelosDummy.add("Celica,77");
		modelosDummy.add("Corolla,77");
		modelosDummy.add("Cressida,77");
		modelosDummy.add("Echo,77");
		modelosDummy.add("FJ Cruiser,77");
		modelosDummy.add("Highlander,77");
		modelosDummy.add("Highlander Hybrid,77");
		modelosDummy.add("Ipsum,77");
		modelosDummy.add("Land Cruiser,77");
		modelosDummy.add("Matrix,77");
		modelosDummy.add("MR2,77");
		modelosDummy.add("Paseo,77");
		modelosDummy.add("Previa,77");
		modelosDummy.add("Prius,77");
		modelosDummy.add("Prius c,77");
		modelosDummy.add("Prius Plug-in,77");
		modelosDummy.add("Prius Plug-in Hybrid,77");
		modelosDummy.add("Prius v,77");
		modelosDummy.add("RAV4,77");
		modelosDummy.add("Sequoia,77");
		modelosDummy.add("Sienna,77");
		modelosDummy.add("Solara,77");
		modelosDummy.add("Supra,77");
		modelosDummy.add("T100,77");
		modelosDummy.add("T100 Xtra,77");
		modelosDummy.add("Tacoma,77");
		modelosDummy.add("Tacoma Xtra,77");
		modelosDummy.add("Tercel,77");
		modelosDummy.add("Truck Xtracab SR5,77");
		modelosDummy.add("Tundra,77");
		modelosDummy.add("TundraMax,77");
		modelosDummy.add("Venza,77");
		modelosDummy.add("Xtra,77");
		modelosDummy.add("Yaris,77");
		modelosDummy.add("Beetle,78");
		modelosDummy.add("Cabriolet,78");
		modelosDummy.add("CC,78");
		modelosDummy.add("Corrado,78");
		modelosDummy.add("Eos,78");
		modelosDummy.add("Eurovan,78");
		modelosDummy.add("Fox,78");
		modelosDummy.add("GLI,78");
		modelosDummy.add("Golf,78");
		modelosDummy.add("Golf III,78");
		modelosDummy.add("GTI,78");
		modelosDummy.add("Jetta,78");
		modelosDummy.add("Jetta III,78");
		modelosDummy.add("New Beetle,78");
		modelosDummy.add("Passat,78");
		modelosDummy.add("Phaeton,78");
		modelosDummy.add("Quantum,78");
		modelosDummy.add("R32,78");
		modelosDummy.add("Rabbit,78");
		modelosDummy.add("rio,78");
		modelosDummy.add("riolet,78");
		modelosDummy.add("Routan,78");
		modelosDummy.add("Scirocco,78");
		modelosDummy.add("Tiguan,78");
		modelosDummy.add("Touareg,78");
		modelosDummy.add("Touareg 2,78");
		modelosDummy.add("Type 2,78");
		modelosDummy.add("Vanagon,78");
		modelosDummy.add("240,79");
		modelosDummy.add("740,79");
		modelosDummy.add("850,79");
		modelosDummy.add("940,79");
		modelosDummy.add("960,79");
		modelosDummy.add("C30,79");
		modelosDummy.add("C70,79");
		modelosDummy.add("S40,79");
		modelosDummy.add("S60,79");
		modelosDummy.add("S70,79");
		modelosDummy.add("S80,79");
		modelosDummy.add("S90,79");
		modelosDummy.add("V40,79");
		modelosDummy.add("V50,79");
		modelosDummy.add("V70,79");
		modelosDummy.add("V90,79");
		modelosDummy.add("XC60,79");
		modelosDummy.add("XC70,79");
		modelosDummy.add("XC90,79");
		
		List<ModeloAuto> modelosResult = new ArrayList<ModeloAuto>();
		ModeloAuto modelo = null;
		
		for(int i =0; i< modelosDummy.size(); i++) {
			String[] dataDummy =  modelosDummy.get(i).split(",");
			
			String nombre = dataDummy[0];
			int idMarca = Integer.parseInt(dataDummy[1]);
			
			modelo = new ModeloAuto();
			modelo.setId((i+1));
			modelo.setIdMarca(idMarca);
			modelo.setNombre(nombre);
			
			modelosResult.add(modelo);
		}

		return modelosResult;
	}
}
