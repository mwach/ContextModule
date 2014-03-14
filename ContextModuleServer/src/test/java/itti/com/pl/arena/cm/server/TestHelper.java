package itti.com.pl.arena.cm.server;

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
     * @return valid class/instance name
     */
    public static String getOntologyClassName(){
        return String.format("className_%d", random.nextInt(10000));
    }

    /**
     * Returns geographical coordinate (value of meridian/circle) (range 0-360, measured in degrees)
     * @return geographical coordinate
     */
    public static double getCoordinate() {
        return 360 * random.nextDouble();
    }

    /**
     * Returns position of camera on platform (range 0-5, measured in meters)
     * @return position on platform
     */
    public static double getPlatformCoordinate() {
        return 5 * random.nextDouble();
    }

    /**
     * Returns value of the bearing (range 0-360)
     * @return bearing
     */
    public static int getBearing() {
        return random.nextInt(360);
    }

    /**
     * Creates a dummy {@link Platform} object for tests purposes
     * @return platform object with random values
     */
    public static Platform createDummyPlatform(String platformName) {

        Platform platform = new Platform(platformName);
        //platform cameras
        platform.addCamera(createDummyCamera("dummyCamera_" + System.currentTimeMillis()));
        platform.addCamera(createDummyCamera("anotherDummyCamera_" + System.currentTimeMillis()));
        //platform dimensions
        platform.setHeight(random.nextInt(3));
        platform.setWidth(random.nextInt(3));
        platform.setLength(random.nextInt(20));
        //platform location
        platform.setLocation(new itti.com.pl.arena.cm.dto.Location(getCoordinate(), getCoordinate()));
        return platform;
    }

    /**
     * Creates a dummy {@link Camera} object for tests purposes
     * @return camera object with random values
     */
    private static Camera createDummyCamera(String cameraId) {

        //random camera type is going to be used
        return new Camera(cameraId, CameraType.values()[random.nextInt(CameraType.values().length)].name(), 
                // camera angles
                random.nextInt(360), random.nextInt(360), 
                //position of camera on platform (relative position where middle-front of the platform is marked as (0,0)
                //plus camera direction (comparing to main truck angle - Y axis)
                new CartesianCoordinate(getPlatformCoordinate(), getPlatformCoordinate()), getBearing());
    }

    public static ParkingLot createDummyParkingLot(String parkingLotId) {
        ParkingLot parkingLot = new ParkingLot(parkingLotId);

        //general information about parking
        parkingLot.setCountry("UK");
        parkingLot.setLocation(new itti.com.pl.arena.cm.dto.Location(-0.94, 51.43));
        parkingLot.setTown("Reading");
        parkingLot.setStreet("London Street");
        parkingLot.setStreetNumber(23);

        //add parking boundaries
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94334, 51.43234));
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94345, 51.43233));
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94332, 51.43245));
        parkingLot.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94366, 51.43212));

        //parking infrastructure
        parkingLot.addBuilding(createDummyBuilding("dummyBuilding1_" + System.currentTimeMillis()));
        parkingLot.addBuilding(createDummyBuilding("dummyBuilding2_" + System.currentTimeMillis()));
        parkingLot.addIntrastructure(createDummyInfrastructure("dummyInfrastructure_" + System.currentTimeMillis()));

        return parkingLot;
    }

    public static Building createDummyBuilding(String buildingId) {

        //new building object
        Building building = new Building(buildingId, Type.Hotel);

        //building boundaries
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94334, 51.43234));
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94345, 51.43233));
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94332, 51.43245));
        building.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94366, 51.43212));

        return building;
    }

    public static Infrastructure createDummyInfrastructure(String infrastructureId) {

        //new infrastructure object
        Infrastructure infrastructure = new Infrastructure(infrastructureId, Infrastructure.Type.Fence);

        //building boundaries
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94334, 51.43234));
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94345, 51.43233));
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94332, 51.43245));
        infrastructure.addBoundary(new itti.com.pl.arena.cm.dto.Location(-0.94366, 51.43212));

        return infrastructure;
    }

}
