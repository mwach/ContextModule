package itti.com.pl.arena.cm.server.service.jms;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import itti.com.pl.arena.cm.dto.coordinates.ArenaObjectCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.server.TestHelper;
import itti.com.pl.arena.cm.server.ontology.Ontology;
import itti.com.pl.arena.cm.server.ontology.OntologyConstants;
import itti.com.pl.arena.cm.server.ontology.OntologyException;
import itti.com.pl.arena.cm.server.service.jms.ContextModuleJmsService;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * Test class for the {@link ContextModuleJmsService}
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleJmsServiceTest {

    private static ContextModuleJmsService CMJmsService = null;

    @BeforeClass
    public static void beforeClass() {
        CMJmsService = new ContextModuleJmsService();
    }

    @Test
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="NP_NULL_PARAM_DEREF", 
            justification="Intentionally testing code can handle null value")
    public void testUpdatePlatformServiceNullRequest() {

        //null request
        SimpleNamedValue request = null;
        BooleanNamedValue response = CMJmsService.updatePlatform(request);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(null, response.getFeatureName());

    }

    @Test
    public void testUpdatePlatformServiceEmptyRequest() {

        //empty request
        BooleanNamedValue response = CMJmsService.updatePlatform(new SimpleNamedValue());
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(null, response.getFeatureName());

    }

    @Test
    public void testUpdatePlatformServiceInvalidValueRequest() {

        //non-parseable value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        snv.setValue("dummyText");
        BooleanNamedValue response = CMJmsService.updatePlatform(snv);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(null, response.getFeatureName());

    }

    @Test
    public void testUpdatePlatformServiceProperValueRequest() throws JsonHelperException, OntologyException {

        //prepare dummy ontology
        Ontology ontology = Mockito.mock(Ontology.class);
        CMJmsService.setOntology(ontology);

        //valid value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        Platform platform = TestHelper.createDummyPlatform(null);
        snv.setValue(JsonHelper.toJson(platform));
        BooleanNamedValue response = CMJmsService.updatePlatform(snv);
        //request failure returned
        assertEquals(true, response.isFeatureValue());
        assertEquals(platform.getId(), response.getFeatureName());

        //verify ontology was called
        Mockito.verify(ontology).updatePlatform(Mockito.any(Platform.class));
    }

    @Test
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="NP_NULL_PARAM_DEREF", 
            justification="Intentionally testing code can handle null value")
    public void testUpdateParkingLotServiceNullRequest() {

        //null request
        SimpleNamedValue request = null;
        BooleanNamedValue response = CMJmsService.updateParkingLot(request);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(null, response.getFeatureName());

    }

    @Test
    public void testUpdateParkingLotServiceEmptyRequest() {

        //empty request
        BooleanNamedValue response = CMJmsService.updateParkingLot(new SimpleNamedValue());
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(null, response.getFeatureName());

    }

    @Test
    public void testUpdateParkingLotServiceInvalidValueRequest() {

        //non-parseable value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        snv.setValue("dummyText");
        BooleanNamedValue response = CMJmsService.updateParkingLot(snv);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(null, response.getFeatureName());

    }

    @Test
    public void testUpdateParkingLotServiceProperValueRequest() throws JsonHelperException, OntologyException {

        //prepare dummy ontology
        Ontology ontology = Mockito.mock(Ontology.class);
        CMJmsService.setOntology(ontology);

        //valid value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        ParkingLot parkingLot = TestHelper.createDummyParkingLot(null);
        snv.setValue(JsonHelper.toJson(parkingLot));
        BooleanNamedValue response = CMJmsService.updateParkingLot(snv);
        //request failure returned
        assertEquals(true, response.isFeatureValue());
        assertEquals(parkingLot.getId(), response.getFeatureName());

        //verify ontology was called
        Mockito.verify(ontology).updateParkingLot(Mockito.any(ParkingLot.class));
    }

    @Test
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="NP_NULL_PARAM_DEREF", 
            justification="Intentionally testing code can handle null value")
    public void testGetPlatformNeighborhoodNullRequest() {

        //null request
        SimpleNamedValue request = null;
        Object response = CMJmsService.getPlatformNeighborhood(request);
        //empty response returned
        assertTrue(response.getFeatureVector().getFeature().isEmpty());
    }

    @Test
    public void testGetPlatformNeighborhoodEmptyRequest() {

        //empty request
        Object response = CMJmsService.getPlatformNeighborhood(new SimpleNamedValue());
        //empty response returned
        assertTrue(response.getFeatureVector().getFeature().isEmpty());
    }

    @Test
    public void testGetPlatformNeighborhoodInvalidValueRequest() {

        //non-parseable value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        snv.setValue("dummyText");
        Object response = CMJmsService.getPlatformNeighborhood(snv);
        //empty response returned
        assertTrue(response.getFeatureVector().getFeature().isEmpty());
    }

    @Test
    public void testGetPlatformNeighborhoodProperValueRequest() throws JsonHelperException, OntologyException {

        //prepare dummy ontology
        Ontology ontology = Mockito.mock(Ontology.class);
        //object found, ontology is going to return it
        Set<ArenaObjectCoordinate> coordinates = new HashSet<>();
        coordinates.add(new ArenaObjectCoordinate("ID"));
        Mockito.when(ontology.getPlatformNeighborhood(Mockito.anyString())).thenReturn(
                coordinates);
        CMJmsService.setOntology(ontology);

        //valid value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        snv.setValue("ValidPlatform");
        Object response = CMJmsService.getPlatformNeighborhood(snv);
        //there are some items in the response
        assertTrue(!response.getFeatureVector().getFeature().isEmpty());

        //verify ontology was called
        Mockito.verify(ontology).getPlatformNeighborhood(Mockito.anyString());
    }

    @Test
    public void testGetListOfPlatformsRequest() throws OntologyException {

        //prepare dummy ontology
        Ontology ontology = Mockito.mock(Ontology.class);
        Mockito.when(ontology.getInstances(OntologyConstants.Vehicle_with_cameras.name())).thenReturn(Arrays.asList(new String[]{"a", "b"}));
        CMJmsService.setOntology(ontology);

        //null request
        Object response = CMJmsService.getListOfPlatforms(Mockito.mock(SimpleNamedValue.class));
        //there are some platforms defined
        assertFalse(response.getFeatureVector().getFeature().isEmpty());
    }

}
