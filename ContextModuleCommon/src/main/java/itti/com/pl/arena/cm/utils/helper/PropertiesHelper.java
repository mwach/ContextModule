package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.ErrorMessages;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Utilities for properties
 * 
 * @author mawa
 * 
 */
public final class PropertiesHelper {

    /**
     * Loads properties from file into {@link Properties} object
     * 
     * @param fileName
     *            name of the file with properties
     * @return {@link Properties} object
     * @throws IOHelperException
     */
    public static Properties loadProperties(String fileName) throws IOHelperException {

        Properties props = null;
        InputStream propsInputStream = null;

        // check, if properties file name was provided
        if (!StringHelper.hasContent(fileName)) {
            throw new IOHelperException(ErrorMessages.PROPERTIES_HELPER_NO_FILE_PROVIDED);
        }
        // try to read properties from file
        try {
            propsInputStream = new FileInputStream(fileName);
            props = new Properties();
            props.load(propsInputStream);
        } catch (IOException | RuntimeException exc) {
            throw new IOHelperException(exc, ErrorMessages.PROPERTIES_HELPER_COULD_NOT_PARSE, fileName, exc.getLocalizedMessage());
        } finally {
            // always try to close the properties data stream
            IOHelper.closeStream(propsInputStream);
        }
        return props;
    }

    /**
     * Loads properties from file into Java {@link Map} object
     * 
     * @param fileName
     *            name of the file with properties
     * @return {@link Map} object
     * @throws IOHelperException
     */
    public static Map<String, String> loadPropertiesAsMap(String propertiesFileName) throws IOHelperException {

        // first, load properties into object
        Properties props = loadProperties(propertiesFileName);

        // later, convert it into map
        Map<String, String> propsAsMap = new HashMap<String, String>();
        for (Entry<Object, Object> entry : props.entrySet()) {
            propsAsMap.put(String.valueOf(entry.getKey()), entry.getValue() == null ? null : String.valueOf(entry.getValue()));
        }
        return propsAsMap;
    }

    /**
     * Returns value of property specified by its name from properties object
     * 
     * @param properties
     *            {@link Properties}
     * @param propertyName
     *            name of the property
     * @param defaultValue
     *            value to be returned, if searched one was not found
     * @return value of the property, or default value if not found
     */
    public static String getPropertyAsString(Properties properties, String propertyName, String defaultValue) {
        // check, if property with given name was found in provided properties
        if (properties == null || propertyName == null || !properties.containsKey(propertyName)) {
            return defaultValue;
        }
        // get the property value
        return properties.getProperty(propertyName);
    }

    /**
     * Returns value of property specified by its name from properties object
     * 
     * @param properties
     *            {@link Properties}
     * @param propertyName
     *            name of the property
     * @param defaultValue
     *            value to be returned, if searched one was not found
     * @return value of the property, or default value if not found
     */
    public static int getPropertyAsInteger(Properties properties, String propertyName, int defaultValue) {
        // check, if property with given name was found in provided properties
        String propertyValue = getPropertyAsString(properties, propertyName, null);
        if (propertyValue == null) {
            return defaultValue;
        }
        // get the property value
        Integer value = NumbersHelper.getIntegerFromString(properties.getProperty(propertyName));
        // if cannot be parsed into integer, return default value
        if (value == null) {
            return defaultValue;
        } else {
            return value.intValue();
        }
    }

    /**
     * Returns value of the boolean property specified by its name from properties object
     * 
     * @param properties
     *            {@link Properties}
     * @param propertyName
     *            name of the property
     * @param defaultValue
     *            value to be returned, if searched one was not found
     * @return value of the property, or default value if not found
     */
    public static boolean getPropertyAsBoolean(Properties properties, String propertyName, boolean defaultValue) {
        // check, if property with given name was found in provided properties
        String propertyValue = getPropertyAsString(properties, propertyName, null);
        if (propertyValue == null) {
            return defaultValue;
        }
        // get the property value
        String value = properties.getProperty(propertyName);
        // if cannot be parsed into boolean, return default value
        if (StringHelper.equalsIgnoreCase(value, Boolean.TRUE.toString())) {
            return true;
        } else if (StringHelper.equalsIgnoreCase(value, Boolean.FALSE.toString())) {
            return false;
        }
        return defaultValue;
    }
}
