package itti.com.pl.arena.cm.server.ontology;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import itti.com.pl.arena.cm.dto.Location;
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

public class ContextModuleOntologyManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static Ontology cmOntologyManager;

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

    // test for complex functionality for retrieving cameras field of view
    @Test
    public void testCalculateArenaDistancesForPlatform() throws OntologyException {
        String truckId = "Vehicle_CNA0544";
        cmOntologyManager.calculateArenaDistancesForPlatform(truckId);
    }

    // test 'define zone' functionality
    @Test
    public void testDefineZoneNullLocations() throws OntologyException {

        //if no locations were specified, zone is not going to be created
        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(
                String.format(ErrorMessages.ONTOLOGY_EMPTY_VALUE_PROVIDED.getMessage(), OntologyConstants.Object_has_GPS_coordinates.name()));

        cmOntologyManager.defineZone(null);
    }

    @Test
    public void testDefineZoneEmptyLocations() throws OntologyException {

        //if no locations were specified, zone is not going to be created
        expectedException.expect(OntologyException.class);
        expectedException.expectMessage(
                String.format(ErrorMessages.ONTOLOGY_EMPTY_VALUE_PROVIDED.getMessage(), OntologyConstants.Object_has_GPS_coordinates.name()));

        cmOntologyManager.defineZone(new ArrayList<Location>());
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
    public void testDefineGetZoneValid() throws OntologyException {

        List<Location> locations = new ArrayList<>();
        int noOfLocations = 5;
        for(int i=0;i<noOfLocations ; i++){
            locations.add(
                    new Location(TestHelper.getCoordinate(), TestHelper.getCoordinate(), 0, TestHelper.getCoordinate()));
        }
        //add new zone to ontology
        String zoneId = cmOntologyManager.defineZone(locations);
        assertNotNull(zoneId);

        //now try to get the zone from ontology
        List<Location> response = cmOntologyManager.getZone(zoneId);
        assertEquals(noOfLocations, response.size());

        //verify returned values
        for (Location location : response) {
            assertTrue(locations.contains(location));
            locations.remove(location);
        }
    }

}
