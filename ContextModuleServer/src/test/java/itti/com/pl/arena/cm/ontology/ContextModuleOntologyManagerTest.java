package itti.com.pl.arena.cm.ontology;

import org.junit.Assert;
import org.junit.Test;

import itti.com.pl.arena.cm.dto.PlatformInformation;

public class ContextModuleOntologyManagerTest {

	@Test
	public void testGetInstances() throws OntologyException{
		ContextModuleOntologyManager mgr = new ContextModuleOntologyManager();
		mgr.setOntologyLocation("Arena_update_v06.owl");
		mgr.setOntologyNamespace("http://www.owl-ontologies.com/Ontology1350654591.owl#");
		mgr.init();
		PlatformInformation information = mgr.getPlatformInformation("Truck_A1");
		Assert.assertNotNull(information);
	}
}
