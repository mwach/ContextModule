package itti.com.pl.arena.cm.server;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.CameraType;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.dto.staticobj.Building.Type;

import java.util.Random;

public class TestHelper {

    private static Random random = new Random();

    /**
     * generates valid, random ontology name which can be used as a name of class or instance
     * 
     * @return valid class/instance name
     */
    public static String getOntologyClassName() {
        return String.format("className_%d", random.nextInt(10000));
    }

    /**
     * Returns geographical coordinate (value of meridian/circle) (range 0-360, measured in degrees)
     * 
     * @return geographical coordinate
     */
    public static double getCoordinate() {
        return 360 * random.nextDouble();
    }

    /**
     * Returns position of camera on platform (range 0-5, measured in meters)
     * 
     * @return position on platform
     */
    public static double getPlatformCoordinate() {
        return 5 * random.nextDouble();
    }

    /**
     * Returns value of the bearing (range 0-360)
     * 
     * @return bearing
     */
    public static int getBearing() {
        return random.nextInt(360);
    }

    /**
     * Creates dummy location
     * 
     * @param longitude
     *            reference longitude
     * @param latitude
     *            reference latitude
     * @return location close to the reference data (max difference is one degree)
     */
    public static Location createDummyLocation(double longitude, double latitude) {
        return new Location(longitude + random.nextDouble(), latitude - random.nextDouble());
    }

    /**
     * Creates a dummy {@link Platform} object for tests purposes
     * 
     * @return platform object with random values
     */
    public static Platform createDummyPlatform(String platformName) {

        Platform platform = new Platform(platformName);
        // platform cameras
        platform.addCamera(createDummyCamera("dummyCamera_" + System.currentTimeMillis(), platformName));
        platform.addCamera(createDummyCamera("anotherDummyCamera_" + System.currentTimeMillis(), platformName));
        // platform dimensions
        platform.setHeight(random.nextInt(3));
        platform.setWidth(random.nextInt(3));
        platform.setLength(random.nextInt(20));
        // platform location
        platform.setLocation(createDummyLocation(getCoordinate(), getCoordinate()));
        return platform;
    }

    /**
     * Creates a dummy {@link Camera} object for tests purposes
     * 
     * @return camera object with random values
     */
    private static Camera createDummyCamera(String cameraId, String platformName) {

        // random camera type is going to be used
        return new Camera(cameraId, CameraType.values()[random.nextInt(CameraType.values().length)].name(),
        // camera angles
                random.nextInt(360), random.nextInt(360),
                // position of camera on platform (relative position where middle-front of the platform is marked as
                // (0,0)
                // plus camera direction (comparing to main truck angle - Y axis)
                new CartesianCoordinate(getPlatformCoordinate(), getPlatformCoordinate()), getBearing());
    }

    public static ParkingLot createDummyParkingLot(String parkingLotId) {
        ParkingLot parkingLot = new ParkingLot(parkingLotId);

        // general information about parking
        parkingLot.setCountry("UK");
        parkingLot.setLocation(createDummyLocation(-0.94, 51.43));
        parkingLot.setTown("Reading-" + random.nextInt(100));
        parkingLot.setStreet("London Street");
        parkingLot.setStreetNumber(23);

        // add parking boundaries
        for (int i = 0; i < 4; i++) {
            parkingLot.addBoundary(createDummyLocation(-0.94334, 51.43234));
        }

        // parking infrastructure
        parkingLot.addBuilding(createDummyBuilding("dummyBuilding1_" + System.currentTimeMillis(), parkingLotId));
        parkingLot.addBuilding(createDummyBuilding("dummyBuilding2_" + System.currentTimeMillis(), parkingLotId));
        parkingLot
                .addIntrastructure(createDummyInfrastructure("dummyInfrastructure_" + System.currentTimeMillis(), parkingLotId));

        return parkingLot;
    }

    public static Building createDummyBuilding(String buildingId, String parkingLotId) {

        // new building object
        Building building = new Building(buildingId, parkingLotId, Type.Hotel);

        // building boundaries
        for (int i = 0; i < 4; i++) {
            building.addBoundary(createDummyLocation(-0.94345, 51.43233));
        }

        return building;
    }

    public static Infrastructure createDummyInfrastructure(String infrastructureId, String parkingLotName) {

        // new infrastructure object
        Infrastructure infrastructure = new Infrastructure(infrastructureId, parkingLotName, Infrastructure.Type.Fence);

        // building boundaries
        for (int i = 0; i < 4; i++) {
            infrastructure.addBoundary(createDummyLocation(-0.94345, 51.43233));
        }

        return infrastructure;
    }

}
