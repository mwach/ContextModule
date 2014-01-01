package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.dto.Camera;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Infrastructure;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.Parking;
import itti.com.pl.arena.cm.dto.Platform;
import itti.com.pl.arena.cm.dto.RelativePosition;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalKeys;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.ontology.Constants.ContextModuleConstants;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NumbersHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;

/**
 * Extension of the {@link OntologyManager} providing ContextManager-specific services
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleOntologyManager extends OntologyManager implements Ontology {

    private static final String QUERY_GET_PLATFORMS = 
	 "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ " + "?%s rdf:type ?subclass. "
	        + "?subclass rdfs:subClassOf ns:%s. " + "?%s ns:Platform_has_GPS_coordinates ?coordinate. "
	        + "FILTER ( (?coordinate >= %f && ?coordinate <= %f) || (?coordinate >= %f && ?coordinate <= %f)) " + "}";

    private static final String QUERY_GIS_OBJECTS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ " + "?%s rdf:type ns:%s. "
	        + "?%s ns:Parking_has_GPS_coordinates ?coordinate. "
	        + "FILTER ( (?coordinate >= %f && ?coordinate <= %f) || (?coordinate >= %f && ?coordinate <= %f)) " + "}";

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatformInformation(java.lang.String)
     */
    @Override
    public Platform getPlatform(String platformId) throws OntologyException {

	// prepare data object
	String platformType = getInstanceClass(platformId);
	Platform information = PlatformFactory.getPlatform(platformType, platformId);

	// get information about the platform from ontology
	Map<String, String[]> properties = getInstanceProperties(platformId);

	// get information about cameras installed on platform
	String[] cameras = properties.get(ContextModuleConstants.Vehicle_has_cameras.name());
	if (cameras != null) {
	    for (String cameraId : cameras) {
		Camera cameraInfo = getCameraInformation(cameraId);
		information.addCamera(cameraInfo);

	    }
	}
	//TODO
	information.setLastPosition(new Location(0, 0, getIntProperty(properties, ContextModuleConstants.Object_has_GPS_bearing)));

	return information;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatformInformation(java.lang.String)
     */
    @Override
    public void updatePlatform(Platform platform) throws OntologyException {

	Map<String, String[]> properties = new HashMap<>();
	// TODO
	if (platform.getLastLocation() != null) {
	    platform.getLastLocation().getLatitude();
	    platform.getLastLocation().getLatitude();
	    properties.put(ContextModuleConstants.Object_has_GPS_bearing.name(),
		    new String[] { String.valueOf(platform.getLastLocation().getBearing()) });
	}

	// process cameras
	if (platform.getCameras() != null) {
	    String[] cameraIds = new String[platform.getCameras().size()];
	    int currentCamera = 0;
	    for (Entry<String, Camera> camera : platform.getCameras().entrySet()) {
		updateCameraInformation(camera.getValue());
		cameraIds[currentCamera++] = camera.getKey();
	    }
	    properties.put(ContextModuleConstants.Vehicle_has_cameras.name(), cameraIds);
	}
	// create instance in the ontology
	createSimpleInstance(platform.getType().name(), platform.getId(), properties);
    }

    /**
     * Retrieves information about camera from ontology
     * 
     * @param cameraId
     *            ID of the camera
     * @return camera object
     * @throws OntologyException
     */
    private Camera getCameraInformation(String cameraId) throws OntologyException {

	Camera cameraInfo = null;

	Map<String, String[]> cameraInstance = getInstanceProperties(cameraId);
	if (!cameraInstance.isEmpty()) {
	    String cameraType = getStringProperty(cameraInstance, ContextModuleConstants.Camera_has_type);
	    Double angleXVal = getDoubleProperty(cameraInstance, ContextModuleConstants.Camera_has_angle_x);
	    Double angleYVal = getDoubleProperty(cameraInstance, ContextModuleConstants.Camera_has_angle_y);
	    RelativePosition position = getRelativePosition(cameraInstance, ContextModuleConstants.Camera_view);

	    cameraInfo = new Camera(cameraId, cameraType, angleXVal == null ? 0 : angleXVal.doubleValue(), angleYVal == null ? 0
		    : angleYVal.doubleValue(), position);
	}
	return cameraInfo;
    }

    private OWLIndividual updateCameraInformation(Camera camera) throws OntologyException {

	Map<String, String[]> properties = new HashMap<>();
	properties.put(ContextModuleConstants.Camera_has_type.name(), new String[] { camera.getType() });
	properties.put(ContextModuleConstants.Camera_has_angle_x.name(), new String[] { String.valueOf(camera.getAngleX()) });
	properties.put(ContextModuleConstants.Camera_has_angle_y.name(), new String[] { String.valueOf(camera.getAngleY()) });
	properties.put(ContextModuleConstants.Camera_view.name(), new String[] { camera.getPosition().name() });
	return createSimpleInstance(ContextModuleConstants.Camera.name(), camera.getId(), properties);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatforms(double, double, double)
     */
    public List<String> getPlatforms(double x, double y, double radius) throws OntologyException {

	List<String> resultList = new ArrayList<String>();

	String queryPattern = QUERY_GET_PLATFORMS;
	String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, ContextModuleConstants.Vehicle.name(), VAR,
	        x - radius, x + radius, y - radius, y + radius);

	// execute the query
	List<String> matches = executeSparqlQuery(query, VAR);
	// filter query results: only doubles should be returned (x and y match)
	for (int i = 0; i < matches.size(); i++) {
	    String match = matches.get(i);
	    // if last occurrence != current one add to the results list (if not added so far)
	    if (matches.lastIndexOf(match) != i && !resultList.contains(match)) {
		resultList.add(match);
	    }
	}
	return resultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObject(java.lang.String)
     */
    @Override
    public GeoObject getGISObject(String objectId) throws OntologyException {

	Map<String, String[]> properties = getInstanceProperties(objectId);

	Parking information = new Parking();
	information.setId(objectId);

	String[] infrastructureList = properties.get(ContextModuleConstants.Parking_has_infrastructure.name());
	if (infrastructureList != null) {
	    for (String infrastrId : infrastructureList) {

		Map<String, String[]> infrastrProperties = getInstanceProperties(infrastrId);
		if (!infrastrProperties.isEmpty()) {
		    String[] coordinates = infrastrProperties.get(ContextModuleConstants.Object_has_GPS_coordinates.name());
		    GeoObject infrastructure = new Infrastructure();
		    infrastructure.setId(infrastrId);
		    infrastructure.setGpsCoordinates(coordinates);
		    information.addStaticObject(infrastructure);
		}
	    }
	}

	return information;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObjects(double, double, double)
     */
    @Override
    public List<String> getGISObjects(double x, double y, double radius) throws OntologyException {

	List<String> resultList = new ArrayList<String>();

	String queryPattern = QUERY_GIS_OBJECTS;
	String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, ContextModuleConstants.Parking.name(), VAR,
	        x - radius, x + radius, y - radius, y + radius);

	// execute the query
	List<String> matches = executeSparqlQuery(query, VAR);
	// filter query results: only doubles should be returned (x and y match)
	for (int i = 0; i < matches.size(); i++) {
	    String match = matches.get(i);
	    // if last occurrence != current one add to the results list (if not added so far)
	    if (matches.lastIndexOf(match) != i && !resultList.contains(match)) {
		resultList.add(match);
	    }
	}
	return resultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#addGeoportalData(double, double,
     * itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse)
     */
    @Override
    public void addGeoportalData(double x, double y, GeoportalResponse geoportalData) {

	if (geoportalData != null) {
	    for (String layerId : geoportalData.getLayersIds()) {
		for (int itemId = 0; itemId < geoportalData.getLayerElements(layerId); itemId++) {
		    String layerName = geoportalData.getValue(layerId, itemId, GeoportalKeys.LayerName);
		    String ontologyClass = GeoportalKeys.getOntlogyClass(layerName);
		    if (StringHelper.hasContent(ontologyClass)) {
			Map<String, String[]> properties = new HashMap<String, String[]>();
			properties.put(Constants.ContextModuleConstants.Object_has_GPS_coordinates.name(),
			        new String[] { String.format("%f,%f", x, y) });
			String instanceName = String.format("%s-%f-%f", ontologyClass, x, y);
			try {
			    createSimpleInstance(ontologyClass, instanceName, properties);
			} catch (OntologyException exc) {
			    LogHelper.warning(ContextModuleOntologyManager.class, "addGeoportalData",
				    "Could not add geoportal data for %s", instanceName);
			}
		    }
		}
	    }
	}
    }

    private String getStringProperty(Map<String, String[]> properties, ContextModuleConstants propertyName) {

	if (properties == null || propertyName == null) {
	    return null;
	}
	String[] values = properties.get(propertyName.name());
	String stringValue = (values != null && values.length > 0 ? values[0] : null);
	return stringValue;
    }

    private Integer getIntProperty(Map<String, String[]> properties, ContextModuleConstants propertyName) {
	return NumbersHelper.getIntegerFromString(getStringProperty(properties, propertyName));
    }

    private Double getDoubleProperty(Map<String, String[]> properties, ContextModuleConstants propertyName) {
	return NumbersHelper.getDoubleFromString(getStringProperty(properties, propertyName));
    }

    private RelativePosition getRelativePosition(Map<String, String[]> properties, ContextModuleConstants propertyName) {
	return RelativePosition.getPostion(getStringProperty(properties, propertyName));
    }

}
