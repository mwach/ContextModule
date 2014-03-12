package itti.com.pl.arena.cm.persistence.jdbc;

import itti.com.pl.arena.cm.exception.ErrorMessages;
import itti.com.pl.arena.cm.utils.helper.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.SpringHelper;

import java.util.Properties;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

public final class JdbcProperties {

    public static final String PROPERTY_URL = "url";
    public static final String PROPERTY_DRIVER = "driver";
    public static final String PROPERTY_USER = "user";
    public static final String PROPERTY_PASSWORD = "password";
    public static final String PROPERTY_TIMESTAMP = "timestamp";

    @Required
    public void setPropertiesFile(String propertiesFile) {
        try {
            this.jdbcProperties = SpringHelper.loadPropertiesFromResource(propertiesFile);
        } catch (IOHelperException e) {
            throw new BeanInitializationException(ErrorMessages.PERSISTENCE_CANNOT_LOAD_PROPERTIES.getMessage(), e);
        }
    }

    private Properties jdbcProperties = null;

    public String getConnectionPropertyValue(String property) {
        return jdbcProperties.getProperty(property);
    }
}
