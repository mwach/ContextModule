package itti.com.pl.arena.cm.client.service;

import java.util.AbstractMap;
import java.util.HashMap;

import com.safran.arena.MessageFilterInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.SimpleNamedValue;
import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.exception.ContextModuleRuntimeException;
import itti.com.pl.arena.cm.jms.CMModuleImpl;
import itti.com.pl.arena.cm.service.LocalContextModule;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequests;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelper;
import itti.com.pl.arena.cm.utils.helper.NetworkHelperException;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

/**
 * Context Module client facade for {@link ModuleImpl}
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleFacade extends CMModuleImpl implements LocalContextModule {

    private static final int DEFAULT_CLIENT_PORT = 45444;
    private static final int DEFAULT_MAX_WAITING_TIME = 5000;
    private static final int DEFAULT_SLEEP_INTERVAL = 100;
    private static final boolean DEFAULT_DEBUG_MODE = false;

    /**
     * Client module used for communication with other Arena modules
     */
    private Client client = null;

    /**
     * URL of the arena broker
     */
    private String brokerUrl = null;

    /**
     * Sets URL of the ARENA broker
     * 
     * @param brokerUrl
     */
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    /**
     * returns URL of the broker
     * @return broker URL
     */
    private String getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * IP of the client (localhost by default)
     */
    private String clientIpAddress = null;

    /**
     * Sets IP address of the client
     * 
     * @param clientIpAddress
     */
    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    /**
     * Returns IP of the client
     * @return client IP address
     */
    private String getClientIpAddress() {
        return clientIpAddress;
    }

    /**
     * Client port
     */
    private int clientPort = DEFAULT_CLIENT_PORT;

    /**
     * Sets port of the client
     * 
     * @param clientPort
     */
    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    /**
     * returns port of the client
     * @return client port
     */
    private int getClientPort() {
        return clientPort;
    }

    /**
     * How long module should wait for the ContextModule response
     */
    private int responseWaitingTime = DEFAULT_MAX_WAITING_TIME;

    /**
     * Sets maximum response waiting time
     * 
     * @param responseWaitingTime
     */
    public void setResponseWaitingTime(int responseWaitingTime) {
        this.responseWaitingTime = responseWaitingTime;
    }

    /**
     * returns maximum response waiting time
     * @return response waiting time
     */
    private int getResponseWaitingTime() {
        return responseWaitingTime;
    }

    /**
     * 'debug' mode flag
     */
    private boolean debug = DEFAULT_DEBUG_MODE;

    /**
     * Sets debug mode
     * 
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * returns value of the 'debug' flag
     * @return debug status
     */
    private boolean isDebug() {
        return debug;
    }

    /**
     * Map containing IDs of all requests that were sent from current client
     */
    private AbstractMap<String, AbstractDataFusionType> requests = new HashMap<String, AbstractDataFusionType>();

    private synchronized AbstractMap<String, AbstractDataFusionType> getRequestsMap() {
        return requests;
    }

    /**
     * Constructor
     * 
     * @param moduleName
     *            name of the module
     * @param brokerUrl
     *            URL to the Arena broker
     */
    public ContextModuleFacade(String moduleName, String brokerUrl) {
        super(moduleName);
        setBrokerUrl(brokerUrl);
    }

    /**
     * Initializes the module. This method must be called before any of the CM services will be called
     * @throws ContextModuleClientException could not initialize client
     */
    public void init() {

        try {
            // if client IP was not provided, use default value (IP of the current host)
            if (!StringHelper.hasContent(getClientIpAddress())) {
                setClientIpAddress(NetworkHelper.getIpAddress());
            }

            // connects to the server
            client = new Client(getBrokerUrl(), getClientIpAddress(), String.valueOf(getClientPort()));
            client.connectToServer();

            // register current module in server
            client.registerModule(this);
            client.registerModuleAsDataProvider(this);
            client.registerModuleAsDataConsumer(this, new MessageFilterInterface() {
                
                @Override
                public boolean accept(AbstractDataFusionType arg0) {
                    return true;
                }
            });
        } catch (RuntimeException exc) {
            //runtime exception handling
            LogHelper.error(ContextModuleFacade.class, "init", "Could not initialize client object. Reason: '%s'",
                    exc.getLocalizedMessage());
            throw new ContextModuleRuntimeException(exc.getLocalizedMessage(), exc);
        } catch (NetworkHelperException exc) {
            //network exception handling
            LogHelper.error(ContextModuleFacade.class, "init", "Could not obtain local IP address. Reason: '%s'",
                    exc.getLocalizedMessage());
            throw new ContextModuleRuntimeException("Could not obtain local IP address during client initialization", exc);
        }
    }

    /**
     * Method called on server response received
     */
    @Override
    public void onDataChanged(Class<? extends AbstractDataFusionType> dataType, String dataSourceId, AbstractDataFusionType data) {

        // check, if data should be processed by current module
        if (dataSourceId.equals(getModuleName()) && getRequestsMap().containsKey(data.getId())) {
            // add response to valid request
            getRequestsMap().put(data.getId(), data);
        }
    }

    /**
     * Unregisters current module from the server
     */
    public void shutdown() {
        if (client != null) {
            client.unregisterModule(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getPlatform(eu.arena_fp7._1.SimpleNamedValue)
     */
    @Override
    public eu.arena_fp7._1.Object getPlatform(SimpleNamedValue object) {
    	object.setHref(ContextModuleRequests.getPlatform.name());
        return (Object) submitData(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getPlatform(eu.arena_fp7._1.SimpleNamedValue)
     */
    @Override
    public eu.arena_fp7._1.Object getPlatformNeighborhood(SimpleNamedValue parkingId) {
    	parkingId.setHref(ContextModuleRequests.getPlatformNeighborhood.name());
        return (Object) submitData(parkingId);
    }

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.service.ContextModule#updateParkingLot(eu.arena_fp7._1.SimpleNamedValue)
     */
    @Override
    public BooleanNamedValue updateParkingLot(SimpleNamedValue parkingLot) {
    	parkingLot.setHref(ContextModuleRequests.updateParkingLot.name());
        return (BooleanNamedValue)submitData(parkingLot);
    }

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.service.ContextModule#updateParkingLot(eu.arena_fp7._1.SimpleNamedValue)
     */
    @Override
    public Object getParkingLot(SimpleNamedValue parkingLot) {
    	parkingLot.setHref(ContextModuleRequests.getParkingLot.name());
        return (Object)submitData(parkingLot);
    }

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.service.ContextModule#updatePlatform(eu.arena_fp7._1.SimpleNamedValue)
     */
    @Override
    public BooleanNamedValue updatePlatform(SimpleNamedValue platform) {
    	platform.setHref(ContextModuleRequests.updatePlatform.name());
        return (BooleanNamedValue)submitData(platform);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getPlatforms(eu.arena_fp7._1.Location)
     */
    @Override
    public Object getPlatforms(Location location) {
    	location.setHref(ContextModuleRequests.getPlatforms.name());
        return (Object) submitData(location);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getGISData(eu.arena_fp7._1.Location)
     */
    @Override
    public Object getGISData(Location location) {
    	location.setHref(ContextModuleRequests.getGISData.name());
        return (Object) submitData(location);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getGISData(eu.arena_fp7._1.Object)
     */
    @Override
    public Object getGISData(Object parameters) {
    	parameters.setHref(ContextModuleRequests.getGISData.name());
        return (Object) submitData(parameters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.service.ContextModule#getGeoportalData(eu.arena_fp7._1.Location)
     */
    @Override
    public Object getGeoportalData(Location location) {
    	location.setHref(ContextModuleRequests.getGeoportalData.name());
        return (Object) submitData(location);
    }

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.service.ContextModule#defineZone(eu.arena_fp7._1.Object)
     */
    @Override
    public SimpleNamedValue updateZone(Object zoneDefinition) {
    	zoneDefinition.setHref(ContextModuleRequests.updateZone.name());
        return (SimpleNamedValue)submitData(zoneDefinition);
    }

	@Override
	public BooleanNamedValue removeZone(SimpleNamedValue zoneId) {
    	zoneId.setHref(ContextModuleRequests.removeZone.name());
        return (BooleanNamedValue)submitData(zoneId);
	}

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.service.ContextModule#getZone(eu.arena_fp7._1.SimpleNamedValue)
     */
    @Override
    public Object getZone(SimpleNamedValue zoneId) {
    	zoneId.setHref(ContextModuleRequests.getZone.name());
        return (Object)submitData(zoneId);
    }

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.service.ContextModule#getZone(eu.arena_fp7._1.SimpleNamedValue)
     */
    @Override
    public Object getListOfZones(SimpleNamedValue zoneId) {
    	zoneId.setHref(ContextModuleRequests.getListOfZones.name());
        return (Object)submitData(zoneId);
    }

    @Override
    public Object getListOfParkingLots(SimpleNamedValue objectId) {
		objectId.setHref(ContextModuleRequests.getListOfParkingLots.name());
        return (Object)submitData(objectId);
    }

	@Override
	public SimpleNamedValue defineRule(SimpleNamedValue rule) {
		rule.setHref(ContextModuleRequests.defineRule.name());
		return (SimpleNamedValue)submitData(rule);
	}

	@Override
	public BooleanNamedValue removeRule(SimpleNamedValue ruleId) {
		ruleId.setHref(ContextModuleRequests.removeRule.name());
		return (BooleanNamedValue)submitData(ruleId);
	}

	@Override
	public BooleanNamedValue applyRules(SimpleNamedValue objectId) {
		objectId.setHref(ContextModuleRequests.applyRules.name());
		return (BooleanNamedValue)submitData(objectId);	
	}

	@Override
	public Object getListOfRules(SimpleNamedValue objectId) {
		objectId.setHref(ContextModuleRequests.getListOfRules.name());
		return (Object)submitData(objectId);
	}

    /**
     * Submits data to the server
     * 
     * @param requestData
     *            data to be submitted
     * @return server response
     */
    private AbstractDataFusionType submitData(AbstractDataFusionType requestData) {

        // get the request ID
        String requestId = requestData.getId();

        AbstractDataFusionType response = null;
        try {
            LogHelper.debug(ContextModuleFacade.class, "submitData", "trying to submit data with ID: %s", requestId);

            // put it to the requests map
            getRequestsMap().put(requestId, null);
            // call the service
            client.publish(Constants.MODULE_NAME, requestData);
            // and wait for the response
            waitForResponse(requestId);
            // get the response and return it to the service
            response = getRequestsMap().get(requestId);
            LogHelper.debug(ContextModuleFacade.class, "submitData", "returning response data with ID: %s", requestId);
        } catch (RuntimeException exc) {
            LogHelper.error(
                    ContextModuleFacade.class,
                    "submitData",
                    "Could not retrieve data from %s. Reason: '%s'", Constants.MODULE_NAME,
                            exc.getLocalizedMessage());

        } finally{
            //remove requestId from the map
            getRequestsMap().remove(requestId);            
        }
        return response;
    }

    /**
     * Waits for the response from remote module
     * 
     * @param id
     *            ID of the request
     * @throws ContextModuleRuntimeException
     *             response not received
     */
    private void waitForResponse(String id) throws ContextModuleRuntimeException {

        LogHelper.debug(ContextModuleFacade.class, "waitForResponse", "Waiting for message with ID: %s", id);

        int waitingTime = 0;
        while (
        // response not yet received
        getRequestsMap().get(id) == null &&
        // debug flag is on, or waiting time not yet exceeded
                (isDebug() || waitingTime < getResponseWaitingTime())) {
            try {
                // sleep for some defined period
                Thread.sleep(DEFAULT_SLEEP_INTERVAL);
                waitingTime += DEFAULT_SLEEP_INTERVAL;
            } catch (InterruptedException e) {
                LogHelper.debug(ContextModuleFacade.class, "waitForResponse", "Interrupted exception: %s",
                        e.getLocalizedMessage());
            }
        }
        // response not received for specified amount of time
        if (getRequestsMap().get(id) == null) {
            throw new ContextModuleRuntimeException("Response from the %s not received for %d ms", Constants.MODULE_NAME,
                    getResponseWaitingTime());

        }
        // response received
        LogHelper.debug(ContextModuleFacade.class, "waitForResponse", "Response received for message with ID: %s", id);
    }
}
