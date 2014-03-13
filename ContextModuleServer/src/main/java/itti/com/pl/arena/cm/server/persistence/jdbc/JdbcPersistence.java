package itti.com.pl.arena.cm.server.persistence.jdbc;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.persistence.Persistence;
import itti.com.pl.arena.cm.server.persistence.PersistenceException;
import itti.com.pl.arena.cm.server.service.Service;
import itti.com.pl.arena.cm.utils.helper.DateTimeHelper;
import itti.com.pl.arena.cm.utils.helper.DateTimeHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

/**
 * JDBC implementation of the ContextModule persistence layer
 * 
 * @author cm-admin
 * 
 */
public class JdbcPersistence implements Persistence, Service {

    private static final String QUERY_LOCATION_CREATE = "DROP TABLE IF EXISTS OBJ_LOCATION; CREATE TABLE OBJ_LOCATION(id identity, objectId varchar(50) not null, longitude double not null, latitude double not null, altitude double not null, bearing integer not null, period timestamp not null, speed double not null, accuracy double not null, PRIMARY KEY (id))";
    private static final String QUERY_LOCATION_INSERT = "INSERT into OBJ_LOCATION(objectId, longitude, latitude, altitude, bearing, period, speed, accuracy) values (?,?,?,?,?,?,?,?)";
    private static final String QUERY_LOCATION_GET_LAST = "SELECT longitude, latitude, altitude, bearing, period, speed, accuracy from OBJ_LOCATION where objectId = ? and period = (select max(period) from OBJ_LOCATION where objectId = ?)";
    private static final String QUERY_LOCATION_GET = "SELECT longitude, latitude, altitude, bearing, period, speed, accuracy from OBJ_LOCATION where objectId = ? and period >=? order by period asc";
    private static final String QUERY_LOCATION_DELETE = "DELETE from OBJ_LOCATION where objectId = ? and period <=?";

    private JdbcProperties properties = null;

    @Required
    public void setJdbcProperties(JdbcProperties properties) {
        this.properties = properties;
    }

    private JdbcProperties getProperties() {
        return properties;
    }

    private String getConnectionPropertyValue(String property) {
        return getProperties().getConnectionPropertyValue(property);
    }

    private String timestampFormat = null;

