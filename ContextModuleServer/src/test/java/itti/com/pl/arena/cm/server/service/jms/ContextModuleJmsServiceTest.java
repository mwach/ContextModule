package itti.com.pl.arena.cm.server.service.jms;

import static org.junit.Assert.*;
import itti.com.pl.arena.cm.server.service.jms.ContextModuleJmsService;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import org.junit.BeforeClass;
import org.junit.Test;

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

}
