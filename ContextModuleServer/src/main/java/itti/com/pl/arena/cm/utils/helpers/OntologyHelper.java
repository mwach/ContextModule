package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.utils.helpers.IOHelper;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.SpringHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

public class OntologyHelper {

	public static JenaOWLModel loadModel(String ontologyLocation) throws FileNotFoundException, Exception{

		LogHelper.debug(OntologyHelper.class, "loadModel", "Init model: %s", ontologyLocation);

		InputStream ontologyInputStream = null;
		JenaOWLModel model = null;
		try{
			ontologyInputStream = SpringHelper.getResourceInputStream(ontologyLocation);
			model = ProtegeOWL.createJenaOWLModelFromInputStream(
					ontologyInputStream);
		}catch(RuntimeException exc){
			throw new IOHelperException(exc, "Failed to load ontlogy '%s' Details: '%s'", ontologyLocation, exc.getLocalizedMessage());
		}finally{
			IOHelper.closeStream(ontologyInputStream);
		}
		
		LogHelper.info(OntologyHelper.class, "loadModel", "Ontology successfully loaded");

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

		LogHelper.info(OntologyHelper.class, "saveModel", "Saving ontology model");

		File outputOntologyFile = new File(fileName);
		OutputStream ontologyOutputStream = null;

		try{
			model.getNamespaceManager().setPrefix(SWRLNames.SWRL_NAMESPACE, SWRLNames.SWRL_PREFIX);
			model.getNamespaceManager().setPrefix(SWRLNames.SWRLB_NAMESPACE, SWRLNames.SWRLB_PREFIX);
			model.getNamespaceManager().setPrefix(SWRLNames.SWRLX_NAMESPACE, SWRLNames.SWRLX_PREFIX);

			List<String> errList = new ArrayList<String>();

			ontologyOutputStream = new FileOutputStream(outputOntologyFile, false);

			model.save(ontologyOutputStream, "RDF/XML-ABBREV", errList);
			LogHelper.info(OntologyHelper.class, "saveModel", "Model successfully saved to file: %s", fileName);			

		}catch(Exception e){
			LogHelper.error(OntologyHelper.class, "saveModel", "Could not save ontology: %s", e.getLocalizedMessage());
		}
		finally{
			if(ontologyOutputStream != null){
				try {
					ontologyOutputStream.close();
				} catch (IOException e) {
					LogHelper.warning(OntologyHelper.class, "saveModel", "Could not save outputStream: %s", e.getLocalizedMessage());
				}
			}
		}
	}
}
