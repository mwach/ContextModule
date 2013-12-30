package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.ontology.Constants.ContextModuleConstants;
import itti.com.pl.arena.cm.utils.helpers.IOHelper;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NumbersHelper;
import itti.com.pl.arena.cm.utils.helpers.SpringHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Required;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgeFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;

public class OntologyManager {

    private JenaOWLModel model = null;

    private String ontologyLocation = null;

    @Required
    public void setOntologyLocation(String ontologyLocation) {
	this.ontologyLocation = ontologyLocation;
    }

    private String getOntologyLocation() {
	return ontologyLocation;
    }

    private String ontologyNamespace = null;

    @Required
    public void setOntologyNamespace(String ontologyNamespace) {
	this.ontologyNamespace = ontologyNamespace;
    }

    protected String getOntologyNamespace() {
	return ontologyNamespace;
    }

    protected static final String var = "instance";

    public void init() throws OntologyException {
	try {
	    model = loadModel(getOntologyLocation());

	} catch (Exception e) {
	    LogHelper.exception(OntologyManager.class, "init",
		    "Could not initialize ontology", e);
	    throw new OntologyException(ErrorMessages.ONTOLOGY_CANNOT_LOAD,
		    String.valueOf(getOntologyLocation()),
		    e.getLocalizedMessage());
	}
    }

    protected JenaOWLModel getModel() {
	return model;
    }

    public List<String> getDirectInstances(ContextModuleConstants className) {
	return getDirectInstances(className.name());
    }

