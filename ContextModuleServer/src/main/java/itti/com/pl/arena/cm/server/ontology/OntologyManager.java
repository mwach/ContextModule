package itti.com.pl.arena.cm.server.ontology;

import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.ontology.OntologyConstants;
import itti.com.pl.arena.cm.server.service.Service;
import itti.com.pl.arena.cm.server.utils.helpers.SpringHelper;
import itti.com.pl.arena.cm.utils.helper.IOHelper;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

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

import edu.stanford.smi.protege.model.DefaultInstance;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
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
public class OntologyManager implements Service {

    private static final String QUERY_GET_DIRECT_INSTANCES = "PREFIX ns:<%s> SELECT ?%s WHERE { ?%s rdf:type ns:%s }";
    private static final String QUERY_GET_INSTANCES = "PREFIX ns:<%s> SELECT ?%s WHERE { ?%s rdf:type ?subclass. "
            + "?subclass rdfs:subClassOf ns:%s }";
    private static final String QUERY_GET_INSTANCE_CLASS = "PREFIX ns:<%s> SELECT ?%s WHERE { ns:%s rdf:type ?%s }";
    private static final String QUERY_GET_INSTANCE_GRAND_CLASS = "PREFIX ns:<%s> SELECT ?%s WHERE { ns:%s rdf:type "
            + "?directParent. ?directParent rdfs:subClassOf ?%s }";

    // in-memory ontology model
    private JenaOWLModel model = null;

    private List<String> ignoredProperties = new ArrayList<>();

