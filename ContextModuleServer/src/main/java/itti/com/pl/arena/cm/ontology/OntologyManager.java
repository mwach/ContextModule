package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.Service;
import itti.com.pl.arena.cm.ontology.Constants.ContextModuleConstants;
import itti.com.pl.arena.cm.utils.helpers.IOHelper;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NumbersHelper;
import itti.com.pl.arena.cm.utils.helpers.SpringHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.BeanInitializationException;
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
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;

/**
 * Base ontology-management class
 * 
 * @author cm-admin
 * 
 */
public class OntologyManager implements Service{

    // in-memory ontology model
    private JenaOWLModel model = null;

    protected final JenaOWLModel getModel() {
	return model;
    }

    // location of the ontology on disc
    private String ontologyLocation = null;

    /**
     * Sets ontology location (must be done before calling 'init' method)
     * 
     * @param ontologyLocation
     *            location of the ontology
     */
    @Required
    public final void setOntologyLocation(String ontologyLocation) {
	this.ontologyLocation = ontologyLocation;
    }

    private final String getOntologyLocation() {
	return ontologyLocation;
    }

    // namespace of the loaded ontology
    private String ontologyNamespace = null;

    /**
     * Sets namespace of the ontology (must be done before calling 'init' method)
     * 
     * @param ontologyNamespace
     *            namespace used by the ontology
     */
    @Required
    public final void setOntologyNamespace(String ontologyNamespace) {
	this.ontologyNamespace = ontologyNamespace;
    }

    protected final String getOntologyNamespace() {
	return ontologyNamespace;
    }

    // variable used for querying ontology
    protected final String VAR = "instance";

    /**
     * Initializes ontology by loading it from file into memory
     */
    @Override
    public void init() {

	LogHelper.debug(OntologyManager.class, "init", "Initialization of the ontology '%s'", getOntologyLocation());
	try {
	    model = loadModel(getOntologyLocation());
	} catch (Exception exc) {
	    LogHelper.exception(OntologyManager.class, "init", "Could not initialize ontology", exc);
	    throw new BeanInitializationException(String.format(ErrorMessages.ONTOLOGY_CANNOT_LOAD.getMessage(), String.valueOf(getOntologyLocation())), exc);
	}
    }

    /**
     * Closes ontology model
     */
    @Override
    public void shutdown(){
	if(model != null){
	    model.close();
	}
    }

    /**
     * Returns list of direct (first level) instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public final List<String> getDirectInstances(ContextModuleConstants className) {
	return getDirectInstances(className.name());
    }

    /**
     * Returns list of direct (first level) instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public final List<String> getDirectInstances(String className) {

	LogHelper.debug(OntologyManager.class, "getDirectInstances", "Query for instances of '%s'", className);

	if (!StringHelper.hasContent(className)) {
	    return new ArrayList<>();
	}

	String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ?%s rdf:type ns:%s }";
	String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, className);
	return executeSparqlQuery(query, VAR);
    }

    /**
     * Returns list of non-direct (second and further levels) instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public List<String> getInstances(String className) {

	LogHelper.debug(OntologyManager.class, "getInstances", "Query for instances of '%s'", className);
	if (!StringHelper.hasContent(className)) {
	    return new ArrayList<>();
	}

	String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ?%s rdf:type ?subclass. ?subclass rdfs:subClassOf ns:%s }";
	String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, className);

	return (executeSparqlQuery(query, VAR));
    }

    /**
     * Returns parent class of given instance
     * 
     * @param instanceName
     *            name of the instance
     * @return parent class name. If instance was not found in the ontology, null will be returned
     */
    public String getInstanceClass(String instanceName) {

	LogHelper.debug(OntologyManager.class, "getInstanceClass", "Query for parent class of '%s'", instanceName);

	if (!StringHelper.hasContent(instanceName)) {
	    return null;
	}

	String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ns:%s rdfs:subClassOf ?%s }";
	String query = String.format(queryPattern, getOntologyNamespace(), VAR, VAR, instanceName);
	List<String> results = executeSparqlQuery(query, VAR);
	return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Executes SPARQL query on ontology model
     * 
     * @param query
     *            query to execute
     * @param variable
     *            variable to be returned
     * @return list of string returned by the query
     */
    public List<String> executeSparqlQuery(String query, String variable) {

	LogHelper.debug(OntologyManager.class, "executeSparqlQuery", "Executing '%s' query using '%s' var", query, variable);

	List<String> resultList = new ArrayList<String>();
	try {
	    QueryResults results = model.executeSPARQLQuery(query);
	    while (results.hasNext()) {
		@SuppressWarnings("unchecked")
		Map<String, SimpleInstance> rsRow = (Map<String, SimpleInstance>) results.next();
		resultList.add(rsRow.get(variable).getBrowserText());
	    }

	} catch (Exception exc) {
	    LogHelper.exception(OntologyManager.class, "executeSparqlQuery",
		    String.format("Failed to execute query '%s'", query), exc);
	    resultList.clear();
	}
	return resultList;
    }

