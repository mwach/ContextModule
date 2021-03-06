package itti.com.pl.arena.cm.server.ontology;

import java.util.List;
import java.util.Set;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.OntologyObject;
import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.dto.coordinates.ArenaObjectCoordinate;
import itti.com.pl.arena.cm.dto.coordinates.FieldOfViewObject;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
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
     * Updates last position of the platform in the ontology. If platform with given ID is not defined in the ontology,
     * then it's going to be created
     * 
     * @param platformId
     *            ID of the platform
     * @param location
     *            last location of the platform
     * @throws OntologyException
     *             could not update information about platform
     */
    public void updatePlatformPosition(String platformId, Location location) throws OntologyException;

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
     * @return list of arena coordinates representing objects in the platform neighborhood
     * @throws OntologyException
     *             processing exception
     */
    public Set<ArenaObjectCoordinate> getPlatformNeighborhood(String platformId) throws OntologyException;

    /**
     * Calculates camera field of view
     * 
     * @param cameraId
     *            ID of the camera, for which calculations should be done
     * @return list of arena coordinates representing camera field of view
     * @throws OntologyException
     *             processing exception
     */
    public Set<FieldOfViewObject> getCameraFieldOfView(String cameraId) throws OntologyException;

    /**
     * Defines a new, or update an existing zone in the ontology
     * 
     * @param zoneId
     *            predefined ID of the zone
     * @param parkingLotName
     *            name of the parking lot
     * @param planeName
     *            name of the plane
     * 
     * @param locations
     *            zone boundaries (list of zone vertexes stored as {@link Location} objects)
     * @return ID of the zone
     * @throws OntologyException
     *             processing exception
     */
    public String updateZone(String zoneId, String parkingLotName, String planeName,
            List<itti.com.pl.arena.cm.dto.Location> locations) throws OntologyException;

    /**
     * Retrieves zone from the ontology
     * 
     * @param zoneId
     *            ID of the zone
     * @return zone
     * @throws OntologyException
     *             processing exception
     */
    public Zone getZone(String zoneId) throws OntologyException;

    /**
     * Returns list of instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public List<String> getInstances(String className) throws OntologyException;

    /**
     * Returns list of non-direct (second and further levels) instances of given ontology class
     * 
     * @param className
     *            name of the class
     * @return list of instances names. If there are no instances, empty list will be returned
     */
    public List<String> getNonDirectInstances(String className) throws OntologyException;

    /**
     * Returns parent class of given instance
     * 
     * @param instanceName
     *            name of the instance
     * @return parent class name.
     * @exception OntologyExceptiont
     *                could not find object
     */
    public String getInstanceClass(String instanceName) throws OntologyException;

    /**
     * Removes instance identified by its name from ontology
     * 
     * @param instanceName
     *            name of the instance to remove
     */
    public void remove(String instanceName) throws OntologyException;

    /**
     * Defines a new, or update an existing camera in the ontology
     * 
     * @param camera
     *            camera object
     * @param platformName
     *            ID of the platform, on which camera is installed
     * 
     * @throws OntologyException
     *             processing exception
     */
    public void updateCamera(Camera camera, String platformName) throws OntologyException;

    /**
     * Defines a new, or update an existing building in the ontology
     * 
     * @param building
     *            building to be added/updated. Instance of the {@link Building} or {@link Infrastructure}
     * @throws OntologyException
     *             processing exception
     */
    public void updateBuilding(GeoObject building) throws OntologyException;

    /**
     * Adds a new rule to the ontology
     * 
     * @param ruleName
     *            name of the rule
     * @param ruleContent
     *            content of the rule
     * @throws OntologyException
     */
    public void addSwrlRule(String ruleName, String ruleContent) throws OntologyException;

    /**
     * Returns rule identified by its name from the ontology
     * 
     * @param ruleName
     *            name of the rule
     * @return rule definition
     * @throws OntologyException
     */
    public String getSwrlRule(String ruleName) throws OntologyException;

    /**
     * Removes rule identified by its name from the ontology
     * 
     * @param ruleName
     *            name of the rule
     * @throws OntologyException
     */
    public void removeSwrlRule(String ruleName) throws OntologyException;

    /**
     * Runs SWRL engine on existing model
     */
    public void runSwrlEngine() throws OntologyException;

    /**
     * Returns list of SWRL rules defined in the ontology
     * 
     * @return list of SWRL rules. If there are no rules, empty list will be returned
     */
    public List<String> getSwrlRules() throws OntologyException;

    /**
     * Saves current ontology model to file
     * 
     * @param fileName
     *            location of the file, where ontology should be saved
     */
    public void saveOntology(String fileName) throws OntologyException;

    /**
     * Loads ontology model from the file
     * 
     * @param fileName
     *            location of the file containing ontology
     */
    public void loadOntology(String fileName) throws OntologyException;

    /**
     * Returns a list of all ontologies from the repository
     * @return list of strings representing ontology names
     * @throws OntologyException
     */
    public List<String> getListOfOntologies() throws OntologyException;

    /**
     * Returns name of the currently loaded ontology
     * @return name of the ontology
     * @throws OntologyException
     */
    public String getCurrentOntology() throws OntologyException;
}
