package itti.com.pl.arena.cm.ontology;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import itti.com.pl.arena.cm.dto.Camera;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.Platform;
import itti.com.pl.arena.cm.dto.RelativePosition;
import itti.com.pl.arena.cm.dto.Truck;

public class ContextModuleOntologyManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static Ontology cmOntologyManager;
    private Random random = new Random();

    @BeforeClass
    public static void beforeClass(){

	//to speed up tests, load ontology and then reuse it among tests
	ContextModuleOntologyManager manager = new ContextModuleOntologyManager();
	manager.setOntologyLocation(OntologyManagerTest.ONTOLOGY_LOCATION);
	manager.setOntologyNamespace(OntologyManagerTest.ONTOLOGY_NAMESPACE);
	manager.init();
	cmOntologyManager = manager;
    }

    @Test
    public void testGetInstances() throws OntologyException {
	Platform information = cmOntologyManager.getPlatform("Truck_A1");
	Assert.assertNotNull(information);
    }

    @Test
    public void testAddRetrieveTruck() throws OntologyException {
	//adds a new truck to the ontology
	//then tries to retrieve it
	Set<Camera> cameras = new HashSet<Camera>();
	Camera cameraOne = new Camera(UUID.randomUUID().toString(), "typeA", random.nextDouble(), random.nextDouble(), RelativePosition.Back);
	Camera cameraTwo = new Camera(UUID.randomUUID().toString(), "typeB", random.nextDouble(), random.nextDouble(), RelativePosition.Front);
	cameras.add(cameraOne);
	cameras.add(cameraTwo);
	Platform information = new Truck("Truck_test1", new Location(random.nextDouble(), random.nextDouble(), random.nextInt(100)), cameras);
	cmOntologyManager.updatePlatform(information);
	assertEquals(information, cmOntologyManager.getPlatform(information.getId()));
    }

    @Test
    public void testUpdateTruckPosition() throws OntologyException {
	//checks, if location of the platform is correctly updated
	Location initLocation = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(100));
	Location nextLocation = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(100));
	Platform information = new Truck("truck_m1", initLocation, null);
	cmOntologyManager.updatePlatform(information);
	assertEquals(initLocation, cmOntologyManager.getPlatform(information.getId()).getLastLocation());
	information.setLastPosition(nextLocation);
	cmOntologyManager.updatePlatform(information);
	assertEquals(nextLocation, cmOntologyManager.getPlatform(information.getId()).getLastLocation());
    }

    @Test
    public void testGetGISObjects() throws OntologyException{
	Set<String> gisObjects = cmOntologyManager.getParkingLots(1, 1, 100);
	assertFalse(gisObjects.isEmpty());
    }
}
