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
import itti.com.pl.arena.cm.location.Range;
import itti.com.pl.arena.cm.ontology.Ontology;
import itti.com.pl.arena.cm.ontology.OntologyConstants;
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
    // maximum break/parking time. After that time, CM will assume, that platform is on the parking
    private long maxBreakTime = 30;

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

    private long getMaxBreakTime() {
        return maxBreakTime;
    }

    @Required
    public void setMaxBreakTime(long maxBreakTime) {
        this.maxBreakTime = maxBreakTime;
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
        try {
            //check all the ranges, from the widest one to the closest
            for (Range range : Range.values()) {

                //get parking lots for given range
                Set<GeoObject> parkingLots = ontology.getGISObjects(location.getLongitude(), location.getLatitude(), range.getRangeInKms(),
                        OntologyConstants.Parking.name());

                if (!parkingLots.isEmpty()) {
                    LogHelper.debug(PlatformTracker.class, "onLocationChange",
                            "Found parking lot in the close range: %f. Parking details: %s", range.getRangeInKms(), parkingLots);

                    //we are on the parking
                    if(range == Range.getClosestRange()){
                        //TODO: do we want to do something with that?
                    }
                }else{
                    //no parking lots in the given area, break the loop
                    break;
                }
            }

        } catch (OntologyException exc) {
            LogHelper.warning(PlatformTracker.class, "onLocationChange",
                    "Could not find parking lots in the given area. Details: %s", exc.getLocalizedMessage());
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
                LogHelper.debug(PlatformTracker.class, "checkIfMoving", "platform %s is not moving for last timestamp: %d",
                        getPlatformId(), lastLocation.getTime());

                // check, for how long platform is not moving
                long pauseTime = DateTimeHelper.delta(System.currentTimeMillis(), lastLocation.getTime(), DateTimeHelper.MINUTE);

                // if MaxBreakTime will be reached, assume that parking was reached
                // note double condition here - it's here to avoid calling geoportal service every function call
                if (pauseTime > getMaxBreakTime() && pauseTime < (getMaxBreakTime() + 1)) {

                    LogHelper.debug(PlatformTracker.class, "checkIfMoving", "platform %s is not moving for last %d minutes",
                            getPlatformId(), getMaxBreakTime());

                    // check, if there is data in ontology collected for given location
                    Set<GeoObject> ontoData = getOntology().getGISObjects(lastLocation.getLongitude(), lastLocation.getLatitude(), getRadius());

                    // if there is no data, try to download it from geoportal
                    if (ontoData.isEmpty()) {
                        Set<GeoObject> geoportalData = getGeoportal().getGeoportalData(lastLocation, getRadius());
                        // store downloaded data into ontology
                        getOntology().addGeoportalData(lastLocation.getLongitude(), lastLocation.getLatitude(), geoportalData);
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
