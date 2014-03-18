package itti.com.pl.arena.cm.service;

import eu.arena_fp7._1.BooleanNamedValue;

/**
 * Constants used by the ContextModule service Contains names of all parameters used during communication with other
 * modules
 * 
 * @author mawa
 * 
 */
public final class MessageConstants {

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
         * Service name used by the {@link ContextModule} 'getParkingLot' method
         */
        getParkingLot,
        /**
         * Service name used by the {@link ContextModule} 'updateParkingLot' method
         */
        updateParkingLot,
        /**
         * Service name used by the {@link ContextModule} 'getPlatforms' method
         */
        getPlatforms,

        /**
         * Service name used by the {@link ContextModule} 'getGISData' method
         */
        getGISData,
        /**
         * Service name used by the {@link ContextModule} extended 'getGISDataExt' method
         */
        getGISDataExt,
        /**
         * Service name used by the {@link ContextModule} 'getGeoportalData' method
         */
        getGeoportalData,
        
        /**
         * Service name used by the {@link ContextModule} 'getCameraFieldOfView' method
         */
        getPlatformNeighborhood,

        /**
         * Service name used by the {@link ContextModule} 'updateZone' method
         */
        updateZone,

        /**
         * Service name used by the {@link ContextModule} 'getZone' method
         */
        getZone,

        /**
         * Service name used by the 'destinationReached' notifier method
         */
        destinationReached,
        /**
         * Service name used by the 'destinationLeft' notifier method
         */
        destinationLeft,

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