    /**
     * creates simple instance in the ontology model
     * 
     * @param className
     *            name of the ontology class
     * @param instanceName
     *            name of the instance
     * @param properties
     *            optional list of instance properties
     * @return reference to the newly created instance
     */
    public OWLIndividual createSimpleInstance(String className, String instanceName, Map<String, String[]> properties) {

	LogHelper.debug(OntologyManager.class, "createSimpleInstance", "Creating instance '%s' for class '%s'", instanceName,
	        className);
	OWLIndividual individual = null;

	// find parent class, then create individual
	OWLNamedClass parentClass = model.getOWLNamedClass(className);
	if (parentClass != null) {
	    individual = parentClass.createOWLIndividual(instanceName);
	} else {
	    LogHelper.warning(OntologyManager.class, "createSimpleInstance", "Base class '%s' not found in the ontology",
		    className);
	}

	if (individual != null) {
	    // individual create, now add all the properties
	    if (properties != null) {
		for (Entry<String, String[]> entry : properties.entrySet()) {
		    createPropertyValue(individual, className, entry.getKey(), entry.getValue());
		}
	    }
	    return individual;
	}
	return null;
    }

    private void createPropertyValue(OWLIndividual individual, String parentClass, String propertyName, String[] values) {

	LogHelper.debug(OntologyManager.class, "createPropertyValue", "Creting property '%s' for instance", propertyName,
	        individual.getName());

	// get the property
	RDFProperty property = model.getRDFProperty(propertyName);
	if (property == null) {
	    LogHelper.warning(OntologyManager.class, "createSimpleInstance", "Property '%s' not found for class %s",
		    propertyName, parentClass);
	} else {
	    // now, set property value
	    for (String value : values) {
		// find value as an instance
		OWLIndividual valueInd = model.getOWLIndividual(value);
		if (valueInd != null) {
		    setPropertyValue(individual, property, valueInd);
		    // not an instance - try number
		} else if (NumbersHelper.isInteger(value)) {
		    int valueInt = NumbersHelper.getIntegerFromString(value);
		    setPropertyValue(individual, property, valueInt);
		    // try to add value as a string
		} else {
		    setPropertyValue(individual, property, value);
		}
	    }
	}
    }

    /**
     * returns list of instance properties from the ontology model
     * 
     * @param instanceName
     *            name of the instance
     * @return list of non-empty properties
     * @throws OntologyException
     */
    public Map<String, String[]> getInstanceProperties(String instanceName) throws OntologyException {

	LogHelper.debug(OntologyManager.class, "getInstanceProperties", "Collecting properties for instance '%s'", instanceName);

	Map<String, String[]> properties = new HashMap<>();

	OWLIndividual individual = getModel().getOWLIndividual(instanceName);
	if (individual == null) {
	    LogHelper.warning(OntologyManager.class, "getInstanceProperties", "Instance '%s' was not found in the ontology",
		    instanceName);
	    return properties;
	}

	@SuppressWarnings("unchecked")
	Collection<RDFProperty> instanceProperties = individual.getRDFProperties();

	for (RDFProperty rdfProperty : instanceProperties) {
	    String propertyName = rdfProperty.getName();
	    @SuppressWarnings("rawtypes")
	    Collection propertyValues = individual.getPropertyValues(rdfProperty);
	    String[] values = new String[propertyValues.size()];
	    int propertyNo = 0;
	    for (Object propertyValue : propertyValues) {
		if (propertyValue instanceof Instance) {
		    values[propertyNo++] = ((Instance) propertyValue).getName();
		} else {
		    values[propertyNo++] = String.valueOf(propertyValue);
		}
	    }
	    LogHelper.debug(OntologyManager.class, "getInstanceProperties", "Collected %d values for property '%s'",
		    values.length, propertyName);
	    properties.put(propertyName, values);
	}
	return properties;
    }

    /**
     * Creates a new class in the ontology model
     * @param className name of the class
     * @return true, if class was successfully created, or already existed in the ontology, false otherwise
     */
    public boolean createOwlClass(String className) {

	LogHelper.debug(OntologyManager.class, "createOwlClass", "Creating class '%s'", className);

	//check, if class existed in the ontology
	if (model.getOWLIndividual(className) == null) {
	    //if not, try to create it
	    OWLNamedClass individual = model.createOWLNamedClass(className);
	    return individual != null;
	}
	return true;
    }

    /**
     * Adds a value of the property to the instance
     * @param instance OWL instance
     * @param property property of the instance
     * @param value value to be set
     */
    public void setPropertyValue(OWLIndividual instance, RDFProperty property, Object value) {

	//get the current value of the property
	Object currentOntValue = instance.getPropertyValue(property);

	//if new value is different that the old one, replace
	if (currentOntValue == null || !currentOntValue.equals(value)) {
	    instance.addPropertyValue(property, value);
	}
    }

    /**
     * Saves current ontology model to file
     * @param fileName location of the file, where ontology should be saved
     */
    public void saveOntology(String fileName) {
	saveModel(getModel(), fileName);
    }

