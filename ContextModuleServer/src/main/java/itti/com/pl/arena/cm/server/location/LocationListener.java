package itti.com.pl.arena.cm.server.location;

import itti.com.pl.arena.cm.dto.Location;

/**
 * Interface used by all modules interested in the location changes
 * 
 * @author cm-admin
 * 
 */
public interface LocationListener {

    /**
     * Method invoked by the {@link LocationPublisher} when change of the location o the platform will be detected
     * 
     * @param newLocation
     *            current location of the platform
     */
    void onLocationChange(Location newLocation);

    /**
     * Returns unique ID of the listener Used for registering and deregistering it from the {@link LocationPublisher}
     * 
     * @return ID of the listener
     */
    public String getId();
}
