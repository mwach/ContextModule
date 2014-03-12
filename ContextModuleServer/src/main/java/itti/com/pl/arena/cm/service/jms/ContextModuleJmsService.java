package itti.com.pl.arena.cm.service.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.exception.ErrorMessages;
import itti.com.pl.arena.cm.geoportal.Geoportal;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.ontology.Ontology;
import itti.com.pl.arena.cm.ontology.OntologyException;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequests;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.service.PlatformListener;
import itti.com.pl.arena.cm.service.Service;
import itti.com.pl.arena.cm.utils.helper.DateTimeHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelperException;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.RealWorldCoordinate;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * Implementation of the {@link ContextModule} interface
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleJmsService extends ModuleImpl implements ContextModule, Service, PlatformListener {

    /*
     * Objects used to communicate via Arena Bus
     */
    private Client client = null;
    private ObjectFactory factory = null;

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

    private ObjectFactory getFactory()
    {
        return factory;
    }

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

            factory = new ObjectFactory();

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
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.updatePlatform.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = updatePlatform((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getPlatforms.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getPlatforms((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGISData.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getGISData((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGISDataExt.name(), data.getHref())
                        && (data instanceof Object)) {
                    response = getGISData((Object) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGeoportalData.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getGeoportalData((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.updatePlatform.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = updateGISData((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getCameraFieldOfView.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getCameraFieldOfView((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.defineZone.name(), data.getHref())
                        && (data instanceof Object)) {
                    response = defineZone((Object) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getZone.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getZone((SimpleNamedValue) data);
                } else {
                    // invalid service name provided
                    LogHelper.info(ContextModuleJmsService.class, "onDataChanged", "Invalid method requested: '%s'",
                            data.getHref());
                    SimpleNamedValue invalidRequestResponse = new SimpleNamedValue();
                    invalidRequestResponse.setValue(String.format("Unsupprted service name specified: '%s'", data.getHref()));
                    response = invalidRequestResponse;
                }

                // prepare valid response object
                if (response != null) {
                    response.setId(data.getId());
                    response.setDataSourceId(Constants.MODULE_NAME);
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
                vector.add(createSimpleNamedValue(platform.getId(), JsonHelper.toJson(platform)));
            } catch (JsonHelperException exc) {
                LogHelper.warning(ContextModuleJmsService.class, "getPlatforms",
                        "Could not add given object to the response: '%s'. Details: %s", platform, exc.getLocalizedMessage());
            }
        }
        // prepare response object
        Object response = createObject(objectId.getId(), vector);
        return response;
    }

    // @Override
    // public Object getPlatformNeighborhood(SimpleNamedValue platformId) {
    //
    // }

    @Override
    public Object getCameraFieldOfView(SimpleNamedValue cameraRequestObject) {

        List<AbstractNamedValue> vector = new ArrayList<>();

        String cameraId = cameraRequestObject.getValue();
        Camera camera = null;
        // try to retrieve data from ontology
        try {
            camera = getOntology().getOntologyObject(cameraId, Camera.class);
        } catch (OntologyException e) {
            LogHelper.exception(ContextModuleJmsService.class, "getPlatform", "Could not retrieve data from ontology", e);
        }
        // data retrieved -try to process it
        //TODO
        if (camera != null) {
        }

        // prepare response object
        Object response = createObject(cameraRequestObject.getId(), vector);
        return response;
    }

    @Override
    public BooleanNamedValue updatePlatform(SimpleNamedValue platformObject) {

        Platform platform = null;
        boolean status = false;

        try {
            // try to parse JSON into object
            platform = JsonHelper.fromJson(platformObject.getValue(), Platform.class);
            // update ontology with provided data
            ontology.updatePlatform(platform);
            status = true;

        } catch (JsonHelperException | OntologyException exc) {
            // could not update data
            LogHelper.exception(ContextModuleJmsService.class, "updatePlatform", "Could not update platform", exc);
        }
        // prepare response object
        BooleanNamedValue response = createBooleanNamedValue(platformObject.getId(), status);
        return response;
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
                    responseVector.add(
                            createSimpleNamedValue(platformInformation.getId(), JsonHelper.toJson(platformInformation)));
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
                    responseVector.add(createSimpleNamedValue(geoObject.getId(), JsonHelper.toJson(geoObject)));
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
            geoData = getGeoportal().getGeoportalData(new itti.com.pl.arena.cm.dto.Location(location.getX(), location.getY()),
                    getRadius());
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
                    responseVector.add(createSimpleNamedValue(geoObject.getId(), JsonHelper.toJson(geoObject)));
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
    public BooleanNamedValue updateGISData(SimpleNamedValue gisData) {
        // TODO: to be implemented
        return createBooleanNamedValue(gisData.getId(), false);
    }

    @Override
    public SimpleNamedValue defineZone(Object zoneDefinition) {

        //get the zone definition
        List<itti.com.pl.arena.cm.dto.Location> locations = new ArrayList<>();
        for(AbstractNamedValue vertex : zoneDefinition.getFeatureVector().getFeature()){
            if(vertex instanceof Location){
                Location flatLocation = (Location)vertex;
                locations.add(new itti.com.pl.arena.cm.dto.Location(flatLocation.getX(), flatLocation.getY()));
            }else if(vertex instanceof RealWorldCoordinate){
                RealWorldCoordinate sphereLocation = (RealWorldCoordinate)vertex;
                locations.add(new itti.com.pl.arena.cm.dto.Location(sphereLocation.getX(), sphereLocation.getY(), 0, sphereLocation.getZ()));
            }
        }
        //ID of the created zone
        String zoneId = "";
        try {
            zoneId = getOntology().defineZone(locations);
        } catch (OntologyException exc) {
            LogHelper.exception(ContextModuleJmsService.class, "defineZone", "Could not define a zone in ontology", exc);
        }

        // prepare response object
        SimpleNamedValue response = createSimpleNamedValue(zoneDefinition.getId(), zoneId);
        return response;
    }

    @Override
    public Object getZone(SimpleNamedValue zoneMessage) {

        List<AbstractNamedValue> responseVector = new  ArrayList<>();
        //get the zone ID
        String zoneId = zoneMessage.getValue();
        try {
            List<itti.com.pl.arena.cm.dto.Location> zoneLocations = getOntology().getZone(zoneId);
            for (itti.com.pl.arena.cm.dto.Location location : zoneLocations) {
                AbstractNamedValue coordinate = createCoordinate(zoneMessage.getId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                responseVector.add(coordinate);
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
    public void destinationReached(String platformId, itti.com.pl.arena.cm.dto.Location location) {

        // prepare valid response object
        AbstractNamedValue destinationReachedMessage = createSimpleNamedValue(
                String.format("%s.%s.%s", Constants.MODULE_NAME,
                        ContextModuleRequests.destinationReached.name(), 
                        getDataInDefaultFormat(), platformId), 
                        location);
        destinationReachedMessage.setHref(ContextModuleRequests.destinationReached.name());
        destinationReachedMessage.setDataSourceId(Constants.MODULE_NAME);
        client.publish(destinationReachedMessage.getDataSourceId(), destinationReachedMessage);
    }

    @Override
    public void destinationLeft(String platformId, itti.com.pl.arena.cm.dto.Location location) {

        // prepare valid response object
        AbstractNamedValue destinationLeftMessage = createSimpleNamedValue(
                String.format("%s.%s.%s", Constants.MODULE_NAME,
                        ContextModuleRequests.destinationLeft.name(), 
                        getDataInDefaultFormat(), platformId),
                        location);
        destinationLeftMessage.setHref(ContextModuleRequests.destinationLeft.name());
        destinationLeftMessage.setDataSourceId(Constants.MODULE_NAME);
        client.publish(destinationLeftMessage.getDataSourceId(), destinationLeftMessage);
    }

    /**
     * Returns timestamp in default date-time format
     * @return timestamp in default format used by the ContextModule
     */
    private String getDataInDefaultFormat(){
        return DateTimeHelper.formatTime(System.currentTimeMillis(), Constants.TIMESTAMP_FORMAT);
    }
    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    private SimpleNamedValue createSimpleNamedValue(String id, java.lang.Object value) {
        SimpleNamedValue snv = getFactory().createSimpleNamedValue();
        snv.setDataSourceId(Constants.MODULE_NAME);
        snv.setId(id);
        snv.setValue(String.valueOf(value));
        return snv;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    private BooleanNamedValue createBooleanNamedValue(String id, boolean status) {
        BooleanNamedValue bnv = getFactory().createBooleanNamedValue();
        bnv.setDataSourceId(Constants.MODULE_NAME);
        bnv.setId(id);
        bnv.setFeatureValue(status);
        return bnv;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    private AbstractNamedValue createCoordinate(String id, double x, double y, double z) {
        RealWorldCoordinate rwc = getFactory().createRealWorldCoordinate();
        rwc.setDataSourceId(Constants.MODULE_NAME);
        rwc.setId(id);
        rwc.setX(x);
        rwc.setY(y);
        rwc.setZ(z);
        return rwc;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param vector 
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    private Object createObject(String id, List<AbstractNamedValue> vector) {
        Object object = getFactory().createObject();
        object.setDataSourceId(Constants.MODULE_NAME);
        object.getFeatureVector().setId(id);
        object.getFeatureVector().setDataSourceId(Constants.MODULE_NAME);
        object.getFeatureVector().getFeature().addAll(vector);
        return object;
    }

}
