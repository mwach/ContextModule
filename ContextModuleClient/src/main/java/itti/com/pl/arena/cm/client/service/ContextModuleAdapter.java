package itti.com.pl.arena.cm.client.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.service.LocalContextModule;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequestProperties;
import itti.com.pl.arena.cm.utils.helper.ArenaObjectsMapper;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.LocationHelper;
import itti.com.pl.arena.cm.utils.helper.LocationHelperException;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.FeatureVector;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.RealWorldCoordinate;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * Context Module client facade for {@link ModuleImpl}
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleAdapter {

    private String moduleName;
    private LocalContextModule contextModule;
    private boolean connected = false;

    /**
     * Default constructor
     * 
     * @param moduleName
     *            name of the module (used during communication with CM server)
     */
    public ContextModuleAdapter(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Connects to the CM server
     * 
     * @param brokerUrl
     *            broker of the ARENA Bus
     */
    public void connect(String brokerUrl) {
        if (!connected) {
            contextModule = new ContextModuleFacade(moduleName, brokerUrl);
            contextModule.init();
            connected = true;
        }
    }

    /**
     * Unregisters from the ARENA bus
     */
    public void disconnect() {
        if (connected) {
            contextModule.shutdown();
            connected = false;
        }
    }

    /**
     * Returns connection status
     * 
     * @return true, if component is connected to the ARENA bus, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Returns a list of parking lots defined in the ontology
     * 
     * @return list of strings representing parking lots names
     */
    public List<String> getListOfParkingLots() {

        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.ParkingLotName.name(), null);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getListOfParkingLots(request);
        // return parsed response
        return getStringsFromFeatureVector(response == null ? null : response.getFeatureVector());
    }

    public List<String> getListOfZones(String parkingLot) {

        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.ParkingLotName.name(), parkingLot);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getListOfZones(request);
        // return parsed response
        return getStringsFromFeatureVector(response == null ? null : response.getFeatureVector());
    }

    public Zone getZoneDefinition(String zoneId) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), zoneId);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getZone(request);
        // return parsed response
        return ArenaObjectsMapper.fromZoneObject(response);
    }

    public boolean removeZone(String zoneId) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), zoneId);
        // send/receive
        BooleanNamedValue response = contextModule.removeZone(request);
        // return parsed response
        return response.isFeatureValue();
    }

    public boolean updateZone(String zoneName, String parkingLot, String planeName, List<String> coordinates) {
        // prepare a request
        List<AbstractNamedValue> vector = new ArrayList<>();
        vector.add(contextModule.createSimpleNamedValue(moduleName, 
                ContextModuleRequestProperties.ParkingLotName.name(), parkingLot));
        vector.add(contextModule.createSimpleNamedValue(moduleName, 
                ContextModuleRequestProperties.Name.name(), zoneName));
        vector.add(contextModule.createSimpleNamedValue(moduleName, 
                ContextModuleRequestProperties.PlaneName.name(), planeName));
        if(coordinates != null){
            itti.com.pl.arena.cm.dto.Location[] locations = null;
            try {
                locations = LocationHelper.getLocationsFromStrings(coordinates.toArray(new String[coordinates.size()]));
            } catch (LocationHelperException e) {
                return false;
            }
            for (itti.com.pl.arena.cm.dto.Location location : locations) {
                vector.add(ArenaObjectsMapper.toRealWorldCoordinate(location));
            }
        }
        eu.arena_fp7._1.Object request = contextModule.createObject(moduleName, vector);
        // send/receive
        SimpleNamedValue response = contextModule.updateZone(request);
        // return parsed response
        return response != null ? StringHelper.hasContent(response.getValue()) : false;
    }

    /**
     * Parses response stored inside {@link FeatureVector} object
     * 
     * @param featureVector
     *            information stored inside {@link FeatureVector}
     */
    private List<String> getStringsFromFeatureVector(FeatureVector featureVector) {

        List<String> response = new ArrayList<>();
        if (featureVector != null) {
            for (AbstractDataFusionType feature : featureVector.getFeature()) {
                if (feature instanceof RealWorldCoordinate) {
                    response.add(LocationHelper.createStringFromLocation(ArenaObjectsMapper
                            .fromLocation((RealWorldCoordinate) feature)));
                } else {
                    if (feature instanceof Location) {
                        response.add(LocationHelper.createStringFromLocation(ArenaObjectsMapper.fromLocation((Location) feature)));
                    } else {
                        response.add(((SimpleNamedValue) feature).getValue());
                    }
                }
            }
        }
        return response;
    }

    public List<String> getListOfPlatforms() {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Platform.name(), null);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getListOfPlatforms(request);
        // return parsed response
        return getStringsFromFeatureVector(response == null ? null : response.getFeatureVector());
    }

    public Platform getPlatformDefinition(String platformName) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Platform.name(), platformName);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getPlatform(request);
        // return parsed response
        return ArenaObjectsMapper.fromPlatformObject(response);
    }

    public boolean removePlatform(String platformId) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), platformId);
        // send/receive
        BooleanNamedValue response = contextModule.removePlatform(request);
        // return parsed response
        return response.isFeatureValue();
    }

    public boolean updatePlatform(String platformName, itti.com.pl.arena.cm.dto.Location location, double width, double height,
            double length, Collection<Camera> cameras) throws JsonHelperException {
        Platform platform = new Platform(platformName);
        platform.setLocation(location);
        platform.setWidth(width);
        platform.setHeight(height);
        platform.setLength(length);
        platform.setCameras(cameras);

        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Platform.name(), JsonHelper.toJson(platform));
        // send/receive
        BooleanNamedValue response = contextModule.updatePlatform(request);
        // return parsed response
        return response != null ? response.isFeatureValue() : false;

    }

    public boolean updateCamera(String cameraName, String platformName, String cameraType, double horizontalAngle, double verticalAngle,
            CartesianCoordinate cameraLocationOnTruck, int cameraAngle) throws JsonHelperException {

        Camera camera = new Camera(cameraName, cameraType, horizontalAngle, verticalAngle, cameraLocationOnTruck, cameraAngle);
        List<AbstractNamedValue> vector = new ArrayList<>();
        vector.add(contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Camera.name(), JsonHelper.toJson(camera)));
        vector.add(contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.PlatformName.name(), platformName));
        eu.arena_fp7._1.Object request = contextModule.createObject(moduleName, vector);
        // send/receive
        BooleanNamedValue response = contextModule.updateCamera(request);
        // return parsed response
        return response != null ? response.isFeatureValue() : false;

    }

    public boolean removeCamera(String cameraId) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), cameraId);
        // send/receive
        BooleanNamedValue response = contextModule.removeCamera(request);
        // return parsed response
        return response.isFeatureValue();
    }

    public Camera getCameraDefinition(String selectedCamera) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Camera.name(), selectedCamera);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getCamera(request);
        // return parsed response
        return ArenaObjectsMapper.fromCameraObject(response);
    }

    public boolean removeParkingLot(String parkingLotId) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), parkingLotId);
        // send/receive
        BooleanNamedValue response = contextModule.removeParkingLot(request);
        // return parsed response
        return response.isFeatureValue();
    }

    public boolean updateParkingLot(String name, String description, String country,
            String town, String street, Collection<itti.com.pl.arena.cm.dto.Location> locations, Collection<Building> buildings, Collection<Infrastructure> infrastructures) throws JsonHelperException {

        ParkingLot parkingLot = new ParkingLot(name);
        parkingLot.setCountry(country);
        parkingLot.setTown(town);
        parkingLot.setStreet(street);
        parkingLot.setBoundaries(locations);
        if(locations != null  && !locations.isEmpty()){
            parkingLot.setLocation(locations.iterator().next());
        }
        parkingLot.setBuildings(buildings);
        parkingLot.setInfrastructures(infrastructures);
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.ParkingLot.name(), JsonHelper.toJson(parkingLot));
        // send/receive
        BooleanNamedValue response = contextModule.updateParkingLot(request);
        // return parsed response
        return response != null ? response.isFeatureValue() : false;

    }

    public ParkingLot getParkingLotDefinition(String parkingLotName) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.ParkingLot.name(), parkingLotName);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getParkingLot(request);
        // return parsed response
        return ArenaObjectsMapper.fromParkingLotObject(response);
    }

    public boolean removeBuilding(String buildingName) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), buildingName);
        // send/receive
        BooleanNamedValue response = contextModule.removeBuilding(request);
        // return parsed response
        return response.isFeatureValue();
    }

    public GeoObject getBuildingDefinition(String buildingName) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), buildingName);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getBuilding(request);
        // return parsed response
        return ArenaObjectsMapper.fromBuildingObject(response);
    }

    public boolean updateBuilding(String buildingName, String parkingLotName, String type,
            List<itti.com.pl.arena.cm.dto.Location> locations) throws JsonHelperException, ContextModuleClientException {

        GeoObject building = null;

        if(Building.Type.getType(type) != null){
            building = new Building(buildingName, parkingLotName, Building.Type.getType(type));
        }else if(Infrastructure.Type.getType(type) != null){
            building = new Infrastructure(buildingName, parkingLotName, Infrastructure.Type.getType(type));
        }else{
            throw new ContextModuleClientException(String.format("Invalid building type specified: %s", type));
        }
        building.setBoundaries(locations);

        SimpleNamedValue request = contextModule.createSimpleNamedValue(
                moduleName, ContextModuleRequestProperties.ParkingLot.name(), JsonHelper.toJson(building));
        // send/receive
        BooleanNamedValue response = contextModule.updateBuilding(request);
        // return parsed response
        return response != null ? response.isFeatureValue() : false;
    }

    public boolean updateRule(String ruleName, String ruleContent) {
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ruleName, ruleContent);
        // send/receive
        BooleanNamedValue response = contextModule.updateRule(request);
        // return parsed response
        return response != null ? response.isFeatureValue() : false;
    }

    public boolean removeRule(String ruleName) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), ruleName);
        // send/receive
        BooleanNamedValue response = contextModule.removeRule(request);
        // return parsed response
        return response.isFeatureValue();
    }

    public String getRule(String ruleName) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), ruleName);
        // send/receive
        SimpleNamedValue response = contextModule.getRule(request);
        // return parsed response
        return response.getValue();
    }

    public List<String> getListOfRules() {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), null);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getListOfRules(request);
        // return parsed response
        return getStringsFromFeatureVector(response == null ? null : response.getFeatureVector());
    }

    public boolean applyRules() {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.Name.name(), null);
        // send/receive
        BooleanNamedValue response = contextModule.applyRules(request);
     // return parsed response
        return response.isFeatureValue();    }
}
