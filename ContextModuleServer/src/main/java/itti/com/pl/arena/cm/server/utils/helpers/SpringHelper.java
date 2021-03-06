package itti.com.pl.arena.cm.server.utils.helpers;

import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.utils.helper.IOHelper;
import itti.com.pl.arena.cm.utils.helper.IOHelperException;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

/**
 * Spring framework based utilities
 * 
 * @author cm-admin
 * 
 */
public class SpringHelper {

    /**
     * Loads properties from given resource
     * 
     * @param resourceLocation
     *            location of the resource
     * @return {@link Properties}
     * @throws IOHelperException
     *             could not find or read properties from given location
     */
    public static final Properties loadPropertiesFromResource(String resourceLocation) throws SpringHelperException {

        Properties props = null;
        InputStream propsInputStream = null;
        try {
            // open data stream from given resource
            propsInputStream = getResourceInputStream(resourceLocation);
            props = new Properties();
            // load properties from stream
            props.load(propsInputStream);
        } catch (IOException exc) {
            throw new SpringHelperException(exc, ErrorMessages.SPRING_HELPER_COULD_NOT_READ_RESOURCE, resourceLocation,
                    exc.getLocalizedMessage());
        } finally {
            // always try to close the stream
            IOHelper.closeStream(propsInputStream);
        }
        return props;
    }

    /**
     * Tries to get a data stream from given resource
     * 
     * @param resourceLocation
     *            location of the resource
     * @return data stream
     * @throws IOHelperException
     *             could not find or open given resource
     */
    public static InputStream getResourceInputStream(String resourceLocation) throws SpringHelperException {

        // check, if path to the resource was provided
        if (!StringHelper.hasContent(resourceLocation)) {
            throw new SpringHelperException(ErrorMessages.SPRING_HELPER_EMPTY_RESOURCE_LOCATION);
        }

        // try to open the stream using spring utility classes
        InputStream stream = null;
        try {
            stream = new ClassPathResource(resourceLocation).getInputStream();
        } catch (IOException exc) {
            throw new SpringHelperException(exc, ErrorMessages.SPRING_HELPER_COULD_NOT_OPEN_RESOURCE, resourceLocation,
                    exc.getLocalizedMessage());
        }
        return stream;
    }

}
