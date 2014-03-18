package itti.com.pl.arena.cm.server.utils.helpers;

import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.RealWorldCoordinate;
import itti.com.pl.arena.cm.dto.Location;

/**
 * Factory for new {@link Location} objects from provided Arena objects
 * @author cm-admin
 *
 */
public final class LocationFactory {

    /**
     * Private constructor
     */
    private LocationFactory(){
    }

    /**
     * Creates a new {@link Location} object from provided Arena object
     * @param arenaLocation location in one of the Arena-provided objects
     * It can be {@link eu.arena_fp7._1.Location} or {@link RealWorldCoordinate}
     * @return CM location object, or null if conversion failed
     */
    public static Location createLocation(AbstractNamedValue arenaLocation) {
        
        //null request param provided
        if(arenaLocation == null){
            return null;
        }
 
        //create CM Location object from ARENA Location 
        if (arenaLocation instanceof eu.arena_fp7._1.Location) {
            eu.arena_fp7._1.Location flatLocation = (eu.arena_fp7._1.Location) arenaLocation;
            return new Location(flatLocation.getX(), flatLocation.getY());
        //create CM Location object from ARENA Real World Coordinate object
        } else if (arenaLocation instanceof RealWorldCoordinate) {
            RealWorldCoordinate sphereLocation = (RealWorldCoordinate) arenaLocation;
            return new Location(sphereLocation.getX(), sphereLocation.getY(), 0, sphereLocation.getZ());
        }
        //other modes are not supported
        return null;
    }
}
