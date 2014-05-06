package itti.com.pl.arena.cm.server.service;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.server.location.LocationListener;

/**
 * Listener used to inform all the components, that platform had reached the destination
 * 
 * @author cm-admin
 * 
 */
public interface PlatformListener {

    /**
     * method called by the {@link LocationListener} once the destination will be reached
     * 
     * @param platformId
     *            ID of the platform
     * @param location
     *            final position of the platform
     */
    public void destinationReached(String platformId, Location location);

    /**
     * method called by the {@link LocationListener} once the destination will be left
     * 
     * @param platformId
     *            ID of the platform
     * @param location
     *            last position of the platform
     */
    void destinationLeft(String platformId, itti.com.pl.arena.cm.dto.Location location);

}
