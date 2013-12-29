package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.Service;
import itti.com.pl.arena.cm.dto.Building;
import itti.com.pl.arena.cm.dto.Camera;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Infrastructure;
import itti.com.pl.arena.cm.dto.Parking;
import itti.com.pl.arena.cm.dto.PlatformInformation;
import itti.com.pl.arena.cm.dto.RelativePosition;
import itti.com.pl.arena.cm.geoportal.govpl.GeoportalKeys;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.ontology.Constants.ContextModuleConstants;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NumbersHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanInitializationException;

public class ContextModuleOntologyManager extends OntologyManager implements Ontology, Service{

	private double locationDelta = 0.02;

	public void setLocationDelta(double delta)
	{
		this.locationDelta = delta;
	}

	private double getLocationDelta()
	{
		return locationDelta;
	}
	@Override
	public void init(){
		try {
			super.init();

		} catch (Exception e) {
			LogHelper.exception(ContextModuleOntologyManager.class, "init", "Coulnd not initialize component", e);
			throw new BeanInitializationException("Could not initialize ontology component", e);
		}
	}

	@Override
	public void shutdown() {
	}

	@Override
	public PlatformInformation getPlatformInformation(String platformId) throws OntologyException {

		Map<String, String[]> properties = getInstanceProperties(platformId);

		PlatformInformation information = new PlatformInformation(platformId, null, null);

		String[] cameras = properties.get(ContextModuleConstants.Vehicle_has_camera.name());
		if(cameras != null){
			for (String cameraId : cameras) {
				
				Map<String, String[]> cameraInstance = getInstanceProperties(cameraId);
				if(!cameraInstance.isEmpty()){
					String[] cameraType = cameraInstance.get(ContextModuleConstants.Camera_type.name());
					String[] angleX = cameraInstance.get(ContextModuleConstants.Camera_has_angle_x.name());
					String[] angleY = cameraInstance.get(ContextModuleConstants.Camera_has_angle_y.name());
					String[] view = cameraInstance.get(ContextModuleConstants.Camera_view.name());

					Double angleXVal = NumbersHelper.getDoubleFromString(angleX != null && angleX.length > 0 ? angleX[0] : null);
					Double angleYVal = NumbersHelper.getDoubleFromString(angleY != null && angleY.length > 0 ? angleY[0] : null);
					RelativePosition position = RelativePosition.getPostion(view != null && view.length > 0 ? view[0] : null);
					
					Camera cameraInfo = new Camera(cameraId, platformId,
							cameraType == null || cameraType.length == 0 ? null : cameraType[0],
							angleXVal == null ? 0 : angleXVal, 
							angleYVal == null ? 0 : angleYVal, 
							position
					);
					information.addCamera(cameraInfo);
				}
			}
		}
		String[] bearing = properties.get(ContextModuleConstants.Object_has_GPS_bearing.name());
		Integer bearingVal = NumbersHelper.getIntegerFromString(bearing != null && bearing.length > 0 ? bearing[0] : null);
		information.addBearing(bearingVal);

		return information;
	}

	public List<String> getPlatforms(double x, double y) throws OntologyException {

		List<String> resultList = new ArrayList<String>();

		String queryPattern = "PREFIX ns: <%s> "
				+ "SELECT ?%s "
				+ "WHERE "
				+ "{ "
				+ "?%s rdf:type ?subclass. "
				+ "?subclass rdfs:subClassOf ns:%s. "
				+ "?%s ns:Platform_has_GPS_coordinates ?coordinate. "
				+ "FILTER ( (?coordinate >= %f && ?coordinate <= %f) || (?coordinate >= %f && ?coordinate <= %f)) "
				+ "} ";
		String query = String.format(queryPattern, getOntologyNamespace(), 
				var, var, 
				ContextModuleConstants.Vehicle.name(), var, 
				x-getLocationDelta(), x+getLocationDelta(), y-getLocationDelta(), y+getLocationDelta());

		//execute the query
		List<String> matches = executeSparqlQuery(query, var);
		//filter query results: only doubles should be returned (x and y match)
		for(int i=0 ; i<matches.size() ; i++){
			String match = matches.get(i);
			// if last occurrence != current one add to the results list (if not added so far)
			if(matches.lastIndexOf(match) != i && !resultList.contains(match))
			{
				resultList.add(match);
			}
		}
		return resultList;
	}

