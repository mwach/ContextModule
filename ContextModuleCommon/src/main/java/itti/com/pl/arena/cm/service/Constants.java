package itti.com.pl.arena.cm.service;

/**
 * Constants used by the ContextModule
 * 
 * @author mawa
 * 
 */
public final class Constants {

	public enum ContextModuleRequests {
		/**
		 * Service name used by the {@link ContextModule} 'getPlatform' method
		 */
		getPlatform,

		/**
		 * Service name used by the {@link ContextModule} 'getPlatforms' method
		 */
		getPlatforms,

		/**
		 * Service name used by the {@link ContextModule} 'getGISData' method
		 */
		getGISData,
		/**
		 * Service name used by the {@link ContextModule} 'getGeoportalData' method
		 */
		getGeoportalData;
	}

	public enum ContextModuleRequestProperties {
		/**
		 * ID of the object
		 */
		Id,

		/**
		 * Location of the platform
		 */
		Location,

		/**
		 * Instance of the Camera node
		 */
		CameraId,
		CameraPosition,
		CameraType,
		CameraAngleX,
		CameraAngleY,
		
		Bearing,
		;

	}

}
