package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.ontology.Constants.ContextModuleConstants;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NumericHelper;
import itti.com.pl.arena.cm.utils.helpers.OntologyHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Required;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgeFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;


public class OntologyManager{

	private JenaOWLModel model = null;

	private String ontologyLocation = null;

	@Required
	public void setOntologyLocation(String ontologyLocation) {
		this.ontologyLocation = ontologyLocation;
	}
	private String getOntologyLocation(){
		return ontologyLocation;
	}

	private String ontologyNamespace = null;

	@Required
	public void setOntologyNamespace(String ontologyNamespace) {
		this.ontologyNamespace = ontologyNamespace;
	}
	protected String getOntologyNamespace(){
		return ontologyNamespace;
	}

	protected static final String var = "instance";

	public void init() throws OntologyException{
		try {
			model = OntologyHelper.loadModel(getOntologyLocation());

		} catch (Exception e) {
			LogHelper.exception(OntologyManager.class, "init", "Coulnd not initialize component", e);
			throw new OntologyException("Could not initialize ontology. Details: '%s'", e.getLocalizedMessage());
		}
	}

	protected JenaOWLModel getModel()
	{
		return model;
	}

	public List<String> getDirectInstances(ContextModuleConstants className){
		return getDirectInstances(className.name());
	}

	public List<String> getDirectInstances(String className){

        List<String> resultList = new ArrayList<String>();

		if(model == null || !StringHelper.hasContent(className)){
			return resultList;
		}

		String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ?%s rdf:type ns:%s }";
		String query = String.format(queryPattern, getOntologyNamespace(), var, var, 
				className);
        return executeSparqlQuery(query, var);
	}

	public List<String> getInstances(String className){

		if(!StringHelper.hasContent(className)){
			return new ArrayList<String>();
		}
		
		String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ?%s rdf:type ?subclass. ?subclass rdfs:subClassOf ns:%s }";
		String query = String.format(queryPattern, getOntologyNamespace(), var, 
				var, className);

		return(executeSparqlQuery(query, var));
	}

	public String getInstanceClass(String instanceName){

		if(!StringHelper.hasContent(instanceName)){
			return null;
		}
		
		String queryPattern = "PREFIX ns: <%s> SELECT ?%s WHERE { ns:%s rdfs:subClassOf ?%s }";
		String query = String.format(queryPattern, getOntologyNamespace(), var, 
				var, instanceName);
		List<String> results = executeSparqlQuery(query, var);
		return results.isEmpty() ? null : results.get(0);
	}

	public List<String> executeSparqlQuery(String query, String variable){
		List<String> resultList = new ArrayList<String>();
        try{
        	 QueryResults results = model.executeSPARQLQuery(query);
        	 while(results.hasNext()){
        		 @SuppressWarnings("unchecked")
				Map<String, SimpleInstance> rsRow = (Map<String, SimpleInstance>) results.next();
        		 resultList.add(rsRow.get(variable).getBrowserText());
        	 }

        } catch (Exception e) {
			resultList.clear();
		}
        return resultList;
	}

	/**
	 * creates simple instance in owl ontology model
	 * @param className name of the ontology class
	 * @param instanceName name of the instance
	 * @param properties optional list of instance properties
	 * @return reference to the newly created instance
	 */
	public OWLIndividual createSimpleInstance(String className, String instanceName, Map<String, String[]> properties){

		OWLIndividual individual = null;

		if(model.getOWLIndividual(instanceName) != null){
			individual = model.getOWLIndividual(instanceName);
		}else{
			OWLNamedClass parentClass = model.getOWLNamedClass(className);
			if(parentClass != null){
				individual = parentClass.createOWLIndividual(instanceName);
			}
		}

		if(individual != null){
			if(properties != null && !properties.isEmpty()){
				for (Entry<String, String[]> entry : properties.entrySet()) {
					RDFProperty property = model.getRDFProperty(entry.getKey());
					if(property == null){
						continue;
					}
					for (String value : entry.getValue()) {
						OWLIndividual valueInd = model.getOWLIndividual(value);
						if(valueInd != null){
							addMultiProperty(individual, property, valueInd);
						}else if(NumericHelper.isNumber(value)){
							int valueInt = NumericHelper.getIntValue(value);
							addMultiProperty(individual, property, valueInt);
						}else{
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
	 * @param instanceName name of the instance
	 * @return list of non-empty properties
	 * @throws OntologyException 
	 */
	public Map<String, String[]> getInstanceProperties(String instanceName) throws OntologyException{

		validateRequest("getInstanceProperties", model, instanceName);

		Map<String, String[]> properties = new HashMap<>();

		OWLIndividual individual = model.getOWLIndividual(instanceName);
		if(individual == null){
			LogHelper.warning(OntologyManager.class, "getInstanceProperties", "Instance '%s' was not found in the ontology", instanceName);
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
				if(propertyValue instanceof Instance){
					values[propertyNo++] = ((Instance)propertyValue).getName();
				}else{
					values[propertyNo++] = String.valueOf(propertyValue);
				}
			}
			properties.put(propertyName, values);
		}
		return properties;
	}

	private void validateRequest(String methodName, OWLModel model, Object... args) throws OntologyException {
		if(model == null){
			throw new OntologyException("%s: Empty ontlogy model provided", methodName);
		}
		int argNo = 1;
		for (Object arg : args) {
			if(arg == null)
			{
				throw new OntologyException("%s: Required parameter no '%d' was not provided", methodName, argNo);
			}
			argNo++;
		}
	}

	public boolean createOwlClass(String className){

		if(model.getOWLIndividual(className) == null){
			OWLNamedClass individual = model.createOWLNamedClass(className);
			return individual != null;
		}
		return false;
	}

	private void addMultiProperty(OWLIndividual subject, RDFProperty property, Object value) {
		Object currentOntValue = subject.getPropertyValue(property);
		if(currentOntValue == null || !currentOntValue.equals(value)){
			subject.addPropertyValue(property, value);
		}
	}

	public void saveOntology(String fileName) {
		OntologyHelper.saveModel(model, fileName);
	}

	public String[] addSwrlRules(String[] swrlRules){

		LogHelper.info(OntologyManager.class, "updateModelWithSwrlRule", "Upading models with rules");

		String[] ruleIds = null;

		if(swrlRules != null){

			ruleIds = new String[swrlRules.length];

			SWRLFactory factory = new SWRLFactory(model);

			try {

				int ruleNo = 0;
				for (String rule : swrlRules) {

					SWRLImp ruleImpl = factory.createImp(rule);
					LogHelper.debug(OntologyManager.class, "updateModelWithSwrlRule", "Rule '%s' added suffessfully", rule);
					ruleIds[ruleNo++] = ruleImpl.getName();
				}
			} catch (SWRLParseException e) {
				LogHelper.error(OntologyManager.class, "updateModelWithSwrlRule", "Failed to add rule: %s", e.getLocalizedMessage());
			}


		}else{
			LogHelper.info(OntologyManager.class, "updateModelWithSwrlRule", "Cannot update model with swrl rules. There are no rules provided");
		}
		return ruleIds;
	}

	//TODO: is it returns new, or update existing model?
	public void runSwrlEngine(){

		LogHelper.info(OntologyManager.class, "runSwrlEngine", "Swrl Rule bridge will be run now");

		try {
			SWRLFactory factory = new SWRLFactory(model);
			factory.createImp("Man(?x) ∧ Object_is_in_parking_zone(?x, ?y) ∧ Parking_zone_gives_properties(?y, ?z) →  Man_has_properties(?x, ?z)");
			SWRLRuleEngineBridge bridge = BridgeFactory.createBridge("SWRLJessBridge", model);

			LogHelper.debug(OntologyManager.class, "runSwrlEngine", "Bridge infer");

			bridge.infer();

			LogHelper.debug(OntologyManager.class, "runSwrlEngine", "Getting updated model from Swrl ridge");

			model = (JenaOWLModel)bridge.getOWLModel();

		} catch (Exception e) {
			LogHelper.error(OntologyManager.class, "runSwrlEngine", "Error during using SWRL bridge: %s",  e.toString());
		} catch (SWRLRuleEngineBridgeException e) {
			LogHelper.error(OntologyManager.class, "runSwrlEngine", "Error during using SWRL bridge: %s",  e.toString());
		}
	}
}
