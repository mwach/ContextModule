package itti.com.pl.arena.cm.persistence.jdbc;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.persistence.Persistence;
import itti.com.pl.arena.cm.persistence.PersistenceException;
import itti.com.pl.arena.cm.utils.helpers.DateTimeHelper;
import itti.com.pl.arena.cm.utils.helpers.DateTimeHelperException;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.SpringHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Required;

/**
 * JDBC implementation of the ContextModule persistence layer
 * 
 * @author cm-admin
 * 
 */
public class JdbcPersistence implements Persistence {

    private final static String PROPERTY_URL = "url";
    private final static String PROPERTY_DRIVER = "driver";
    private final static String PROPERTY_USER = "user";
    private final static String PROPERTY_PASSWORD = "password";
    private final static String PROPERTY_TIMESTAMP = "timestamp";

    private final static String QUERY_LOCATION_CREATE = "LOCATION_CREATE";
    private final static String QUERY_LOCATION_INSERT = "LOCATION_INSERT";
    private final static String QUERY_LOCATION_READ_LAST = "LOCATION_READ_LAST";
    private final static String QUERY_LOCATION_READ = "LOCATION_READ";
    private final static String QUERY_LOCATION_DELETE = "LOCATION_DELETE";

    private String propertiesFile;
    private String schemaFile;

    private Properties queryProperties = null;
    private Properties jdbcProperties = null;
    private String timestampFormat = null;

    private Connection connection = null;
    private LocationRowProcessor locationRowProcessor = new LocationRowProcessor();

    private String getPropertiesFile() {
	return propertiesFile;
    }

    @Required
    public void setPropertiesFile(String propertiesFile) {
	this.propertiesFile = propertiesFile;
    }

    private String getSchemaFile() {
	return schemaFile;
    }

    @Required
    public void setSchemaFile(String schemaFile) {
	this.schemaFile = schemaFile;
    }

    @Override
    public void init() throws PersistenceException {

	boolean initialized = false;
	try {
	    jdbcProperties = SpringHelper
		    .loadPropertiesFromResource(getPropertiesFile());
	    queryProperties = SpringHelper
		    .loadPropertiesFromResource(getSchemaFile());

	    Class.forName(jdbcProperties.getProperty(PROPERTY_DRIVER));

	    connection = DriverManager.getConnection(
		    jdbcProperties.getProperty(PROPERTY_URL),
		    jdbcProperties.getProperty(PROPERTY_USER),
		    jdbcProperties.getProperty(PROPERTY_PASSWORD));
	    timestampFormat = jdbcProperties.getProperty(PROPERTY_TIMESTAMP);

	    prepareTables();
	    initialized = true;

	} catch (SQLException e) {
	    throw new PersistenceException(e,
		    ErrorMessages.PERSISTENCE_CANNOT_INITIALIZE,
		    e.getLocalizedMessage());
	} catch (ClassNotFoundException e) {
	    throw new PersistenceException(e,
		    ErrorMessages.PERSISTENCE_CANNOT_LOAD_DRIVER, e.getLocalizedMessage());
	} catch (IOHelperException e) {
	    throw new PersistenceException(e,
		    ErrorMessages.PERSISTENCE_CANNOT_LOAD_PROPERTIES,
		    e.getLocalizedMessage());
	} finally {
	    if (!initialized) {
		shutdown();
	    }
	}
    }

    private void prepareTables() throws SQLException {
	Statement statement = null;
	try {
	    statement = connection.createStatement();
	    statement.execute(queryProperties
		    .getProperty(QUERY_LOCATION_CREATE));
	} finally {
	    if (statement != null) {
		statement.close();
	    }
	}
    }

    @Override
    public void shutdown() throws PersistenceException {
	if (connection != null) {
	    try {
		connection.close();
	    } catch (SQLException exc) {
		throw new PersistenceException(exc,
			ErrorMessages.PERSISTENCE_CANNOT_CLOSE_CONNECTION,
			exc.getLocalizedMessage());
	    }
	}
    }

