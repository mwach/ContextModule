package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.dto.Camera;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Infrastructure;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.Parking;
import itti.com.pl.arena.cm.dto.Platform;
import itti.com.pl.arena.cm.dto.RelativePosition;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalKeys;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.ontology.Constants.OntologyConstants;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NumbersHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;

/**
 * Extension of the {@link OntologyManager} providing ContextManager-specific services
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleOntologyManager extends OntologyManager implements Ontology {

    private static final String QUERY_GET_PLATFORMS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
	    + "?%s rdf:type ?subclass. " + "?subclass rdfs:subClassOf ns:%s. "
	    + "?%s ns:Platform_has_GPS_coordinates ?coordinate. "
	    + "FILTER ( (?coordinate >= %f && ?coordinate <= %f) || (?coordinate >= %f && ?coordinate <= %f)) " + "}";

    private static final String QUERY_PARKING_OBJECTS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
	    + "?%s rdf:type ns:%s. " + "?%s ns:Parking_has_GPS_y ?coordinate_y. " + "?%s ns:Parking_has_GPS_x ?coordinate_x. "
	    + "FILTER ( (?coordinate_y >= %f && ?coordinate_y <= %f) || (?coordinate_x >= %f && ?coordinate_x <= %f)) " + "}";

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
	String[] cameras = properties.get(OntologyConstants.Vehicle_has_cameras.name());
	if (cameras != null) {
	    for (String cameraId : cameras) {
		Camera cameraInfo = getCameraInformation(cameraId);
		information.addCamera(cameraInfo);

	    }
	}
	Location lastLocation = prepareLastLocation(properties);
	information.setLastPosition(lastLocation);

	return information;
    }

    private Location prepareLastLocation(Map<String, String[]> properties) {
	Double longitude = getDoubleProperty(properties, OntologyConstants.Vehicle_has_GPS_x);
	Double latitude = getDoubleProperty(properties, OntologyConstants.Vehicle_has_GPS_y);
	Integer bearing = getIntProperty(properties, OntologyConstants.Object_has_GPS_bearing);
	return new Location(longitude == null ? 0 : longitude.doubleValue(), latitude == null ? 0 : latitude.doubleValue(),
	        bearing == null ? 0 : bearing.intValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatformInformation(java.lang.String)
     */
    @Override
    public void updatePlatform(Platform platform) throws OntologyException {

	Map<String, String[]> properties = new HashMap<>();
	if (platform.getLastLocation() != null) {
	    properties.put(OntologyConstants.Vehicle_has_GPS_x.name(),
		    new String[] { String.valueOf(platform.getLastLocation().getLongitude()) });
	    properties.put(OntologyConstants.Vehicle_has_GPS_y.name(),
		    new String[] { String.valueOf(platform.getLastLocation().getLatitude()) });
	    properties.put(OntologyConstants.Object_has_GPS_bearing.name(),
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
	    properties.put(OntologyConstants.Vehicle_has_cameras.name(), cameraIds);
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
	    String cameraType = getStringProperty(cameraInstance, OntologyConstants.Camera_has_type);
	    Double angleXVal = getDoubleProperty(cameraInstance, OntologyConstants.Camera_has_angle_x);
	    Double angleYVal = getDoubleProperty(cameraInstance, OntologyConstants.Camera_has_angle_y);
	    RelativePosition position = getRelativePosition(cameraInstance, OntologyConstants.Camera_view);

	    cameraInfo = new Camera(cameraId, cameraType, angleXVal == null ? 0 : angleXVal.doubleValue(), angleYVal == null ? 0
		    : angleYVal.doubleValue(), position);
	}
	return cameraInfo;
    }

    private OWLIndividual updateCameraInformation(Camera camera) throws OntologyException {

	Map<String, String[]> properties = new HashMap<>();
	properties.put(OntologyConstants.Camera_has_type.name(), new String[] { camera.getType() });
	properties.put(OntologyConstants.Camera_has_angle_x.name(), new String[] { String.valueOf(camera.getAngleX()) });
	properties.put(OntologyConstants.Camera_has_angle_y.name(), new String[] { String.valueOf(camera.getAngleY()) });
	properties.put(OntologyConstants.Camera_view.name(), new String[] { camera.getPosition().name() });
	return createSimpleInstance(OntologyConstants.Camera.name(), camera.getId(), properties);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatforms(double, double, double)
     */
    public Set<String> getPlatforms(double x, double y, double radius) throws OntologyException {

	Set<String> resultList = new HashSet<String>();

	String queryPattern = QUERY_GET_PLATFORMS;
	String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, OntologyConstants.Vehicle.name(), VAR,
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

	String[] infrastructureList = properties.get(OntologyConstants.Parking_has_infrastructure.name());
	if (infrastructureList != null) {
	    for (String infrastrId : infrastructureList) {

		Map<String, String[]> infrastrProperties = getInstanceProperties(infrastrId);
		if (!infrastrProperties.isEmpty()) {
		    String[] coordinates = infrastrProperties.get(OntologyConstants.Object_has_GPS_coordinates.name());
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
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObjects(Location, double)
     */
    @Override
    public Set<String> getParkingLots(Location location, double radius) throws OntologyException {
	if (location == null) {
	    LogHelper.warning(ContextModuleOntologyManager.class, "getGISObjects", "Null location provided");
	    throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_LOCATION_OBJECT);
	}
	return getParkingLots(location.getLongitude(), location.getLatitude(), radius);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObjects(String, String...)
     */
    @Override
    public Set<String> getParkingLotInfrastructure(String parkingId, String... classFilters) throws OntologyException {

	LogHelper.debug(ContextModuleOntologyManager.class, "getParkingLotObjects", "get parking objects for '%s'", String.valueOf(parkingId));
	if (!StringHelper.hasContent(parkingId)) {
	    LogHelper.warning(ContextModuleOntologyManager.class, "getParkingLotObjects", "Null parkingId provided");
	    throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_PARKING_ID_OBJECT);
	}
	// check, if parking is defined in the ontology
	String parkingClass = getInstanceClass(parkingId);
	// and it's member of valid class
	if(!StringHelper.equals(OntologyConstants.Parking.name(), parkingClass)){
	    LogHelper.warning(ContextModuleOntologyManager.class, "getParkingLotObjects", "Provided object '%s' is not a member of Parking class", parkingId);
	    throw new OntologyException(ErrorMessages.ONTOLOGY_INSTANCE_IS_NOT_A_PARKING, parkingId);	    
	}

	// get information about parking lot infrastructure
	Map<String, String[]> properties = getInstanceProperties(parkingId);
	//prepare the response
	Set<String> parkingObjects = new HashSet<>();
	//add infrastructure objects
	if(properties.containsKey(OntologyConstants.Parking_has_infrastructure.name())){
	    parkingObjects.addAll(Arrays.asList(properties.get(OntologyConstants.Parking_has_infrastructure.name())));
	}
	//add buildings objects
	if(properties.containsKey(OntologyConstants.Parking_has_building.name())){
	    parkingObjects.addAll(Arrays.asList(properties.get(OntologyConstants.Parking_has_building.name())));
	}
	//apply filters (if defined)
	if(classFilters != null && classFilters.length > 0){
	    Set<String> filters = new HashSet<>(Arrays.asList(classFilters));
	    Set<String> filteredParkingObjects = new HashSet<>();
	    for (String parkingObject : parkingObjects) {
	        if(
	        	filters.contains(getInstanceClass(parkingObject)) ||
	        	filters.contains(getInstanceGrandClass(parkingObject))
	        	)
	        {
	            filteredParkingObjects.add(parkingObject);
	        }
            }
	    parkingObjects = filteredParkingObjects;
	}
	LogHelper.debug(ContextModuleOntologyManager.class, "getParkingLotObjects", "returning %d objects: %s", parkingObjects.size(), parkingObjects);
	return parkingObjects;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObjects(double, double, double)
     */
    @Override
    public Set<String> getParkingLots(double x, double y, double radius) throws OntologyException {

	String queryPattern = QUERY_PARKING_OBJECTS;
	String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, OntologyConstants.Parking.name(), VAR,
	        VAR, x - radius, x + radius, y - radius, y + radius);

	// execute the query
	List<String> matches = executeSparqlQuery(query, VAR);
	return new HashSet<>(matches);
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
			properties.put(Constants.OntologyConstants.Object_has_GPS_coordinates.name(),
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

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#calculateDistancesForTruck(java.lang.String, double)
     */
    @Override
    public void calculateDistancesForPlatform(String platformId, double radius) throws OntologyException {

	LogHelper.debug(ContextModuleOntologyManager.class, "calculateDistancesForPlatform",
	        "Calculating distance for platform %s using radius %f", platformId, radius);

	// get the platform data from ontology
	Platform platform = getPlatform(platformId);
	Set<String> parkingLots = getParkingLots(platform.getLastLocation(), radius);
	if (parkingLots.isEmpty()) {
	    LogHelper.warning(ContextModuleOntologyManager.class, "calculateDistancesForPlatform",
		    "There are no parkings for platform %s in location %s and radius %f", platformId, platform.getLastLocation(),
		    radius);
	}
	Set<String> buildings = getParkingLotInfrastructure("Parking_Reading", OntologyConstants.Building.name());
	for (String buildingId : buildings) {
	    
        }
    }

    private String getStringProperty(Map<String, String[]> properties, OntologyConstants propertyName) {

	if (properties == null || propertyName == null) {
	    return null;
	}
	String[] values = properties.get(propertyName.name());
	String stringValue = (values != null && values.length > 0 ? values[0] : null);
	return stringValue;
    }

    private Integer getIntProperty(Map<String, String[]> properties, OntologyConstants propertyName) {
	return NumbersHelper.getIntegerFromString(getStringProperty(properties, propertyName));
    }

    private Double getDoubleProperty(Map<String, String[]> properties, OntologyConstants propertyName) {
	return NumbersHelper.getDoubleFromString(getStringProperty(properties, propertyName));
    }

    private RelativePosition getRelativePosition(Map<String, String[]> properties, OntologyConstants propertyName) {
	return RelativePosition.getPostion(getStringProperty(properties, propertyName));
    }
}
