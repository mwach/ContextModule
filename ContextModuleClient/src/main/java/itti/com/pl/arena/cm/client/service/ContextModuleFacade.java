package itti.com.pl.arena.cm.client.service;

import java.util.AbstractMap;
import java.util.HashMap;

import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.SimpleNamedValue;
import eu.arena_fp7._1.Situation;
import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.ContextModuleRuntimeException;
import itti.com.pl.arena.cm.Service;
import itti.com.pl.arena.cm.service.ContextModule;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NetworkHelper;
import itti.com.pl.arena.cm.utils.helpers.NetworkHelperException;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

public class ContextModuleFacade extends ModuleImpl implements
		Service, ContextModule {

	private Client client = null;

	private String brokerUrl = null;
	public void setBrokerUrl(String brokerUrl){
		this.brokerUrl = brokerUrl;
	}
	private String getBrokerUrl(){
		return brokerUrl;
	}

	private String clientIpAddress = null;
	public void setClientIpAddress(String clientIpAddress){
		this.clientIpAddress = clientIpAddress;
	}
	private String getClientIpAddress(){
		return clientIpAddress;
	}

	private int clientPort = 45444;
	public void setClientPort(int clientPort){
		this.clientPort = clientPort;
	}
	private int getClientPort(){
		return clientPort;
	}

	private int responseWaitingTime = 5000;
	public void setResponseWaitingTime(int responseWaitingTime){
		this.responseWaitingTime = responseWaitingTime;
	}
	private int getResponseWaitingTime(){
		return responseWaitingTime;
	}

	private boolean debug = false;
	public void setDebug(boolean debug){
		this.debug = debug;
	}
	private boolean isDebug(){
		return debug;
	}

	private AbstractMap<String, AbstractDataFusionType> requests = new HashMap<String, AbstractDataFusionType>();

	public ContextModuleFacade(String moduleName, String brokerUrl) {
		super(moduleName);
		setBrokerUrl(brokerUrl);
	}

	@Override
	public void init() {

		try {
			if(!StringHelper.hasContent(getClientIpAddress())){
				setClientIpAddress(NetworkHelper.getIpAddress());
			}

			client = new Client(getBrokerUrl(), getClientIpAddress(), String.valueOf(getClientPort()));
			client.connectToServer();
	
			client.registerModule(this);
			client.registerModuleAsDataProvider(this);
			client.registerModuleAsDataConsumer(this);
		}catch(RuntimeException exc){
			LogHelper.error(ContextModuleFacade.class, "init", "Could not initialize client object. Reason: '%s'", exc.getLocalizedMessage());
			throw new ContextModuleRuntimeException(exc.getLocalizedMessage(), exc);
		} catch (NetworkHelperException e) {
			LogHelper.error(ContextModuleFacade.class, "init", "Could not obtain local IP address. Reason: '%s'", e.getLocalizedMessage());
			throw new ContextModuleRuntimeException(
				"Could not obtain local IP address during client initialization", e);
		}
	}


	@Override
	public void onDataChanged(Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {
		if (dataSourceId.equals(getModuleName())) {
			requests.put(data.getId(), data);
		}
	}

	@Override
	public void shutdown() {
		if(client != null)
		{
			client.unregisterModule(this);
		}
	}

	@Override
	public eu.arena_fp7._1.Object getPlatform(SimpleNamedValue object) {
		return (Object)submitData(object);
	}

	@Override
	public Situation getPlatforms(Location location) {
		return (Situation)submitData(location);
	}

	@Override
	public Situation getGISData(Location location) {
		return (Situation)submitData(location);
	}

	@Override
	public Situation getGeoportalData(Location location) {
		return (Situation) submitData(location);
	}

	private AbstractDataFusionType submitData(AbstractDataFusionType requestData) {

		AbstractDataFusionType response = null;
		try{
			String requestId = requestData.getId();
			LogHelper.debug(ContextModuleFacade.class, "submitData", "trying to submit data with ID: %s", requestId);

			requests.put(requestId, null);
			client.publish(Constants.MODULE_NAME, requestData);
			waitForResponse(requestId);
			response = requests.get(requestId);
			requests.remove(requestId);
			LogHelper.debug(ContextModuleFacade.class, "submitData", "returning response data with ID: %s", requestId);
		}catch(RuntimeException exc){
			LogHelper.exception(ContextModuleFacade.class, "submitData", 
					String.format("Could not retrieve data from %s. Reason: '%s'", Constants.MODULE_NAME, exc.getLocalizedMessage()), exc);
			
		}
		return response;
	}

	private void waitForResponse(String id) throws ContextModuleRuntimeException{

		LogHelper.debug(ContextModuleFacade.class, "waitForResponse", "Waiting for message with ID: %s", id);

		int waitingTime = 0;
		int sleepTime = 100;
		while (requests.get(id) == null && (isDebug() || waitingTime < getResponseWaitingTime())) {
			try {
				Thread.sleep(sleepTime);
				waitingTime+=sleepTime;
			} catch (InterruptedException e) {
				LogHelper.debug(ContextModuleFacade.class, "waitForResponse", "Interrupted exception: %s", e.getLocalizedMessage());
			}
		}
		if(requests.get(id) == null){
			String errorMsg = String.format("Response from the %s not received for %d ms", Constants.MODULE_NAME, getResponseWaitingTime());
			LogHelper.warning(ContextModuleFacade.class, "waitForResponse", errorMsg);
			throw new ContextModuleRuntimeException(errorMsg);
		}
		LogHelper.debug(ContextModuleFacade.class, "waitForResponse", "Response received for message with ID: %s", id);
	}

}
