package itti.com.pl.arena.cm.ontology;

import java.util.Set;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;

/**
 * Interface defining Arena-specific ontology operations
 * 
 * @author cm-admin
 * 
 */
public interface Ontology {

    /**
     * Returns information about platform
     * 
     * @param platformId
     *            ID of the platform
     * @return {@link Platform} object containing information about the platform
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Platform getPlatform(String platformId) throws OntologyException;

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
    public Set<String> getPlatformNames(double x, double y, double radius) throws OntologyException;

    /**
     * Returns list of platform objects found near given location
     * 
     * @param x
     *            latitude
     * @param y
     *            longitude
     * @param radius
     *            radius
     * @return list of platforms objects
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<Platform> getPlatforms(double x, double y, double radius) throws OntologyException;

    /**
     * Returns IDs parking lots found near given location
     * 
     * @param location
     *            location
     * @param radius
     *            radius of the search area
     * @return list of GIS object IDs
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<String> getParkingLots(Location location, double radius) throws OntologyException;

    /**
     * Returns IDs of parking lots found near given location
     * 
     * @param x
     *            latitude
     * @param y
     *            longitude
     * @param radius
     *            radius of the search area
     * @return list of GIS object IDs
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<String> getParkingLots(double x, double y, double radius) throws OntologyException;

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
     * @return information about the object
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public GeoObject getGISObject(String id) throws OntologyException;

    /**
     * Returns list of GIS objects found near given location
     * 
     * @param location
     *            location (information about longitude and latitude)
     * @param radius
     *            radius
     * @return list of GIS objects
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<GeoObject> getGISObjects(Location location, double radius) throws OntologyException;

    /**
     * Returns list of specific GIS objects found near given location
     * 
     * @param location
     *            location (information about longitude and latitude)
     * @param radius
     *            radius
     * @param gisObjectClasses
     *            classes of the GIS objects found in given area. Only instances of that class will be returned
     * @return list of GIS objects
     * @throws OntologyException
     *             could not retrieve information from the ontology
     */
    public Set<GeoObject> getGISObjects(Location location, double radius, String... gisObjectClasses) throws OntologyException;

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
    public void addGeoportalData(Location location, Set<GeoObject> geoportalData) throws OntologyException;

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
}