	@Override
	public GeoObject getGISObject(String objectId)
			throws OntologyException {

		Map<String, String[]> properties = getInstanceProperties(objectId);

		Parking information = new Parking();
		information.setId(objectId);

		String[] infrastructureList = properties.get(ContextModuleConstants.Parking_has_infrastructure.name());
		if(infrastructureList != null){
			for (String infrastrId : infrastructureList) {
				
				Map<String, String[]> infrastrProperties = getInstanceProperties(infrastrId);
				if(!infrastrProperties.isEmpty()){
					String[] coordinates = infrastrProperties.get(ContextModuleConstants.Object_has_GPS_coordinates.name());
					GeoObject infrastructure = new Infrastructure();
					infrastructure.setId(infrastrId);
					infrastructure.setGpsCoordinates(coordinates);
					information.addStaticObject(infrastructure);
				}
			}
		}
		
		String[] buildingList = properties.get(ContextModuleConstants.Parking_has_building.name());
		if(buildingList != null){
			for (String buildingId : buildingList) {

				Map<String, String[]> buildingProperties = getInstanceProperties(buildingId);
				if(!buildingProperties.isEmpty()){
					String[] coordinates = buildingProperties.get(ContextModuleConstants.Object_has_GPS_coordinates.name());
					GeoObject building = new Building();
					building.setId(buildingId);
					building.setGpsCoordinates(coordinates);
					information.addStaticObject(building);
				}
			}
		}

		return information;
	}

	@Override
	public List<String> getGISObjects(double x, double y)
			throws OntologyException {

		List<String> resultList = new ArrayList<String>();

		String queryPattern = "PREFIX ns: <%s> "
				+ "SELECT ?%s "
				+ "WHERE "
				+ "{ "
				+ "?%s rdf:type ns:%s. "
				+ "?%s ns:Parking_has_GPS_coordinates ?coordinate. "
				+ "FILTER ( (?coordinate >= %f && ?coordinate <= %f) || (?coordinate >= %f && ?coordinate <= %f)) "
				+ "} ";
		String query = String.format(queryPattern, getOntologyNamespace(), 
				var, var, 
				ContextModuleConstants.Parking.name(), var, 
				x-getLocationDelta(), x+getLocationDelta(), y-getLocationDelta(), y+getLocationDelta());

		//execute the query
		List<String> matches = executeSparqlQuery(query, var);
		//filter query results: only doubles should be returned (x and y match)
		for(int i=0 ; i<matches.size() ; i++){
			String match = matches.get(i);
			// if last occurrence != current one add to the results list (if not added so far)
			if(matches.lastIndexOf(match) != i && !resultList.contains(match))
			{
				resultList.add(match);
			}
		}
		return resultList;
	}

	@Override
	public void addGeoportalData(double x, double y, GeoportalResponse geoportalData) {
		
		if(geoportalData != null){
			for (String layerId  : geoportalData.getLayersIds()) {
				for(int itemId = 0 ; itemId < geoportalData.getLayerElements(layerId) ; itemId++){
					String layerName = geoportalData.getValue(layerId, itemId, GeoportalKeys.LayerName);
					String ontologyClass = GeoportalKeys.getOntlogyClass(layerName);
					if(StringHelper.hasContent(ontologyClass)){
						Map<String, String[]> properties = new HashMap<String, String[]>();
						properties.put(Constants.ContextModuleConstants.Object_has_GPS_coordinates.name(), new String[]{String.format("%d,%d", x, y)});
						createSimpleInstance(ontologyClass, String.format("%s-%f-%f", ontologyClass, x, y), properties);
					}
				}
			}
		}
	}
}
