package itti.com.pl.arena.cm.service;

import eu.arena_fp7._1.BooleanNamedValue;

/**
 * Constants used by the ContextModule service Contains names of all parameters used during communication with other
 * modules
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
         * Service name used by the {@link ContextModule} 'updatePlatform' method
         */
        updatePlatform,

        /**
         * Service name used by the {@link ContextModule} 'getPlatforms' method
         */
        getPlatforms,

        /**
         * Service name used by the {@link ContextModule} 'getGISData' method
         */
        getGISData,
        /**
         * Service name used by the {@link ContextModule} extended 'getGISData' method
         */
        getGISDataExt,
        /**
         * Service name used by the {@link ContextModule} 'updateGISData' method
         */
        updateGISData,
        /**
         * Service name used by the {@link ContextModule} 'getGeoportalData' method
         */
        getGeoportalData,
        
        /**
         * Service name used by the {@link ContextModule} 'getCameraFieldOfView' method
         */
        getCameraFieldOfView,

        /**
         * Service name used by the {@link ContextModule} 'defineZone' method
         */
        defineZone,

        /**
         * Service name used by the {@link ContextModule} 'getZone' method
         */
        getZone,

        ;
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
         * Platform size
         */
        Width, Height, Length,

        /**
         * Properties of the Camera node
         */
        CameraId, CameraPosition, CameraType, CameraAngleX, CameraAngleY, Bearing, ;
    }

    public enum ContextModuleResponseProperties{
        /**
         * name of the status property in the {@link BooleanNamedValue} response
         */
        Status
    }
}
