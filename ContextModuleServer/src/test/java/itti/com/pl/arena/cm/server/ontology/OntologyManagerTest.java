package itti.com.pl.arena.cm.server.ontology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itti.com.pl.arena.cm.server.TestHelper;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.ontology.OntologyConstants;
import itti.com.pl.arena.cm.server.ontology.OntologyException;
import itti.com.pl.arena.cm.server.ontology.OntologyManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanInitializationException;

public class OntologyManagerTest {

    public static final String ONTOLOGY_LOCATION = "TestOntology.owl";
    public static final String ONTOLOGY_NAMESPACE = "http://www.owl-ontologies.com/Ontology1350654591.owl#";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static OntologyManager ontologyManager;

    @BeforeClass
    public static void beforeClass() {

        // to speed up tests, load ontology and then reuse it among tests
        ontologyManager = new OntologyManager();
        ontologyManager.setOntologyLocation(ONTOLOGY_LOCATION);
        ontologyManager.setOntologyNamespace(ONTOLOGY_NAMESPACE);
        ontologyManager.init();
    }

    @AfterClass
    public static void afterClass() {
        ontologyManager.shutdown();
    }

    @Test
    public void testInitInvalidLocation() {
        // check exception, when no location was provided
        expectedException.expect(BeanInitializationException.class);
        expectedException.expectMessage(String.format(ErrorMessages.ONTOLOGY_CANNOT_LOAD.getMessage(), "null"));
        OntologyManager om = new OntologyManager();
        om.init();
    }

    @Test
    public void testCreateOwlClass() throws OntologyException {
        String className = TestHelper.getOntologyClassName();
        String instanceName = className + "inst";
        // verify class was added
        Assert.assertTrue(ontologyManager.createOwlClass(className));
        // now add some basic instance to newly create class
        ontologyManager.createSimpleInstance(className, instanceName, null);
        
        List<String> instances = ontologyManager.getInstances(className);
        // verify, instance was added to given class
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(instanceName, instances.get(0));
    }

    @Test
    public void testAddInstanceWithProperties() throws OntologyException {

        // select existing class having at least one known property
        String className = OntologyConstants.Parking.name();
        String propertyName = OntologyConstants.Object_has_GPS_x.name();
        String propertyValue = String.valueOf(123.456);
        Map<String, String[]> properties = new HashMap<>();
        properties.put(propertyName, new String[] { propertyValue });

        // try to add instance of parking
        String instanceName = TestHelper.getOntologyClassName();
        // now add some basic instance to newly create class
        ontologyManager.createSimpleInstance(className, instanceName, properties);

        // verify, instance was correctly added
        Map<String, String[]> ontologyProperties = ontologyManager.getInstanceProperties(instanceName);
        // verify, instance was added to given class
        // two properties should be returned - added in this test and ontology-related one (rdf:type)
        Assert.assertEquals(1, ontologyProperties.size());
        // verify value of added property
        Assert.assertEquals(propertyValue, ontologyProperties.get(propertyName)[0]);
    }

}
