package itti.com.pl.arena.cm.location;

import itti.com.pl.arena.cm.service.Service;

/**
 * Service publishes current location of the platform
 * 
 * @author cm-admin
 * 
 */
public interface LocationPublisher extends Service {

    /**
     * Registers a new location listener. All registered listeners will be notified about location updates
     * 
     * @param listener
     *            listener
     */
    public void registerListener(LocationListener listener);

    /**
     * Replaces existing list of location listeners with the new ones.
     * 
     * @param listeners
     *            array of listeners
     */
    void setListeners(LocationListener... listeners);

    /**
     * Deregisters existing location listener
     * 
     * @param listener
     *            listener, which should be deregistered
     */
    public void deregisterListener(LocationListener listener);

}