    @Override
    public void create(Location location) throws PersistenceException {

	QueryRunner runner = new QueryRunner();
	try {
	    String timestampString = DateTimeHelper.formatTime(location.getTime(),
		    timestampFormat);

	    runner.update(connection,
		    queryProperties.getProperty(QUERY_LOCATION_INSERT), 1,
		    location.getLongitude(), location.getLatitude(),
		    location.getAltitude(), location.getBearing(),
		    timestampString, location.getSpeed(),
		    location.getAccuracy());
	    connection.commit();
	} catch (DateTimeHelperException exc) {
		throw new PersistenceException(exc,
			ErrorMessages.PERSISTENCE_CANNOT_PREPARE_TIMESTAMP,
			location.getTime(),
			exc.getLocalizedMessage());
	} catch (SQLException exc) {
	    throw new PersistenceException(
		    exc,
		    ErrorMessages.PERSISTENCE_CANNOT_CREATE_RECORD,
		    exc.getLocalizedMessage());
	}
    }

    @Override
    public Location readLastPosition() throws PersistenceException {

	Location Location = null;

	QueryRunner runner = new QueryRunner();
	ResultSetHandler<Location> rsHandler = new BeanHandler<>(
		Location.class, locationRowProcessor);
	try {
	    Location = runner.query(connection,
		    queryProperties.getProperty(QUERY_LOCATION_READ_LAST),
		    rsHandler);
	} catch (SQLException exc) {
	    throw new PersistenceException(
		    exc,
		    ErrorMessages.PERSISTENCE_CANNOT_READ_LAST_RECORD,
		    exc.getLocalizedMessage());
	}
	return Location;
    }

    @Override
    public List<Location> readHistory(long timestamp)
	    throws PersistenceException {

	List<Location> retList = null;
	QueryRunner runner = new QueryRunner();
	ResultSetHandler<List<Location>> rsHandler = new BeanListHandler<>(
		Location.class, locationRowProcessor);
	try {

	    String timestampString = DateTimeHelper.formatTime(timestamp,
		    timestampFormat);

	    retList = runner.query(connection,
		    queryProperties.getProperty(QUERY_LOCATION_READ),
		    rsHandler, timestampString);
	} catch (DateTimeHelperException exc) {
	    throw new PersistenceException(exc, ErrorMessages.PERSISTENCE_CANNOT_PREPARE_TIMESTAMP, timestamp, exc.getLocalizedMessage());
	} catch (SQLException exc) {
	    throw new PersistenceException(
		    exc,
		    ErrorMessages.PERSISTENCE_CANNOT_READ_RECORDS,
		    exc.getLocalizedMessage());
	}
	return retList;
    }

    @Override
    public void delete(long timestamp) throws PersistenceException {

	QueryRunner runner = new QueryRunner();
	try {
	    String timestampString = DateTimeHelper.formatTime(timestamp,
		    timestampFormat);

	    runner.update(connection,
		    queryProperties.getProperty(QUERY_LOCATION_DELETE),
		    timestampString);
	} catch (DateTimeHelperException exc) {
	    throw new PersistenceException(exc, ErrorMessages.PERSISTENCE_CANNOT_PREPARE_TIMESTAMP, timestamp, exc.getLocalizedMessage());
	} catch (SQLException exc) {
	    throw new PersistenceException(
		    exc,
		    ErrorMessages.PERSISTENCE_CANNOT_DELETE_RECORD,
		    exc.getLocalizedMessage());
	}
    }

    private static class LocationRowProcessor implements RowProcessor {

	@Override
	public Object[] toArray(ResultSet arg0) throws SQLException {
	    throw new SQLException("'toArray' is not supported");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T toBean(ResultSet rs, Class<T> arg1) throws SQLException {
	    Location Location = new Location(rs.getDouble("longitude"),
		    rs.getDouble("latitude"), rs.getDouble("altitude"),
		    rs.getInt("bearing"), rs.getDouble("speed"), rs
			    .getTimestamp("period").getTime(),
		    rs.getDouble("accuracy"));
	    return (T) Location;
	}

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> clazz)
		throws SQLException {

	    List<T> list = null;
	    while (rs.next()) {
		if (list == null) {
		    list = new ArrayList<>();
		}
		T Location = toBean(rs, clazz);
		list.add(Location);
	    }
	    return list;
	}

	@Override
	public Map<String, Object> toMap(ResultSet arg0) throws SQLException {
	    throw new SQLException("'toMap' is not supported");
	}

    }
}
