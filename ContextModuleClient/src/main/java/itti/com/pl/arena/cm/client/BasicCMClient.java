package itti.com.pl.arena.cm.client;

import java.util.List;
import java.util.UUID;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.CameraType;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequests;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;

import com.safran.arena.MessageFilterInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * Basic version of the ContextModule client prepared for testing/presentation purposes
 * @author cm-admin
 *
 */
public class BasicCMClient extends ModuleImpl{

    /*
     * ARENA/ZMQ adapter
     */
    private Client client = null;
    private ObjectFactory objectFactory = new ObjectFactory();

    //primitive flag to verify if response was received
    private boolean responseReceived = false;

    /**
     * Constructor
     * @param moduleName name of the client
     */
    public BasicCMClient(String moduleName) {
        super(moduleName);
    }


    /**
     * main method
     * @param args optional args (not supported for now)
     * @throws JsonHelperException could not parse object into JSON
     */
    public static void main(String[] args) throws JsonHelperException {

        //create new client object
        BasicCMClient cmClient = new BasicCMClient("BasicCMClient");
        //connect to the ARENA BUS
        cmClient.initializeClient();

        //test service - add new platform to the ontology
        cmClient.addPlatform();

        cmClient.waitForResponse();

        //test service - get platform from the ontology
        cmClient.getPlatform();

        //wait for the response
        cmClient.waitForResponse();
        
        //shutdown
        cmClient.shutdown();
        System.exit(0);
    }


    private void waitForResponse() {
        //wait for the response
        while(!responseReceived){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        responseReceived = false;
    }


    private void addPlatform() throws JsonHelperException {

        //create dummy platform
        Platform platform = createPlatform("testPlatform");
        //parse it into JSON object
        String jsonPlatform = JsonHelper.toJson(platform);
        //create request object
        SimpleNamedValue platformRequest = createSimpleNamedValue(jsonPlatform);

        //specify service name on the CM side
        platformRequest.setHref(ContextModuleRequests.updatePlatform.name());
        //send the request
        client.publish(Constants.MODULE_NAME, platformRequest);
    }

    private void getPlatform() {

        //create basic request, specify name of the requested platform
        SimpleNamedValue platformRequest = createSimpleNamedValue("testPlatform");
        //specify service name on the CM side
        platformRequest.setHref(ContextModuleRequests.getPlatform.name());
        //send the request
        client.publish(Constants.MODULE_NAME, platformRequest);
    }


    /**
     * 'Standard' ARENA component initialization
     */
    private void initializeClient(){
        //create new client instance
        client = new Client("127.0.0.1", "", "45444");

        //connect and register
        client.connectToServer();
        client.registerModule(this);
        client.registerModuleAsDataProvider(this);
        client.registerModuleAsDataConsumer(this, new MessageFilterInterface() {
            
            @Override
            public boolean accept(AbstractDataFusionType arg0) {
                return true;
            }
        });
    }

    /**
     * Disconnects from the ARENA bus
     */
    private void shutdown() {
        client.unregisterModule(this);
    }

    /**
     * Helper method - creates platform with camera using random values
     * @param platformId ID of the platform
     * @return platform object
     */
    private static Platform createPlatform(String platformId) {
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

    /**
     * Helper method - creates camera object
     * @param cameraId ID of the camera
     * @return camera object
     */
    private static Camera createDummyCamera(String cameraId) {
        //thermal camera 
        // camera angles (area of view) are 120 and 90 degrees
        //located on the right side of the truck (X coordinate is set to '2', which means 2m right from platform center), 
        //5m back from the front of the truck
        //angle is 90 degree, which means, camera is monitoring right side of the truck (0 means main axis of the truck: Y) 
        return new Camera(cameraId, CameraType.Thermal.name(), 120, 90, new CartesianCoordinate(2, -5), 90);
    }

    /**
     * Creates simple request object
     * 
     * @param value
     *            String value to be send to the CM
     * @return created request object
     */
    private SimpleNamedValue createSimpleNamedValue(String value) {
        //request object
        SimpleNamedValue object = objectFactory.createSimpleNamedValue();
        //random ID
        object.setId(getObjectId());
        //data source set to module name
        object.setDataSourceId(getModuleName());
        //value passed as an argument
        object.setValue(value);
        return object;
    }

    /**
     * Returns random, unique message ID
     * 
     * @return message ID
     */
    private String getObjectId() {
        return String.format("%s.%s.%s", Constants.MODULE_NAME, getModuleName(), UUID.randomUUID().toString());
    }

    /**
     * Method called on server response received
     */
    @Override
    public void onDataChanged(Class<? extends AbstractDataFusionType> dataType, String dataSourceId, AbstractDataFusionType data) {

        // check, if data should be processed by current module
        if (dataSourceId.equals(getModuleName())) {

            responseReceived = true;

            // do something with the response

            // true/false is returned by the 'addPlatform' method
            if(data instanceof BooleanNamedValue){
                System.out.println(String.format("response received: %b", ((BooleanNamedValue)data).isFeatureValue()));
            }
            // string value is returned by the 'getPlatform' method
            else if(data instanceof Object && 
                    data.getHref().equals(ContextModuleRequests.getPlatform.name())){
                List<AbstractNamedValue> response = ((Object)data).getFeatureVector().getFeature();
                System.out.println(String.format("response received: %s", response));
                try {
                    Platform responsePlatform = JsonHelper.fromJson(((SimpleNamedValue)response.get(0)).getValue(), Platform.class);
                    System.out.println(String.format("Platform object: %s", responsePlatform));
                } catch (JsonHelperException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
