package itti.com.pl.arena.cm;

/**
 * Messages used by all exceptions, which may be thrown by this module
 * 
 * @author cm-admin
 * 
 */
public enum ErrorMessages {

    GEOPORTAL_SERVICE_NOT_PROVIDED(100, "Geoportal service not specified"), 
    GEOPORTAL_REQUEST_DATA_NOT_PROVIDED(101, "Request data not provided"), 
    GEOPORTAL_NULL_START_LOCATION(102, "StartLocation not provided"), 
    GEOPORTAL_NULL_END_LOCATION(103, "StartLocation not provided"),
    GEOPORTAL_REQUEST_FAILED(104, "Could not retrieve data from the Geoportal service"), 
    GEOPORTAL_CANNOT_PREPARE_REQUEST_URL(105, "Could not preare valid request URL. Details: %s"), 
    GEOPORTAL_SERIALIZE_NULL_OBJECT_PROVIDED(120, "Could not serialize null object into valid JSON request"), 
    GEOPORTAL_DESERIALIZE_NULL_JSON_PROVIDED(121, "Could not deserialize null string into valid object"), 
    GEOPORTAL_DESERIALIZE_INVALID_JSON_PROVIDED(122, "Could not deserialize given string into valid object"), 
    GEOPORTAL_REQUEST_NULL_OBJECT_PROVIDED(123, "Could not create valid request from null object"),

    ONTOLOGY_CANNOT_LOAD(200, "Failed to load ontlogy '%s' Details: '%s'"),

    SPRING_HELPER_EMPTY_RESOURCE_LOCATION(501, "Empty resource provided"), 
    SPRING_HELPER_COULD_NOT_OPEN_RESOURCE(502, "Could not open resource '%s'. Details: %s"),
    SPRING_HELPER_COULD_NOT_READ_RESOURCE(503, "Could not read resource '%s'. Details: %s"), 

    ;

    /*
     * Unique ID of the error
     */
    private int id;

    /*
     * Descriptive error description
     */
    private String message;

    /**
     * Default constructor
     * 
     * @param message
     *            message to be displayed
     */
    private ErrorMessages(int id, String message) {
	this.id = id;
	this.message = message;
    }

    /**
     * Returns ID
     * 
     * @return id
     */
    public int getId() {
	return id;
    }

    /**
     * Returns message
     * 
     * @return message
     */
    public String getMessage() {
	return message;
    }
}