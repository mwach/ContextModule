package itti.com.pl.arena.cm.persistence;

import itti.com.pl.arena.cm.dto.Location;

import java.util.List;

/**
 * General interface for the persistence layer It's used to store information about platform position history Can be
 * used to store any other data as well
 * 
 * @author cm-admin
 * 
 */
public interface Persistence {

    /**
     * Initializes persistence storage
     * 
     * @throws PersistenceException
     *             Could not initialize storage
     */
    public void init() throws PersistenceException;

    /**
     * Shutdowns persistence storage
     * 
     * @throws PersistenceException
     *             Could not shutdown storage
     */
    public void shutdown() throws PersistenceException;

    /**
     * Persist information about location of the platform in the database
     * 
     * @param platformId
     *            ID of the platform
     * @param location
     *            location of the platform
     * @throws PersistenceException
     *             Could not store that information in the database
     */
    public void create(String platformId, Location location) throws PersistenceException;

    /**
     * Purges outdated information about locations from the database
     * 
     * @param platformId
     *            ID of the platform
     * @param oldestTimestamp
     *            threshold timestamp. All data older than this timestamp will be removed from database
     * @throws PersistenceException
     *             Could not delete old data from the database
     */
    public void delete(String platformId, long oldestTimestamp) throws PersistenceException;

    /**
     * Returns information about last know platorm position from the database
     * 
     * @param platformId
     *            ID of the platform
     * @return last known position of the platform
     * @throws PersistenceException
     *             Could not retrieve information from the database
     */
    public Location getLastPosition(String platformId) throws PersistenceException;

    /**
     * Returns a list of known positions of the platform starting with given timestamp
     * 
     * @param platformId
     *            ID of the platform
     * @param initialTimestamp
     *            initial timestamp, locations saved after that timestamp will be returned
     * @return list of last platform positions
     * @throws PersistenceException
     */
    public List<Location> getHistory(String platformId, long initialTimestamp) throws PersistenceException;
}
