package itti.com.pl.arena.cm.exception;

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

    ONTOLOGY_CANNOT_LOAD(200, "Failed to load ontology '%s'"),
    ONTOLOGY_CLASS_DOESNT_EXIST(201, "Class '%s' not found in ontology"),
    ONTOLOGY_COULD_NOT_ADD_INSTANCE(202, "Could not add instance '%s' of class '%s' to the ontology"),
    ONTOLOGY_EMPTY_INSTANCE_NAME(203, "Empty instance name was provided"),
    ONTOLOGY_INSTANCE_NOT_FOUND(204, "Instance '%s' not found in the ontology"),
    ONTOLOGY_EMPTY_LOCATION_OBJECT(205, "Null Location was provided"),
    ONTOLOGY_EMPTY_PARKING_ID_OBJECT(206, "Null parkingId was provided"),
    ONTOLOGY_INSTANCE_IS_NOT_A_PARKING(207, "Provided object '%s' does not represent a parking lot"),
    ONTOLOGY_EMPTY_VALUE_PROVIDED(208, "Null or empty value provided for ontology variable '%s' was provided"),

    SPRING_HELPER_EMPTY_RESOURCE_LOCATION(501, "Empty resource provided"),
    SPRING_HELPER_COULD_NOT_OPEN_RESOURCE(502, "Could not open resource '%s'. Details: %s"),
    SPRING_HELPER_COULD_NOT_READ_RESOURCE(503, "Could not read resource '%s'. Details: %s"),

    PERSISTENCE_CANNOT_INITIALIZE(600, "Could not initialize database. Details: %s"),
    PERSISTENCE_CANNOT_LOAD_DRIVER(601, "Failed to load JDBC driver. Details: %s"),
    PERSISTENCE_CANNOT_LOAD_PROPERTIES(602, "Failed to load DAO properties."),
    PERSISTENCE_CANNOT_CLOSE_CONNECTION(603, "Could not close connection. Details: %s"),
    PERSISTENCE_CANNOT_PREPARE_TIMESTAMP(604, "Could not parse provided timestamp '%d' into DB-supported form. Details: %s"),
    PERSISTENCE_CANNOT_CREATE_RECORD(605, "Could not add new entry to the database. Details: %s"),
    PERSISTENCE_CANNOT_READ_LAST_RECORD(606, "Could not read last entry from the database. Details: %s"),
    PERSISTENCE_CANNOT_READ_RECORDS(607, "Could not read list of entries from the database. Details: %s"),
    PERSISTENCE_CANNOT_DELETE_RECORD(608, "Could not delete entry from the database. Details: %s"),
    PERSISTENCE_CANNOT_DEREGISTER_DRIVER(609, "Failed to deregister JDBC driver. Details: %s"),

    JMS_SERVER_IP_NOT_PROVIDED(700, "ServerIpAddress not provided"),
    JMS_CLIENT_IP_NOT_PROVIDED(700, "ClientIpAddress not provided"),
    JMS_CLIENT_PORT_NOT_PROVIDED(700, "ClientPort not provided"),

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