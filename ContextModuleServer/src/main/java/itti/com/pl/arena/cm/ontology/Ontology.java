package itti.com.pl.arena.cm.ontology;

import java.util.Set;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.Platform;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse;

/**
 * Interface defining Arena-specific ontology operations
 * 
 * @author cm-admin
 * 
 */
public interface Ontology {

    /**
     * Returns information about platform
     * @param platformId ID of the platform
     * @return {@link Platform} object containing information about the platform
     * @throws OntologyException could not retrieve information from the ontology
     */
    public Platform getPlatform(String platformId)
	    throws OntologyException;

    /**
     * Updates (or create new if not found) ontology object representing platform
     * @param platformInformation information about platform
     * @throws OntologyException could not update information about platform
     */
    public void updatePlatform(Platform platformInformation)
    		throws OntologyException;

    /**
     * Returns IDs of platforms found near given location
     * @param x latitude
     * @param y longitude
     * @param radius radius
     * @return list of platforms IDs
     * @throws OntologyException could not retrieve information from the ontology
     */
    public Set<String> getPlatforms(double x, double y, double radius)
	    throws OntologyException;

    /**
     * Returns IDs of GIS objects (like buildings or parking lots) found near given location
     * @param location location
     * @param radius radius of the search area
     * @param classesFilter optional filter: list of classes names, which should be returned
     * @return list of GIS objects IDs
     * @throws OntologyException could not retrieve information from the ontology
     */
    public Set<String> getParkingLots(Location location, double radius) throws OntologyException;

    /**
     * Returns IDs of GIS objects (like buildings or parking lots) found near given location
     * @param x latitude
     * @param y longitude
     * @param radius radius of the search area
     * @param classesFilter optional filter: list of classes names, which should be returned
     * @return list of GIS objects IDs
     * @throws OntologyException could not retrieve information from the ontology
     */
    public Set<String> getParkingLots(double x, double y, double radius)
	    throws OntologyException;


    /**
     * Returns information about GIS object identified by its ID
     * @param id ID of the object
     * @return information about the object
     * @throws OntologyException could not retrieve information from the ontology
     */
    public GeoObject getGISObject(String id) throws OntologyException;

    /**
     * Adds information about new GIS object to the ontology
     * @param x latitude
     * @param y longitude
     * @param geoportalData information about GIS object
     * @throws OntologyException could not retrieve information from the ontology
     */
    public void addGeoportalData(double x, double y,
	    GeoportalResponse geoportalData) throws OntologyException;

    /**
     * Calculates distance between platform, and objects localized on given parking lot
     * @param platformId ID of the platform, calculations should be done
     * @param radius radius of the search area
     * @throws OntologyException processing exception
     */
    public void calculateDistancesForPlatform(String platformId, double radius) throws OntologyException;
}
