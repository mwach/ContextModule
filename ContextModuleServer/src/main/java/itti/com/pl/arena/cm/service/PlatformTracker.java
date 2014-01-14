package itti.com.pl.arena.cm.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.Service;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalService;
import itti.com.pl.arena.cm.location.LocationListener;
import itti.com.pl.arena.cm.ontology.Ontology;
import itti.com.pl.arena.cm.ontology.OntologyException;
import itti.com.pl.arena.cm.persistence.Persistence;
import itti.com.pl.arena.cm.persistence.PersistenceException;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

/**
 * Default implementation of the ContextModule tracker
 * 
 * @author cm-admin
 * 
 */
public class PlatformTracker implements Service, LocationListener {

    //persistence module used to store information about platform
    private Persistence persistence = null;
    //ontology module used to store/retrieve platform information
    private Ontology ontology = null;
    // geoportal module used to collect geoportal information
    private GeoportalService geoportal = null;

    /*
     * ID of the listener (if not provided, random UUID generated during initialization)
     */
    private String platformId;
    public void setPlatformId(String id){
	this.platformId = id;
    }
    private String getPlatformId(){
	return platformId;
    }

    private Persistence getPersistence() {
	return persistence;
    }

    @Required
    public void setPersistence(Persistence persistence) {
	this.persistence = persistence;
    }

    private Ontology getOntology() {
	return ontology;
    }

    @Required
    public void setOntology(Ontology ontology) {
	this.ontology = ontology;
    }

    private GeoportalService getGeoportal() {
	return geoportal;
    }

    @Required
    public void setGeoportal(GeoportalService geoportal) {
	this.geoportal = geoportal;
    }

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.Service#init()
     */
    @Override
    public void init() {
	if(StringHelper.hasContent(getPlatformId())){
	    setPlatformId(UUID.randomUUID().toString());
	}
    }

    /* (non-Javadoc)
     * @see itti.com.pl.arena.cm.Service#shutdown()
     */
    @Override
    public void shutdown() {
	// not used by this service
    }

    @Override
    public void onLocationChange(Location location) {

	LogHelper.debug(PlatformTracker.class, "onLocationChange", "New platform location received: %s", location);

	// first, try to persist latest location in the database
	try {
	    getPersistence().create(getId(), location);
	} catch (PersistenceException e) {
	    LogHelper.warning(PlatformTracker.class, "onLocationChange", "Could not persist location data: %s", e.getLocalizedMessage());
	}

	// if object is no moving, get info from the ontology
	if (location.getSpeed() == 0) {
	    try {
		double radius = 0.0;
		Set<GeoObject> ontoData = getOntology().getGISObjects(location, radius);
		if(ontoData.isEmpty()){
		    Set<GeoObject> geoportalData = getGeoportal().getGeoportalData(location, radius);
		    getOntology().addGeoportalData(location, geoportalData);
		}
            } catch (OntologyException | GeoportalException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
            }
	    //
	}
	try {
	    getOntology().getPlatform(getId());
	} catch (OntologyException e) {
	    LogHelper.exception(PlatformTracker.class, "onLocationChange", e.getLocalizedMessage(), e);
	}
    }

    @Override
    public String getId() {
	return getPlatformId();
    }

}
