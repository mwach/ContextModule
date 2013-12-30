package itti.com.pl.arena.cm.ontology;

import org.junit.Assert;
import org.junit.Test;

import itti.com.pl.arena.cm.dto.PlatformInformation;

public class ContextModuleOntologyManagerTest {

    private static final String ontologyLocation = "TestOntology.owl";
    private static final String ontologyNamespace = "http://www.owl-ontologies.com/Ontology1350654591.owl#";

    @Test
    public void testGetInstances() throws OntologyException {
	ContextModuleOntologyManager mgr = new ContextModuleOntologyManager();
	mgr.setOntologyLocation(ontologyLocation);
	mgr.setOntologyNamespace(ontologyNamespace);
	mgr.init();
	PlatformInformation information = mgr.getPlatformInformation("Truck_A1");
	Assert.assertNotNull(information);
    }
}
