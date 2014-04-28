package itti.com.pl.arena.cm.service;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.RealWorldCoordinate;
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
     * @return information about platform stored in Object’s featureVector
     */
    Object getPlatform(SimpleNamedValue objectId);

    /**
     * This method allows retrieval of information about the building/infrastructure (location, type, boundaries).
     * 
     * @param buildingId
     *            ID of the building/infrastructure
     * @return information about building/infrastructure stored in Object’s featureVector
     */
    Object getBuilding(SimpleNamedValue buildingId);

    /**
     * Removes an existing platform from the ontology
     * 
     * @param platformId ID of the platform to be removed from ontology
     * @return removal status (true == platform removed, false otherwise)
     */
    BooleanNamedValue removePlatform(SimpleNamedValue platformId);

    /**
     * Removes an existing building/infrastructure from the ontology
     * 
     * @param request ID of the building/infrastructure to be removed from ontology
     * @return removal status (true == building/infrastructure removed, false otherwise)
     */
    BooleanNamedValue removeBuilding(SimpleNamedValue request);

    /**
     * Removes an existing parking lot from the ontology
     * 
     * @param parkingLotId ID of the parking lot to be removed from ontology
     * @return removal status (true == parking lot removed, false otherwise)
     */
    BooleanNamedValue removeParkingLot(SimpleNamedValue parkingLotId);

    /**
     * This method allows to update existing or create new {@link Platform} in the ContextModule
     * 
     * @param platform
     *            new or updated Platform object. Attribute 'value' of the provided {@link SimpleNamedValue} should contain
     *            serialized {@link Platform} object
     * @return update status
     */
    BooleanNamedValue updatePlatform(SimpleNamedValue platform);

    /**
     * This method allows to update existing or create new {@link Camera} in the ContextModule
     * 
     * @param camera
     *            new or updated Camera object. Attribute 'value' of the provided {@link SimpleNamedValue} should contain
     *            serialized {@link Camera} object
     * @return update status
     */
    BooleanNamedValue updateCamera(SimpleNamedValue camera);

    /**
     * This method allows retrieval of information about the camera (its parameters)
     * 
     * @param cameraId
     *            ID of the camera
     * @return information about camera stored in Object’s featureVector
     */
    Object getCamera(SimpleNamedValue cameraId);

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
     * This method allows to update existing or create new parking lot in the ContextModule
     * 
     * @param parkingLot
     *            new or updated {@link ParkingLot} object. Attribute 'value' of the provided {@link SimpleNamedValue} should contain
     *            serialized {@link ParkingLot} object
     * @return update status
     */
    BooleanNamedValue updateParkingLot(SimpleNamedValue parkingLot);

    /**
     * This method allows to update existing or create new building/infrastructure in the ContextModule
     * 
     * @param building
     *            new or updated {@link GeoObject} object. Attribute 'value' of the provided {@link SimpleNamedValue} should contain
     *            serialized {@link Building} or {@link Infrastructure} object
     * @return update status
     */
    BooleanNamedValue updateBuilding(SimpleNamedValue building);

    /**
     * This method retries information about the parking lot (location, boundaries, objects like buildings or infrastructure).
     * 
     * @param objectId
     *            ID of the object
     * @return information about parking lot stored in Object’s featureVector
     */
    Object getParkingLot(SimpleNamedValue objectId);

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
     * Retrieves data from the Geoportal service
     * 
     * @param location
     *            geographical location
     * @return Geoportal-defined data
     */
    Object getGeoportalData(Location location);

    /**
     * returns information about platform neighborhood in the ContextModule-specific Cartesian coordinate system
     * Point (0,0) of that system is located in the driver's cabin, default unit is calibrated to one meter
     * and axis are compatible with the main truck axis
     * @param platformId ID of the platform
     * @return information about platform neighborhood (surrounding buildings or parking infrastructure)
     */
    Object getPlatformNeighborhood(SimpleNamedValue platformId);

    /**
     * Defines a new, or updates an existing zone in the ontology
     * 
     * @param zoneDefinition
     *            definition of the zone (list of zone vertexes stored as {@link Location} or
     *            {@link RealWorldCoordinate} objects)
     * @return ID of the zone
     */
    SimpleNamedValue updateZone(Object zoneDefinition);

    /**
     * Removes an existing zone from the ontology
     * 
     * @param zoneId ID of the zone to be removed from ontology
     * @return removal status (true == zone removed, false otherwise)
     */
    BooleanNamedValue removeZone(SimpleNamedValue zoneId);

    /**
     * Removes an existing camera from the ontology
     * 
     * @param cameraId ID of the camera to be removed from ontology
     * @return removal status (true == camera removed, false otherwise)
     */
    BooleanNamedValue removeCamera(SimpleNamedValue cameraId);

    /**
     * Returns zone definition: list of zone vertexes stored as {@link RealWorldCoordinate} objects
     * 
     * @param zoneId
     *            ID of the zone
     * @return list of coordinates, or empty list, if zone with given ID not found in the ontology
     */
    Object getZone(SimpleNamedValue zoneId);

    /**
     * Returns list of zones defined for given parking lot
     * 
     * @param requestId request message containing parking lot name
     * @return list of zone names, or empty list, if no zones were defined for given parking lot in the ontology
     */
    Object getListOfZones(SimpleNamedValue requestId);

    /**
     * Returns list of platforms defined in application
     * 
     * @param requestId request message
     * @return list of platform names, or empty list, if no platforms were defined
     */
    Object getListOfPlatforms(SimpleNamedValue requestId);

    /**
     * This method retries a list of parking lots defined in the ontology
     * 
     * @param objectId
     *            empty request object
     * @return list of parking lot names defined in the ontology
     */
    Object getListOfParkingLots(SimpleNamedValue objectId);

    /**
     * This method adds a new one, or updates existing SWRL rule to the ontology
     * 
     * @param rule
     *            rule to be added to /updated in ontology
     * @return update status
     */
    BooleanNamedValue updateRule(SimpleNamedValue rule);

    /**
     * This method removes existing SWRL rule from the ontology
     * 
     * @param ruleId
     *            ID of the rule
     * @return removal status
     */
    BooleanNamedValue removeRule(SimpleNamedValue ruleId);

    /**
     * This method returns existing SWRL rule from the ontology
     * 
     * @param ruleId
     *            ID of the rule
     * @return rule content
     */
    SimpleNamedValue getRule(SimpleNamedValue ruleId);

    /**
     * This method executes existing SWRL rule on the ontology
     * 
     * @param objectId
     *            empty request object
     * @return execution status
     */
    BooleanNamedValue applyRules(SimpleNamedValue objectId);

    /**
     * This method returns a list of rules added to the ontology
     * 
     * @param objectId
     *            empty request object
     * @return list of rules
     */
    Object getListOfRules(SimpleNamedValue objectId);

}
