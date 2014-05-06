package itti.com.pl.arena.cm.server.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.dynamicobj.PlatformStatus;
import itti.com.pl.arena.cm.server.location.LocationListener;
import itti.com.pl.arena.cm.server.location.Range;
import itti.com.pl.arena.cm.server.ontology.Ontology;
import itti.com.pl.arena.cm.server.ontology.OntologyConstants;
import itti.com.pl.arena.cm.server.ontology.OntologyException;
import itti.com.pl.arena.cm.server.persistence.Persistence;
import itti.com.pl.arena.cm.server.persistence.PersistenceException;
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
    // platform listener to be notified once platform will reach its destination
    private PlatformListener platformListener = null;
    // max idle time: after that time passes, destinationReached of the platformListener will be called
    private int maxIdleTime = 0;

    // current status of the platform
    private PlatformStatus status = PlatformStatus.Unknown;

    private void updateStatus(PlatformStatus newStatus) {
        this.status = newStatus;
    }

    public PlatformStatus getStatus() {
        return status;
    }

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

    private PlatformListener getPlatformListener() {
        return platformListener;
    }

    @Required
    public void setPlatformListener(PlatformListener platformListener) {
        this.platformListener = platformListener;
    }

    private int getMaxIdleTime() {
        return maxIdleTime;
    }

    @Required
    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.Service#init()
     */
    @Override
    public void init() {
        LogHelper.debug(PlatformTracker.class, "init", "init using ID: %s", getPlatformId());
        if (!StringHelper.hasContent(getPlatformId())) {
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
                StringHelper.toString(location), getPlatformId());

        // try to persist latest location in the database
        if (location != null) {
            try {
                // store location in the database
                getPersistence().create(getPlatformId(), location);
                // update platform position in the ontology
                getOntology().updatePlatformPosition(getPlatformId(), location);
            } catch (PersistenceException exc) {
                LogHelper.warning(PlatformTracker.class, "onLocationChange",
                        "Could not persist location data: for platform %s. Details: %s", getPlatformId(),
                        exc.getLocalizedMessage());
            } catch (OntologyException exc) {
                LogHelper.warning(PlatformTracker.class, "onLocationChange",
                        "Could not update ontlogy with platform '%s' data. Details: %s", getPlatformId(),
                        exc.getLocalizedMessage());
            }
        } else {
            LogHelper.info(PlatformTracker.class, "onLocationChange", "Null location received for platform %s", getPlatformId());
        }
    }

    /**
     * Method periodically called by application scheduler to verify platform status (is it moving, stopped, parked on
     * the parking lot)
     */
    public void checkPlatformStopped() {

        try {
            // TODO: verify GPS time units
            Location lastLocation = getPersistence().getLastPosition(getPlatformId());
            if (lastLocation == null) {
                LogHelper.debug(PlatformTracker.class, "checkPlatformStopped",
                        "No location retrieved from the persistence storage");
                return;
            }// TODO: remove multiplier, but use delta location (e.g. min diff=1m, otherwise false positives)
            if (DateTimeHelper.delta(lastLocation.getTime() * 1000, System.currentTimeMillis(), DateTimeHelper.SECOND) > getMaxIdleTime()) {

                // check all the ranges, from the widest one to the closest
                for (Range range : Range.values()) {

                    // get parking lots for given range
                    Set<GeoObject> parkingLots = getOntology().getGISObjects(lastLocation.getLongitude(),
                            lastLocation.getLatitude(), range.getRangeInKms(), OntologyConstants.Parking.name());

                    if (!parkingLots.isEmpty()) {
                        LogHelper.debug(PlatformTracker.class, "checkPlatformStopped",
                                "Found parking lot in the close range: %f. Parking details: %s", range.getRangeInKms(),
                                parkingLots);

                        // we are on the parking
                        if (range == Range.getClosestRange() && getStatus() != PlatformStatus.StoppedOnParking) {
                            updateStatus(PlatformStatus.StoppedOnParking);
                            // notify the listener
                            getPlatformListener().destinationReached(getId(), lastLocation);
                        }
                    } else {
                        // no parking lots in the given area, break the loop
                        updateStatus(PlatformStatus.StoppedOutsideParking);
                        break;
                    }
                }
            } else {
                // platform started moving again
                if (getStatus() == PlatformStatus.StoppedOnParking) {
                    getPlatformListener().destinationLeft(getId(), lastLocation);
                }
                updateStatus(PlatformStatus.Moving);
            }
        } catch (OntologyException exc) {
            LogHelper.warning(PlatformTracker.class, "checkPlatformStopped",
                    "Could not find parking lots in the given area. Details: %s", exc.getLocalizedMessage());
        } catch (PersistenceException exc) {
            LogHelper.warning(PlatformTracker.class, "checkPlatformStopped",
                    "Could not retrieve parking data from the persistence storage. Details: %s", exc.getLocalizedMessage());
        } catch (RuntimeException exc) {
            LogHelper.warning(PlatformTracker.class, "checkPlatformStopped",
                    "Runtime exception during data processing. Details: %s", exc.getLocalizedMessage());
        }
    }

    @Override
    public String getId() {
        return getPlatformId();
    }

}
