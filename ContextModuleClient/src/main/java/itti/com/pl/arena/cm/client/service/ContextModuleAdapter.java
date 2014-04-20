package itti.com.pl.arena.cm.client.service;

import java.util.ArrayList;
import java.util.List;

import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.service.LocalContextModule;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequestProperties;
import itti.com.pl.arena.cm.utils.helper.ArenaObjectsMapper;
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
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.ZoneName.name(), zoneId);
        // send/receive
        eu.arena_fp7._1.Object response = contextModule.getZone(request);
        // return parsed response
        return ArenaObjectsMapper.fromZoneObject(response);
    }

    public boolean removeZone(String zoneId) {
        // prepare a request
        SimpleNamedValue request = contextModule.createSimpleNamedValue(moduleName, ContextModuleRequestProperties.ZoneName.name(), zoneId);
        // send/receive
        BooleanNamedValue response = contextModule.removeZone(request);
        // return parsed response
        return response.isFeatureValue();
    }

    public boolean updateZone(String zoneName, String parkingLot, String[] coordinates) {
        // prepare a request
        List<AbstractNamedValue> vector = new ArrayList<>();
        vector.add(contextModule.createSimpleNamedValue(moduleName, 
                ContextModuleRequestProperties.ParkingLotName.name(), parkingLot));
        vector.add(contextModule.createSimpleNamedValue(moduleName, 
                ContextModuleRequestProperties.ZoneName.name(), zoneName));
        if(coordinates != null){
            itti.com.pl.arena.cm.dto.Location[] locations = null;
            try {
                locations = LocationHelper.getLocationsFromStrings(coordinates);
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
}
