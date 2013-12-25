package itti.com.pl.arena.cm.utils.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

public class SpringHelper {

	public static final Properties loadPropertiesFromResource(String resourceLocation) throws IOHelperException{

		Properties props = null;
		InputStream propsInputStream = null;
		try{
			propsInputStream = getResourceInputStream(resourceLocation);
			props = new Properties();
			props.load(propsInputStream);
		}catch(IOException e)
		{
			throw new IOHelperException(e, "Failed to load resource '%s' Details: '%s'", resourceLocation, e.getLocalizedMessage());
		}finally{
			IOHelper.closeStream(propsInputStream);
		}
		return props;
	}

	public static InputStream getResourceInputStream(String resourceLocation) throws IOHelperException {

		if(!StringHelper.hasContent(resourceLocation)){
			throw new IOHelperException("Empty resource provided");
		}

		InputStream stream = null;
		try{
			stream = new ClassPathResource(resourceLocation).getInputStream();
		}catch(IOException exc){
			throw new IOHelperException(exc, "Could not access resource: %s. Details: %s", resourceLocation, exc.getLocalizedMessage());
		}
		return stream;
	}

}
