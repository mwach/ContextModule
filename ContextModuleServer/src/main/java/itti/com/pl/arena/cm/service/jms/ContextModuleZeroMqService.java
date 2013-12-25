package itti.com.pl.arena.cm.service.jms;

import java.util.List;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.Service;
import itti.com.pl.arena.cm.dto.Camera;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.PlatformInformation;
import itti.com.pl.arena.cm.service.Constants.ContextModuleRequestProperties;
import itti.com.pl.arena.cm.service.Constants.ContextModuleRequests;
import itti.com.pl.arena.cm.service.ContextManagerService;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.utils.helpers.JsonHelper;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NetworkHelper;
import itti.com.pl.arena.cm.utils.helpers.NetworkHelperException;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

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

public class ContextModuleZeroMqService extends ModuleImpl implements
		ContextModule, Service {

	private Client client = null;
	private ObjectFactory factory = null;

	private String brokerUrl;
	private String localIpAddress;
	private String connectionPort;

	private ContextManagerService contextManager = null;

	private ContextManagerService getContextManager() {
		return contextManager;
	}

	@Required
	public void setContextManager(ContextManagerService contextManager) {
		this.contextManager = contextManager;
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

	public ContextModuleZeroMqService() {
		super(Constants.MODULE_NAME);
	}

	@Override
	public void init() {

		try {
			setLocalIpAddress(NetworkHelper.getIpAddress());

			validateParams(getBrokerUrl(), getLocalIpAddress(),
					getConnectionPort());

			client = new Client(getBrokerUrl(), getLocalIpAddress(),
					getConnectionPort());
			client.connectToServer();

			client.registerModule(this);
			client.registerModuleAsDataProvider(this);
			client.registerModuleAsDataConsumer(this);

			factory = new ObjectFactory();

		} catch (JmsException exc) {
			LogHelper.error(ContextModuleZeroMqService.class, "init",
					"Validation failed: %s", exc.getLocalizedMessage());
			throw new BeanInitializationException(exc.getLocalizedMessage());
		} catch (NetworkHelperException exc) {
			LogHelper.exception(ContextModuleZeroMqService.class, "init",
					"Could not set clientIpAddress", exc);
			throw new BeanInitializationException(exc.getLocalizedMessage());
		} catch (RuntimeException exc) {
			LogHelper.exception(ContextModuleZeroMqService.class, "init",
					"Could not initialize ContextModule", exc);
			throw new BeanInitializationException(exc.getLocalizedMessage());
		}
	}

	private void validateParams(String serverIpAddress, String clientIpAddress,
			String clientPort) throws JmsException {

		if (!StringHelper.hasContent(serverIpAddress)) {
			throw new JmsException("ServerIpAddress not provided");
		}

		if (!StringHelper.hasContent(clientIpAddress)) {
			throw new JmsException("ClientIpAddress not provided");
		}

		if (!StringHelper.hasContent(clientPort)) {
			throw new JmsException("ClientPort not provided");
		}
	}

	@Override
	public void shutdown() {
		try {
			client.unregisterModule(this);
		} catch (RuntimeException exc) {
			LogHelper.exception(ContextModuleZeroMqService.class, "shutdown",
					"Could not shutdown the ContextModule", exc);
		}
	}

	@Override
	public void onDataChanged(Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {

		try {
			// process responses from the Ontology Server only
			if (StringHelper.equalsIgnoreCase(Constants.MODULE_NAME,
					dataSourceId)) {

				AbstractDataFusionType response = null;

				if (StringHelper.equalsIgnoreCase(
						ContextModuleRequests.getPlatform.name(),
						data.getHref()) && (data instanceof SimpleNamedValue)) {
					response = getPlatform((SimpleNamedValue) data);
				} else if (StringHelper.equalsIgnoreCase(
						ContextModuleRequests.getPlatforms.name(),
						data.getHref()) && (data instanceof Location)) {
					response = getPlatforms((Location) data);
				} else if (StringHelper
						.equalsIgnoreCase(
								ContextModuleRequests.getGISData.name(),
								data.getHref()) && (data instanceof Location)) {
					response = getGISData((Location) data);
				} else if (StringHelper
						.equalsIgnoreCase(
								ContextModuleRequests.getGeoportalData.name(),
								data.getHref()) && (data instanceof Location)) {
					response = getGeoportalData((Location) data);
				} else {
					LogHelper.info(ContextModuleZeroMqService.class,
							"onDataChanged", "Invalid method requested: '%s'",
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
		PlatformInformation platformInformation = getContextManager()
				.getPlatformData(platformId);
		if (platformInformation != null) {
			FeatureVector vector = factory.createFeatureVector();
			vector.setDataSourceId(Constants.MODULE_NAME);
			vector.getFeature().add(
					createSimpleNamedValue(
							ContextModuleRequestProperties.Id.name(),
							platformInformation.getId()));
			if(platformInformation.getBearing() != null){
				vector.getFeature().add(
					createSimpleNamedValue(
							ContextModuleRequestProperties.Bearing.name(),
							platformInformation.getBearing()));
			}
			if (platformInformation.getLastLocation() != null) {
				vector.getFeature().add(
						createLocation(
								ContextModuleRequestProperties.Location
										.name(), platformInformation
										.getLastLocation().getLatitude(),
								platformInformation.getLastLocation()
										.getLongitude()));
			}
			if (platformInformation.getCameras() != null) {
				for (Camera camera : platformInformation.getCameras()
						.values()) {
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

		List<PlatformInformation> platformsInformation = getContextManager()
				.getPlatformsData(location.getX(), location.getY());
		if (platformsInformation != null) {
			FeatureVector fv = new FeatureVector();

			for (PlatformInformation platformInformation : platformsInformation) {
				fv.getFeature().add(createSimpleNamedValue(
						platformInformation.getId(), JsonHelper.toJson(platformInformation)));
			}
			response.setGlobalSceneProperty(fv);
		}
		return response;
	}

	@Override
	public Situation getGISData(Location location) {

		Situation response = factory.createSituation();

		List<GeoObject> geographicalInformation = getContextManager()
				.getGISData(location.getX(), location.getY());
		if (geographicalInformation != null) {
			FeatureVector fv = new FeatureVector();

			for (GeoObject geoObject : geographicalInformation) {
				fv.getFeature().add(createSimpleNamedValue(
						geoObject.getId(), JsonHelper.toJson(geoObject)));
			}
			response.setGlobalSceneProperty(fv);
		}
		return response;
	}

	@Override
	public Situation getGeoportalData(Location location) {

		Situation response = factory.createSituation();

		List<GeoObject> geoportalInformation = getContextManager()
				.getGeoportalData(location.getX(), location.getY());
		if (geoportalInformation != null) {
			FeatureVector fv = new FeatureVector();

			for (GeoObject geoObject : geoportalInformation) {
				fv.getFeature().add(createSimpleNamedValue(
						geoObject.getId(), JsonHelper.toJson(geoObject)));
			}
			response.setGlobalSceneProperty(fv);
		}
		return response;
	}

	private void collectCameraInformation(FeatureVector vector, Camera camera) {
		vector.getFeature().add(
				createSimpleNamedValue(
						ContextModuleRequestProperties.CameraId.name(),
						camera.getId()));
		vector.getFeature().add(
				createSimpleNamedValue(
						ContextModuleRequestProperties.CameraPosition.name(),
						camera.getPosition().name()));
		vector.getFeature().add(
				createSimpleNamedValue(
						ContextModuleRequestProperties.CameraType.name(),
						camera.getType()));
		vector.getFeature().add(
				createSimpleNamedValue(
						ContextModuleRequestProperties.CameraAngleX.name(),
						camera.getAngleX()));
		vector.getFeature().add(
				createSimpleNamedValue(
						ContextModuleRequestProperties.CameraAngleY.name(),
						camera.getAngleY()));
	}

	private AbstractNamedValue createSimpleNamedValue(String key,
			java.lang.Object value) {
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
