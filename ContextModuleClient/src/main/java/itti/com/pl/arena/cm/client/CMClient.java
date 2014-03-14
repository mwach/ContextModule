package itti.com.pl.arena.cm.client;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.client.service.ContextModuleClientException;
import itti.com.pl.arena.cm.client.service.ContextModuleFacade;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.CameraType;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.Building.Type;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequests;
import itti.com.pl.arena.cm.utils.helper.IOHelperException;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.PropertiesHelper;
import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.FeatureVector;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * Sample client class showing base CM functionalities
 * 
 * @author cm-admin
 * 
 */
public class CMClient {

    // name of the client: used for logging purposes
    private static final String CLIENT_MODULE_NAME = CMClient.class.getSimpleName();

    // client properties
    private static Properties properties = new Properties();

    // Arena bus wrapper
    private ContextModule contextModule = null;
    // factory used to create Arena bus objects
    private ObjectFactory objectFactory = null;

    /**
     * client main class
     * 
     * @param args
     */
    public static void main(String[] args) {

        // initialize the client
        CMClient client = new CMClient();
        try {
            // parse properties
            String propertiesFileName = getPropertiesFileName(args);
            client.loadProperties(propertiesFileName);
            // initialize the client
            client.init();
            // call all available CM public services
            // get the platform info
            parseGetPlatformServiceResponse(client.getPlatformService("Vehicle_CB04432"));
            // get all the platforms from given location
            parseGetPlatformsServiceResponse(client.getPlatformsService(-0.94, 51.43));
            // get GIS data from the ontology
            parseGetGISDataServiceResponse(client.getGISDataService(-0.94, 51.43));
            // get GIS data from the ontology using additional filters
            parseGetGISDataServiceResponse(client.getGISDataService(-0.94, 51.43, 1.0, "Parking"));
            // retrieve data from the external service (geoportal) and add it to ontology
            parseGetGeoportalDataServiceResponse(client.getGeoportalDataService(17.972946559166793, 53.124318916278824));
            // retrieve info about camera field of view
            parseGetCameraFieldOfViewResponse(client.getCameraFieldOfView("Camera_3"));

            Platform platform = createDummyPlatform("dummyPlatform_" + System.currentTimeMillis());
            parseUpdatePlatformResponse(client.updatePlatform(platform));

            ParkingLot parkingLot = createDummyParkingLot("dummyParkingLot_" + System.currentTimeMillis());
            parseUpdatePlatformResponse(client.updateParkingLot(parkingLot));

            // defines new zone in the ontology
            String zoneId = parseDefineZoneResponse(client.defineZone(new double[][] {
                    // define square-shaped zone
                    { -1.0, -1.0 }, { 1.0, -1.0 }, { 1.0, 1.0 }, { -1.0, 1.0 } }));

            // retrieves zone information from the ontology
            parseGetZoneResponse(client.getZone(zoneId));

            
        } catch (ContextModuleClientException exc) {
            // CM exception e.g. properties file parsing
            LogHelper.error(CMClient.class, "main", "Could not perform operation. Details: %s", exc.getMessage());
            printUsage();
        } catch (RuntimeException | JsonHelperException exc) {
            // other exception e.g. response parsing error
            LogHelper.error(CMClient.class, "main", "Could not perform operation. Details: %s", exc.getMessage());
        } finally {
            // shutdown the client
            client.shutdown();
        }
        // call 'exit' to interrupt the client listener thread
        System.exit(0);
    }

