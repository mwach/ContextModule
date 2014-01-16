package itti.com.pl.arena.cm.client;

import java.util.Properties;
import java.util.UUID;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.client.service.ContextModuleClientException;
import itti.com.pl.arena.cm.client.service.ContextModuleFacade;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.service.Constants.ContextModuleRequests;
import itti.com.pl.arena.cm.utils.helper.IOHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.PropertiesHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;
import eu.arena_fp7._1.AbstractDataFusionType;
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

    //client properties
    private static Properties properties = new Properties();

    //Arena bus wrapper
    private ContextModule contextModule = null;
    private ObjectFactory objectFactory = null;

    /**
     * client main class
     * @param args
     */
    public static void main(String[] args) {

        //initialize the client
        CMClient client = new CMClient();
        try {
            //parse properties
            String propertiesFileName = getPropertiesFileName(args);
            client.loadProperties(propertiesFileName);
            //initialize the client
            client.init();
            //call all available CM public services
            parseGetPlatformServiceResponse(client.getPlatformService("Truck_A1"));
            parseGetPlatformsServiceResponse(client.getPlatformsService(0.12, 51.1));
            parseGetGISDataServiceResponse(client.getGISDataService(0.1, 51.0));
            parseGetGeoportalDataServiceResponse(client.getGeoportalDataService(17.974734282593246, 53.12344164937794));
        } catch (ContextModuleClientException | RuntimeException exc) {
            LogHelper.error(CMClient.class, "main", "Could not perform opertation. Details: %s", exc.getMessage());
            printUsage();
        } finally {
            client.shutdown();
        }
        //call 'exit' to interrupt the client listener thread
        System.exit(0);
    }

    /**
     * Returns name of the properties file, (the first passed argument)
     * @param args command arguments
     * @return name of the property file (first argument)
     * @throws ContextModuleClientException could not parse arguments
     */
    private static String getPropertiesFileName(String[] args) throws ContextModuleClientException {
        if (args == null || args.length != 1) {
            throw new ContextModuleClientException("Properties file name not provided");
        }
        return args[0];
    }

    /**
     * Parses response received from getPlatform service
     * @param platform information about truck
     */
    private static void parseGetPlatformServiceResponse(Object platform) {
        if (platform != null) {
            for (AbstractDataFusionType feature : platform.getFeatureVector().getFeature()) {
                LogHelper.info(CMClient.class, "parseGetPlatformServiceResponse", 
                        "%s: %s", feature.getId(), ((SimpleNamedValue) feature).getValue());
            }
        }
    }

    /**
     * Parses response received from getPlatforms service
     * @param platforms information about trucks
     */
    private static void parseGetPlatformsServiceResponse(Situation platforms) {
        if (platforms != null && platforms.getGlobalSceneProperty() != null) {
            for (AbstractDataFusionType feature : platforms.getGlobalSceneProperty().getFeature()) {
                LogHelper.info(CMClient.class, "parseGetPlatformsServiceResponse", 
                        "%s: %s", feature.getId(), ((SimpleNamedValue) feature).getValue());

            }
        }
    }

    /**
     * Parses response received from getGISData service
     * @param gisData information about GIS objects
     */
    private static void parseGetGISDataServiceResponse(Situation gisData) {
        if (gisData != null && gisData.getGlobalSceneProperty() != null) {
            for (AbstractDataFusionType feature : gisData.getGlobalSceneProperty().getFeature()) {
                LogHelper.info(CMClient.class, "parseGetGISDataServiceResponse", 
                        "%s: %s", feature.getId(), ((SimpleNamedValue) feature).getValue());
            }
        }
    }

    /**
     * Parses response received from getGeoportalData service
     * @param geoportalData information retrieved from geoportal
     */
    private static void parseGetGeoportalDataServiceResponse(Situation geoportalData) {
        if (geoportalData != null && geoportalData.getGlobalSceneProperty() != null) {
            for (AbstractDataFusionType feature : geoportalData.getGlobalSceneProperty().getFeature()) {
                LogHelper.info(CMClient.class, "parseGetgeoportalDataServiceResponse", 
                        "%s: %s", feature.getId(), ((SimpleNamedValue) feature).getValue());
            }
        }
    }

    /**
     * Loads properties from given file
     * Also performs some basic validation
     * @param propertiesFile location of the properties file
     * @throws ContextModuleClientException
     */
    private void loadProperties(String propertiesFile) throws ContextModuleClientException {

        try {
            properties.putAll(PropertiesHelper.loadPropertiesAsMap(propertiesFile));
        } catch (IOHelperException e) {
            throw new ContextModuleClientException(String.format("Could not load properties file. Details: '%s'",
                    e.getLocalizedMessage()));
        }
        if (!StringHelper
                .hasContent(PropertiesHelper.getPropertyAsString(properties, ClientPropertyNames.brokerUrl.name(), null))) {
            throw new ContextModuleClientException(String.format("Required property '%s' not found",
                    ClientPropertyNames.brokerUrl.name()));
        }
    }

    /**
     * Returns information about platform
     * @param platformId ID of the platform
     * @return Object containing information retrieved from ContextModule
     */
    public Object getPlatformService(String platformId) {
        SimpleNamedValue objectId = createSimpleNamedValue(platformId);
        objectId.setHref(ContextModuleRequests.getPlatform.name());
        Object data = contextModule.getPlatform(objectId);
        LogHelper.info(CMClient.class, "getPlatformsService", "Server response received:\n%s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about platforms
     * @param x longitude
     * @param y latitude
     * @return Object containing information retrieved from ContextModule
     */
    public Situation getPlatformsService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getPlatforms.name());
        Situation data = contextModule.getPlatforms(objectLocation);
        LogHelper.info(CMClient.class, "getPlatformsService", "Server response received:\n%s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about GIS data
     * @param x longitude
     * @param y latitude
     * @return Object containing information retrieved from ContextModule
     */
    public Situation getGISDataService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getGISData.name());
        Situation data = contextModule.getPlatforms(objectLocation);
        LogHelper.info(CMClient.class, "getGISDataService", "Server response received:\n%s", String.valueOf(data));
        return data;
    }

    /**
     * Returns information about GIS data
     * @param x longitude
     * @param y latitude
     * @return Object containing information retrieved from external Geoportal service
     */
    public Situation getGeoportalDataService(double x, double y) {
        Location objectLocation = createLocation(x, y);
        objectLocation.setHref(ContextModuleRequests.getGeoportalData.name());
        Situation data = contextModule.getGeoportalData(objectLocation);
        LogHelper.info(CMClient.class, "getGeoportalDataService", "Server response received:\n%s", String.valueOf(data));
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

        //URL of the arena bus
        String brokerUrl = PropertiesHelper.getPropertyAsString(properties, ClientPropertyNames.brokerUrl.name(), null);

        //client's port
        int clientPort = PropertiesHelper.getPropertyAsInteger(properties, ClientPropertyNames.clientPort.name(), -1);

        //optional - client run in the debug mode
        boolean debugMode = PropertiesHelper.getPropertyAsBoolean(properties, ClientPropertyNames.debugMode.name(), false);
        //optional - maximum client waiting time (waiting for the response from CM)
        int responseWaitingTime = PropertiesHelper.getPropertyAsInteger(properties,
                ClientPropertyNames.responseWaitingTime.name(), 5000);

        ContextModuleFacade cmFacade = new ContextModuleFacade(CLIENT_MODULE_NAME, brokerUrl);
        cmFacade.setDebug(debugMode);
        cmFacade.setResponseWaitingTime(responseWaitingTime);

        if (clientPort > 0 && clientPort < 65500) {
            cmFacade.setClientPort(clientPort);
        }else{
            LogHelper.info(CMClient.class, "init", String.format("Client port out of range: %d", clientPort));
        }

        //initialize the client (tries to connect to the Arena bus)
        cmFacade.init();

        this.contextModule = cmFacade;
        this.objectFactory = new ObjectFactory();
    }

    /**
     * Creates simple request object
     * @param value String value to be send to the CM
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
     * @param x longitude
     * @param y latitude
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
     * Returns random, unique message ID
     * @return message ID
     */
    private String getObjectId() {
        return String.format("%s.%s.%s", Constants.MODULE_NAME, CLIENT_MODULE_NAME, UUID.randomUUID().toString());
    }

    /**
     * Shutdowns the client
     */
    public void shutdown() {
        if (contextModule != null && contextModule instanceof ContextModuleFacade) {
            ((ContextModuleFacade) contextModule).shutdown();
        }else{
            LogHelper.warning(CMClient.class, "shutdown", String.format("Could not shutdown module. "
                    + "Unrecognized client class module: %s", contextModule));
        }
    }

}
