package itti.com.pl.arena.cm.server.persistence.jdbc;

import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.utils.helpers.SpringHelper;
import itti.com.pl.arena.cm.server.utils.helpers.SpringHelperException;

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
        } catch (SpringHelperException e) {
            throw new BeanInitializationException(ErrorMessages.PERSISTENCE_CANNOT_LOAD_PROPERTIES.getMessage(), e);
        }
    }

    private Properties jdbcProperties = null;

    public String getConnectionPropertyValue(String property) {
        return jdbcProperties.getProperty(property);
    }
}