    public List<String> getDirectInstances(String className) {

	List<String> resultList = new ArrayList<String>();

	if (model == null || !StringHelper.hasContent(className)) {
	    return resultList;
	}

	String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ?%s rdf:type ns:%s }";
	String query = String.format(queryPattern, getOntologyNamespace(), var,
		var, className);
	return executeSparqlQuery(query, var);
    }

    public List<String> getInstances(String className) {

	if (!StringHelper.hasContent(className)) {
	    return new ArrayList<String>();
	}

	String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ?%s rdf:type ?subclass. ?subclass rdfs:subClassOf ns:%s }";
	String query = String.format(queryPattern, getOntologyNamespace(), var,
		var, className);

	return (executeSparqlQuery(query, var));
    }

    public String getInstanceClass(String instanceName) {

	if (!StringHelper.hasContent(instanceName)) {
	    return null;
	}

	String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ns:%s rdfs:subClassOf ?%s }";
	String query = String.format(queryPattern, getOntologyNamespace(), var,
		var, instanceName);
	List<String> results = executeSparqlQuery(query, var);
	return results.isEmpty() ? null : results.get(0);
    }

    public List<String> executeSparqlQuery(String query, String variable) {
	List<String> resultList = new ArrayList<String>();
	try {
	    QueryResults results = model.executeSPARQLQuery(query);
	    while (results.hasNext()) {
		@SuppressWarnings("unchecked")
		Map<String, SimpleInstance> rsRow = (Map<String, SimpleInstance>) results
			.next();
		resultList.add(rsRow.get(variable).getBrowserText());
	    }

	} catch (Exception e) {
	    resultList.clear();
	}
	return resultList;
    }

    /**
     * creates simple instance in owl ontology model
     * 
     * @param className
     *            name of the ontology class
     * @param instanceName
     *            name of the instance
     * @param properties
     *            optional list of instance properties
     * @return reference to the newly created instance
     */
    public OWLIndividual createSimpleInstance(String className,
	    String instanceName, Map<String, String[]> properties) {

	OWLIndividual individual = null;

	if (model.getOWLIndividual(instanceName) != null) {
	    individual = model.getOWLIndividual(instanceName);
	} else {
	    OWLNamedClass parentClass = model.getOWLNamedClass(className);
	    if (parentClass != null) {
		individual = parentClass.createOWLIndividual(instanceName);
	    }
	}

	if (individual != null) {
	    if (properties != null && !properties.isEmpty()) {
		for (Entry<String, String[]> entry : properties.entrySet()) {
		    RDFProperty property = model.getRDFProperty(entry.getKey());
		    if (property == null) {
			continue;
		    }
		    for (String value : entry.getValue()) {
			OWLIndividual valueInd = model.getOWLIndividual(value);
			if (valueInd != null) {
			    addMultiProperty(individual, property, valueInd);
			} else if (NumbersHelper.isInteger(value)) {
			    int valueInt = NumbersHelper
				    .getIntegerFromString(value);
			    addMultiProperty(individual, property, valueInt);
			} else {
			    addMultiProperty(individual, property, value);
			}
		    }
		}
	    }
	    return individual;
	}
	return null;
    }

    /**
     * returns list of instance properties from the owl ontology model
     * 
     * @param instanceName
     *            name of the instance
     * @return list of non-empty properties
     * @throws OntologyException
     */
    public Map<String, String[]> getInstanceProperties(String instanceName)
	    throws OntologyException {

	Map<String, String[]> properties = new HashMap<>();

	OWLIndividual individual = model.getOWLIndividual(instanceName);
	if (individual == null) {
	    LogHelper
		    .warning(OntologyManager.class, "getInstanceProperties",
			    "Instance '%s' was not found in the ontology",
			    instanceName);
	    return properties;
	}

	@SuppressWarnings("unchecked")
	Collection<RDFProperty> instanceProperties = individual
		.getRDFProperties();

	for (RDFProperty rdfProperty : instanceProperties) {
	    String propertyName = rdfProperty.getName();
	    @SuppressWarnings("rawtypes")
	    Collection propertyValues = individual
		    .getPropertyValues(rdfProperty);
	    String[] values = new String[propertyValues.size()];
	    int propertyNo = 0;
	    for (Object propertyValue : propertyValues) {
		if (propertyValue instanceof Instance) {
		    values[propertyNo++] = ((Instance) propertyValue).getName();
		} else {
		    values[propertyNo++] = String.valueOf(propertyValue);
		}
	    }
	    properties.put(propertyName, values);
	}
	return properties;
    }

    public boolean createOwlClass(String className) {

	if (model.getOWLIndividual(className) == null) {
	    OWLNamedClass individual = model.createOWLNamedClass(className);
	    return individual != null;
	}
	return false;
    }

    private void addMultiProperty(OWLIndividual subject, RDFProperty property,
	    Object value) {
	Object currentOntValue = subject.getPropertyValue(property);
	if (currentOntValue == null || !currentOntValue.equals(value)) {
	    subject.addPropertyValue(property, value);
	}
    }

    public void saveOntology(String fileName) {
	saveModel(model, fileName);
    }

    public String[] addSwrlRules(String[] swrlRules) {

	LogHelper.info(OntologyManager.class, "updateModelWithSwrlRule",
		"Upading models with rules");

	String[] ruleIds = null;

	if (swrlRules != null) {

	    ruleIds = new String[swrlRules.length];

	    SWRLFactory factory = new SWRLFactory(model);

	    try {

		int ruleNo = 0;
		for (String rule : swrlRules) {

		    SWRLImp ruleImpl = factory.createImp(rule);
		    LogHelper.debug(OntologyManager.class,
			    "updateModelWithSwrlRule",
			    "Rule '%s' added suffessfully", rule);
		    ruleIds[ruleNo++] = ruleImpl.getName();
		}
	    } catch (SWRLParseException e) {
		LogHelper.error(OntologyManager.class,
			"updateModelWithSwrlRule", "Failed to add rule: %s",
			e.getLocalizedMessage());
	    }

	} else {
	    LogHelper
		    .info(OntologyManager.class, "updateModelWithSwrlRule",
			    "Cannot update model with swrl rules. There are no rules provided");
	}
	return ruleIds;
    }

    // TODO: is it returns new, or update existing model?
    public void runSwrlEngine() {

	LogHelper.info(OntologyManager.class, "runSwrlEngine",
		"Swrl Rule bridge will be run now");

	try {
	    SWRLFactory factory = new SWRLFactory(model);
	    factory.createImp("Man(?x) ∧ Object_is_in_parking_zone(?x, ?y) ∧ Parking_zone_gives_properties(?y, ?z) →  Man_has_properties(?x, ?z)");
	    SWRLRuleEngineBridge bridge = BridgeFactory.createBridge(
		    "SWRLJessBridge", model);

	    LogHelper.debug(OntologyManager.class, "runSwrlEngine",
		    "Bridge infer");

	    bridge.infer();

	    LogHelper.debug(OntologyManager.class, "runSwrlEngine",
		    "Getting updated model from Swrl ridge");

	    model = (JenaOWLModel) bridge.getOWLModel();

	} catch (Exception e) {
	    LogHelper.error(OntologyManager.class, "runSwrlEngine",
		    "Error during using SWRL bridge: %s", e.toString());
	} catch (SWRLRuleEngineBridgeException e) {
	    LogHelper.error(OntologyManager.class, "runSwrlEngine",
		    "Error during using SWRL bridge: %s", e.toString());
	}
    }

	public static JenaOWLModel loadModel(String ontologyLocation) throws FileNotFoundException, Exception{

		LogHelper.debug(OntologyManager.class, "loadModel", "Init model: %s", ontologyLocation);

		InputStream ontologyInputStream = null;
		JenaOWLModel model = null;
		try{
			ontologyInputStream = SpringHelper.getResourceInputStream(ontologyLocation);
			model = ProtegeOWL.createJenaOWLModelFromInputStream(
					ontologyInputStream);
		}catch(RuntimeException exc){
			throw new IOHelperException(exc, ErrorMessages.ONTOLOGY_CANNOT_LOAD, ontologyLocation, exc.getLocalizedMessage());
		}finally{
			IOHelper.closeStream(ontologyInputStream);
		}
		
		LogHelper.info(OntologyManager.class, "loadModel", "Ontology successfully loaded");

		return model;
	}

	/**
	 * Saves an (updated/modified) ontology model in the Ontology Repository
	 * @param model JenaOWLObject ontology model
	 * @param fileName name of the file, in which ontology should be saved
	 * @throws OntologyAssemblyLayerException
	 */
	@SuppressWarnings("deprecation")
	public static void saveModel(JenaOWLModel model, String fileName){

		LogHelper.info(OntologyManager.class, "saveModel", "Saving ontology model");

		File outputOntologyFile = new File(fileName);
		OutputStream ontologyOutputStream = null;

		try{
			model.getNamespaceManager().setPrefix(SWRLNames.SWRL_NAMESPACE, SWRLNames.SWRL_PREFIX);
			model.getNamespaceManager().setPrefix(SWRLNames.SWRLB_NAMESPACE, SWRLNames.SWRLB_PREFIX);
			model.getNamespaceManager().setPrefix(SWRLNames.SWRLX_NAMESPACE, SWRLNames.SWRLX_PREFIX);

			List<String> errList = new ArrayList<String>();

			ontologyOutputStream = new FileOutputStream(outputOntologyFile, false);

			model.save(ontologyOutputStream, "RDF/XML-ABBREV", errList);
			LogHelper.info(OntologyManager.class, "saveModel", "Model successfully saved to file: %s", fileName);			

		}catch(Exception e){
			LogHelper.error(OntologyManager.class, "saveModel", "Could not save ontology: %s", e.getLocalizedMessage());
		}
		finally{
			if(ontologyOutputStream != null){
				try {
					ontologyOutputStream.close();
				} catch (IOException e) {
					LogHelper.warning(OntologyManager.class, "saveModel", "Could not save outputStream: %s", e.getLocalizedMessage());
				}
			}
		}
	}

}
