package itti.com.pl.arena.cm.utils.helper;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.RealWorldCoordinate;
import eu.arena_fp7._1.SimpleNamedValue;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.exception.ErrorMessages;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequestProperties;

/**
 * Translates ARENA DTO objects into CM internal objects
 * 
 * @author cs-admin
 * 
 */
public final class ArenaObjectsMapper {

    private ArenaObjectsMapper() {
    }

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static Location fromLocation(eu.arena_fp7._1.Location arenaLocation) {
        return new Location(arenaLocation.getX(), arenaLocation.getY());
    }

    public static Location fromLocation(eu.arena_fp7._1.RealWorldCoordinate arenaLocation) {
        return new Location(arenaLocation.getX(), arenaLocation.getY(), 0, arenaLocation.getZ());
    }

    public static eu.arena_fp7._1.Location toLocation(Location location) {
        eu.arena_fp7._1.Location arenaLocation = objectFactory.createLocation();
        arenaLocation.setX(location.getLongitude());
        arenaLocation.setY(location.getLatitude());
        return arenaLocation;
    }

    public static eu.arena_fp7._1.RealWorldCoordinate toRealWorldCoordinate(Location location) {
        eu.arena_fp7._1.RealWorldCoordinate arenaLocation = objectFactory.createRealWorldCoordinate();
        arenaLocation.setX(location.getLongitude());
        arenaLocation.setY(location.getLatitude());
        arenaLocation.setZ(location.getAltitude());
        return arenaLocation;
    }

    public static Zone fromZoneObject(Object response) {

        String zoneName = null;
        String planeName = null;
        long ident = 0;
        List<Location> coordinates = new ArrayList<>();

        for (AbstractNamedValue feature : response.getFeatureVector().getFeature()) {

            if (feature instanceof SimpleNamedValue) {
                String name = ((SimpleNamedValue) feature).getFeatureName();
                String value = ((SimpleNamedValue) feature).getValue();
                if (StringHelper.equalsIgnoreCase(ContextModuleRequestProperties.Name.name(), name)) {
                    zoneName = value;
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequestProperties.PlaneName.name(), name)) {
                    planeName = value;
                } else if (StringHelper.equalsIgnoreCase(ContextModuleRequestProperties.Ident.name(), name)) {
                    ident = NumbersHelper.getIntegerFromString(value);
                }

            } else if (feature instanceof RealWorldCoordinate) {
                coordinates.add(ArenaObjectsMapper.fromLocation((RealWorldCoordinate) feature));
            } else if (feature instanceof eu.arena_fp7._1.Location) {
                coordinates.add(ArenaObjectsMapper.fromLocation((eu.arena_fp7._1.Location) feature));
            }
        }

        Zone zone = new Zone(zoneName);
        zone.setPlaneName(planeName);
        zone.setIdent(ident);
        zone.addCoordinates(coordinates);
        return zone;
    }

    public static Platform fromPlatformObject(Object response) {

        Platform platform = null;
        for (AbstractNamedValue feature : response.getFeatureVector().getFeature()) {
            if (feature instanceof SimpleNamedValue) {
                String platformJson = ((SimpleNamedValue) feature).getValue();
                try {
                    platform = JsonHelper.fromJson(platformJson, Platform.class);
                } catch (JsonHelperException e) {
                    e.printStackTrace();
                }
            }
        }
        return platform;
    }

    public static Camera fromCameraObject(Object response) {

        Camera camera = null;
        for (AbstractNamedValue feature : response.getFeatureVector().getFeature()) {
            if (feature instanceof SimpleNamedValue) {
                String cameraJson = ((SimpleNamedValue) feature).getValue();
                try {
                    camera = JsonHelper.fromJson(cameraJson, Camera.class);
                } catch (JsonHelperException e) {
                    e.printStackTrace();
                }
            }
        }
        return camera;
    }

    public static ParkingLot fromParkingLotObject(Object response) {
        ParkingLot parkingLot = null;
        for (AbstractNamedValue feature : response.getFeatureVector().getFeature()) {
            if (feature instanceof SimpleNamedValue) {
                String parkingLotJson = ((SimpleNamedValue) feature).getValue();
                try {
                    parkingLot = JsonHelper.fromJson(parkingLotJson, ParkingLot.class);
                } catch (JsonHelperException e) {
                    e.printStackTrace();
                }
            }
        }
        return parkingLot;
    }

    public static GeoObject fromBuildingObject(Object response) {
        GeoObject responseObject = null;
        for (AbstractNamedValue feature : response.getFeatureVector().getFeature()) {
            if (feature instanceof SimpleNamedValue) {
                String buildingJson = ((SimpleNamedValue) feature).getValue();
                JOptionPane.showMessageDialog(null, buildingJson);
                try {
                    Building building = JsonHelper.fromJson(buildingJson, Building.class);
                    if(building.getType() == null){
                        throw new JsonHelperException(ErrorMessages.JSON_HELPER_CANNOT_DESERIALIZE, buildingJson,"Empty type: probably a Infrastructure");
                    }
                    responseObject = building;
                } catch (JsonHelperException e) {
                    try {
                        Infrastructure infrastructure = JsonHelper.fromJson(buildingJson, Infrastructure.class);
                        responseObject = infrastructure;
                        JOptionPane.showMessageDialog(null, "PARSED: " + infrastructure);

                    } catch (JsonHelperException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(null, "RESPONSE: " + responseObject);
        return responseObject;
    }

}
