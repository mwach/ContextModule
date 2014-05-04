package itti.com.pl.arena.cm.server.ontology;

import itti.com.pl.arena.cm.dto.GeoObject;
//import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.OntologyObject;
import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.dto.coordinates.ArenaObjectCoordinate;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.coordinates.RadialCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform.Type;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.location.Range;
import itti.com.pl.arena.cm.server.ontology.OntologyConstants;
import itti.com.pl.arena.cm.server.service.PlatformTracker;
import itti.com.pl.arena.cm.utils.helper.LocationHelper;
import itti.com.pl.arena.cm.utils.helper.LocationHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

/**
 * Extension of the {@link OntologyManager} providing ContextManager-specific services
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleOntologyManager extends OntologyManager implements Ontology {

    private static final String QUERY_GET_OBJECTS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
            + "?%s ns:Object_has_GPS_x ?coordinate_x. " + "?%s ns:Object_has_GPS_y ?coordinate_y. "
            + "FILTER ( (?coordinate_x >= %f && ?coordinate_x <= %f) && (?coordinate_y >= %f && ?coordinate_y <= %f)) " + "}";

    private static final String QUERY_GET_PLATFORMS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
            + "?%s rdf:type ns:%s. " + "?%s ns:Object_has_GPS_x ?coordinate_x. " + "?%s ns:Object_has_GPS_y ?coordinate_y. "
            + "FILTER ( (?coordinate_x >= %f && ?coordinate_x <= %f) && (?coordinate_y >= %f && ?coordinate_y <= %f)) " + "}";

    private static final String QUERY_PARKING_OBJECTS = "PREFIX ns: <%s> " + "SELECT ?%s " + "WHERE " + "{ "
            + "?%s rdf:type ns:%s. " + "?%s ns:Object_has_GPS_x ?coordinate_x. " + "?%s ns:Object_has_GPS_y ?coordinate_y. "
            + "FILTER ( (?coordinate_x >= %f && ?coordinate_x <= %f) && (?coordinate_y >= %f && ?coordinate_y <= %f)) " + "}";

    protected static final int DIMENSIONS_XY = 2;
    protected static final int DIMENSIONS_XYZ = 3;

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
            platform.setWidth(getValue(width));
            platform.setHeight(getValue(height));
            platform.setLength(getValue(length));
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

        // default properties for vehicle (platform) classes
        OntologyConstants longitudeProperty = OntologyConstants.Object_has_GPS_x;
        OntologyConstants latitudeProperty = OntologyConstants.Object_has_GPS_y;

        Double longitude = getDoubleProperty(properties, longitudeProperty);
        Double latitude = getDoubleProperty(properties, latitudeProperty);
        Integer bearing = getIntProperty(properties, OntologyConstants.Object_has_GPS_bearing);
        return new Location(getValue(longitude), getValue(latitude), getValue(bearing));
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

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.server.ontology.Ontology#updatePlatformPosition(java.lang.String, itti.com.pl.arena.cm.dto.Location)
     */
    @Override
    public void updatePlatformPosition(String platformId, Location location) throws OntologyException {

        //validation
        if(location == null){
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_LOCATION_OBJECT);
        }
        if(!StringHelper.hasContent(platformId)){
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_INSTANCE_NAME);
        }

        //get existing platform properties
        Map<String, String[]> properties = null;
        String parentClass = null;
        //if platform exist in the ontology get existing properties
        if(hasInstance(platformId)){
            properties = getInstanceProperties(platformId);
            parentClass = getInstanceClass(platformId);
        }else{
            //otherwise create new properties for new instance
            properties = new HashMap<>();
            //if new instance in ontology, use default type
            parentClass = Platform.Type.getDefaultType().name();
        }
        // update location info
        properties.put(OntologyConstants.Object_has_GPS_x.name(),
                new String[] { String.valueOf(location.getLongitude()) });
        properties.put(OntologyConstants.Object_has_GPS_y.name(),
                new String[] { String.valueOf(location.getLatitude()) });
        properties.put(OntologyConstants.Object_has_GPS_bearing.name(),
                new String[] { String.valueOf(location.getBearing()) });

        // create instance in the ontology
        createSimpleInstance(parentClass, platformId, properties);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatformInformation(java.lang.String)
     */
    @Override
    public void updateParkingLot(ParkingLot parkingLot) throws OntologyException {

        Map<String, String[]> properties = new HashMap<>();
        // prepare location info
        if (parkingLot.getLocation() != null) {
            properties.put(OntologyConstants.Object_has_GPS_x.name(),
                    new String[] { String.valueOf(parkingLot.getLocation().getLongitude()) });
            properties.put(OntologyConstants.Object_has_GPS_y.name(),
                    new String[] { String.valueOf(parkingLot.getLocation().getLatitude()) });
        }

        // prepare information about boundaries
        properties.put(OntologyConstants.Object_has_GPS_coordinates.name(),
                LocationHelper.createStringsFromLocations(parkingLot.getBoundaries().toArray(
                        new Location[parkingLot.getBoundaries().size()])));

        // process buildings
        if (parkingLot.getBuildings() != null) {
            String[] buildingIds = new String[parkingLot.getBuildings().size()];
            int currentBuilding = 0;
            for (Entry<String, Building> building : parkingLot.getBuildings().entrySet()) {
                updateBuildingInformation(building.getValue());
                buildingIds[currentBuilding++] = building.getKey();
            }
            properties.put(OntologyConstants.Parking_has_building.name(), buildingIds);
        }

        // process infrastructure
        if (parkingLot.getInfrastructure() != null) {
            String[] infrastructureIds = new String[parkingLot.getInfrastructure().size()];
            int currentInfrastructure = 0;
            for (Entry<String, Infrastructure> infrastructure : parkingLot.getInfrastructure().entrySet()) {
                updateInfrastructureInformation(infrastructure.getValue());
                infrastructureIds[currentInfrastructure++] = infrastructure.getKey();
            }
            properties.put(OntologyConstants.Parking_has_infrastructure.name(), infrastructureIds);
        }

        // create instance in the ontology
        createSimpleInstance(OntologyConstants.Parking.name(), parkingLot.getId(), properties);
    }

    /**
     * Retrieves information about camera from ontology
     * 
     * @param cameraId
     *            ID of the camera
     * @return camera object
     * @throws OntologyException
     */
    private Camera getCamera(String cameraId) throws OntologyException {

        Camera cameraInfo = null;

        Map<String, String[]> cameraInstance = getInstanceProperties(cameraId);
        if (!cameraInstance.isEmpty()) {
            String cameraType = getStringProperty(cameraInstance, OntologyConstants.Camera_has_type);
            Double angleXVal = getDoubleProperty(cameraInstance, OntologyConstants.Camera_has_angle_x);
            Double angleYVal = getDoubleProperty(cameraInstance, OntologyConstants.Camera_has_angle_y);

            Double positionX = getDoubleProperty(cameraInstance, OntologyConstants.Camera_has_position_x);
            Double positionY = getDoubleProperty(cameraInstance, OntologyConstants.Camera_has_position_y);
            Integer cameraDirection = getIntProperty(cameraInstance, OntologyConstants.Camera_has_direction);

            cameraInfo = new Camera(cameraId, cameraType, getValue(angleXVal), getValue(angleYVal), 
                    new CartesianCoordinate(getValue(positionX), getValue(positionY)), getValue(cameraDirection));
        }
        return cameraInfo;
    }

    /**
     * Returns name of the platform holding the camera with given Id
     * @param cameraId ID of the camera
     * @return ID of the platform, where camera is installed
     * @throws OntologyException 
     */
    public String getPlatformWithCamera(String cameraId) throws OntologyException {
        //get all the available platforms
        List<String> platformNames = getInstances(OntologyConstants.Vehicle_with_cameras.name());
        //for each of them, search for camera
        for (String platformName : platformNames) {
            if(getPlatform(platformName).getCameras().containsKey(cameraId)){
                return platformName;
            }
        }
        return null;
    }

    private OWLIndividual updateCameraInformation(Camera camera) throws OntologyException {

        Map<String, String[]> properties = new HashMap<>();
        properties.put(OntologyConstants.Camera_has_type.name(), new String[] { camera.getType() });
        properties.put(OntologyConstants.Camera_has_angle_x.name(), new String[] { String.valueOf(camera.getAngleX()) });
        properties.put(OntologyConstants.Camera_has_angle_y.name(), new String[] { String.valueOf(camera.getAngleY()) });

        CartesianCoordinate onPlatformPosition = camera.getOnPlatformPosition();
        if(onPlatformPosition != null){
            properties.put(OntologyConstants.Camera_has_position_x.name(), new String[] { String.valueOf(onPlatformPosition.getX()) });
            properties.put(OntologyConstants.Camera_has_position_y.name(), new String[] { String.valueOf(onPlatformPosition.getY()) });
        }
        properties.put(OntologyConstants.Camera_has_direction.name(), new String[] { String.valueOf(camera.getDirectionAngle()) });
        return createSimpleInstance(OntologyConstants.Camera.name(), camera.getId(), properties);
    }

    private OWLIndividual updateBuildingInformation(Building building) throws OntologyException {

        String buildingType = building.getType().name();

        Map<String, String[]> properties = new HashMap<>();

        // prepare information about boundaries
         properties.put(OntologyConstants.Object_has_GPS_coordinates.name(),
                LocationHelper.createStringsFromLocations(building.getBoundaries().toArray(
                        new Location[building.getBoundaries().size()])));

        return createSimpleInstance(buildingType, building.getId(), properties);
    }

    private OWLIndividual updateInfrastructureInformation(Infrastructure infrastructure) throws OntologyException {

        String infrastructureType = infrastructure.getType().name();

        Map<String, String[]> properties = new HashMap<>();

        // prepare information about boundaries
         properties.put(OntologyConstants.Object_has_GPS_coordinates.name(),
                LocationHelper.createStringsFromLocations(infrastructure.getBoundaries().toArray(
                        new Location[infrastructure.getBoundaries().size()])));

        return createSimpleInstance(infrastructureType, infrastructure.getId(), properties);
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
        } else if (objectclass == Camera.class) {
            response = (T) getCamera(id);
        } else if (objectclass == Building.class) {
            response = (T) getBuilding(id);
        } else if (objectclass == Infrastructure.class) {
            response = (T) getInfrastructure(id);
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
                Camera cameraInfo = getCamera(cameraId);
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
     * 
     * @param parkingLotId
     *            ID of the parking lot
     * @return Information about parking lot
     * @throws OntologyException
     *             Could not retrieve information from the ontology
     */
    private ParkingLot getParkingLot(String parkingLotId) throws OntologyException {

        Map<String, String[]> properties = getInstanceProperties(parkingLotId);

        ParkingLot parkingLotInformation = new ParkingLot(parkingLotId);

        //location
        parkingLotInformation.setLocation(prepareLocationFromProperties(properties));
        try {
            String[] boundaries = properties.get(OntologyConstants.Object_has_GPS_coordinates.name());
            parkingLotInformation.setBoundaries(LocationHelper.getLocationsFromStrings(
                    boundaries));
        } catch (LocationHelperException exc) {
            LogHelper.exception(ContextModuleOntologyManager.class, "getParkingLot", String.format(
                    "Could not parse ontlogy-stored data. Location data is not available for given object: %s",
                    parkingLotId), exc);
        }
        
        //infrastructure
        String[] infrastructureList = properties.get(OntologyConstants.Parking_has_infrastructure.name());
        if (infrastructureList != null) {
            for (String infrastrId : infrastructureList) {
                if(getInstance(infrastrId) == null){
                    removePropertyValues(parkingLotId, OntologyConstants.Parking_has_infrastructure.name(), infrastrId);                    
                }else{
                    parkingLotInformation.addIntrastructure(getInfrastructure(infrastrId));
                }
            }
        }

        //buildings
        String[] buildingList = properties.get(OntologyConstants.Parking_has_building.name());
        if (buildingList != null) {
            for (String buildingId : buildingList) {
                if(getInstance(buildingId) == null){
                    removePropertyValues(parkingLotId, OntologyConstants.Parking_has_building.name(), buildingId);
                }else{
                parkingLotInformation.addBuilding(getBuilding(buildingId));
                }
            }
        }

        return parkingLotInformation;
    }

    private void removePropertyValues(String instanceName, String propertyName, String infrastrId) throws OntologyException {

        OWLIndividual instance = getInstance(instanceName);
        RDFProperty property = getModel().getRDFProperty(propertyName);
        int propsCount = instance.getPropertyValueCount(property);
        for (int i = 0; i < propsCount; i++) {
            Object currentOntValue = instance.getPropertyValue(property);
            if(currentOntValue.toString().equals(infrastrId)){
                instance.removePropertyValue(property, currentOntValue);
            }
        }
    }

    private Infrastructure getInfrastructure(String infrastructureId) throws OntologyException {

        Infrastructure infrastructure = new Infrastructure(infrastructureId, null, null);

        Map<String, String[]> infrastrProperties = getInstanceProperties(infrastructureId);
        if (!infrastrProperties.isEmpty()) {
            try {
                Location[] coordinates = parseCoordinates(infrastrProperties.get(OntologyConstants.Object_has_GPS_coordinates
                        .name()));
                infrastructure.setBoundaries(coordinates);
            } catch (LocationHelperException exc) {
                LogHelper.exception(ContextModuleOntologyManager.class, "getInfrastructure", String.format(
                        "Could not parse ontlogy-stored data. Location data is not available for given object: %s",
                        infrastructureId), exc);
            }
        }
        String objectType = getInstanceClass(infrastructureId);
        infrastructure.setType(itti.com.pl.arena.cm.dto.staticobj.Infrastructure.Type.getType(objectType));
        return infrastructure;
    }

    private Building getBuilding(String buildingId) throws OntologyException {

        Building building = new Building(buildingId, null, null);

        Map<String, String[]> buildingProperties = getInstanceProperties(buildingId);
        if (!buildingProperties.isEmpty()) {

            try {
                Location[] coordinates = parseCoordinates(buildingProperties.get(OntologyConstants.Object_has_GPS_coordinates
                        .name()));
                building.setBoundaries(coordinates);
            } catch (LocationHelperException exc) {
                LogHelper.exception(ContextModuleOntologyManager.class, "getBuilding", String.format(
                        "Could not parse ontlogy-stored data. Location data is not available for given object: %s", buildingId),
                        exc);
            }
        }
        String objectType = getInstanceClass(buildingId);
        building.setType(itti.com.pl.arena.cm.dto.staticobj.Building.Type.getType(objectType));

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
        String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, VAR, x - radius, x + radius, y - radius, y
                + radius);

        // execute the query
        List<String> matches = executeSparqlQuery(query, VAR);

        Set<GeoObject> responseSet = new HashSet<>();

        Set<String> classesFilter = new HashSet<>();
        if (gisObjectClasses != null) {
            classesFilter.addAll(Arrays.asList(gisObjectClasses));
        }
//TODO: this method returns parking lot as well as truck - update to use filter 'Platform' during the search, or update PARENTCLASS to be searched inside IF
        for (String geoObject : matches) {
            String objectClass = getInstanceClass(geoObject);
            String parentClass = getInstanceGrandClass(geoObject);
            if (classesFilter.isEmpty() || classesFilter.contains(objectClass)) {
                if (StringHelper.equalsIgnoreCase(objectClass, OntologyConstants.Parking.name())) {
                    responseSet.add(getParkingLot(geoObject));
                } else if (StringHelper.equalsIgnoreCase(objectClass, OntologyConstants.Vehicle_with_cameras.name())) {
                    // ignore this one
                } else if (StringHelper.equalsIgnoreCase(parentClass, OntologyConstants.Building.name())) {
                    responseSet.add(getBuilding(geoObject));
                } else if (StringHelper.equalsIgnoreCase(parentClass, OntologyConstants.Infrastructure.name())) {
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

    private String findNearestParkingLot(Location location, double radius) throws OntologyException {

        //get list of all available parking lots
        Set<String> availableParkings = getParkingLots(location.getLongitude(), location.getLatitude(), radius);

        //no parking lots returned
        if(availableParkings.isEmpty()){
            return null;
        }
        //one parking lot returned - return its name
        if(availableParkings.size() == 1){
            return availableParkings.iterator().next();
        }
        //more than one parking lots - do some processing

        //check all parking lots
        String closestParkingLot = null;
        double closestDistance = Double.MAX_VALUE;

        for (String parkingLotName : availableParkings) {
            ParkingLot parkingLot = getOntologyObject(parkingLotName, ParkingLot.class);
            Collection<Location> parkingLotBoundaries = parkingLot.getBoundaries();
            //in case, there are no boundaries for parking, use its location
            parkingLotBoundaries.add(parkingLot.getLocation());
            
            //for each parking lot, check all boundaries
            for (Location boundary : parkingLotBoundaries){
                double distance = LocationHelper.calculateDistance(boundary, location);
                if(distance < closestDistance){
                    closestDistance = distance;
                    closestParkingLot = parkingLotName;
                }
            }
        }
        return closestParkingLot;
    }

//    private double calculateDistance(Location locationOne, Location locationTwo) {
//        if(locationOne == null || locationTwo == null){
//            return Double.MAX_VALUE;
//        }
//
//        double deltaLatitude = Math.toRadians(locationTwo.getLatitude() - locationTwo.getLatitude());
//        double deltaLongitude = Math.toRadians(locationOne.getLongitude() - locationOne.getLongitude());
//
//        double firstLatitude = Math.toRadians(locationTwo.getLatitude());
//        double secondLatitude = Math.toRadians(locationOne.getLatitude());
//
//        double a = Math.pow(Math.sin(deltaLatitude / 2), 2.0) + Math.pow(Math.sin(deltaLongitude / 2), 2.0)
//                * Math.cos(firstLatitude) * Math.cos(secondLatitude);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = EARTH_RADIUS * c;
//        return distance;
//    }

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

        String parkingId = findNearestParkingLot(platform.getLocation(),
                radius);
        if (!StringHelper.hasContent(parkingId)) {
            LogHelper.warning(ContextModuleOntologyManager.class, "calculateDistancesForPlatform",
                    "There are no parkings for platform %s in location %s and radius %f", platformId, platform.getLocation(),
                    radius);
            return;
        }

        Set<String> buildings = getParkingLotInfrastructure(parkingId);
        for (String buildingId : buildings) {
            // get the coordinates of the building
            String[] objectCoordinates = getInstanceProperties(buildingId, OntologyConstants.Object_has_GPS_coordinates.name());
            // try to parse them into doubles
            if (objectCoordinates != null) {
                float maxDistance = Float.MAX_VALUE;
                for (String coordinateStr : objectCoordinates) {
                    try {
                        Location coordinate = LocationHelper.getLocationFromString(coordinateStr);
                        float distance = (float) LocationHelper.calculateDistance(platform.getLocation(), coordinate);
                        if(distance < maxDistance){
                            maxDistance = distance;
                        }
                    } catch (LocationHelperException e) {
                        LogHelper.warning(ContextModuleOntologyManager.class, "calculateDistancesForPlatform", "Could not convert '%s' into location. Details: %s", coordinateStr, e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
                updatePropertyValue(buildingId, OntologyConstants.Object_has_distance.name(), maxDistance);
            } else {
                LogHelper.info(ContextModuleOntologyManager.class, "calculateDistanceForObject",
                        "No GPS coordinates found for instance: '%s'", buildingId);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getPlatformNeighborhood(java.lang.String)
     */
    @Override
    public Set<ArenaObjectCoordinate> getPlatformNeighborhood(String platformId) throws OntologyException {
        LogHelper.debug(ContextModuleOntologyManager.class, "calculateArenaDistancesForPlatform",
                "Calculating distance for platform %s", platformId);

        // get the platform data from ontology
        Platform platform = getPlatform(platformId);
        Location referenceLocation = platform.getLocation();

        // use closest parking as a default one
        String parkingLotId = findNearestParkingLot(referenceLocation,
                Range.Km1.getRangeInKms());
        if (!StringHelper.hasContent(parkingLotId)) {
            LogHelper.warning(ContextModuleOntologyManager.class, "calculateDistancesForPlatform",
                    "There are no parkings for platform %s in location %s and radius %f", platformId, referenceLocation,
                    Range.Km1.getRangeInKms());
            return null;
        }

        Set<String> buildings = getParkingLotInfrastructure(parkingLotId);
        Set<ArenaObjectCoordinate> objectCoordinates = new HashSet<>();
        for (String buildingId : buildings) {
            // get the coordinates of the building
            ArenaObjectCoordinate objectCoordinate = new ArenaObjectCoordinate(buildingId);
            String[] buildingCoordinates = getInstanceProperties(buildingId, OntologyConstants.Object_has_GPS_coordinates.name());
            // try to parse them into doubles
            if (objectCoordinates != null) {
                // calculate radius coordinates
                for (String coordinateStr : buildingCoordinates) {
                    Location coordinate = null;
                    try {
                        coordinate = LocationHelper.getLocationFromString(coordinateStr);
                        double radius = LocationHelper.calculateDistance(coordinate, referenceLocation);
                        double angle = LocationHelper.calculateAngle(coordinate, referenceLocation);
                        objectCoordinate.addRadialCoordinates(radius, angle);
                    } catch (LocationHelperException e) {
                        LogHelper.warning(ContextModuleOntologyManager.class, "calculateArenaDistancesForPlatform", "Could not parse '%s' into location. Details: %s", coordinateStr, e.getLocalizedMessage());
                    }
                }
            } else {
                LogHelper.info(ContextModuleOntologyManager.class, "calculateDistanceForObject",
                        "No GPS coordinates found for instance: '%s'", buildingId);
            }
            objectCoordinates.add(objectCoordinate);
        }

        // now update objects angle with the platform bearing
        for (ArenaObjectCoordinate objectCoordinate : objectCoordinates) {
            for (RadialCoordinate radialCoordinate : objectCoordinate) {
                radialCoordinate.updateAngle(referenceLocation.getBearing());
            }
        }
        return objectCoordinates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.ontology.Ontology#getCameraFieldOfView(java.lang.String)
     */
    @Override
    public Set<ArenaObjectCoordinate> getCameraFieldOfView(String cameraId) throws OntologyException {
        LogHelper.debug(ContextModuleOntologyManager.class, "getCameraFieldOfView",
                "Calculating field of view for camera %s", cameraId);

        // get the platform data from ontology
        Camera camera = getCamera(cameraId);
        String platformId = getPlatformWithCamera(camera.getId());
        return getPlatformNeighborhood(platformId);
    }

    @Override
    public String updateZone(String zoneName, String parkingLotName, String planeName, List<Location> locations) throws OntologyException {

        //check, if zoneId was defined in the request
        if(!StringHelper.hasContent(zoneName)){
            zoneName = generateZoneName();
        }

        Map<String, String[]> properties = new HashMap<>();

        //add plane name
        if(StringHelper.hasContent(planeName)){
            properties.put(OntologyConstants.Plane_name.name(),
                    new String[]{planeName});
        }

        //add location of the zone
        if(locations != null){
            properties.put(OntologyConstants.Object_has_GPS_coordinates.name(),
                    LocationHelper.createStringsFromLocations(locations.toArray(new Location[locations.size()])));
        }
        OWLIndividual zoneInstance = createSimpleInstance(OntologyConstants.Car_parking_zone.name(), zoneName, properties);
        zoneName = zoneInstance.getName();

        //assign zone to the parking lot
        if(StringHelper.hasContent(parkingLotName)){
            ParkingLot parkingLot = getParkingLot(parkingLotName);
            //check if already assigned
            if(!parkingLot.getInfrastructure().keySet().contains(zoneName)){
                parkingLot.addIntrastructure(new Infrastructure(zoneName, parkingLotName, 
                        itti.com.pl.arena.cm.dto.staticobj.Infrastructure.Type.Car_parking_zone));
            }
            updateParkingLot(parkingLot);
        }
        return zoneName;
    }

    @Override
    public Zone getZone(String zoneId) throws OntologyException {

        Zone zone = new Zone(zoneId);

        List<Location> coordinates = new ArrayList<>();
        if (!StringHelper.hasContent(zoneId)) {
            LogHelper.info(ContextModuleOntologyManager.class, "getZone",
                    "Null or empty zoneId was provided");
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_VALUE_PROVIDED, OntologyConstants.Car_parking_zone.name());
        }

        String[] coordinateStrings = getInstanceProperties(zoneId, OntologyConstants.Object_has_GPS_coordinates.name());
        if(coordinateStrings != null){
            try {
                coordinates = Arrays.asList(LocationHelper.getLocationsFromStrings(coordinateStrings));
            } catch (LocationHelperException e) {
                LogHelper.error(ContextModuleOntologyManager.class, "getZone", "Could not retrieve information about zone: '%s' from ontology. Details: %s", zoneId, e.getLocalizedMessage());
            }
        }
        String[] planeName = getInstanceProperties(zoneId, OntologyConstants.Plane_name.name());

        zone.addCoordinates(coordinates);
        zone.setPlaneName(planeName != null && planeName.length == 1 ? planeName[0] : null);
        return zone;
    }

    @Override
    public void updateCamera(Camera camera, String platformName) throws OntologyException {

        Map<String, String[]> properties = new HashMap<>();
        properties.put(OntologyConstants.Camera_has_angle_x.name(), new String[]{StringHelper.toString(camera.getAngleX())});
        properties.put(OntologyConstants.Camera_has_angle_y.name(), new String[]{StringHelper.toString(camera.getAngleY())});
        properties.put(OntologyConstants.Camera_has_direction.name(), new String[]{StringHelper.toString(camera.getDirectionAngle())});
        properties.put(OntologyConstants.Camera_has_position_x.name(), new String[]{StringHelper.toString(camera.getOnPlatformPosition().getX())});
        properties.put(OntologyConstants.Camera_has_position_y.name(), new String[]{StringHelper.toString(camera.getOnPlatformPosition().getY())});
        properties.put(OntologyConstants.Camera_has_type.name(), new String[]{StringHelper.toString(camera.getType())});

        // create instance in the ontology
        createSimpleInstance(OntologyConstants.Camera.name(), camera.getId(), properties);    

        //update platform with camera
        if(StringHelper.hasContent(platformName)){
            String[] cameraNames = getInstanceProperties(platformName, OntologyConstants.Vehicle_has_cameras.name());
            if(!StringHelper.arrayContainsItem(cameraNames, camera.getId())){
                addPropertyValue(platformName, OntologyConstants.Vehicle_has_cameras.name(), camera.getId());
            }
        }
    }
    
    @Override
    public void updateBuilding(GeoObject building) throws OntologyException {

        Map<String, String[]> properties = new HashMap<>();
        properties.put(OntologyConstants.Object_has_GPS_coordinates.name(), 
                LocationHelper.createStringsFromLocations(building.getBoundaries().toArray(new Location[building.getBoundaries().size()])));

        String parentClassName = null;
        String parkingLotName = null;
        String propertyName = null;
        if(building instanceof Building){
            parentClassName = ((Building)building).getType().name();
            parkingLotName = ((Building)building).getParkingLotName();
            propertyName = OntologyConstants.Parking_has_building.name();
        }else{
            parentClassName = ((Infrastructure)building).getType().name();
            parkingLotName = ((Infrastructure)building).getParkingLotName();
            propertyName = OntologyConstants.Parking_has_infrastructure.name();
        }

        // create instance in the ontology
        createSimpleInstance(parentClassName, building.getId(), properties);    

        //update platform with camera
        if(StringHelper.hasContent(parkingLotName)){
            String[] buildings = getInstanceProperties(parkingLotName, propertyName);
            if(!StringHelper.arrayContainsItem(buildings, building.getId())){
                addPropertyValue(parkingLotName, propertyName, building.getId());
            }
        }
    }

    /**
     * generates zone name in form of 'zone_%id', where 'id' is an unique zone identifier
     * 
     * @return zone name
     */
    private String generateZoneName() {
        // zone name format
        String zoneNameFormat = "zone_%d";
        // get all available instances
        List<String> zones = getDirectInstances(OntologyConstants.Car_parking_zone.name());
        // start counting
        int startId = zones.size();
        // search for first, not found zone
        while (zones.contains(String.format(zoneNameFormat, startId++)))
            ;
        return String.format(zoneNameFormat, startId--);
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

    private double getValue(Double value){
        return value == null ? 0 : value.doubleValue();
    }

    private int getValue(Integer value){
        return value == null ? 0 : value.intValue();
    }
}
