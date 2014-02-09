package itti.com.pl.arena.cm.client;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.client.service.ContextModuleClientException;
import itti.com.pl.arena.cm.client.service.ContextModuleFacade;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.service.Constants.ContextModuleRequests;
import itti.com.pl.arena.cm.utils.helper.IOHelperException;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.PropertiesHelper;
import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.FeatureVector;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.SimpleNamedValue;
import eu.arena_fp7._1.Situation;

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
            //get the platform info
            parseGetPlatformServiceResponse(client.getPlatformService("Vehicle_CB04432"));
            //get all the platforms from given location
            parseGetPlatformsServiceResponse(client.getPlatformsService(-0.94, 51.43));
            //get GIS data from the ontology
            parseGetGISDataServiceResponse(client.getGISDataService(-0.94, 51.43));
            //get GIS data from the ontology using additional filters
            parseGetGISDataServiceResponse(client.getGISDataService(-0.94, 51.43, 1.0, "Parking"));
            //retrieve data from the external service (geoportal) and add it to ontology
            parseGetGeoportalDataServiceResponse(client.getGeoportalDataService(17.972946559166793, 53.124318916278824));
            //retrieve info about camera field of view
            parseGetCameraFieldOfViewResponse(client.getCameraFieldOfView("Camera_3"));

        } catch (ContextModuleClientException exc) {
            //CM exception e.g. properties file parsing
            LogHelper.error(CMClient.class, "main", "Could not perform operation. Details: %s", exc.getMessage());
            printUsage();
        } catch (RuntimeException | JsonHelperException exc) {
            //other exception e.g. response parsing error
            LogHelper.error(CMClient.class, "main", "Could not perform operation. Details: %s", exc.getMessage());
        } finally {
            //shutdown the client
            client.shutdown();
        }
        // call 'exit' to interrupt the client listener thread
        System.exit(0);
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
    private static void parseGetPlatformsServiceResponse(Situation platforms) {
        if (platforms != null) {
            parseFeatureVector(platforms.getGlobalSceneProperty());
        }
    }

    /**
     * Parses response received from getGISData service
     * 
     * @param gisData
     *            information about GIS objects
     */
    private static void parseGetGISDataServiceResponse(Situation gisData) {
        if (gisData != null) {
            parseFeatureVector(gisData.getGlobalSceneProperty());
        }
    }

    /**
     * Parses response received from getGeoportalData service
     * 
     * @param geoportalData
     *            information retrieved from geoportal
     */
    private static void parseGetGeoportalDataServiceResponse(Situation geoportalData) {
        if (geoportalData != null) {
            parseFeatureVector(geoportalData.getGlobalSceneProperty());
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
     * Parses response received from getGeoportalData service
     * 
     * @param geoportalData
     *            information retrieved from geoportal
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
        //check, if all required properties were defined in the file
        if(!PropertiesHelper.hasProperty(properties, ClientPropertyNames.brokerUrl.name())){
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
    public Situation getPlatformsService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getPlatforms.name());
        Situation data = contextModule.getPlatforms(objectLocation);
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
    public Situation getGISDataService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getGISData.name());
        Situation data = contextModule.getGISData(objectLocation);
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
    public Situation getGISDataService(double x, double y, Double radius, String... classes) throws JsonHelperException {

        Location locationObject = createLocation(x, y);

        SimpleNamedValue radiusObject = createSimpleNamedValue(radius != null ? String.valueOf(radius) : String
                .valueOf(Constants.UNDEFINED_VALUE));

        SimpleNamedValue classesObject = createSimpleNamedValue(JsonHelper.toJson(classes));

        Situation situation = createSituation(locationObject, radiusObject, classesObject);
        situation.setHref(ContextModuleRequests.getGISDataExt.name());

        Situation data = contextModule.getGISData(situation);

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
    public Situation getGeoportalDataService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getGeoportalData.name());
        Situation data = contextModule.getGeoportalData(objectLocation);
        LogHelper.info(CMClient.class, "getGeoportalDataService", "Server response received: %s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about objects in the camera field of view
     * @param cameraId ID of the camera
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
     * Prints command usage
     */
    public static void printUsage() {
        LogHelper.info(CMClient.class, "printUsage", String.format("Usage: %s <propertiesFile>", CLIENT_MODULE_NAME));
    }

    /**
     * Initializes client module. Connects to the Arena bus
     */
    public void init() {

        // URL of the arena bus
        String brokerUrl = PropertiesHelper.getPropertyAsString(properties, ClientPropertyNames.brokerUrl.name(), null);

        // client's port
        int clientPort = PropertiesHelper.getPropertyAsInteger(properties, ClientPropertyNames.clientPort.name(), ClientDefaults.DEFAULT_CLIENT_PORT);

        // debug mode state
        boolean debugMode = PropertiesHelper.getPropertyAsBoolean(properties, ClientPropertyNames.debugMode.name(), ClientDefaults.DEFAULT_DEBUG_MODE);

        // maximum client waiting time
        int responseWaitingTime = PropertiesHelper.getPropertyAsInteger(properties, ClientPropertyNames.responseWaitingTime.name(), ClientDefaults.DEFAULT_WAITING_TIME);

        //create CM facade object
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
     * Creates simple location object
     * 
     * @param x
     *            longitude
     * @param y
     *            latitude
     * @return created request object
     */
    private Situation createSituation(AbstractNamedValue... params) {
        Situation object = objectFactory.createSituation();
        object.setId(getObjectId());
        object.setDataSourceId(CLIENT_MODULE_NAME);
        object.setGlobalSceneProperty(new FeatureVector());
        object.getGlobalSceneProperty().getFeature().addAll(Arrays.asList(params));
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
