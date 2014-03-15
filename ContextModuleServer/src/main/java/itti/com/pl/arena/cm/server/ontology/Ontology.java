package itti.com.pl.arena.cm.server.ontology;

import java.util.List;
import java.util.Set;

import eu.arena_fp7._1.Location;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.OntologyObject;
import itti.com.pl.arena.cm.dto.coordinates.ArenaObjectCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;

/**
 * Interface defining Arena-specific ontology operations
 * 
 * @author cm-admin
 * 
 */
public interface Ontology {

    /**
     * Updates (or create new if not found) ontology object representing platform
     * 
     * @param platform
     *            information about platform
     * @throws OntologyException
     *             could not update information about platform
     */
    public void updatePlatform(Platform platform) throws OntologyException;

    /**
     * Updates (or create new if not found) ontology object representing parking lot
     * 
     * @param parkingLot
     *            information about parking lot
     * @throws OntologyException
     *             could not update information about parking lot
     */
    public void updateParkingLot(ParkingLot parkingLot) throws OntologyException;

    /**
     * Adds information about new GIS object to the ontology
     * 
     * @param x
     *            latitude
     * @param y
     *            longitude
     * @param geoportalData
     *            information about GIS objects
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public void updateGeoportalData(double x, double y, Set<GeoObject> geoportalData) throws OntologyException;

    /**
     * Returns IDs of platforms found near given location
     * 
     * @param x
     *            latitude
     * @param y
     *            longitude
     * @param radius
     *            radius
     * @return list of platforms IDs
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<String> getInstanceNames(double x, double y, double radius, Class<? extends OntologyObject> ontologyClass)
            throws OntologyException;

    /**
     * Returns list of platform objects found near given location
     * 
     * @param x
     *            longitude
     * @param y
     *            langitude
     * @param radius
     *            radius
     * @return list of platforms objects
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<Platform> getPlatforms(double x, double y, double radius) throws OntologyException;

    /**
     * Returns IDs of all GIS objects (like buildings, fences, trees) found on given parking lot
     * 
     * @param parkingId
     *            ID of the parking lot
     * @param classFilter
     *            returns only objects belonging to specified classes
     * @return list of GIS object IDs
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<String> getParkingLotInfrastructure(String parkingId, String... classFilter) throws OntologyException;

    /**
     * Returns information about GIS object identified by its ID
     * 
     * @param id
     *            ID of the object
     * @param <T>
     *            class of the requested {@link OntologyObject}
     * @return information about the object
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public <T extends OntologyObject> T getOntologyObject(String id, Class<T> objectclass) throws OntologyException;

    /**
     * Returns list of specific GIS objects found near given location
     * 
     * @param x
     *            longitude
     * @param y
     *            latitude
     * @param radius
     *            radius
     * @param gisObjectClasses
     *            classes of the GIS objects found in given area. Only instances of that class will be returned
     * @return list of GIS objects
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<GeoObject> getGISObjects(double x, double y, double radius, String... gisObjectClasses) throws OntologyException;

    /**
     * Calculates distance between platform, and objects localized on given parking lot
     * 
     * @param platformId
     *            ID of the platform, calculations should be done
     * @param radius
     *            radius of the search area
     * @throws OntologyException
     *             processing exception
     */
    public void calculateDistancesForPlatform(String platformId, double radius) throws OntologyException;

    /**
     * Calculates distance between platform, and all objects localized on given parking lot
     * 
     * @param platformId
     *            ID of the platform, calculations should be done
     * @return list of
     * @throws OntologyException
     *             processing exception
     */
    public Set<ArenaObjectCoordinate> calculateArenaDistancesForPlatform(String platformId) throws OntologyException;

    /**
     * Defines a new zone in the ontology
     * 
     * @param locations
     *            zone boundaries (list of zone vertexes stored as {@link Location} objects)
     * @return ID of the zone
     * @throws OntologyException
     *             processing exception
     */
    public String defineZone(List<itti.com.pl.arena.cm.dto.Location> locations) throws OntologyException;

    /**
     * Defines a new zone in the ontology
     * 
     * @param zoneId
     *            ID of the zone
     * @return zone boundaries (list of zone vertexes stored as {@link Location} objects)
     * @throws OntologyException
     *             processing exception
     */
    public List<itti.com.pl.arena.cm.dto.Location> getZone(String zoneId) throws OntologyException;
}