    private Connection connection = null;
    private LocationRowProcessor locationRowProcessor = new LocationRowProcessor();

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.Service#init()
     */
    @Override
    public void init() {

        boolean initialized = false;
        try {
            Class.forName(getConnectionPropertyValue(JdbcProperties.PROPERTY_DRIVER));

            connection = DriverManager.getConnection(getConnectionPropertyValue(JdbcProperties.PROPERTY_URL),
                    getConnectionPropertyValue(JdbcProperties.PROPERTY_USER),
                    getConnectionPropertyValue(JdbcProperties.PROPERTY_PASSWORD));
            timestampFormat = getConnectionPropertyValue(JdbcProperties.PROPERTY_TIMESTAMP);

            prepareTables();
            initialized = true;

        } catch (SQLException e) {
            throw new BeanInitializationException("Could not initialize persistance service", new PersistenceException(
                    ErrorMessages.PERSISTENCE_CANNOT_INITIALIZE, e, e.getLocalizedMessage()));
        } catch (ClassNotFoundException e) {
            throw new BeanInitializationException("Could not initialize persistance service", new PersistenceException(
                    ErrorMessages.PERSISTENCE_CANNOT_LOAD_DRIVER, e, e.getLocalizedMessage()));
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
            statement.execute(QUERY_LOCATION_CREATE);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.Service#shutdown()
     */
    @Override
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException exc) {
                LogHelper.exception(
                        JdbcPersistence.class,
                        "shutdown",
                        "Could not shutdown JDBC service",
                        new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_CLOSE_CONNECTION, exc, exc
                                .getLocalizedMessage()));
            }
        }

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException exc) {
                LogHelper.exception(
                        JdbcPersistence.class,
                        "shutdown",
                        "Could not deregister the JDBC driver",
                        new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_DEREGISTER_DRIVER, exc, exc
                                .getLocalizedMessage()));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.persistence.Persistence#create(java.lang.String, itti.com.pl.arena.cm.dto.Location)
     */
    @Override
    public void create(String platformId, Location location) throws PersistenceException {

        QueryRunner runner = new QueryRunner();
        try {
            String timestampString = DateTimeHelper.formatTime(location.getTime(), timestampFormat);

            runner.update(connection, QUERY_LOCATION_INSERT, platformId, location.getLongitude(), location.getLatitude(),
                    location.getAltitude(), location.getBearing(), timestampString, location.getSpeed(), location.getAccuracy());
            connection.commit();
        } catch (DateTimeHelperException exc) {
            throw new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_PREPARE_TIMESTAMP, exc, location.getTime(),
                    exc.getLocalizedMessage());
        } catch (SQLException exc) {
            throw new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_CREATE_RECORD, exc, exc.getLocalizedMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.persistence.Persistence#getLastPosition(java.lang.String)
     */
    @Override
    public Location getLastPosition(String platformId) throws PersistenceException {

        Location Location = null;

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<Location> rsHandler = new BeanHandler<>(Location.class, locationRowProcessor);
        try {
            Location = runner.query(connection, QUERY_LOCATION_GET_LAST, rsHandler, platformId, platformId);
        } catch (SQLException exc) {
            throw new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_READ_LAST_RECORD, exc, exc.getLocalizedMessage());
        }
        return Location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.persistence.Persistence#getHistory(java.lang.String, long)
     */
    @Override
    public List<Location> getHistory(String platformId, long timestamp) throws PersistenceException {

        List<Location> retList = null;
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<Location>> rsHandler = new BeanListHandler<>(Location.class, locationRowProcessor);
        try {

            String timestampString = DateTimeHelper.formatTime(timestamp, timestampFormat);

            retList = runner.query(connection, QUERY_LOCATION_GET, rsHandler, platformId, timestampString);
        } catch (DateTimeHelperException exc) {
            throw new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_PREPARE_TIMESTAMP, exc, timestamp,
                    exc.getLocalizedMessage());
        } catch (SQLException exc) {
            throw new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_READ_RECORDS, exc, exc.getLocalizedMessage());
        }
        return retList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see itti.com.pl.arena.cm.persistence.Persistence#delete(long)
     */
    @Override
    public void delete(String platformId, long timestamp) throws PersistenceException {

        QueryRunner runner = new QueryRunner();
        try {
            String timestampString = DateTimeHelper.formatTime(timestamp, timestampFormat);

            runner.update(connection, QUERY_LOCATION_DELETE, platformId, timestampString);
        } catch (DateTimeHelperException exc) {
            throw new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_PREPARE_TIMESTAMP, exc, timestamp,
                    exc.getLocalizedMessage());
        } catch (SQLException exc) {
            throw new PersistenceException(ErrorMessages.PERSISTENCE_CANNOT_DELETE_RECORD, exc, exc.getLocalizedMessage());
        }
    }

    /**
     * Helper class used to parse DB results into {@link Location} objects
     * 
     * @author cm-admin
     * 
     */
    private static class LocationRowProcessor implements RowProcessor {

        @Override
        public Object[] toArray(ResultSet arg0) throws SQLException {
            throw new SQLException("'toArray' is not supported");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T toBean(ResultSet rs, Class<T> arg1) throws SQLException {
            Location location = new Location(rs.getDouble("longitude"), rs.getDouble("latitude"), rs.getInt("bearing"),
                    rs.getDouble("altitude"), rs.getDouble("accuracy"), rs.getDouble("speed"), rs.getTimestamp("period")
                            .getTime());
            return (T) location;
        }

        @Override
        public <T> List<T> toBeanList(ResultSet rs, Class<T> clazz) throws SQLException {

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
