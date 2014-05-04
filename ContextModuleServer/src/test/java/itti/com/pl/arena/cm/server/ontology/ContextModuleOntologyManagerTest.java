package itti.com.pl.arena.cm.server.ontology;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.coordinates.ArenaObjectCoordinate;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform.Type;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.server.TestHelper;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.location.Range;
import itti.com.pl.arena.cm.server.ontology.ContextModuleOntologyManager;
import itti.com.pl.arena.cm.server.ontology.Ontology;
import itti.com.pl.arena.cm.server.ontology.OntologyConstants;
import itti.com.pl.arena.cm.server.ontology.OntologyException;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

public class ContextModuleOntologyManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static Ontology cmOntologyManager;
    private static Random random = new Random();

    @BeforeClass
    public static void beforeClass() {

        // to speed up tests, load ontology and then reuse it among tests
        ContextModuleOntologyManager manager = new ContextModuleOntologyManager();
        manager.setOntologyLocation(OntologyManagerTest.ONTOLOGY_LOCATION);
        manager.setOntologyNamespace(OntologyManagerTest.ONTOLOGY_NAMESPACE);
        manager.init();
        cmOntologyManager = manager;
    }

    @Test
    public void testGetPlatform() throws OntologyException {
        Platform information = cmOntologyManager.getOntologyObject("Vehicle_CNA0544", Platform.class);
        assertNotNull(information);
    }

    @Test
    public void testGetPlatforms() throws OntologyException {
        Set<Platform> platforms = cmOntologyManager.getPlatforms(-0.94, 51.43, 1.0);
        assertFalse(platforms.isEmpty());
    }

    @Test
    public void testUpdateNewPlatform() throws OntologyException {
        // adds a new platform to the ontology, then tries to retrieve it
        Platform information = TestHelper.createDummyPlatform("TestPlatform_" + System.currentTimeMillis());
        cmOntologyManager.updatePlatform(information);
        assertEquals(information, cmOntologyManager.getOntologyObject(information.getId(), Platform.class));
    }

    @Test
    public void testUpdateExistingPlatform() throws OntologyException {
        // adds a new platform to the ontology
        Platform information = TestHelper.createDummyPlatform("TestPlatform_" + System.currentTimeMillis());
        cmOntologyManager.updatePlatform(information);
        assertEquals(information, cmOntologyManager.getOntologyObject(information.getId(), Platform.class));

        //then modifies it and updates one more time
        Platform updatedPlatform = TestHelper.createDummyPlatform(information.getId());
        cmOntologyManager.updatePlatform(updatedPlatform);
        assertEquals(updatedPlatform, cmOntologyManager.getOntologyObject(information.getId(), Platform.class));
    }

    @Test
    public void testUpdateTruckPosition() throws OntologyException {
        // checks, if location of the platform is correctly updated
        Location initLocation = new Location(TestHelper.getCoordinate(), TestHelper.getCoordinate(), TestHelper.getBearing());
        Location nextLocation = new Location(TestHelper.getCoordinate(), TestHelper.getCoordinate(), TestHelper.getBearing());
        Platform information = new Platform("vehicle_m1", initLocation, Type.Vehicle_with_cameras, null);
        cmOntologyManager.updatePlatform(information);
        assertEquals(initLocation, cmOntologyManager.getOntologyObject(information.getId(), Platform.class).getLocation());
        information.setLocation(nextLocation);
        cmOntologyManager.updatePlatform(information);
        assertEquals(nextLocation, cmOntologyManager.getOntologyObject(information.getId(), Platform.class).getLocation());
    }

    @Test
    public void testGetParkingLots() throws OntologyException {
        // corrects args - at least one parking expected
        Set<String> gisObjects = cmOntologyManager.getInstanceNames(-0.94, 51.40, 1, ParkingLot.class);
        assertFalse(gisObjects.isEmpty());

        // incorrect args - no parking lots expected
        gisObjects = cmOntologyManager.getInstanceNames(1, 1, 0, ParkingLot.class);
        assertTrue(gisObjects.isEmpty());
    }

    @Test
    public void testGetParkingLotObjectsValid() throws OntologyException {
        // all object from existing parking
        Set<String> gisObjects = cmOntologyManager.getParkingLotInfrastructure("Parking_Reading");
        assertFalse(gisObjects.isEmpty());
        Set<String> buildings = cmOntologyManager.getParkingLotInfrastructure("Parking_Reading", "Building");
        assertFalse(buildings.isEmpty());
        // there are more infrastructure objects than buildings
        assertTrue(buildings.size() < gisObjects.size());
    }

    @Test
    public void testGetParkingLotObjectsInvalid() throws OntologyException {
        // all object from non-existing parking
        String parkingId = "dummy parking lot";
        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(String.format(ErrorMessages.ONTOLOGY_INSTANCE_NOT_FOUND.getMessage(), parkingId));
        cmOntologyManager.getParkingLotInfrastructure(parkingId);
    }

    @Test
    public void testUpdateParkingLot() throws OntologyException{
        //creates a new parking lot
        ParkingLot parkingLot = TestHelper.createDummyParkingLot("MyParkingLot_" + System.currentTimeMillis());

        //add to ontology, then try to retrieve it back
        cmOntologyManager.updateParkingLot(parkingLot);
        ParkingLot returnedParkingLot = cmOntologyManager.getOntologyObject(parkingLot.getId(), ParkingLot.class);
        assertEquals(parkingLot, returnedParkingLot);
    }

    @Test
    public void testCalculateDistance() throws OntologyException {
        // create parking lot and add truck to it
        String truckId = "Vehicle_CNA0544";
        cmOntologyManager.calculateDistancesForPlatform(truckId, 5);
    }

    // test for complex functionality for retrieving cameras field of view
    @Test
    public void testGetTruckWorld() throws OntologyException {
        String truckId = "Vehicle_CNA0544";
        Platform platformWithCameras = cmOntologyManager.getOntologyObject(truckId, Platform.class);
        Location location = platformWithCameras.getLocation();
        Set<String> availableParkings = cmOntologyManager.getInstanceNames(location.getLongitude(), location.getLatitude(),
                Range.Km10.getRangeInKms(), ParkingLot.class);
        String firstParking = availableParkings.iterator().next();
        ParkingLot parkingLot = cmOntologyManager.getOntologyObject(firstParking, ParkingLot.class);
        assertNotNull(parkingLot);
    }

    // test for complex functionality for retrieving information about platform neighborhood
    @Test
    public void testGetPlatformNeighborhoodNullId() throws OntologyException {

        //null platformId provided
        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(ErrorMessages.ONTOLOGY_EMPTY_INSTANCE_NAME.getMessage());

        cmOntologyManager.getPlatformNeighborhood(null);
    }

    @Test
    public void testGetPlatformNeighborhoodEmptyId() throws OntologyException {

        //empty platformId provided
        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(ErrorMessages.ONTOLOGY_EMPTY_INSTANCE_NAME.getMessage());

        cmOntologyManager.getPlatformNeighborhood("");
    }

    @Test
    public void testGetPlatformNeighborhoodInvalidId() throws OntologyException {

        //invalid platformId provided
        String platformId = "nonExistingPlatformId";

        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(String.format(
                ErrorMessages.ONTOLOGY_INSTANCE_NOT_FOUND.getMessage(), platformId));

        cmOntologyManager.getPlatformNeighborhood(platformId);
    }

    @Test
    public void testCalculateArenaDistancesForPlatformValidId() throws OntologyException {

        //valid platformId provided
        String platformId = "platformId_" + System.currentTimeMillis();
        //valid parkingLotId provided
        String parkingLotOneId = "parkingLotOne_" + System.currentTimeMillis();
        String parkingLotTwoId = "parkingLotTwo_" + System.currentTimeMillis();

        //create dummy objects for test purposes
        ParkingLot parkingLotOne = TestHelper.createDummyParkingLot(parkingLotOneId);
        ParkingLot parkingLotTwo = TestHelper.createDummyParkingLot(parkingLotTwoId);
        Platform platform = TestHelper.createDummyPlatform(platformId);

        //make sure, their locations match
        platform.setLocation(parkingLotTwo.getLocation());
        //now add them to the ontology
        cmOntologyManager.updatePlatform(platform);
        cmOntologyManager.updateParkingLot(parkingLotOne);
        cmOntologyManager.updateParkingLot(parkingLotTwo);

        //verify stored objects
        Platform ontoPlatform = cmOntologyManager.getOntologyObject(platformId, Platform.class);
        ParkingLot ontoParkingLotOne = cmOntologyManager.getOntologyObject(parkingLotOneId, ParkingLot.class);
        ParkingLot ontoParkingLotTwo = cmOntologyManager.getOntologyObject(parkingLotTwoId, ParkingLot.class);
        assertEquals(platform, ontoPlatform);
        assertEquals(parkingLotOne, ontoParkingLotOne);
        assertEquals(parkingLotTwo, ontoParkingLotTwo);

        //later, try to retrieve parking data
        Set<ArenaObjectCoordinate> coordinates = cmOntologyManager.getPlatformNeighborhood(platformId);

        //verify data was returned
        assertFalse(coordinates.isEmpty());
        //data is from added parking lots
        for (ArenaObjectCoordinate arenaObjectCoordinate : coordinates) {
            String objectId = arenaObjectCoordinate.getId();
            assertTrue(
                    parkingLotOne.getBuildings().containsKey(objectId) ||
                    parkingLotOne.getInfrastructure().containsKey(objectId) ||
                    parkingLotTwo.getBuildings().containsKey(objectId) ||
                    parkingLotTwo.getInfrastructure().containsKey(objectId)
            );
        }
    }

    // test 'update zone' functionality
    @Test
    public void testUpdateZoneNullLocations() throws OntologyException {

        //if no locations were specified, empty zone is going to be created
        String zoneName = cmOntologyManager.updateZone(null, null, "planeName", null);
        assertNotNull(cmOntologyManager.getZone(zoneName));
    }

    @Test
    public void testUpdateZoneEmptyLocations() throws OntologyException {

        //if no locations were specified, empty zone is going to be created
        String zoneName = cmOntologyManager.updateZone(null, null, "planeName", new ArrayList<Location>());
        assertNotNull(cmOntologyManager.getZone(zoneName));
    }

    // test 'get zone' functionality
    @Test
    public void testGetZoneNullId() throws OntologyException {

        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(
                String.format(ErrorMessages.ONTOLOGY_EMPTY_VALUE_PROVIDED.getMessage(), OntologyConstants.Car_parking_zone.name()));
        //if invalid zoneId was specified, nothing is going to be returned
        cmOntologyManager.getZone(null);
    }

    @Test
    public void testGetZoneEmptyId() throws OntologyException {

        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(
                String.format(ErrorMessages.ONTOLOGY_EMPTY_VALUE_PROVIDED.getMessage(), OntologyConstants.Car_parking_zone.name()));
        //if invalid zoneId was specified, nothing is going to be returned
        cmOntologyManager.getZone("");
    }

    @Test
    public void testGetZoneInvalidId() throws OntologyException {

        String instanceName = "someDummyZoneName";
        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(
                String.format(ErrorMessages.ONTOLOGY_INSTANCE_NOT_FOUND.getMessage(), 
                        instanceName));
        //if invalid zoneId was specified, nothing is going to be returned
        cmOntologyManager.getZone(instanceName);
    }

    @Test
    public void testUpdateGetZoneValid() throws OntologyException {

        List<Location> locations = new ArrayList<>();
        int noOfLocations = 5;
        for(int i=0;i<noOfLocations ; i++){
            locations.add(
                    new Location(TestHelper.getCoordinate(), TestHelper.getCoordinate(), 0, TestHelper.getCoordinate()));
        }
        //add new zone to ontology
        String zoneId = cmOntologyManager.updateZone(null, null, "planeName", locations);
        assertNotNull(zoneId);

        //now try to get the zone from ontology
        Location[] response = cmOntologyManager.getZone(zoneId).getLocations();
        assertEquals(noOfLocations, response.length);

        //verify returned values
        for (Location location : response) {
            assertTrue(locations.contains(location));
            locations.remove(location);
        }
    }

    @Test
    public void testUpdateGetMultipleZones() throws OntologyException {

        //update/get more than one zone at once
        List<Location> locationsZoneA = new ArrayList<>();
            locationsZoneA.add(
                    new Location(TestHelper.getCoordinate(), TestHelper.getCoordinate(), 0, TestHelper.getCoordinate()));
        List<Location> locationsZoneB = new ArrayList<>();
        locationsZoneB.add(
                    new Location(TestHelper.getCoordinate(), TestHelper.getCoordinate(), 0, TestHelper.getCoordinate()));

        //add new zones to ontology
        String zoneIdA = cmOntologyManager.updateZone(null, null, "planeNameA", locationsZoneA);
        assertNotNull(zoneIdA);

        String zoneIdB = cmOntologyManager.updateZone(null, null, "planeNameA", locationsZoneB);
        assertNotNull(zoneIdB);

        assertFalse(StringHelper.equalsIgnoreCase(zoneIdA, zoneIdB));

        //now try to get the zone from ontology
        //verify returned values
        Location[] responseZoneA = cmOntologyManager.getZone(zoneIdA).getLocations();
        assertTrue(responseZoneA[0].equals(locationsZoneA.get(0)));

        Location[] responseZoneB = cmOntologyManager.getZone(zoneIdB).getLocations();
        assertTrue(responseZoneB[0].equals(locationsZoneB.get(0)));
    }

    @Test
    public void testUpdatePlatformPosition() throws OntologyException {

        //create dummyPlatform
        String platformId = "platform-" + random.nextInt(1000);
        Location dummyLocation = new Location(
                random.nextDouble(), random.nextDouble(), 0);

        //add new zone platform to the ontology
        cmOntologyManager.updatePlatformPosition(platformId, dummyLocation);

        //get platform from the ontology
        Platform ontoPlatform = cmOntologyManager.getOntologyObject(platformId, Platform.class);
        assertEquals(platformId, ontoPlatform.getId());
        assertEquals(dummyLocation, ontoPlatform.getLocation());

        //simulate platform position update
        Location newDummyLocation =  new Location(
                random.nextDouble(), random.nextDouble());
        //add new zone platform to the ontology
        cmOntologyManager.updatePlatformPosition(platformId, newDummyLocation);

        //get platform from the ontology
        ontoPlatform = cmOntologyManager.getOntologyObject(platformId, Platform.class);
        assertEquals(platformId, ontoPlatform.getId());
        assertEquals(newDummyLocation, ontoPlatform.getLocation());
    }

    @Test
    public void testCreateGetCamera() throws OntologyException {
        //first, create platform
        String platformName = "platform_" + System.currentTimeMillis();
        cmOntologyManager.updatePlatformPosition(platformName, new Location());

        //now, create camera
        String cameraName = "camera_" + System.currentTimeMillis();
        Camera camera = new Camera(cameraName, "fisheye", TestHelper.getBearing(), TestHelper.getBearing(), 
                new CartesianCoordinate(random.nextDouble(), random.nextDouble()), TestHelper.getBearing());
        cmOntologyManager.updateCamera(camera, platformName);

        //now get the camera from ontology
        Camera ontoCamera = cmOntologyManager.getOntologyObject(cameraName, Camera.class);
        assertEquals(camera, ontoCamera);
    }

}
