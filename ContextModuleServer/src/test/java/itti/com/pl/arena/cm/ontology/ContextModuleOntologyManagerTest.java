package itti.com.pl.arena.cm.ontology;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform.Type;
import itti.com.pl.arena.cm.dto.dynamicobj.RelativePosition;

public class ContextModuleOntologyManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static Ontology cmOntologyManager;
    private Random random = new Random();

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
        Platform information = cmOntologyManager.getPlatform("Vehicla_with_cameras_R1");
        assertNotNull(information);
    }

    @Test
    public void testGetPlatforms() throws OntologyException {
        Set<Platform> platforms = cmOntologyManager.getPlatforms(-0.94, 51.43, 1.0);
        assertFalse(platforms.isEmpty());
    }

    @Test
    public void testAddRetrieveTruck() throws OntologyException {
        // adds a new truck to the ontology
        // then tries to retrieve it
        Set<Camera> cameras = new HashSet<Camera>();
        Camera cameraOne = new Camera(UUID.randomUUID().toString(), "typeA", random.nextDouble(), random.nextDouble(),
                RelativePosition.Back);
        Camera cameraTwo = new Camera(UUID.randomUUID().toString(), "typeB", random.nextDouble(), random.nextDouble(),
                RelativePosition.Front);
        cameras.add(cameraOne);
        cameras.add(cameraTwo);
        Platform information = new Platform("Vehicle_test1", new Location(random.nextDouble(), random.nextDouble(),
                random.nextInt(100)), Type.Vehicle_with_cameras, cameras);
        information.setWidth(random.nextDouble());
        information.setHeight(random.nextDouble());
        information.setLength(random.nextDouble());
        cmOntologyManager.updatePlatform(information);
        assertEquals(information, cmOntologyManager.getPlatform(information.getId()));
    }

    @Test
    public void testUpdateTruckPosition() throws OntologyException {
        // checks, if location of the platform is correctly updated
        Location initLocation = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(100));
        Location nextLocation = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(100));
        Platform information = new Platform("vehicle_m1", initLocation, Type.Vehicle_with_cameras, null);
        cmOntologyManager.updatePlatform(information);
        assertEquals(initLocation, cmOntologyManager.getPlatform(information.getId()).getLocation());
        information.setLocation(nextLocation);
        cmOntologyManager.updatePlatform(information);
        assertEquals(nextLocation, cmOntologyManager.getPlatform(information.getId()).getLocation());
    }

    @Test
    public void testGetParkingLots() throws OntologyException {
        // corrects args - at least one parking expected
        Set<String> gisObjects = cmOntologyManager.getParkingLots(-0.94, 51.40, 1);
        assertFalse(gisObjects.isEmpty());

        // incorrect args - no parking lots expected
        gisObjects = cmOntologyManager.getParkingLots(1, 1, 0);
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
    public void testCalculateDistance() throws OntologyException {
        // create parking lot and add truck to it
        String parkingId = "Vehicla_with_cameras_R1";
        cmOntologyManager.calculateDistancesForPlatform(parkingId, 5);
    }

}
