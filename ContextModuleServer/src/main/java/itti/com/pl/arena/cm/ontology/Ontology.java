package itti.com.pl.arena.cm.ontology;

import java.util.List;

import itti.com.pl.arena.cm.dto.GeoObject;
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
    public List<String> getPlatforms(double x, double y, double radius)
	    throws OntologyException;

    /**
     * Returns IDs of GIS objects (like buildings or parking lots) found near given location
     * @param x latitude
     * @param y longitude
     * @param radius
     * @return list of GIS objects IDs
     * @throws OntologyException could not retrieve information from the ontology
     */
    public List<String> getGISObjects(double x, double y, double radius)
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
	    GeoportalResponse geoportalData);
}
