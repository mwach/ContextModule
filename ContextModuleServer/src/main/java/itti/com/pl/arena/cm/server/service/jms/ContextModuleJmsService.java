package itti.com.pl.arena.cm.server.service.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.dto.coordinates.ArenaObjectCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.jms.CMModuleImpl;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.geoportal.Geoportal;
import itti.com.pl.arena.cm.server.geoportal.GeoportalException;
import itti.com.pl.arena.cm.server.ontology.Ontology;
import itti.com.pl.arena.cm.server.ontology.OntologyConstants;
import itti.com.pl.arena.cm.server.ontology.OntologyException;
import itti.com.pl.arena.cm.server.service.PlatformListener;
import itti.com.pl.arena.cm.server.service.Service;
import itti.com.pl.arena.cm.server.utils.helpers.LocationFactory;
import itti.com.pl.arena.cm.service.LocalContextModule;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequestProperties;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequests;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.utils.helper.ArenaObjectsMapper;
import itti.com.pl.arena.cm.utils.helper.DateTimeHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelperException;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import com.safran.arena.impl.Client;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * Implementation of the {@link ContextModule} interface
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleJmsService extends CMModuleImpl implements LocalContextModule, Service, PlatformListener {

    /*
     * Objects used to communicate via Arena Bus
     */
    private Client client = null;

    /*
     * Connection properties
     */
    private String brokerUrl;
    private String localIpAddress;
    private String connectionPort;

    /*
     * Reference to the Ontology module
     */
    private Ontology ontology = null;
    /*
     * Reference to the Geoportal module
     */
    private Geoportal geoportal = null;

    /*
     * radius used for search data in ontology module
     */
    private double radius;

    @Required
    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }

    private Ontology getOntology() {
        return ontology;
    }

    @Required
    public void setGeoportal(Geoportal geoportal) {
        this.geoportal = geoportal;
    }

    private Geoportal getGeoportal() {
        return geoportal;
    }

    private String getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    private String getBrokerUrl() {
        return brokerUrl;
    }

    @Required
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    private String getConnectionPort() {
        return connectionPort;
    }

    @Required
    public void setConnectionPort(String connectionPort) {
        this.connectionPort = connectionPort;
    }

    private double getRadius() {
        return radius;
    }

    @Required
    public void setRadius(double radius) {
        this.radius = radius;
    }

    public ContextModuleJmsService() {
        super(Constants.MODULE_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.Service#init()
     */
    @Override
    public void init() {

        try {
            // get the IP address of the host
            setLocalIpAddress(NetworkHelper.getIpAddress());

            // validate input params
            validateParams(getBrokerUrl(), getLocalIpAddress(), getConnectionPort());

            // connect to the Arena Bus
            client = new Client(getBrokerUrl(), getLocalIpAddress(), getConnectionPort());
            client.connectToServer();

            // and register CM module
            client.registerModule(this);
            client.registerModuleAsDataProvider(this);
            client.registerModuleAsDataConsumer(this, new ContextModuleFilter());

        } catch (JmsException exc) {
            LogHelper.error(ContextModuleJmsService.class, "init", "Validation failed: %s", exc.getLocalizedMessage());
            throw new BeanInitializationException(exc.getLocalizedMessage());
        } catch (NetworkHelperException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "init", "Could not set clientIpAddress", exc);
            throw new BeanInitializationException(exc.getLocalizedMessage());
        } catch (RuntimeException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "init", "Could not initialize ContextModule", exc);
            throw new BeanInitializationException(exc.getLocalizedMessage());
        }
    }

    /**
     * Validates, if all parameters required by the ContextModule service were provided
     */
    private void validateParams(String serverIpAddress, String clientIpAddress, String clientPort) throws JmsException {

        if (!StringHelper.hasContent(serverIpAddress)) {
            throw new JmsException(ErrorMessages.JMS_SERVER_IP_NOT_PROVIDED);
        }

        if (!StringHelper.hasContent(clientIpAddress)) {
            throw new JmsException(ErrorMessages.JMS_CLIENT_IP_NOT_PROVIDED);
        }

        if (!StringHelper.hasContent(clientPort)) {
            throw new JmsException(ErrorMessages.JMS_CLIENT_PORT_NOT_PROVIDED);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.Service#shutdown()
     */
    @Override
    public void shutdown() {
        try {
            client.unregisterModule(this);
        } catch (RuntimeException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "shutdown", "Could not shutdown the ContextModule", exc);
        }
    }

    @Override
    public void onDataChanged(Class<? extends AbstractDataFusionType> dataType, String dataSourceId, AbstractDataFusionType data) {

        // Arena Bus request detected
        try {
            // process responses from the Ontology Server only
            if (StringHelper.equalsIgnoreCase(Constants.MODULE_NAME, dataSourceId)) {

                AbstractDataFusionType response = null;

                // try to determine, if any of the ContextModule services was called
                if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getPlatform.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getPlatform((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.updateParkingLot.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = updateParkingLot((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getParkingLot.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getParkingLot((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.updatePlatform.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = updatePlatform((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.updateCamera.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = updateCamera((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getPlatforms.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getPlatforms((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGISData.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getGISData((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGISData.name(), data.getHref())
                        && (data instanceof Object)) {
                    response = getGISData((Object) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGeoportalData.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getGeoportalData((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getPlatformNeighborhood.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getPlatformNeighborhood((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.updateZone.name(), data.getHref())
                        && (data instanceof Object)) {
                    response = updateZone((Object) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getZone.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getZone((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.removeZone.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = removeZone((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getListOfZones.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getListOfZones((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getListOfParkingLots.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getListOfParkingLots((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.defineRule.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = defineRule((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.removeRule.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = removeRule((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.applyRules.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = applyRules((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getListOfRules.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getListOfRules((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getListOfPlatforms.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getListOfPlatforms((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.removePlatform.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = removePlatform((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.removeCamera.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = removeCamera((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getCamera.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getCamera((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.removeParkingLot.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = removeParkingLot((SimpleNamedValue) data);

                } else if (StringHelper.equalsIgnoreCase(data.getDataSourceId(), dataSourceId)) {
                    // special cases: error, loop detected
                    LogHelper.warning(ContextModuleJmsService.class, "onDataChanged", "Possible loop detected: "
                            + "DataSourceId is the same as data.dataSourceId. DataId: %s", data.getId());
                    response = null;
                } else {
                    // invalid service name provided
                    LogHelper.info(ContextModuleJmsService.class, "onDataChanged", "Invalid method requested: '%s'",
                            data.getHref());
                    SimpleNamedValue invalidRequestResponse = createSimpleNamedValue(data.getId(),
                            ContextModuleRequestProperties.Error.name(),
                            String.format("Unsupprted service name specified: '%s'", data.getHref()));
                    response = invalidRequestResponse;
                }

                // prepare valid response object
                if (response != null) {
                    response.setHref(data.getHref());
                    client.publish(data.getDataSourceId(), response);
                }
            }
        } catch (RuntimeException exc) {
            LogHelper.info(ContextModuleJmsService.class, "onDataChanged", "Runtime exception catched: %s",
                    exc.getLocalizedMessage());
        }
    }

    @Override
    public Object getPlatform(SimpleNamedValue objectId) {

        List<AbstractNamedValue> vector = new ArrayList<>();

        String platformId = objectId.getValue();
        Platform platform = null;
        // try to retrieve data from ontology
        try {
            platform = getOntology().getOntologyObject(platformId, Platform.class);
        } catch (OntologyException e) {
            LogHelper.exception(ContextModuleJmsService.class, "getPlatform", "Could not retrieve data from ontology", e);
        }
        // data retrieved -try to process it
        if (platform != null) {
            try {
                vector.add(createSimpleNamedValue(platform.getId(), ContextModuleRequestProperties.Platform.name(),
                        JsonHelper.toJson(platform)));
            } catch (JsonHelperException exc) {
                LogHelper.warning(ContextModuleJmsService.class, "getPlatform",
                        "Could not add given object to the response: '%s'. Details: %s", platform, exc.getLocalizedMessage());
            }
        }
        // prepare response object
        Object response = createObject(objectId.getId(), vector);
        return response;
    }

    @Override
    public Object getCamera(SimpleNamedValue cameraRequest) {

        List<AbstractNamedValue> vector = new ArrayList<>();

        String cameraId = cameraRequest.getValue();
        Camera camera = null;
        // try to retrieve data from ontology
        try {
            camera = getOntology().getOntologyObject(cameraId, Camera.class);
        } catch (OntologyException e) {
            LogHelper.exception(ContextModuleJmsService.class, "getPlatform", "Could not retrieve data from ontology", e);
        }
        // data retrieved -try to process it
        if (camera != null) {
            try {
                vector.add(createSimpleNamedValue(camera.getId(), ContextModuleRequestProperties.Platform.name(),
                        JsonHelper.toJson(camera)));
            } catch (JsonHelperException exc) {
                LogHelper.warning(ContextModuleJmsService.class, "getPlatform",
                        "Could not add given object to the response: '%s'. Details: %s", camera, exc.getLocalizedMessage());
            }
        }
        // prepare response object
        Object response = createObject(cameraRequest.getId(), vector);
        return response;
    }

    @Override
    public Object getParkingLot(SimpleNamedValue objectId) {

        List<AbstractNamedValue> vector = new ArrayList<>();

        String parkingLotId = objectId.getValue();
        ParkingLot parkingLot = null;
        // try to retrieve data from ontology
        try {
            parkingLot = getOntology().getOntologyObject(parkingLotId, ParkingLot.class);
        } catch (OntologyException e) {
            LogHelper.exception(ContextModuleJmsService.class, "getParkingLot", "Could not retrieve data from ontology", e);
        }
        // data retrieved -try to process it
        if (parkingLot != null) {
            try {
                vector.add(createSimpleNamedValue(parkingLot.getId(), ContextModuleRequestProperties.ParkingLotName.name(),
                        JsonHelper.toJson(parkingLot)));
            } catch (JsonHelperException exc) {
                LogHelper.warning(ContextModuleJmsService.class, "getParkingLot",
                        "Could not add given object to the response: '%s'. Details: %s", parkingLot, exc.getLocalizedMessage());
            }
        }
        // prepare response object
        Object response = createObject(objectId.getId(), vector);
        return response;
    }

    @Override
    public Object getPlatformNeighborhood(SimpleNamedValue platformIdRequestObject) {

        List<AbstractNamedValue> vector = new ArrayList<>();
        String requestId = null;

        try {
            verifyRequestObject(platformIdRequestObject);

            requestId = StringHelper.toString(platformIdRequestObject.getId());

            String platformId = platformIdRequestObject.getValue();
            // try to retrieve data from ontology
            Set<ArenaObjectCoordinate> objects = getOntology().calculateArenaDistancesForPlatform(platformId);

            // data retrieved -create response message
            if (objects != null) {
                for (ArenaObjectCoordinate objectCoordinate : objects) {
                    // if any of the response objects fail, ignore it
                    try {
                        vector.add(createSimpleNamedValue(platformIdRequestObject.getId(),
                                ContextModuleRequestProperties.Coordinate.name(), JsonHelper.toJson(objectCoordinate)));
                    } catch (JsonHelperException e) {
                        LogHelper.warning(ContextModuleJmsService.class, "getPlatformNeighborhood",
                                "Could not serialize object coordinate: '%s'. Details: %s", objectCoordinate,
                                e.getLocalizedMessage());
                    }
                }
            }

        } catch (OntologyException | JmsException exc) {
            // could not update data
            LogHelper.exception(ContextModuleJmsService.class, "getPlatformNeighborhood",
                    "Could not retrieve data from ontology", exc);
        }

        // prepare response object
        Object response = createObject(requestId, vector);
        return response;
    }

    @Override
    public BooleanNamedValue updateParkingLot(SimpleNamedValue platformObject) {

        ParkingLot parkingLot = null;
        String parkingLotName = null;
        boolean status = false;
        String requestId = null;

        try {
            verifyRequestObject(platformObject);
            requestId = platformObject.getId();

            // try to parse JSON into object
            parkingLot = JsonHelper.fromJson(platformObject.getValue(), ParkingLot.class);
            // update ontology with provided data
            ontology.updateParkingLot(parkingLot);
            parkingLotName = parkingLot.getId();

            status = true;

        } catch (JsonHelperException | OntologyException | JmsException exc) {
            // could not update data
            LogHelper.exception(ContextModuleJmsService.class, "updateParkingLot", "Could not update parking lot", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(requestId, parkingLotName, status);
        return response;
    }

    @Override
    public BooleanNamedValue updatePlatform(SimpleNamedValue platformObject) {

        Platform platform = null;
        String platformName = null;
        boolean status = false;
        String requestId = null;

        try {
            verifyRequestObject(platformObject);
            requestId = platformObject.getId();

            // try to parse JSON into object
            platform = JsonHelper.fromJson(platformObject.getValue(), Platform.class);
            // update ontology with provided data
            ontology.updatePlatform(platform);
            platformName = platform.getId();

            status = true;

        } catch (JsonHelperException | OntologyException | JmsException exc) {
            // could not update data
            LogHelper.exception(ContextModuleJmsService.class, "updatePlatform", "Could not update platform", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(requestId, platformName, status);
        return response;
    }

    @Override
    public BooleanNamedValue updateCamera(SimpleNamedValue platformObject) {

        Camera camera = null;
        String cameraName = null;
        boolean status = false;
        String requestId = null;

        try {
            verifyRequestObject(platformObject);
            requestId = platformObject.getId();

            // try to parse JSON into object
            camera = JsonHelper.fromJson(platformObject.getValue(), Camera.class);
            // update ontology with provided data
            ontology.updateCamera(camera);
            cameraName = camera.getId();

            status = true;

        } catch (JsonHelperException | OntologyException | JmsException exc) {
            // could not update data
            LogHelper.exception(ContextModuleJmsService.class, "updatePlatform", "Could not update camera", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(requestId, cameraName, status);
        return response;
    }

    /**
     * Verifies provided {@link SimpleNamedValue} object Checks, if object is not null and has defined value
     * 
     * @param requestObject
     *            object to be verified
     * @throws JmsException
     *             validation failed
     */
    private void verifyRequestObject(SimpleNamedValue requestObject) throws JmsException {

        // null object provided
        if (requestObject == null) {
            throw new JmsException(ErrorMessages.JMS_NULL_REQUEST_OBJECT);
        }

        // no value in the object
        if (!StringHelper.hasContent(requestObject.getValue())) {
            throw new JmsException(ErrorMessages.JMS_NULL_VALUE_REQUEST_OBJECT);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getPlatforms(eu.arena_fp7._1.Location)
     */
    @Override
    public Object getPlatforms(Location location) {

        List<AbstractNamedValue> responseVector = new ArrayList<>();

        // try to get data from the ontology
        Set<Platform> platformsInformation = null;
        try {
            platformsInformation = getOntology().getPlatforms(location.getX(), location.getY(), getRadius());
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getPlatforms", "Could not retrieve ontology data", exc);
            ;
        }
        // add list of ontology objects to the response
        if (platformsInformation != null) {

            for (Platform platformInformation : platformsInformation) {
                try {
                    responseVector.add(createSimpleNamedValue(platformInformation.getId(),
                            ContextModuleRequestProperties.Platform.name(), JsonHelper.toJson(platformInformation)));
                } catch (JsonHelperException exc) {
                    LogHelper.warning(ContextModuleJmsService.class, "getPlatforms",
                            "Could not add given object to the response: '%s'. Details: %s", platformInformation,
                            exc.getLocalizedMessage());
                }
            }
        }
        // prepare response object
        Object response = createObject(location.getId(), responseVector);
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getGISData(eu.arena_fp7._1.Location)
     */
    @Override
    public Object getGISData(Location location) {

        return getGeoObjects(location, getRadius(), (String[]) null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getGISData(eu.arena_fp7._1.Object)
     */
    @Override
    public Object getGISData(Object parameters) {

        Location requestLocation = null;
        double requestRange = getRadius();
        String[] requestClasses = new String[] { null };
        // try to parse request object into parameters
        try {
            for (AbstractNamedValue parameter : parameters.getFeatureVector().getFeature()) {

                if (parameter instanceof Location) {
                    requestLocation = (Location) parameter;
                } else if (parameter instanceof SimpleNamedValue) {
                    String parameterValue = ((SimpleNamedValue) parameter).getValue();
                    if (NumbersHelper.isDouble(parameterValue)) {
                        requestRange = NumbersHelper.getDoubleFromString(parameterValue);
                    } else {
                        requestClasses = JsonHelper.fromJson(parameterValue, String[].class);
                    }
                }
            }
        } catch (RuntimeException | JsonHelperException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getGISData", "Could not parse request data", exc);
        }

        return getGeoObjects(requestLocation, requestRange, requestClasses);
    }

    private Object getGeoObjects(Location location, double radius, String... classes) {

        List<AbstractNamedValue> responseVector = new ArrayList<>();

        Set<GeoObject> geographicalInformation = null;
        try {
            geographicalInformation = getOntology().getGISObjects(location.getX(), location.getY(), radius, classes);
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getGISData", "Could not retrieve ontology data", exc);
        }
        // parse ontology data into service response
        if (geographicalInformation != null) {

            for (GeoObject geoObject : geographicalInformation) {
                try {
                    responseVector.add(createSimpleNamedValue(geoObject.getId(),
                            ContextModuleRequestProperties.GeoportalData.name(), JsonHelper.toJson(geoObject)));
                } catch (JsonHelperException exc) {
                    LogHelper
                            .warning(ContextModuleJmsService.class, "getGISData",
                                    "Could not add given object to the response: '%s'. Details: %s", geoObject,
                                    exc.getLocalizedMessage());
                }
            }
        }
        // prepare response object
        Object response = createObject(location.getId(), responseVector);
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getGeoportalData(eu.arena_fp7._1.Location)
     */
    @Override
    public Object getGeoportalData(Location location) {

        List<AbstractNamedValue> responseVector = new ArrayList<>();

        Set<GeoObject> geoData = null;
        try {
            // call geoportal service to retrieve all available data
            geoData = getGeoportal().getGeoportalData(ArenaObjectsMapper.fromLocation(location), getRadius());
            // update ontology with the geoportal data
            getOntology().updateGeoportalData(location.getX(), location.getY(), geoData);
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getGeoportalData", "Could not add data to the ontology ", exc);
        } catch (GeoportalException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getGeoportalData", "Could not retrieve data from the geoportal",
                    exc);
        }

        // fill response with the data
        if (geoData != null) {

            for (GeoObject geoObject : geoData) {
                try {
                    responseVector.add(createSimpleNamedValue(geoObject.getId(),
                            ContextModuleRequestProperties.GeoportalData.name(), JsonHelper.toJson(geoObject)));
                } catch (JsonHelperException exc) {
                    LogHelper
                            .warning(ContextModuleJmsService.class, "getGeoportalData",
                                    "Could not add given object to the response: '%s'. Details: %s", geoObject,
                                    exc.getLocalizedMessage());
                }
            }
        }
        // prepare response object
        Object response = createObject(location.getId(), responseVector);

        return response;
    }

    @Override
    public SimpleNamedValue updateZone(Object zoneDefinition) {

        // ID of the created zone
        String zoneId = null;
        // name of the parking lot correlated with that zone
        String parkingLotName = null;
        //'plane name' zone attribute
        String planeName = null;

        // get the zone definition
        List<itti.com.pl.arena.cm.dto.Location> locations = new ArrayList<>();
        for (AbstractNamedValue feature : zoneDefinition.getFeatureVector().getFeature()) {
            itti.com.pl.arena.cm.dto.Location location = LocationFactory.createLocation(feature);
            if (location != null) {
                locations.add(location);
            } else if (feature instanceof SimpleNamedValue) {
                if (StringHelper.equalsIgnoreCase(ContextModuleRequestProperties.ParkingLotName.name(), feature.getFeatureName())) {
                    parkingLotName = ((SimpleNamedValue) feature).getValue();
                } else if (StringHelper
                        .equalsIgnoreCase(ContextModuleRequestProperties.Name.name(), feature.getFeatureName())) {
                    zoneId = ((SimpleNamedValue) feature).getValue();
                } else if (StringHelper
                        .equalsIgnoreCase(ContextModuleRequestProperties.PlaneName.name(), feature.getFeatureName())) {
                    planeName = ((SimpleNamedValue) feature).getValue();
                }
            } else {
                LogHelper.warning(ContextModuleJmsService.class, "updateZone",
                        "Could not convert given ARENA object into Location: %s", StringHelper.toString(feature));
            }
        }
        try {
            zoneId = getOntology().updateZone(zoneId, parkingLotName, planeName, locations);
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "updateZone", "Could not update zone", exc);
        }

        // prepare response object
        SimpleNamedValue response = createSimpleNamedValue(zoneDefinition.getId(),
                ContextModuleRequestProperties.Name.name(), zoneId);
        return response;
    }

    @Override
    public Object getZone(SimpleNamedValue zoneMessage) {

        List<AbstractNamedValue> responseVector = new ArrayList<>();
        // get the zone ID
        String zoneId = zoneMessage.getValue();
        try {
            Zone zone = getOntology().getZone(zoneId);
            if(zone != null)
            {
                responseVector.add(createSimpleNamedValue(zoneMessage.getId(), ContextModuleRequestProperties.Name.name(), zone.getId()));
                if(StringHelper.hasContent(zone.getPlaneName())){
                    responseVector.add(createSimpleNamedValue(zoneMessage.getId(), ContextModuleRequestProperties.PlaneName.name(), zone.getPlaneName()));
                }
                for (itti.com.pl.arena.cm.dto.Location location : zone.getLocations()) {
                    AbstractNamedValue coordinate = createCoordinate(zoneMessage.getId(), location.getLongitude(),
                            location.getLatitude(), location.getAltitude());
                    responseVector.add(coordinate);
                }
            }
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getZone", "Could not retrieve zone from the ontology", exc);
        }
        // prepare response object
        Object response = createObject(zoneMessage.getId(), responseVector);

        // add results to the response
        return response;
    }

    @Override
    public BooleanNamedValue removeZone(SimpleNamedValue zoneMessage) {

        // removal status
        boolean status = false;
        // get the zone ID
        String zoneId = zoneMessage.getValue();
        try {
            getOntology().remove(zoneId);
            status = true;
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "removeZone", "Could not remove zone from the ontology", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(zoneMessage.getId(), zoneId, status);

        // add results to the response
        return response;
    }

    @Override
    public Object getListOfZones(SimpleNamedValue zoneMessage) {

        List<AbstractNamedValue> responseVector = new ArrayList<>();
        try {
            Set<String> zoneNames = getOntology().getParkingLotInfrastructure(zoneMessage.getValue(),
                    OntologyConstants.Car_parking_zone.name());
            for (String zoneName : zoneNames) {
                AbstractNamedValue zoneObject = createSimpleNamedValue(zoneMessage.getId(),
                        ContextModuleRequestProperties.Name.name(), zoneName);
                responseVector.add(zoneObject);
            }
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getZoneNames", "Could not retrieve zone names from the ontology",
                    exc);
        }
        // prepare response object
        Object response = createObject(zoneMessage.getId(), responseVector);

        // add results to the response
        return response;
    }

    @Override
    public Object getListOfParkingLots(SimpleNamedValue request) {
        List<AbstractNamedValue> responseVector = new ArrayList<>();
        try {
            List<String> parkingLotNames = getOntology().getInstances(OntologyConstants.Parking.name());
            for (String parkingLotName : parkingLotNames) {
                AbstractNamedValue parkingLotObject = createSimpleNamedValue(request.getId(),
                        ContextModuleRequestProperties.ParkingLotName.name(), parkingLotName);
                responseVector.add(parkingLotObject);
            }
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getListOfParkingLots",
                    "Could not retrieve parking lot names from the ontology", exc);
        }
        // prepare response object
        Object response = createObject(request.getId(), responseVector);

        // add results to the response
        return response;
    }

    @Override
    public Object getListOfPlatforms(SimpleNamedValue request) {
        List<AbstractNamedValue> responseVector = new ArrayList<>();
        try {
            List<String> platformNames = getOntology().getInstances(OntologyConstants.Vehicle_with_cameras.name());
            for (String platformName : platformNames) {
                AbstractNamedValue platformObject = createSimpleNamedValue(request.getId(),
                        ContextModuleRequestProperties.Platform.name(), platformName);
                responseVector.add(platformObject);
            }
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "getListOfPlatforms",
                    "Could not retrieve platform names from the ontology", exc);
        }
        // prepare response object
        Object response = createObject(request.getId(), responseVector);

        // add results to the response
        return response;
    }

    @Override
    public BooleanNamedValue removePlatform(SimpleNamedValue platformMessage) {

        // removal status
        boolean status = false;
        // get the zone ID
        String platformId = platformMessage.getValue();
        try {
            getOntology().remove(platformId);
            status = true;
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "removePlatform", "Could not remove platform from the ontology", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(platformMessage.getId(), platformId, status);

        // add results to the response
        return response;
    }
    @Override
    public SimpleNamedValue defineRule(SimpleNamedValue rule) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BooleanNamedValue removeRule(SimpleNamedValue ruleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BooleanNamedValue applyRules(SimpleNamedValue objectId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getListOfRules(SimpleNamedValue objectId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void destinationReached(String platformId, itti.com.pl.arena.cm.dto.Location location) {

        // prepare valid response object
        AbstractNamedValue destinationReachedMessage = createSimpleNamedValue(String.format("%s.%s.%s.%s", Constants.MODULE_NAME,
                ContextModuleRequests.destinationReached.name(), getDataInDefaultFormat(), platformId),
                ContextModuleRequestProperties.Notification.name(), StringHelper.toString(location));
        destinationReachedMessage.setHref(ContextModuleRequests.destinationReached.name());
        destinationReachedMessage.setDataSourceId(Constants.MODULE_NAME);
        client.publish(destinationReachedMessage.getDataSourceId(), destinationReachedMessage);
    }

    @Override
    public void destinationLeft(String platformId, itti.com.pl.arena.cm.dto.Location location) {

        // prepare valid response object
        AbstractNamedValue destinationLeftMessage = createSimpleNamedValue(String.format("%s.%s.%s.%s", Constants.MODULE_NAME,
                ContextModuleRequests.destinationLeft.name(), getDataInDefaultFormat(), platformId),
                ContextModuleRequestProperties.Notification.name(), StringHelper.toString(location));
        destinationLeftMessage.setHref(ContextModuleRequests.destinationLeft.name());
        destinationLeftMessage.setDataSourceId(Constants.MODULE_NAME);
        client.publish(destinationLeftMessage.getDataSourceId(), destinationLeftMessage);
    }

    /**
     * Returns timestamp in default date-time format
     * 
     * @return timestamp in default format used by the ContextModule
     */
    private String getDataInDefaultFormat() {
        return DateTimeHelper.formatTime(System.currentTimeMillis(), Constants.TIMESTAMP_FORMAT);
    }

    @Override
    public BooleanNamedValue removeCamera(SimpleNamedValue cameraMessage) {
        // removal status
        boolean status = false;
        // get the zone ID
        String cameraId = cameraMessage.getValue();
        try {
            getOntology().remove(cameraId);
            status = true;
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "removeCamera", "Could not remove camera from the ontology", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(cameraMessage.getId(), cameraId, status);

        // add results to the response
        return response;    
    }

    @Override
    public BooleanNamedValue removeParkingLot(SimpleNamedValue parkingLotRequest) {
        // removal status
        boolean status = false;
        // get the zone ID
        String parkingLotId = parkingLotRequest.getValue();
        try {
            getOntology().remove(parkingLotId);
            status = true;
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "removeParkingLot", "Could not remove parking lot from the ontology", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(parkingLotRequest.getId(), parkingLotId, status);

        // add results to the response
        return response;    
    }
}
