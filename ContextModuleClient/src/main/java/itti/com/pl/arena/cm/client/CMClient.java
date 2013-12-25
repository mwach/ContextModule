package itti.com.pl.arena.cm.client;

import java.util.Properties;
import java.util.UUID;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.client.service.ContextModuleClientException;
import itti.com.pl.arena.cm.client.service.ContextModuleFacade;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.service.Constants.ContextModuleRequests;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.PropertiesHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;
import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.SimpleNamedValue;
import eu.arena_fp7._1.Situation;

public class CMClient {

	private static final String CLIENT_MODULE_NAME = CMClient.class
			.getSimpleName();
	private static final String PROPERTIES_FILE = "src/main/resources/client.properties";
	private static Properties properties = new Properties();
	private String brokerUrl = null;

	private ContextModule contextModule = null;
	private ObjectFactory objectFactory = null;

	public static void main(String[] args) {

		CMClient client = new CMClient();
		try {
			client.loadProperties();
			client.parseCmdLineParams(args);
			client.init();
			parseTruckInfoResponse(client.getPlatformService("Truck_A1"));
			parseTrucksInfoResponse(client.getPlatformsService(0.12, 51.1));
			parseGISDataResponse(client.getGISDataService(0.1, 51.0));
			parseGISDataResponse(client.getGeoportalDataService(17.974734282593246, 53.12344164937794));
		} catch (ContextModuleClientException exc) {
			printUsage();
		} catch (RuntimeException exc) {
			LogHelper
					.exception(CMClient.class, "main", "Client exception", exc);
		} finally {
			client.shutdown();
		}
		System.exit(0);
	}

	private static void parseTruckInfoResponse(Object platformService) {
		if(platformService != null){
			for (AbstractDataFusionType feature : platformService.getFeatureVector().getFeature()) {
				System.out.println(String.format("%s: %s", feature.getId(), ((SimpleNamedValue)feature).getValue()));
			}
		}
	}

	private static void parseTrucksInfoResponse(Situation platformService) {
		if(platformService != null && platformService.getGlobalSceneProperty() != null){
			for (AbstractDataFusionType feature : platformService.getGlobalSceneProperty().getFeature()) {
				System.out.println(String.format("%s: %s", feature.getId(), ((SimpleNamedValue)feature).getValue()));
			}
		}
	}

	private static void parseGISDataResponse(Situation gisDataService) {
		if(gisDataService != null && gisDataService.getGlobalSceneProperty() != null){
			for (AbstractDataFusionType feature : gisDataService.getGlobalSceneProperty().getFeature()) {
				System.out.println(String.format("%s: %s", feature.getId(), ((SimpleNamedValue)feature).getValue()));
			}
		}
	}

	private void loadProperties() {

		try {
			properties.putAll(PropertiesHelper.loadPropertiesAsMap(PROPERTIES_FILE));
		} catch (IOHelperException e) {
			LogHelper.info(CMClient.class, "loadProperties", "Could not load properites file: %s", e.getLocalizedMessage());
		}
	}

	public Object getPlatformService(String truckId) {
		SimpleNamedValue objectId = createSimpleNamedValue(truckId);
		objectId.setHref(ContextModuleRequests.getPlatform.name());
		Object data = contextModule.getPlatform(objectId);
		LogHelper.info(CMClient.class, "getPlatformsService",
				"Server response received:\n%s", String.valueOf(data));
		return data;
	}

	public Situation getPlatformsService(double x, double y) {
		Location objectLocation = createLocation(x, y);
		objectLocation.setHref(ContextModuleRequests.getPlatforms.name());
		Situation data = contextModule.getPlatforms(objectLocation);
		LogHelper.info(CMClient.class, "getPlatformsService",
				"Server response received:\n%s", String.valueOf(data));
		return data;
	}

	public Situation getGISDataService(double x, double y) {
		Location objectLocation = createLocation(x, y);
		objectLocation.setHref(ContextModuleRequests.getGISData.name());
		Situation data = contextModule.getPlatforms(objectLocation);
		LogHelper.info(CMClient.class, "getGISDataService",
				"Server response received:\n%s", String.valueOf(data));
		return data;
	}

	public Situation getGeoportalDataService(double x, double y) {
		Location objectLocation = createLocation(x, y);
		objectLocation.setHref(ContextModuleRequests.getGeoportalData.name());
		Situation data = contextModule.getGeoportalData(objectLocation);
		LogHelper.info(CMClient.class, "getGeoportalDataService",
				"Server response received:\n%s", String.valueOf(data));
		return data;
	}

	private void parseCmdLineParams(String[] args) throws ContextModuleClientException {

		brokerUrl = PropertiesHelper.getPropertyAsString(properties, ClientPropertyNames.brokerUrl.name(), null);

		if (args.length != 1 && !StringHelper.hasContent(brokerUrl)) {
			LogHelper.error(CMClient.class, "parseParams",
					"Incorrect number of parameters specified");
			throw new ContextModuleClientException(
					"Incorrect number of parameters specified");
		}
		if(args.length == 1){
			brokerUrl = args[0];
		}
	}

	public static void printUsage() {
		LogHelper.info(CMClient.class, "printUsage",
				String.format("Usage: %s brokerUrl", CLIENT_MODULE_NAME));
	}

	public void init() {
		ContextModuleFacade cmFacade = new ContextModuleFacade(CLIENT_MODULE_NAME,
				brokerUrl);
		cmFacade.setDebug(PropertiesHelper.getPropertyAsBoolean(properties, ClientPropertyNames.debugMode.name(), false));
		cmFacade.setResponseWaitingTime(PropertiesHelper.getPropertyAsInteger(properties, ClientPropertyNames.responseWaitingTime.name(), 5000));

		int clientPort = PropertiesHelper.getPropertyAsInteger(properties, ClientPropertyNames.clientPort.name(), -1);
		if(clientPort > 0 && clientPort < 65500){
			cmFacade.setClientPort(clientPort);
		}

		cmFacade.init();

		this.contextModule = cmFacade;
		this.objectFactory = new ObjectFactory();
	}

	private SimpleNamedValue createSimpleNamedValue(String value) {
		SimpleNamedValue object = objectFactory.createSimpleNamedValue();
		object.setId(getObjectId());
		object.setDataSourceId(CLIENT_MODULE_NAME);
		object.setValue(value);
		return object;
	}

	private Location createLocation(double x, double y) {
		Location object = objectFactory.createLocation();
		object.setId(getObjectId());
		object.setDataSourceId(CLIENT_MODULE_NAME);
		object.setX(x);
		object.setY(y);
		return object;
	}

	private String getObjectId() {
		return String
				.format("%s.%s.%s", Constants.MODULE_NAME, CLIENT_MODULE_NAME, UUID.randomUUID().toString());
	}

	public void shutdown() {
		if(contextModule != null){
			((ContextModuleFacade) contextModule).shutdown();
		}
	}

}
