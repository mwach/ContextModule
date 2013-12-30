package itti.com.pl.arena.cm.persistence.jdbc;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.SpringHelper;

import java.util.Properties;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

public final class JdbcProperties {

    public static String PROPERTY_URL = "url";
    public static String PROPERTY_DRIVER = "driver";
    public static String PROPERTY_USER = "user";
    public static String PROPERTY_PASSWORD = "password";
    public static String PROPERTY_TIMESTAMP = "timestamp";

    public static String QUERY_LOCATION_CREATE = "LOCATION_CREATE";
    public static String QUERY_LOCATION_INSERT = "LOCATION_INSERT";
    public static String QUERY_LOCATION_READ_LAST = "LOCATION_READ_LAST";
    public static String QUERY_LOCATION_READ = "LOCATION_READ";
    public static String QUERY_LOCATION_DELETE = "LOCATION_DELETE";

    @Required
    public void setPropertiesFile(String propertiesFile) {
	try {
	    this.jdbcProperties = SpringHelper.loadPropertiesFromResource(propertiesFile);
	} catch (IOHelperException e) {
	    throw new BeanInitializationException(ErrorMessages.PERSISTENCE_CANNOT_LOAD_PROPERTIES.getMessage(), e);
	}
    }

    @Required
    public void setSchemaFile(String schemaFile) {
	try {
	    this.queryProperties = SpringHelper.loadPropertiesFromResource(schemaFile);
	} catch (IOHelperException e) {
	    throw new BeanInitializationException(ErrorMessages.PERSISTENCE_CANNOT_LOAD_PROPERTIES.getMessage(), e);
	}
    }

    private Properties jdbcProperties = null;
    private Properties queryProperties = null;

    public String getConnectionPropertyValue(String property) {
	return jdbcProperties.getProperty(property);
    }

    public String getQueryPropertyValue(String property) {
	return queryProperties.getProperty(property);
    }
}
