package itti.com.pl.arena.cm.utils.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Utilities for properties
 * @author mawa
 *
 */
public final class PropertiesHelper {

	/**
	 * Loads properties from file into Java {@link Properties} object
	 * @param fileName name of the file with properties
	 * @return {@link Properties} object
	 * @throws IOHelperException
	 */
	public static Properties loadProperties(String fileName) throws IOHelperException{

		Properties props = null;
		InputStream propsInputStream = null;
		if(!StringHelper.hasContent(fileName)){
			throw new IOHelperException("Empty properties file name provided");
		}
		try{
			propsInputStream = new FileInputStream(fileName);
			props = new Properties();
			props.load(propsInputStream);
		}catch(IOException | RuntimeException exc)
		{
			throw new IOHelperException(exc, "Failed to load properties file '%s' Details: '%s'", fileName, exc.getLocalizedMessage());
		}finally{
			IOHelper.closeStream(propsInputStream);
		}
		return props;
	}

	/**
	 * Loads properties from file into Java {@link Map} object
	 * @param fileName name of the file with properties
	 * @return {@link Map} object
	 * @throws IOHelperException
	 */
	public static Map<String, String> loadPropertiesAsMap(String propertiesFileName) throws IOHelperException{

		//first, load properties into object
		Properties props = loadProperties(propertiesFileName);

		//later, convert it into map
		Map<String, String> propsAsMap = new HashMap<String, String>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			propsAsMap.put(String.valueOf(entry.getKey()), entry.getValue() == null ? null : String.valueOf(entry.getValue()));
		}
		return propsAsMap;
	}

	public static String getPropertyAsString(Properties properties, String propertyName, String defaultValue)
	{
		if(properties == null || !StringHelper.hasContent(propertyName))
		{
			return defaultValue;
		}
		return properties.getProperty(propertyName);
	}

	public static int getPropertyAsInteger(Properties properties, String propertyName, int defaultValue)
	{
		if(properties == null || !StringHelper.hasContent(propertyName))
		{
			return defaultValue;
		}
		String value = properties.getProperty(propertyName);
		try
		{
			int parsedValue = Integer.parseInt(value);
			return parsedValue;
		}catch(RuntimeException exc){
		}
		return defaultValue;
	}

	public static boolean getPropertyAsBoolean(Properties properties, String propertyName, boolean defaultValue)
	{
		if(properties == null || !StringHelper.hasContent(propertyName))
		{
			return defaultValue;
		}
		String value = properties.getProperty(propertyName);
		if(StringHelper.equalsIgnoreCase(value, Boolean.TRUE.toString()))
		{
			return true;
		}
		else if(StringHelper.equalsIgnoreCase(value, Boolean.FALSE.toString()))
		{
			return false;
		}
		return defaultValue;
	}
}
