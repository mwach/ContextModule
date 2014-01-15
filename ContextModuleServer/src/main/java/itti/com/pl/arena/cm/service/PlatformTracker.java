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
import itti.com.pl.arena.cm.utils.helper.DateTimeHelper;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

/**
 * Default implementation of the ContextModule tracker
 * 
 * @author cm-admin
 * 
 */
public class PlatformTracker implements Service, LocationListener {

    // persistence module used to store information about platform
    private Persistence persistence = null;
    // ontology module used to store/retrieve platform information
    private Ontology ontology = null;
    // geoportal module used to collect geoportal information
    private GeoportalService geoportal = null;
    // radius used for geoportal search purposes
    private double radius = 0.0;

    /*
     * ID of the listener (if not provided, random UUID generated during initialization)
     */
    private String platformId;

    public void setPlatformId(String id) {
        this.platformId = id;
    }

    private String getPlatformId() {
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

    private double getRadius() {
        return radius;
    }

    @Required
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.Service#init()
     */
    @Override
    public void init() {
        LogHelper.debug(PlatformTracker.class, "init", "init using ID: %s", getPlatformId());
        if (StringHelper.hasContent(getPlatformId())) {
            setPlatformId(UUID.randomUUID().toString());
            LogHelper.debug(PlatformTracker.class, "init", "no ID provided, random values will be used: %s", getPlatformId());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.Service#shutdown()
     */
    @Override
    public void shutdown() {
        LogHelper.debug(PlatformTracker.class, "shutdown", "shutdown");
        // not used by this service
    }

    @Override
    public void onLocationChange(Location location) {

        LogHelper.debug(PlatformTracker.class, "onLocationChange", "New platform location received: %s for platfrom %s",
                location, getPlatformId());

        // first, try to persist latest location in the database
        try {
            getPersistence().create(getPlatformId(), location);
        } catch (PersistenceException e) {
            LogHelper.warning(PlatformTracker.class, "onLocationChange",
                    "Could not persist location data: for platform %s. Details: %s", getPlatformId(), e.getLocalizedMessage());
        }
    }

    public void checkIfMoving() {

        LogHelper.debug(PlatformTracker.class, "checkIfMoving", "check, if platform %s is moving", getPlatformId());
        try {
            // get the last location of the object
            Location lastLocation = getPersistence().getLastPosition(getPlatformId());
            // check if object is moving
            if (lastLocation != null && lastLocation.getSpeed() < 0.01) {
                // if not - check, when it was moving for the last time
                // trigger is set to 30 minutes
                if(DateTimeHelper.delta(System.currentTimeMillis(), lastLocation.getTime(), DateTimeHelper.MINUTE) > 30){
                    Set<GeoObject> ontoData = getOntology().getGISObjects(lastLocation, getRadius());
                    if (ontoData.isEmpty()) {
                        Set<GeoObject> geoportalData = getGeoportal().getGeoportalData(lastLocation, getRadius());
                        getOntology().addGeoportalData(lastLocation, geoportalData);
                        ontoData = geoportalData;
                    }
                }
            }
        } catch (OntologyException | GeoportalException | PersistenceException exc) {
            LogHelper.exception(PlatformTracker.class, "checkIfMoving", "Could not calculate if object is still moving.", exc);
        }
    }

    @Override
    public String getId() {
        return getPlatformId();
    }

}