    public void addSwrlRules(String[] swrlRules) {

	LogHelper.info(OntologyManager.class, "addSwrlRules", "Upading models with SWRL rules");

	if (swrlRules != null) {

	    //create rule factory
	    SWRLFactory factory = new SWRLFactory(model);

	    try {

		for (String rule : swrlRules) {
		    //add rule to the model
		    factory.createImp(rule);
		    LogHelper.debug(OntologyManager.class, "addSwrlRules", "Rule '%s' added suffessfully", rule);
		}
	    } catch (SWRLParseException e) {
		LogHelper.error(OntologyManager.class, "addSwrlRules", "Failed to add rule: %s",
		        e.getLocalizedMessage());
	    }

	} else {
	    LogHelper.warning(OntologyManager.class, "addSwrlRules",
		    "Cannot update model with swrl rules. There are no rules provided");
	}
    }


    /**
     * Runs SWRL engine on existing model
     */
    public void runSwrlEngine() {

	LogHelper.info(OntologyManager.class, "runSwrlEngine", "Swrl Rule bridge will be run now");

	try {
//	    SWRLFactory factory = new SWRLFactory(getModel());
//	    factory.createImp("Man(?x) ∧ Object_is_in_parking_zone(?x, ?y) ∧ Parking_zone_gives_properties(?y, ?z) →  Man_has_properties(?x, ?z)");
	    SWRLRuleEngineBridge bridge = BridgeFactory.createBridge("SWRLJessBridge", getModel());

	    LogHelper.debug(OntologyManager.class, "runSwrlEngine", "Bridge infer");

	    bridge.infer();

	    LogHelper.debug(OntologyManager.class, "runSwrlEngine", "Getting updated model from Swrl ridge");

	    model = (JenaOWLModel) bridge.getOWLModel();

	} catch (Exception exc) {
	    LogHelper.exception(OntologyManager.class, "runSwrlEngine", String.format("Error during running SWRL engine: %s", exc.toString()), exc);
	} catch (SWRLRuleEngineBridgeException exc) {
	    LogHelper.exception(OntologyManager.class, "runSwrlEngine", String.format("Error during using SWRL bridge: %s", exc.toString()), exc);
	}
    }

    /**
     * Load ontology model from file
     * @param ontologyLocation location of the ontology file
     * @return loaded model
     * @throws OntologyException could not load ontology
     */
    public static JenaOWLModel loadModel(String ontologyLocation) throws OntologyException {

	LogHelper.debug(OntologyManager.class, "loadModel", "Init model: %s", ontologyLocation);

	InputStream ontologyInputStream = null;
	JenaOWLModel model = null;
	try {
	    ontologyInputStream = SpringHelper.getResourceInputStream(ontologyLocation);
	    model = ProtegeOWL.createJenaOWLModelFromInputStream(ontologyInputStream);
	} catch (Exception exc) {
	    LogHelper.exception(OntologyManager.class, "loadModel", "Cannot load ontlogy using given location", exc);
	    throw new OntologyException(exc, ErrorMessages.ONTOLOGY_CANNOT_LOAD, ontologyLocation, exc.getLocalizedMessage());
	} finally {
	    IOHelper.closeStream(ontologyInputStream);
	}

	LogHelper.info(OntologyManager.class, "loadModel", "Ontology successfully loaded");
	return model;
    }

    /**
     * Saves an (updated/modified) ontology model in the Ontology Repository
     * 
     * @param model
     *            JenaOWLObject ontology model
     * @param fileName
     *            name of the file, in which ontology should be saved
     * @throws OntologyAssemblyLayerException
     */
    @SuppressWarnings("deprecation")
    public static void saveModel(JenaOWLModel model, String fileName) {

	LogHelper.info(OntologyManager.class, "saveModel", "Saving ontology model");

	File outputOntologyFile = new File(fileName);
	OutputStream ontologyOutputStream = null;

	try {
	    model.getNamespaceManager().setPrefix(SWRLNames.SWRL_NAMESPACE, SWRLNames.SWRL_PREFIX);
	    model.getNamespaceManager().setPrefix(SWRLNames.SWRLB_NAMESPACE, SWRLNames.SWRLB_PREFIX);
	    model.getNamespaceManager().setPrefix(SWRLNames.SWRLX_NAMESPACE, SWRLNames.SWRLX_PREFIX);

	    List<String> errList = new ArrayList<String>();

	    LogHelper.debug(OntologyManager.class, "saveModel", "Preparing file '%s' to write", fileName);
	    ontologyOutputStream = new FileOutputStream(outputOntologyFile, false);

	    model.save(ontologyOutputStream, "RDF/XML-ABBREV", errList);
	    LogHelper.info(OntologyManager.class, "saveModel", "Model successfully saved to file: %s", fileName);

	} catch (Exception exc) {
	    LogHelper.exception(OntologyManager.class, "saveModel", "Could not save ontology", exc);
	} finally {
	    IOHelper.closeStream(ontologyOutputStream);
	}
    }

}
