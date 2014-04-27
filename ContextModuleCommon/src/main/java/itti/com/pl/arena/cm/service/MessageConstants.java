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
         * Service name used by the {@link ContextModule} 'updateCamera' method
         */
        updateCamera,
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
         * Service name used by the {@link ContextModule} 'removeZone' method
         */
        removeZone,

        /**
         * Service name used by the {@link ContextModule} 'getListOfZones' method
         */
        getListOfZones,
        /**
         * Service name used by the {@link ContextModule} 'getListOfParkingLots' method
         */
        getListOfParkingLots,

        /**
         * Service name used by the 'destinationReached' notifier method
         */
        destinationReached,
        /**
         * Service name used by the 'destinationLeft' notifier method
         */
        destinationLeft, 
        /**
         * Service name used by the 'defineRule' method
         */
        defineRule, 
        /**
         * Service name used by the 'removeRule' method
         */
        removeRule, 
        /**
         * Service name used by the 'applyRules' method
         */
        applyRules, 
        /**
         * Service name used by the 'getListOfRules' method
         */
        getListOfRules, 
        /**
         * Service name used by the 'getListOfPlatforms' method
         */
        getListOfPlatforms, 
        /**
         * Service name used by the 'removePlatform' method
         */
        removePlatform,
        /**
         * Service name used by the 'removeCamera' method
         */
        removeCamera, 
        /**
         * Service name used by the 'getCamera' method
         */
        getCamera, 
        /**
         * Service name used by the 'removeParkingLot' method
         */
        removeParkingLot, 
        /**
         * Service name used by the 'removeBuilding' method
         */
        removeBuilding, 
        /**
         * Service name used by the 'getBuilding' method
         */
        getBuilding,

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
         * Platform
         */
        Platform, Width, Height, Length, Coordinate,

        /**
         * Properties of the Camera node
         */
        Camera, CameraId, CameraPosition, CameraType, CameraAngleX, CameraAngleY, Bearing,
        /**
         * Properties of the Parking lot
         */
        ParkingLot, Building,
        /**
         * Properties of the Zones
         */
        ParkingLotName, Name, Ident, PlaneName, 
        /**
         * geoportal data
         */
        GeoportalData, 
        /**
         * Error message
         */
        Error,
        /**
         * Notification message
         */
        Notification
        
        ;

    }

    public enum ContextModuleResponseProperties{
        /**
         * name of the status property in the {@link BooleanNamedValue} response
         */
        Status
    }
}
