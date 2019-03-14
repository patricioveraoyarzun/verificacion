package arg.mps.seguros.broker.dao;

import java.util.List;

import arg.mps.seguros.broker.api.Comuna;
import arg.mps.seguros.broker.api.MarcaAuto;
import arg.mps.seguros.broker.api.ModeloAuto;
import arg.mps.seguros.broker.api.Region;

public interface DatoParametricoDao {
	List<Region> getRegiones();
	List<Comuna> getComunasPorRegion(int idRegion);
	List<MarcaAuto> getMarcasAuto();
	List<ModeloAuto> getModelosAutoPorMarca(int idMarcaAuto);
}
