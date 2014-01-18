package itti.com.pl.arena.cm.service.jms;

import java.util.Set;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.Service;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.geoportal.Geoportal;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.ontology.Ontology;
import itti.com.pl.arena.cm.ontology.OntologyException;
import itti.com.pl.arena.cm.service.Constants.ContextModuleRequestProperties;
import itti.com.pl.arena.cm.service.Constants.ContextModuleRequests;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelperException;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.FeatureVector;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.SimpleNamedValue;
import eu.arena_fp7._1.Situation;

public class ContextModuleJmsService extends ModuleImpl implements ContextModule, Service {

    private Client client = null;
    private ObjectFactory factory = null;

    private String brokerUrl;
    private String localIpAddress;
    private String connectionPort;

    private Ontology ontology = null;
    private Geoportal geoportal = null;

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

    @Override
    public void init() {

        try {
            setLocalIpAddress(NetworkHelper.getIpAddress());

            validateParams(getBrokerUrl(), getLocalIpAddress(), getConnectionPort());

            client = new Client(getBrokerUrl(), getLocalIpAddress(), getConnectionPort());
            client.connectToServer();

            client.registerModule(this);
            client.registerModuleAsDataProvider(this);
            client.registerModuleAsDataConsumer(this);

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

        try {
            // process responses from the Ontology Server only
            if (StringHelper.equalsIgnoreCase(Constants.MODULE_NAME, dataSourceId)) {

                AbstractDataFusionType response = null;

                if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getPlatform.name(), data.getHref())
                        && (data instanceof SimpleNamedValue)) {
                    response = getPlatform((SimpleNamedValue) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getPlatforms.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getPlatforms((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGISData.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getGISData((Location) data);
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequests.getGeoportalData.name(), data.getHref())
                        && (data instanceof Location)) {
                    response = getGeoportalData((Location) data);
                } else {
                    LogHelper.info(ContextModuleJmsService.class, "onDataChanged", "Invalid method requested: '%s'",
                            data.getHref());
                }

                if (response != null) {
                    response.setId(data.getId());
                    response.setDataSourceId(Constants.MODULE_NAME);
                    client.publish(data.getDataSourceId(), response);
                }
            }
        } catch (RuntimeException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public Object getPlatform(SimpleNamedValue objectId) {

        Object response = factory.createObject();

        String platformId = objectId.getValue();
        Platform platformInformation = null;
        try {
            platformInformation = getOntology().getPlatform(platformId);
        } catch (OntologyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (platformInformation != null) {
            FeatureVector vector = factory.createFeatureVector();
            vector.setDataSourceId(Constants.MODULE_NAME);
            vector.getFeature()
                    .add(createSimpleNamedValue(ContextModuleRequestProperties.Id.name(), platformInformation.getId()));
            if (platformInformation.getLocation() != null) {
                vector.getFeature().add(
                        createLocation(ContextModuleRequestProperties.Location.name(), platformInformation.getLocation()
                                .getLatitude(), platformInformation.getLocation().getLongitude()));
                vector.getFeature().add(
                        createSimpleNamedValue(ContextModuleRequestProperties.Bearing.name(), platformInformation
                                .getLocation().getBearing()));
            }
            if (platformInformation.getCameras() != null) {
                for (Camera camera : platformInformation.getCameras().values()) {
                    collectCameraInformation(vector, camera);
                }
            }
            response.setFeatureVector(vector);
        }
        return response;
    }

    @Override
    public Situation getPlatforms(Location location) {

        Situation response = factory.createSituation();

        Set<Platform> platformsInformation = null;
        try {
            platformsInformation = getOntology().getPlatforms(location.getX(), location.getY(), getRadius());
        } catch (OntologyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (platformsInformation != null) {
            FeatureVector fv = new FeatureVector();

            for (Platform platformInformation : platformsInformation) {
                fv.getFeature().add(createSimpleNamedValue(platformInformation.getId(), JsonHelper.toJson(platformInformation)));
            }
            response.setGlobalSceneProperty(fv);
        }
        return response;
    }

    @Override
    public Situation getGISData(Location location) {

        Situation response = factory.createSituation();

        Set<GeoObject> geographicalInformation = null;
        try {
            geographicalInformation = getOntology().getGISObjects(
                    new itti.com.pl.arena.cm.dto.Location(location.getX(), location.getY()), getRadius());
        } catch (OntologyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (geographicalInformation != null) {
            FeatureVector fv = new FeatureVector();

            for (GeoObject geoObject : geographicalInformation) {
                fv.getFeature().add(createSimpleNamedValue(geoObject.getId(), JsonHelper.toJson(geoObject)));
            }
            response.setGlobalSceneProperty(fv);
        }
        return response;
    }

    @Override
    public Situation getGeoportalData(Location location) {

        Situation response = factory.createSituation();

        Set<GeoObject> geoData = null;
        try {
            itti.com.pl.arena.cm.dto.Location cmLocation = new itti.com.pl.arena.cm.dto.Location(location.getX(), location.getY());
            geoData = getGeoportal().getGeoportalData(cmLocation, getRadius());
            getOntology().addGeoportalData(cmLocation, geoData);
        } catch (OntologyException | GeoportalException exc) {

        }

        if (geoData != null) {
            FeatureVector fv = new FeatureVector();

            for (GeoObject geoObject : geoData) {
                fv.getFeature().add(createSimpleNamedValue(geoObject.getId(), JsonHelper.toJson(geoObject)));
            }
            response.setGlobalSceneProperty(fv);
        }
        return response;
    }

    private void collectCameraInformation(FeatureVector vector, Camera camera) {
        vector.getFeature().add(createSimpleNamedValue(ContextModuleRequestProperties.CameraId.name(), camera.getId()));
        vector.getFeature().add(
                createSimpleNamedValue(ContextModuleRequestProperties.CameraPosition.name(), camera.getOnPPlatformPosition().name()));
        vector.getFeature().add(createSimpleNamedValue(ContextModuleRequestProperties.CameraType.name(), camera.getType()));
        vector.getFeature().add(createSimpleNamedValue(ContextModuleRequestProperties.CameraAngleX.name(), camera.getAngleX()));
        vector.getFeature().add(createSimpleNamedValue(ContextModuleRequestProperties.CameraAngleY.name(), camera.getAngleY()));
    }

    private AbstractNamedValue createSimpleNamedValue(String key, java.lang.Object value) {
        SimpleNamedValue snv = factory.createSimpleNamedValue();
        snv.setDataSourceId(Constants.MODULE_NAME);
        snv.setId(key);
        snv.setValue(String.valueOf(value));
        return snv;
    }

    private AbstractNamedValue createLocation(String key, double x, double y) {
        Location location = factory.createLocation();
        location.setDataSourceId(Constants.MODULE_NAME);
        location.setId(key);
        location.setX(x);
        location.setX(y);
        return location;
    }
}
