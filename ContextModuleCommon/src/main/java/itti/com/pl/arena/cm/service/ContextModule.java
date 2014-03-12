package itti.com.pl.arena.cm.service;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * General external interface for the ContextModule defines method, which can be used by external component to access CM
 * resources
 * 
 * @author mawa
 * 
 */
public interface ContextModule {

    /**
     * This method allows retrieval of information about the platform (cameras installed on platform, its dimensions) as
     * well as current position of the platform (e.g. location, velocity and travel direction).
     * 
     * @param objectId
     *            ID of the object
     * @return information about platform with Kinematics and Location classes stored in Object’s featureVector
     */
    Object getPlatform(SimpleNamedValue objectId);

    /**
     * This method returns information about camera field of view. This method requires platform to be parked on
     * parking lot, or any other defined area (Geo-data information about close platform area must be stored in
     * Ontology). Expected result is: complete information objects, which are supposed to be in camera field of view
     * @param cameraId
     *            ID of the camera
     * @return complete information about parking area, where platform is parked 
     */
    Object getCameraFieldOfView(SimpleNamedValue cameraId);

    /**
     * This method allows to update existing or create new platform in the ContextModule
     * 
     * @param platform
     *            new or updated Platform object. Attribute 'value' of the {@link SimpleNamedValue} should contain
     *            serialized {@link Platform} object
     * @return update status
     */
    BooleanNamedValue updatePlatform(SimpleNamedValue platform);

    /**
     * This method allows retrieval of data about platforms found near the given location (i.e. returned Object class
     * will contain Object classes describing each platform).
     * 
     * @param location
     *            location
     * @return data about platforms with Object classes having in their featureVectors Location and Kinematics
     *         attributes
     */
    Object getPlatforms(Location location);

    /**
     * This method allows retrieval of available GIS data for specified location (i.e. creates Object class with
     * Objects having capabilities stored in their featureVectors) Object class contains other Object classes describing
     * GIS features (e.g. buildings, roads, etc.) with appropriate featureVectors (e.g. number of lanes for roads or
     * height for buildings). The type of that data depends on GIS data source capabilities
     * 
     * @param location
     *            location
     * @return available GIS data for specified location
     */
    Object getGISData(Location location);

    /**
     * This method allows retrieval of available GIS data for specified location (i.e. creates Object class with
     * Objects having capabilities stored in their featureVectors) Object class contains other Object classes describing
     * GIS features (e.g. buildings, roads, etc.) with appropriate featureVectors (e.g. number of lanes for roads or
     * height for buildings). The type of that data depends on GIS data source capabilities
     * 
     * @param parameters complex object containing parameters:
     *          - location information about searched location (instance of the {@link Location}
     *          - radius (instance of the double object), measured in kilometers
     *          - filters (instance of the String[] object) array containing names of the ontology classes
     *                  which should be used to filter results
     * All parameters are stored as {@link Location} and {@link SimpleNamedValue} objects inside {@link Object} vector
     * @return available GIS data for specified location
     */
    Object getGISData(Object parameters);

    /**
     * This method allows to update existing or create new geo-object (instance of the {@link GeoObject} interface) in
     * the ContextModule
     * 
     * @param gisData
     *            new or updated geo-object. Attribute 'value' of the {@link SimpleNamedValue} should contain serialized
     *            {@link GeoObject} object
     * @return update status
     */
    BooleanNamedValue updateGISData(SimpleNamedValue gisData);

    /**
     * Retrieves data from the Geoportal service
     * 
     * @param location
     *            geographical location
     * @return Geoportal-defined data
     */
    Object getGeoportalData(Location location);

    /**
     * Defines a new zone in the ontology
     * @param zoneDefinition definition of the zone (list of zone vertexes stored as {@link Location} objects)
     * @return ID of the zone
     */
    SimpleNamedValue defineZone(Object zoneDefinition);

    /**
     * Returns zone definition: list of zone vertexes stored as {@link Location} objects
     * @param zoneId ID of the zone
     * @return list of coordinates, or empty list, if zone with given ID not found in the ontology
     */
    Object getZone(SimpleNamedValue zoneId);
}
