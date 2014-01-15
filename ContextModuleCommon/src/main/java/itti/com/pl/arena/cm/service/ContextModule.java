package itti.com.pl.arena.cm.service;

import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.SimpleNamedValue;
import eu.arena_fp7._1.Situation;

/**
 * General external interface for the ContextModule defines method, which can be used by external component to access CM
 * resources
 * 
 * @author mawa
 * 
 */
public interface ContextModule {

    /**
     * This method allows retrieval of current information about current position of the platform (e.g. location,
     * velocity and travel direction).
     * 
     * @param objectId
     *            ID of the object
     * @return information about platform with Kinematics and Location classes stored in Object’s featureVector
     */
    Object getPlatform(SimpleNamedValue objectId);

    /**
     * This method allows retrieval of data about platforms found near the given location (i.e. returned Situation class
     * will contain Object classes describing each platform).
     * 
     * @param location
     *            location
     * @return data about platforms with Object classes having in their featureVectors Location and Kinematics
     *         attributes
     */
    Situation getPlatforms(Location location);

    /**
     * This method allows retrieval of available GIS data for specified location (i.e. creates Situation class with
     * Objects having capabilities stored in their featureVectors) Situation class contains Object classes describing
     * GIS features (e.g. buildings, roads, etc.) with appropriate featureVectors (e.g. number of lanes for roads or
     * height for buildings). The type of that data depends on GIS data source capabilities
     * 
     * @param location
     *            location
     * @return available GIS data for specified location
     */
    Situation getGISData(Location location);

    /**
     * Retrieves data from the Geoportal service
     * 
     * @param location
     *            geographical location
     * @return Geoportal-defined data
     */
    Situation getGeoportalData(Location location);
}
