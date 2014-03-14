package itti.com.pl.arena.cm.server.service.jms;

import static org.junit.Assert.*;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.server.TestHelper;
import itti.com.pl.arena.cm.server.ontology.Ontology;
import itti.com.pl.arena.cm.server.ontology.OntologyException;
import itti.com.pl.arena.cm.server.service.jms.ContextModuleJmsService;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.ObjectFactory;
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
        CMJmsService.setFactory(new ObjectFactory());
    }

    @Test
    public void testUpdatePlatformServiceNullRequest() {

        //null request
        BooleanNamedValue response = CMJmsService.updatePlatform(null);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(StringHelper.toString(null), response.getFeatureName());

    }

    @Test
    public void testUpdatePlatformServiceEmptyRequest() {

        //empty request
        BooleanNamedValue response = CMJmsService.updatePlatform(new SimpleNamedValue());
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(StringHelper.toString(null), response.getFeatureName());

    }

    @Test
    public void testUpdatePlatformServiceInvalidValueRequest() {

        //non-parseable value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        snv.setValue("dummyText");
        BooleanNamedValue response = CMJmsService.updatePlatform(snv);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(StringHelper.toString(null), response.getFeatureName());

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
        assertEquals(StringHelper.toString(platform.getId()), response.getFeatureName());

        //verify ontology was called
        Mockito.verify(ontology).updatePlatform(Mockito.any(Platform.class));
    }

    @Test
    public void testUpdateParkingLotServiceNullRequest() {

        //null request
        BooleanNamedValue response = CMJmsService.updateParkingLot(null);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(StringHelper.toString(null), response.getFeatureName());

    }

    @Test
    public void testUpdateParkingLotServiceEmptyRequest() {

        //empty request
        BooleanNamedValue response = CMJmsService.updateParkingLot(new SimpleNamedValue());
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(StringHelper.toString(null), response.getFeatureName());

    }

    @Test
    public void testUpdateParkingLotServiceInvalidValueRequest() {

        //non-parseable value in the request
        SimpleNamedValue snv = new SimpleNamedValue();
        snv.setValue("dummyText");
        BooleanNamedValue response = CMJmsService.updateParkingLot(snv);
        //request failure returned
        assertEquals(false, response.isFeatureValue());
        assertEquals(StringHelper.toString(null), response.getFeatureName());

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
        assertEquals(StringHelper.toString(parkingLot.getId()), response.getFeatureName());

        //verify ontology was called
        Mockito.verify(ontology).updatePlatform(Mockito.any(Platform.class));
    }

}