    protected synchronized final JenaOWLModel getModel() {
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
            throw new BeanInitializationException(String.format(ErrorMessages.ONTOLOGY_CANNOT_LOAD.getMessage(),
                    String.valueOf(getOntologyLocation())), exc);
        }
        ignoredProperties.add("rdf:type");
    }

    /**
     * Closes ontology model
     */
    @Override
    public void shutdown() {
        if (getModel() != null) {
            getModel().close();
        }
    }

    /**
     * Returns list of direct (first level) instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public final List<String> getDirectInstances(OntologyConstants className) {
        return getInstances(className.name());
    }

    /**
     * Returns list of direct (first level) instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public final List<String> getInstances(String className) {

        LogHelper.debug(OntologyManager.class, "getInstances", "Query for instances of '%s'", className);

        if (!StringHelper.hasContent(className)) {
            return new ArrayList<>();
        }
        String query = String.format(QUERY_GET_DIRECT_INSTANCES, getOntologyNamespace(), VAR, VAR, className);
        return executeSparqlQuery(query, VAR);
    }

    /**
     * Returns list of non-direct (second and further levels) instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public List<String> getNonDirectInstances(String className) {

        LogHelper.debug(OntologyManager.class, "getNonDirectInstances", "Query for instances of '%s'", className);
        if (!StringHelper.hasContent(className)) {
            return new ArrayList<>();
        }
        String query = String.format(QUERY_GET_INSTANCES, getOntologyNamespace(), VAR, VAR, className);

        return (executeSparqlQuery(query, VAR));
    }

    /**
     * Returns parent class of given instance
     * 
     * @param instanceName
     *            name of the instance
     * @return parent class name.
     * @exception OntologyExceptiont
     *                could not find object
     */
    public String getInstanceClass(String instanceName) throws OntologyException {

        LogHelper
                .debug(OntologyManager.class, "getInstanceClass", "Query for parent class of '%s'", String.valueOf(instanceName));

        if (!StringHelper.hasContent(instanceName)) {
            LogHelper.error(OntologyManager.class, "getInstanceClass", "Null instance name provided");
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_INSTANCE_NAME);
        }
        String query = String.format(QUERY_GET_INSTANCE_CLASS, getOntologyNamespace(), VAR, instanceName, VAR);
        List<String> results = executeSparqlQuery(query, VAR);
        if (results.isEmpty()) {
            LogHelper.warning(OntologyManager.class, "getInstanceClass", "No results were found for instance '%s'", instanceName);
            throw new OntologyException(ErrorMessages.ONTOLOGY_INSTANCE_NOT_FOUND, instanceName);
        }
        return results.get(0);
    }

    /**
     * Returns parent of a parent class of given instance
     * 
     * @param instanceName
     *            name of the instance
     * @return parent of a parent class name.
     * @exception OntologyExceptiont
     *                could not find object
     */
    public String getInstanceGrandClass(String instanceName) throws OntologyException {

        LogHelper.debug(OntologyManager.class, "getInstanceGrandClass", "Query for parent class of a parent of '%s'",
                String.valueOf(instanceName));

        if (!StringHelper.hasContent(instanceName)) {
            LogHelper.warning(OntologyManager.class, "getInstanceGrandClass", "Null instance name provided");
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_INSTANCE_NAME);
        }
        String query = String.format(QUERY_GET_INSTANCE_GRAND_CLASS, getOntologyNamespace(), VAR, instanceName, VAR);
        List<String> results = executeSparqlQuery(query, VAR);
        if (results.isEmpty()) {
            LogHelper.debug(OntologyManager.class, "getInstanceGrandClass", "No results were found for instance '%s'",
                    instanceName);
            return null;
        }
        return results.get(0);
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
            QueryResults results = getModel().executeSPARQLQuery(query);
            while (results.hasNext()) {
                Object value = results.next().get(variable);
                String result = null;
                if (value instanceof DefaultInstance) {
                    result = ((DefaultInstance) value).getBrowserText();
                } else if (value instanceof DefaultRDFSLiteral) {
                    result = ((DefaultRDFSLiteral) value).getBrowserText();
                } else {
                    LogHelper.warning(OntologyManager.class, "executeSparqlQuery", "Unrecognized query results class: '%s'",
                            value);
                }
                resultList.add(result);

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
    public OWLIndividual createSimpleInstance(String className, String instanceName, Map<String, String[]> properties)
            throws OntologyException {

        LogHelper.debug(OntologyManager.class, "createSimpleInstance", "Creating instance '%s' for class '%s'", instanceName,
                className);

        // create individual, or get the existing one
        OWLIndividual individual = null;
        if (getInstances(className).contains(instanceName)) {
            individual = getInstance(instanceName);
        } else {
            individual = createInstanceOnly(className, instanceName);
        }

        // individual created, now add all the properties
        if (properties != null) {
            for (Entry<String, String[]> entry : properties.entrySet()) {
                updateProperty(individual, entry.getKey(), entry.getValue());
            }
        }
        return individual;
    }

    /**
     * Creates a new class in the ontology model
     * 
     * @param className
     *            name of the class
     * @return true, if class was successfully created, or already existed in the ontology, false otherwise
     * @throws OntologyException
     */
    public OWLIndividual getInstance(String instanceName) throws OntologyException {

        LogHelper.debug(OntologyManager.class, "getInstance", "Searching for instance '%s'", instanceName);
        return getModel().getOWLIndividual(instanceName);
    }

    private OWLIndividual createInstanceOnly(String className, String instanceName) throws OntologyException {

        OWLNamedClass parentClass = getModel().getOWLNamedClass(className);
        OWLIndividual individual = null;
        if (parentClass == null) {
            LogHelper
                    .warning(OntologyManager.class, "createInstanceOnly", "Base class '%s' not found in the ontology", className);
            throw new OntologyException(ErrorMessages.ONTOLOGY_CLASS_DOESNT_EXIST, className);
        }
        try {
            individual = parentClass.createOWLIndividual(instanceName);
        } catch (RuntimeException exc) {
            LogHelper.exception(OntologyManager.class, "createInstanceOnly",
                    String.format("Cannot create instance '%s' of class '%s'. Probably a duplicate", instanceName, className),
                    exc);
            throw new OntologyException(ErrorMessages.ONTOLOGY_COULD_NOT_ADD_INSTANCE, instanceName, className);
        }
        return individual;
    }

    private void updateProperty(OWLIndividual individual, String propertyName, String[] values) {

        LogHelper.debug(OntologyManager.class, "createPropertyValue", "Creting property '%s' for instance", propertyName,
                individual.getName());

        // get the property
        RDFProperty property = getModel().getRDFProperty(propertyName);
        if (property == null) {
            LogHelper.warning(OntologyManager.class, "createSimpleInstance", "Property '%s' not found for type %s", propertyName,
                    individual.getBrowserText());
        } else {

            removePropertyValues(individual, property);

            // now, set property value
            for (String value : values) {
                // find value as an instance
                OWLIndividual valueInd = getModel().getOWLIndividual(value);
                if (valueInd != null) {
                    setPropertyValue(individual, property, valueInd);
                    // not an instance - try numbers first
                    // try to add value as a string
                } else if (NumbersHelper.isInteger(value)) {
                    Integer valueInt = NumbersHelper.getIntegerFromString(value);
                    setPropertyValue(individual, property, valueInt);
                    // try to add value as a string
                } else if (NumbersHelper.isDouble(value)) {
                    Float valueFloat = NumbersHelper.getDoubleFromString(value).floatValue();
                    setPropertyValue(individual, property, valueFloat);
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
            throw new OntologyException(ErrorMessages.ONTOLOGY_INSTANCE_NOT_FOUND, instanceName);
        }

        @SuppressWarnings("unchecked")
        Collection<RDFProperty> instanceProperties = individual.getRDFProperties();

        for (RDFProperty rdfProperty : instanceProperties) {
            String propertyName = rdfProperty.getName();

            //check, if property is ignored
            if(ignoredProperties.contains(propertyName)){
                continue;
            }
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
     * Checks, if instance with given name exists in the ontology
     * @param instanceName name of the instance
     * @return true if instance with given name was found in the ontology, false otherwise
     */
    public boolean hasInstance(String instanceName){
        //check, if name was provided
        if(StringHelper.hasContent(instanceName)){
            //check if instance exist in the model
            return getModel().getOWLIndividual(instanceName) != null;
        }
        return false;
    }

    /**
     * Removes instance identified by its name from ontology
     * @param instanceName name of the instance to remove
     * @throws OntologyException 
     */
    public void remove(String instanceName) throws OntologyException{
        if(StringHelper.hasContent(instanceName)){
            Instance instance = getInstance(instanceName);
            if(instance == null){
                throw new OntologyException(ErrorMessages.ONTOLOGY_INSTANCE_NOT_FOUND, instanceName);
            }
            getModel().deleteInstance(instance);
        }else{
            throw new OntologyException(ErrorMessages.ONTOLOGY_EMPTY_INSTANCE_NAME);
        }
    }


    /**
     * returns values for single property of an individual stored in the ontology model
     * 
     * @param instanceName
     *            name of the instance
     * @param propertyName
     *            name of the property
     * @return list of property values
     * @throws OntologyException
     */
    public String[] getInstanceProperties(String instanceName, String propertyName) throws OntologyException {
        return getInstanceProperties(instanceName).get(propertyName);
    }

    /**
     * Creates a new class in the ontology model
     * 
     * @param className
     *            name of the class
     * @return true, if class was successfully created, or already existed in the ontology, false otherwise
     */
    public boolean createOwlClass(String className) {

        LogHelper.debug(OntologyManager.class, "createOwlClass", "Creating class '%s'", className);

        // check, if class existed in the ontology
        if (getModel().getOWLIndividual(className) == null) {
            // if not, try to create it
            OWLNamedClass individual = getModel().createOWLNamedClass(className);
            return individual != null;
        }
        return true;
    }

    /**
     * Adds a value of the property to the instance
     * 
     * @param instance
     *            OWL instance
     * @param property
     *            property of the instance
     * @param value
     *            value to be set
     */
    public void setPropertyValue(OWLIndividual instance, RDFProperty property, Object value) {
        // set new value of the property
        instance.addPropertyValue(property, value);
    }

    /**
     * Updates value of the property for given instance. Before update, removes all existing values of that instance
     * 
     * @param instanceName
     *            name of the instance
     * @param propertyName
     *            name of the property
     * @param propertyValue
     *            new value for the property
     * @throws OntologyException
     *             could not update property value
     */
    public void updatePropertyValue(String instanceName, String propertyName, Object propertyValue) throws OntologyException {
        OWLIndividual instance = getInstance(instanceName);
        RDFProperty property = getModel().getRDFProperty(propertyName);
        removePropertyValues(instance, property);
        setPropertyValue(instance, property, propertyValue);
    }

    /**
     * Removes all values of the property for given instance
     * 
     * @param individual
     *            instance
     * @param property
     *            property
     */
    public void removePropertyValues(OWLIndividual individual, RDFProperty property) {
        // remove current value of the property
        int propsCount = individual.getPropertyValueCount(property);
        for (int i = 0; i < propsCount; i++) {
            Object currentOntValue = individual.getPropertyValue(property);
            individual.removePropertyValue(property, currentOntValue);
        }

    }

    /**
     * Saves current ontology model to file
     * 
     * @param fileName
     *            location of the file, where ontology should be saved
     */
    public void saveOntology(String fileName) {
        saveModel(getModel(), fileName);
    }

    public void addSwrlRules(String[] swrlRules) {

        LogHelper.info(OntologyManager.class, "addSwrlRules", "Upading models with SWRL rules");

        if (swrlRules != null) {

            // create rule factory
            SWRLFactory factory = new SWRLFactory(model);

            try {

                for (String rule : swrlRules) {
                    // add rule to the model
                    factory.createImp(rule);
                    LogHelper.debug(OntologyManager.class, "addSwrlRules", "Rule '%s' added suffessfully", rule);
                }
            } catch (SWRLParseException e) {
                LogHelper.error(OntologyManager.class, "addSwrlRules", "Failed to add rule: %s", e.getLocalizedMessage());
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
            // SWRLFactory factory = new SWRLFactory(getModel());
            // factory.createImp("Man(?x) ∧ Object_is_in_parking_zone(?x, ?y) ∧ Parking_zone_gives_properties(?y, ?z) →  Man_has_properties(?x, ?z)");
            SWRLRuleEngineBridge bridge = BridgeFactory.createBridge("SWRLJessBridge", getModel());

            LogHelper.debug(OntologyManager.class, "runSwrlEngine", "Bridge infer");

            bridge.infer();

            LogHelper.debug(OntologyManager.class, "runSwrlEngine", "Getting updated model from Swrl ridge");

            model = (JenaOWLModel) bridge.getOWLModel();

        } catch (Exception exc) {
            LogHelper.exception(OntologyManager.class, "runSwrlEngine",
                    String.format("Error during running SWRL engine: %s", exc.toString()), exc);
        } catch (SWRLRuleEngineBridgeException exc) {
            LogHelper.exception(OntologyManager.class, "runSwrlEngine",
                    String.format("Error during using SWRL bridge: %s", exc.toString()), exc);
        }
    }

    /**
     * Load ontology model from file
     * 
     * @param ontologyLocation
     *            location of the ontology file
     * @return loaded model
     * @throws OntologyException
     *             could not load ontology
     */
    public static JenaOWLModel loadModel(String ontologyLocation) throws OntologyException {

        LogHelper.debug(OntologyManager.class, "loadModel", "Init model: %s", ontologyLocation);

        InputStream ontologyInputStream = null;
        JenaOWLModel model = null;
        try {
            ontologyInputStream = SpringHelper.getResourceInputStream(ontologyLocation);
            model = ProtegeOWL.createJenaOWLModelFromInputStream(ontologyInputStream);
        } catch (Exception exc) {
            LogHelper.error(OntologyManager.class, "loadModel", "Cannot load ontlogy using given location. Details: %s", exc.getLocalizedMessage());
            throw new OntologyException(ErrorMessages.ONTOLOGY_CANNOT_LOAD, exc, ontologyLocation, exc.getLocalizedMessage());
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