    private static ParkingLot createDummyParkingLot(String parkingLotId) {
        ParkingLot parkingLot = new ParkingLot(parkingLotId);

        //general information about parking
        parkingLot.setCountry("UK");
        parkingLot.setLocation(new itti.com.pl.arena.cm.dto.Location(-0.94, 51.43));
        parkingLot.setTown("Reading");
        parkingLot.setStreet("London Street");
        parkingLot.setStreetNumber(23);

        //add parking boundaries
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94334, 51.43234));
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94345, 51.43233));
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94332, 51.43245));
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94366, 51.43212));

        //parking infrastructure
        parkingLot.addBuilding(createDummyBuilding("dummyBuilding_" + System.currentTimeMillis()));
        parkingLot.addBuilding(createDummyBuilding("dummyBuilding_" + System.currentTimeMillis()));
        parkingLot.addIntrastructure(createDummyInfrastructure("dummyBuilding_" + System.currentTimeMillis()));

        return parkingLot;
    }

    private static Building createDummyBuilding(String buildingId) {

        //new building object
        Building building = new Building(buildingId, Type.Hotel);

        //building boundaries
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94334, 51.43234));
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94345, 51.43233));
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94332, 51.43245));
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94366, 51.43212));

        return building;
    }

    private static Infrastructure createDummyInfrastructure(String infrastructureId) {

        //new infrastructure object
        Infrastructure infrastructure = new Infrastructure(infrastructureId, Infrastructure.Type.Fence);

        //building boundaries
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94334, 51.43234));
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94345, 51.43233));
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94332, 51.43245));
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94366, 51.43212));

        return infrastructure;
    }

    private static Platform createDummyPlatform(String platformId) {
        Platform platform = new Platform(platformId);
        //platform cameras
        platform.addCamera(createDummyCamera("dummyCamera_" + System.currentTimeMillis()));
        platform.addCamera(createDummyCamera("anotherDummyCamera_" + System.currentTimeMillis()));
        //platform dimensions
        platform.setHeight(3);
        platform.setWidth(3);
        platform.setLength(15);
        //platform location
        platform.setLocation(new itti.com.pl.arena.cm.dto.Location(23.434, 32.235235));
        return platform;
    }

    private static Camera createDummyCamera(String cameraId) {
        //thermal camera located on the right side of the truck (X coordinate is set to '2'), 5m back from the front
        //angle is 0.5 rad (90 degree), which means, camera is directed to the right side of the truck
        return new Camera(cameraId, CameraType.Thermal.name(), 120, 90, new CartesianCoordinate(2, -5), 90);
    }

    /**
     * Returns name of the properties file, (the first passed argument)
     * 
     * @param args
     *            command arguments
     * @return name of the property file (first argument)
     * @throws ContextModuleClientException
     *             could not parse arguments
     */
    private static String getPropertiesFileName(String[] args) throws ContextModuleClientException {
        if (args == null || args.length != 1) {
            throw new ContextModuleClientException("Properties file name not provided");
        }
        return args[0];
    }

    /**
     * Parses response received from getPlatform service
     * 
     * @param platform
     *            information about truck
     */
    private static void parseGetPlatformServiceResponse(Object platform) {
        if (platform != null) {
            parseFeatureVector(platform.getFeatureVector());
        }
    }

    /**
     * Parses response received from getPlatforms service
     * 
     * @param platforms
     *            information about trucks
     */
    private static void parseGetPlatformsServiceResponse(Object platforms) {
        if (platforms != null) {
            parseFeatureVector(platforms.getFeatureVector());
        }
    }

    /**
     * Parses response received from getGISData service
     * 
     * @param gisData
     *            information about GIS objects
     */
    private static void parseGetGISDataServiceResponse(Object gisData) {
        if (gisData != null) {
            parseFeatureVector(gisData.getFeatureVector());
        }
    }

    /**
     * Parses response received from getGeoportalData service
     * 
     * @param geoportalData
     *            information retrieved from geoportal
     */
    private static void parseGetGeoportalDataServiceResponse(Object geoportalData) {
        if (geoportalData != null) {
            parseFeatureVector(geoportalData.getFeatureVector());
        }
    }

    /**
     * Parses response received from getCameraFieldOfView service
     * 
     * @param cameraData
     *            information retrieved from the ontology about camera
     */
    private static void parseGetCameraFieldOfViewResponse(Object cameraData) {
        if (cameraData != null) {
            parseFeatureVector(cameraData.getFeatureVector());
        }
    }

    /**
     * Parses response received from defineZone service
     * 
     * @param zoneResponse
     *            information about created zone retrieved from the ontology
     * @return ID of the zone
     */
    private static String parseDefineZoneResponse(SimpleNamedValue zoneResponse) {
        if (zoneResponse != null) {
            parseSimpleNamedValue(zoneResponse);
            return zoneResponse.getValue();
        }
        return null;
    }

    /**
     * Parses response received from defineZone service
     * 
     * @param zoneResponse
     *            information about created zone retrieved from the ontology
     */
    private static void parseGetZoneResponse(Object zoneResponse) {
        if (zoneResponse != null) {
            parseFeatureVector(zoneResponse.getFeatureVector());
        }
    }

    /**
     * Parses response received from updatePlatform service
     * 
     * @param updatePlatformResponse
     *            status information: true (success) or false (failure)
     */
    private static void parseUpdatePlatformResponse(BooleanNamedValue updatePlatformResponse) {
        if (updatePlatformResponse != null) {
            parseBooleanNamedValue(updatePlatformResponse);
        }
    }
    
    /**
     * Parses response stored inside {@link FeatureVector} object
     * 
     * @param featureVector
     *            information stored inside {@link FeatureVector}
     */
    private static void parseFeatureVector(FeatureVector featureVector) {
        if (featureVector != null) {
            StringBuilder output = new StringBuilder();
            output.append("\n");
            for (AbstractDataFusionType feature : featureVector.getFeature()) {
                if (feature instanceof Location) {
                    output.append(String.format("       %s: %f, %f", feature.getId(), ((Location) feature).getX(),
                            ((Location) feature).getY()));
                } else {
                    output.append(String.format("       %s: %s", feature.getId(), ((SimpleNamedValue) feature).getValue()));
                }
                output.append("\n");
            }
            LogHelper.info(CMClient.class, "parseFeatureList", output.toString());

        }
    }

    /**
     * Parses response stored inside {@link SimpleNamedValue} object
     * 
     * @param value
     *            information stored inside {@link SimpleNamedValue}
     */
    private static void parseSimpleNamedValue(SimpleNamedValue value) {
        if (value != null) {
            StringBuilder output = new StringBuilder();
            output.append("\n");
            output.append(String.format("       %s: %s", value.getId(), value.getValue()));
            output.append("\n");
            LogHelper.info(CMClient.class, "parseFeatureList", output.toString());
        }
    }

    /**
     * Parses response stored inside {@link BooleanNamedValue} object
     * 
     * @param value
     *            information stored inside {@link BooleanNamedValue}
     */
    private static void parseBooleanNamedValue(BooleanNamedValue value) {
        if (value != null) {
            StringBuilder output = new StringBuilder();
            output.append("\n");
            output.append(String.format("       %s: %b", value.getFeatureName(), value.isFeatureValue()));
            output.append("\n");
            LogHelper.info(CMClient.class, "parseBooleanNamedValue", output.toString());
        }
    }

    /**
     * Loads properties from given file Also performs some basic validation
     * 
     * @param propertiesFile
     *            location of the properties file
     * @throws ContextModuleClientException
     */
    private void loadProperties(String propertiesFile) throws ContextModuleClientException {

        try {
            properties.putAll(PropertiesHelper.loadProperties(propertiesFile));
        } catch (IOHelperException e) {
            throw new ContextModuleClientException(String.format("Could not load properties file. Details: '%s'",
                    e.getLocalizedMessage()));
        }
        // check, if all required properties were defined in the file
        if (!PropertiesHelper.hasProperty(properties, ClientPropertyNames.brokerUrl.name())) {
            throw new ContextModuleClientException(String.format("Required property '%s' not found",
                    ClientPropertyNames.brokerUrl.name()));
        }
    }

    /**
     * Returns information about platform
     * 
     * @param platformId
     *            ID of the platform
     * @return Object containing information retrieved from ContextModule
     */
    public Object getPlatformService(String platformId) {
        SimpleNamedValue objectId = createSimpleNamedValue(platformId);
        objectId.setHref(ContextModuleRequests.getPlatform.name());
        Object data = contextModule.getPlatform(objectId);
        LogHelper.debug(CMClient.class, "getPlatformsService", "Server response received: %s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about platforms
     * 
     * @param x
     *            longitude
     * @param y
     *            latitude
     * @return Object containing information retrieved from ContextModule
     */
    public Object getPlatformsService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getPlatforms.name());
        Object data = contextModule.getPlatforms(objectLocation);
        LogHelper.info(CMClient.class, "getPlatformsService", "Server response received: %s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about GIS data
     * 
     * @param x
     *            longitude
     * @param y
     *            latitude
     * @return Object containing information retrieved from ContextModule
     */
    public Object getGISDataService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getGISData.name());
        Object data = contextModule.getGISData(objectLocation);
        LogHelper.info(CMClient.class, "getGISDataService", "Server response received: %s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about GIS data
     * 
     * @param x
     *            longitude
     * @param y
     *            latitude
     * @param radius
     *            radius (in meters)
     * @param classes
     *            list of object types, which should be returned by the service
     * @return Object containing information retrieved from ContextModule
     * @throws JsonHelperException
     *             could not create request object
     */
    public Object getGISDataService(double x, double y, Double radius, String... classes) throws JsonHelperException {

        Location locationObject = createLocation(x, y);

        SimpleNamedValue radiusObject = createSimpleNamedValue(radius != null ? String.valueOf(radius) : String
                .valueOf(Constants.UNDEFINED_VALUE));

        SimpleNamedValue classesObject = createSimpleNamedValue(JsonHelper.toJson(classes));

        Object requestData = createObject(locationObject, radiusObject, classesObject);
        requestData.setHref(ContextModuleRequests.getGISDataExt.name());

        Object data = contextModule.getGISData(requestData);

        LogHelper.info(CMClient.class, "getGISDataService", "Server response received: %s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about GIS data
     * 
     * @param x
     *            longitude
     * @param y
     *            latitude
     * @return Object containing information retrieved from external Geoportal service
     */
    public Object getGeoportalDataService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getGeoportalData.name());
        Object data = contextModule.getGeoportalData(objectLocation);
        LogHelper.info(CMClient.class, "getGeoportalDataService", "Server response received: %s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about objects in the camera field of view
     * 
     * @param cameraId
     *            ID of the camera
     * @return Object containing information about camera field of view (ontology data)
     */
    public Object getCameraFieldOfView(String cameraId) {
        SimpleNamedValue cameraObject = createSimpleNamedValue(cameraId);
        cameraObject.setHref(ContextModuleRequests.getCameraFieldOfView.name());
        Object data = contextModule.getCameraFieldOfView(cameraObject);
        LogHelper.info(CMClient.class, "getCameraFieldOfView", "Server response received: %s", String.valueOf(data));
        return data;
    }

    /**
     * Creates a new zone object from list of provided coordinates
     * 
     * @param coordinates
     *            coordinates defining size and shape of the zone
     * @return {@link SimpleNamedValue} object containing ID of the created zone
     */
    private SimpleNamedValue defineZone(double[][] coordinates) {
        AbstractNamedValue[] requestParams = null;
        // check, if there are coordinates provided
        if (coordinates != null) {
            requestParams = new AbstractNamedValue[coordinates.length];
            // for each coordinate
            for (int i = 0; i < coordinates.length; i++) {
                double[] coordinate = coordinates[i];
                // validate it first
                if (coordinate != null && coordinate.length == 2) {
                    // if valid, create location object
                    requestParams[i] = createLocation(coordinate[0], coordinate[1]);
                }
            }
        }
        Object requestObject = createObject(requestParams);
        requestObject.setHref(ContextModuleRequests.defineZone.name());
        SimpleNamedValue data = contextModule.defineZone(requestObject);
        LogHelper.info(CMClient.class, "defineZone", "Server response received: %s", String.valueOf(data));
        return data;
    }
    

    /**
     * Returns information about zone
     * 
     * @param zoneId
     *            ID of the zone
     * @return Object containing information retrieved from ContextModule
     */
    public Object getZone(String zoneId) {
        SimpleNamedValue objectId = createSimpleNamedValue(zoneId);
        objectId.setHref(ContextModuleRequests.getZone.name());
        Object data = contextModule.getZone(objectId);
        LogHelper.debug(CMClient.class, "getZone", "Server response received: %s", String.valueOf(data));
        return data;
    }

    private BooleanNamedValue updateParkingLot(ParkingLot parkingLot) throws ContextModuleClientException {
        String serializedObject = null;
        try{
            serializedObject = JsonHelper.toJson(parkingLot);
        }catch(JsonHelperException exc){
            throw new ContextModuleClientException(exc.getLocalizedMessage());
        }
        SimpleNamedValue platformRequest = createSimpleNamedValue(serializedObject);
        platformRequest.setHref(ContextModuleRequests.updateParkingLot.name());
        BooleanNamedValue data = contextModule.updateParkingLot(platformRequest);
        LogHelper.debug(CMClient.class, "updateParkingLot", "Server response received: %s", String.valueOf(data));
        return data;
    }

    private BooleanNamedValue updatePlatform(Platform platform) throws ContextModuleClientException {
        String serializedObject = null;
        try{
            serializedObject = JsonHelper.toJson(platform);
        }catch(JsonHelperException exc){
            throw new ContextModuleClientException(exc.getLocalizedMessage());
        }
        SimpleNamedValue platformRequest = createSimpleNamedValue(serializedObject);
        platformRequest.setHref(ContextModuleRequests.updatePlatform.name());
        BooleanNamedValue data = contextModule.updatePlatform(platformRequest);
        LogHelper.debug(CMClient.class, "updatePlatform", "Server response received: %s", String.valueOf(data));
        return data;
    }


    /**
     * Prints command usage
     */
    public static void printUsage() {
        LogHelper.info(CMClient.class, "printUsage", String.format("Usage: %s <propertiesFile>", CLIENT_MODULE_NAME));
    }

    /**
     * Initializes client module. Connects to the Arena bus
     * @throws ContextModuleClientException could not initialize client
     */
    public void init() throws ContextModuleClientException {

        // URL of the arena bus
        String brokerUrl = PropertiesHelper.getPropertyAsString(properties, ClientPropertyNames.brokerUrl.name(), null);

        // client's port
        int clientPort = PropertiesHelper.getPropertyAsInteger(properties, ClientPropertyNames.clientPort.name(),
                ClientDefaults.DEFAULT_CLIENT_PORT);

        // debug mode state
        boolean debugMode = PropertiesHelper.getPropertyAsBoolean(properties, ClientPropertyNames.debugMode.name(),
                ClientDefaults.DEFAULT_DEBUG_MODE);

        // maximum client waiting time
        int responseWaitingTime = PropertiesHelper.getPropertyAsInteger(properties,
                ClientPropertyNames.responseWaitingTime.name(), ClientDefaults.DEFAULT_WAITING_TIME);

        // create CM facade object
        ContextModuleFacade cmFacade = new ContextModuleFacade(CLIENT_MODULE_NAME, brokerUrl);
        cmFacade.setDebug(debugMode);
        cmFacade.setResponseWaitingTime(responseWaitingTime);
        cmFacade.setClientPort(clientPort);

        // initialize the client (tries to connect to the Arena bus)
        cmFacade.init();

        this.contextModule = cmFacade;
        this.objectFactory = new ObjectFactory();
    }

    /**
     * Creates simple request object
     * 
     * @param value
     *            String value to be send to the CM
     * @return created request object
     */
    private SimpleNamedValue createSimpleNamedValue(String value) {
        SimpleNamedValue object = objectFactory.createSimpleNamedValue();
        object.setId(getObjectId());
        object.setDataSourceId(CLIENT_MODULE_NAME);
        object.setValue(value);
        return object;
    }

    /**
     * Creates simple location object
     * 
     * @param x
     *            longitude
     * @param y
     *            latitude
     * @return created request object
     */
    private Location createLocation(double x, double y) {
        Location object = objectFactory.createLocation();
        object.setId(getObjectId());
        object.setDataSourceId(CLIENT_MODULE_NAME);
        object.setX(x);
        object.setY(y);
        return object;
    }

    /**
     * Creates simple {@link Object} object
     * 
     * @param params
     *            objects to be put in the {@link FeatureVector} of that object
     * 
     * @return created request object
     */
    private Object createObject(AbstractNamedValue... params) {
        Object object = objectFactory.createObject();
        object.setId(getObjectId());
        object.setDataSourceId(CLIENT_MODULE_NAME);
        object.setFeatureVector(new FeatureVector());
        if (params != null) {
            object.getFeatureVector().getFeature().addAll(Arrays.asList(params));
        }
        return object;
    }

    /**
     * Returns random, unique message ID
     * 
     * @return message ID
     */
    private String getObjectId() {
        return String.format("%s.%s.%s", Constants.MODULE_NAME, CLIENT_MODULE_NAME, UUID.randomUUID().toString());
    }

    /**
     * Shutdowns the client
     */
    public void shutdown() {

        // try to shutdown the client
        if (contextModule != null && contextModule instanceof ContextModuleFacade) {
            ((ContextModuleFacade) contextModule).shutdown();
            // unrecognized client class
        } else if (contextModule != null) {
            LogHelper.warning(CMClient.class, "shutdown", String.format("Could not shutdown module. "
                    + "Unrecognized client class module: %s", contextModule));
        }
    }

}
