package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.OntologyObject;
import itti.com.pl.arena.cm.dto.GeoObject;
//import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform.Type;
import itti.com.pl.arena.cm.dto.dynamicobj.RelativePosition;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.ontology.OntologyConstants;
import itti.com.pl.arena.cm.service.PlatformTracker;
import itti.com.pl.arena.cm.utils.helper.LocationHelper;
import itti.com.pl.arena.cm.utils.helper.LocationHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

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

    // radius of Earth in meters (6371 km)
    private static final double EARTH_RADIUS = 6371000;

    private static final String QUERY_GET_OBJECTS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
            + "?%s ns:Object_has_GPS_x ?coordinate_x. " + "?%s ns:Object_has_GPS_y ?coordinate_y. "
            + "FILTER ( (?coordinate_x >= %f && ?coordinate_x <= %f) && (?coordinate_y >= %f && ?coordinate_y <= %f)) " + "}";

    private static final String QUERY_GET_PLATFORMS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
            + "?%s rdf:type ns:%s. " + "?%s ns:Object_has_GPS_x ?coordinate_x. " + "?%s ns:Object_has_GPS_y ?coordinate_y. "
            + "FILTER ( (?coordinate_x >= %f && ?coordinate_x <= %f) && (?coordinate_y >= %f && ?coordinate_y <= %f)) " + "}";

    private static final String QUERY_PARKING_OBJECTS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
            + "?%s rdf:type ns:%s. " + "?%s ns:Object_has_GPS_x ?coordinate_x. " + "?%s ns:Object_has_GPS_y ?coordinate_y. "
            + "FILTER ( (?coordinate_x >= %f && ?coordinate_x <= %f) || (?coordinate_y >= %f && ?coordinate_y <= %f)) " + "}";

    /**
     * Sets dimensions of the platform object
     * 
     * @param platform
     *            platform to be updated
     * @param width
     *            width of the platform
     * @param height
     *            height of the platform
     * @param length
     *            length of the platform
     */
    private void setPlatformSizes(Platform platform, Double width, Double height, Double length) {
        if (platform != null) {
            if (width != null) {
                platform.setWidth(width);
            }
            if (height != null) {
                platform.setHeight(height);
            }
            if (length != null) {
                platform.setLength(length);
            }
        }
    }

    /**
     * Creates {@link Location} object using ontology-retrieved values
     * 
     * @param properties
     *            values found in the ontology
     * @return location object
     */
    private Location prepareLocationFromProperties(Map<String, String[]> properties) {

        //default properties for vehicle (platform) classes
        OntologyConstants longitudeProperty = OntologyConstants.Object_has_GPS_x;
        OntologyConstants latitudeProperty = OntologyConstants.Object_has_GPS_y;

        Double longitude = getDoubleProperty(properties, longitudeProperty);
        Double latitude = getDoubleProperty(properties, latitudeProperty);
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
        // prepare location info
        if (platform.getLocation() != null) {
            properties.put(OntologyConstants.Object_has_GPS_x.name(),
                    new String[] { String.valueOf(platform.getLocation().getLongitude()) });
            properties.put(OntologyConstants.Object_has_GPS_y.name(),
                    new String[] { String.valueOf(platform.getLocation().getLatitude()) });
            properties.put(OntologyConstants.Object_has_GPS_bearing.name(),
                    new String[] { String.valueOf(platform.getLocation().getBearing()) });
        }

        // prepare platform size info
        properties.put(OntologyConstants.Object_has_width.name(), new String[] { String.valueOf(platform.getWidth()) });
        properties.put(OntologyConstants.Object_has_height.name(), new String[] { String.valueOf(platform.getHeight()) });
        properties.put(OntologyConstants.Object_has_length.name(), new String[] { String.valueOf(platform.getLength()) });

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
        properties.put(OntologyConstants.Camera_view.name(), new String[] { camera.getOnPPlatformPosition().name() });
        return createSimpleInstance(OntologyConstants.Camera.name(), camera.getId(), properties);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatforms(double, double, double)
     */
    public Set<String> getInstanceNames(double x, double y, double radius, Class<? extends OntologyObject> ontologyClassName)
            throws OntologyException {

        Set<String> resultList = new HashSet<String>();

        if (ontologyClassName == Platform.class) {
            resultList.addAll(getPlatformNames(x, y, radius));
        } else if (ontologyClassName == ParkingLot.class) {
            resultList.addAll(getParkingLots(x, y, radius));
        }
        return resultList;
    }

    private Set<String> getPlatformNames(double x, double y, double radius) {
        String queryPattern = QUERY_GET_PLATFORMS;
        String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR,
                OntologyConstants.Vehicle_with_cameras.name(), VAR, VAR, x - radius, x + radius, y - radius, y + radius);

        // execute the query
        return new HashSet<String>(executeSparqlQuery(query, VAR));
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatforms(double, double, double)
     */
    @Override
    public Set<Platform> getPlatforms(double x, double y, double radius) throws OntologyException {
        Set<Platform> platformsInformation = new HashSet<>();
        try {
            Set<String> platformNames = getPlatformNames(x, y, radius);
            for (String platformId : platformNames) {
                platformsInformation.add(getPlatform(platformId));
            }
        } catch (OntologyException e) {
            LogHelper.exception(PlatformTracker.class, "getPlatformsData", e.getLocalizedMessage(), e);
        }
        return platformsInformation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObject(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends OntologyObject> T getOntologyObject(String id, Class<T> objectclass) throws OntologyException {
        T response = null;
        if (objectclass == Platform.class) {
            response = (T) getPlatform(id);
        } else if (objectclass == ParkingLot.class) {
            response = (T) getParkingLot(id);
        }

        return response;
    }

    private Platform getPlatform(String platformId) throws OntologyException {

        // prepare data object
        String platformType = getInstanceClass(platformId);
        Platform platform = new Platform(platformId, null, Type.valueOf(platformType), null);

        // get information about the platform from ontology
        Map<String, String[]> properties = getInstanceProperties(platformId);

        // get information about cameras installed on platform
        String[] cameras = properties.get(OntologyConstants.Vehicle_has_cameras.name());
        if (cameras != null) {
            for (String cameraId : cameras) {
                Camera cameraInfo = getCameraInformation(cameraId);
                platform.addCamera(cameraInfo);

            }
        }
        // set location of the platform
        Location lastLocation = prepareLocationFromProperties(properties);
        platform.setLocation(lastLocation);
        // set size of the platform
        setPlatformSizes(platform, getDoubleProperty(properties, OntologyConstants.Object_has_width),
                getDoubleProperty(properties, OntologyConstants.Object_has_height),
                getDoubleProperty(properties, OntologyConstants.Object_has_length));
        return platform;
    }

    /**
     * Returns complete information about parking lot
     * @param parkingLotId ID of the parking lot
     * @return Information about parking lot
     * @throws OntologyException Could not retrieve information from the ontology
     */
    private ParkingLot getParkingLot(String parkingLotId) throws OntologyException {

        Map<String, String[]> properties = getInstanceProperties(parkingLotId);

        ParkingLot parkingLotInformation = new ParkingLot(parkingLotId);

        String[] infrastructureList = properties.get(OntologyConstants.Parking_has_infrastructure.name());

        parkingLotInformation.setLocation(prepareLocationFromProperties(properties));

        if (infrastructureList != null) {
            for (String infrastrId : infrastructureList) {
                parkingLotInformation.addIntrastructure(getInfrastructure(infrastrId));
            }
        }

        String[] buildingList = properties.get(OntologyConstants.Parking_has_building.name());
        if (buildingList != null) {
            for (String buildingId : buildingList) {
                parkingLotInformation.addBuilding(getBuilding(buildingId));
            }
        }

        return parkingLotInformation;
    }

    private Infrastructure getInfrastructure(String infrastructureId) throws OntologyException {

        Infrastructure infrastructure = new Infrastructure(infrastructureId);

        Map<String, String[]> infrastrProperties = getInstanceProperties(infrastructureId);
        if (!infrastrProperties.isEmpty()) {
            try {
                Location[] coordinates = parseCoordinates(infrastrProperties
                        .get(OntologyConstants.Object_has_GPS_coordinates.name()));
                infrastructure.setBoundaries(coordinates);
            } catch (LocationHelperException exc) {
                LogHelper.exception(ContextModuleOntologyManager.class, "getParkingLot", String.format(
                        "Could not parse ontlogy-stored data. Location data is not available for given object: %s",
                        infrastructureId), exc);
            }
        }
        return infrastructure;
    }

    private Building getBuilding(String buildingId) throws OntologyException {

        Building building = new Building(buildingId);

        Map<String, String[]> buildingProperties = getInstanceProperties(buildingId);
        if (!buildingProperties.isEmpty()) {

            try {
                Location[] coordinates = parseCoordinates(buildingProperties
                        .get(OntologyConstants.Object_has_GPS_coordinates.name()));
                building.setBoundaries(coordinates);
            } catch (LocationHelperException exc) {
                LogHelper.exception(ContextModuleOntologyManager.class, "getParkingLot", String.format(
                        "Could not parse ontlogy-stored data. Location data is not available for given object: %s",
                        buildingId), exc);
            }
        }
        return building;
    }

    /**
     * Create a list of coordinate objects from the provided strings
     * 
     * @param coordinatesStr
     *            list of coordinates in string format
     * @return list of location objects
     * @throws LocationHelperException
     *             could not parse given strings into location objects
     */
    private Location[] parseCoordinates(String[] coordinatesStr) throws LocationHelperException {

        if (coordinatesStr == null) {
            return new Location[] {};
        }
        Location[] coordinates = new Location[coordinatesStr.length];
        for (int i = 0; i < coordinatesStr.length; i++) {
            coordinates[i] = LocationHelper.getLocationFromString(coordinatesStr[i]);
        }
        return coordinates;
    }

    @Override
    public Set<GeoObject> getGISObjects(double x, double y, double radius, String... gisObjectClasses) throws OntologyException {

        // get list of all available objects
        String queryPattern = QUERY_GET_OBJECTS;
        String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, VAR,
                x - radius, x + radius, y - radius, y + radius);

        // execute the query
        List<String> matches = executeSparqlQuery(query, VAR);

        Set<GeoObject> responseSet = new HashSet<>();

        Set<String> classesFilter = new HashSet<>();
        if(gisObjectClasses != null){
            classesFilter.addAll(Arrays.asList(gisObjectClasses));
        }

        for (String geoObject : matches) {
            String objectClass = getInstanceClass(geoObject);
            String parentClass = getInstanceGrandClass(geoObject);
            if (classesFilter.isEmpty() || classesFilter.contains(objectClass)) {
                if(StringHelper.equalsIgnoreCase(objectClass, OntologyConstants.Parking.name())){
                    responseSet.add(getParkingLot(geoObject));
                }
                else if(StringHelper.equalsIgnoreCase(objectClass, OntologyConstants.Vehicle_with_cameras.name())){
                    //ignore this one
                }
                else if(StringHelper.equalsIgnoreCase(parentClass, OntologyConstants.Building.name())){
                    responseSet.add(getBuilding(geoObject));
                }
                else if(StringHelper.equalsIgnoreCase(parentClass, OntologyConstants.Infrastructure.name())){
                    responseSet.add(getInfrastructure(geoObject));
                }
            }
        }
        return responseSet;

    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObjects(String, String...)
     */
    @Override
    public Set<String> getParkingLotInfrastructure(String parkingId, String... classFilters) throws OntologyException {

        LogHelper.debug(ContextModuleOntologyManager.class, "getParkingLotInfrastructure", "get parking objects for '%s'",
                String.valueOf(parkingId));
        if (!StringHelper.hasContent(parkingId)) {
            LogHelper.warning(ContextModuleOntologyManager.class, "getParkingLotInfrastructure", "Null parkingId provided");
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_PARKING_ID_OBJECT);
        }
        // check, if parking is defined in the ontology
        String parkingClass = getInstanceClass(parkingId);
        // and it's member of valid class
        if (!StringHelper.equals(OntologyConstants.Parking.name(), parkingClass)) {
            LogHelper.warning(ContextModuleOntologyManager.class, "getParkingLotInfrastructure",
                    "Provided object '%s' is not a member of Parking class", parkingId);
            throw new OntologyException(ErrorMessages.ONTOLOGY_INSTANCE_IS_NOT_A_PARKING, parkingId);
        }

        // get information about parking lot infrastructure
        Map<String, String[]> properties = getInstanceProperties(parkingId);
        // prepare the response
        Set<String> parkingObjects = new HashSet<>();
        // add infrastructure objects
        if (properties.containsKey(OntologyConstants.Parking_has_infrastructure.name())) {
            parkingObjects.addAll(Arrays.asList(properties.get(OntologyConstants.Parking_has_infrastructure.name())));
        }
        // add buildings objects
        if (properties.containsKey(OntologyConstants.Parking_has_building.name())) {
            parkingObjects.addAll(Arrays.asList(properties.get(OntologyConstants.Parking_has_building.name())));
        }
        // apply filters (if defined)
        if (classFilters != null && classFilters.length > 0) {
            Set<String> filters = new HashSet<>(Arrays.asList(classFilters));
            Set<String> filteredParkingObjects = new HashSet<>();
            for (String parkingObject : parkingObjects) {
                if (filters.contains(getInstanceClass(parkingObject)) || filters.contains(getInstanceGrandClass(parkingObject))) {
                    filteredParkingObjects.add(parkingObject);
                }
            }
            parkingObjects = filteredParkingObjects;
        }
        LogHelper.debug(ContextModuleOntologyManager.class, "getParkingLotInfrastructure", "returning %d objects: %s",
                parkingObjects.size(), parkingObjects);
        return parkingObjects;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getGISObjects(double, double, double)
     */
    private Set<String> getParkingLots(double x, double y, double radius) throws OntologyException {

        String queryPattern = QUERY_PARKING_OBJECTS;
        String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, OntologyConstants.Parking.name(), VAR, VAR,
                x - radius, x + radius, y - radius, y + radius);

        // execute the query
        List<String> matches = executeSparqlQuery(query, VAR);
        return new HashSet<>(matches);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#addGeoportalData(itti.com.pl.arena.cm.dto.Location, java.util.Set)
     */
    @Override
    public void updateGeoportalData(double x, double y, Set<GeoObject> geoportalData) throws OntologyException {
        if (geoportalData != null) {
            // TODO: need to be implemented properly
            for (GeoObject geoObject : geoportalData) {
                String ontologyClass = getInstanceClass(geoObject.getId());
                if (StringHelper.hasContent(ontologyClass)) {
                    Map<String, String[]> properties = new HashMap<String, String[]>();
                    properties.put(OntologyConstants.Object_has_GPS_coordinates.name(),
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
        Set<String> parkingLots = getParkingLots(platform.getLocation().getLongitude(), platform.getLocation().getLatitude(),
                radius);
        if (parkingLots.isEmpty()) {
            LogHelper.warning(ContextModuleOntologyManager.class, "calculateDistancesForPlatform",
                    "There are no parkings for platform %s in location %s and radius %f", platformId, platform.getLocation(),
                    radius);
            return;
        }
        // use first parking as a default one
        Set<String> buildings = getParkingLotInfrastructure(parkingLots.iterator().next());
        for (String buildingId : buildings) {
            calculateDistanceForObject(buildingId, platform.getLocation());
        }
    }

    /**
     * Calculated distance between object identified by its ID and given location
     * 
     * @param objectId
     *            ID of the object, for which distance should be calculated
     * @param referenceLocation
     *            reference location (distance will be calculated between object position and that location)
     * @throws OntologyException
     *             could not calculate location
     */
    private void calculateDistanceForObject(String objectId, Location referenceLocation) throws OntologyException {

        LogHelper.debug(ContextModuleOntologyManager.class, "calculateDistanceForObject",
                "calculate distance for object '%s' and location %s'", String.valueOf(objectId),
                String.valueOf(referenceLocation));
        if (!StringHelper.hasContent(objectId)) {
            LogHelper.warning(ContextModuleOntologyManager.class, "calculateDistanceForObject", "ObjectId was not provided");
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_INSTANCE_NAME);
        }
        if (referenceLocation == null) {
            LogHelper.warning(ContextModuleOntologyManager.class, "calculateDistanceForObject", "Location was not provided");
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_LOCATION_OBJECT);
        }
        Map<String, String[]> objectProperties = getInstanceProperties(objectId);
        String[] objectCoordinates = objectProperties.get(OntologyConstants.Object_has_GPS_coordinates.name());
        if (objectCoordinates != null) {
            Double maxDistance = null;
            for (String coordinate : objectCoordinates) {
                Double distance = calculateDistance(coordinate, referenceLocation);
                if (distance != null && (maxDistance == null || maxDistance < distance)) {
                    maxDistance = distance;
                }
            }
            updatePropertyValue(objectId, OntologyConstants.Object_has_distance.name(), maxDistance.toString());
        } else {
            LogHelper.info(ContextModuleOntologyManager.class, "calculateDistanceForObject",
                    "No GPS coordinates found for instance: '%s'", objectId);
        }
    }

    /**
     * Calculates distance between two locations. One is stored as a string: 'xPos, yPos', the second one is stored as a
     * {@link Location} object calculation was implemented based on instructions from:
     * http://www.movable-type.co.uk/scripts/latlong.html
     * 
     * @param coordinateString
     *            first coordinate in string form
     * @param referenceLocation
     *            second coordinate
     * @return distance between two locations measured in meters
     */
    private Double calculateDistance(String coordinateString, Location referenceLocation) {
        Double[] coordinates = NumbersHelper.getDoublesFromString(coordinateString, ",");
        // invalid coordinates, ignore
        if (coordinates == null || coordinates.length != 2 || coordinates[0] == null || coordinates[1] == null) {
            return null;
        }
        double deltaLongitude = Math.toRadians(coordinates[0] - referenceLocation.getLongitude());
        double deltaLatitude = Math.toRadians(coordinates[1] - referenceLocation.getLatitude());

        double firstLatitude = Math.toRadians(referenceLocation.getLatitude());
        double secondLatitude = Math.toRadians(coordinates[1]);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2.0) + Math.pow(Math.sin(deltaLongitude / 2), 2.0)
                * Math.cos(firstLatitude) * Math.cos(secondLatitude);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return distance;
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
