package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.dto.Platform;
import itti.com.pl.arena.cm.dto.PlatformType;
import itti.com.pl.arena.cm.dto.Truck;
import itti.com.pl.arena.cm.dto.VehicleWithCamaras;

public final class PlatformFactory {

    public static Platform getPlatform(String platformClass, String platformId){

	PlatformType platformType = PlatformType.valueOf(platformClass);
	switch (platformType) {
	case Vehicle_with_cameras:
	    return new VehicleWithCamaras(platformId, null, null);
   
	case Truck:
	    return new Truck(platformId, null, null);

	default:
	    return null;
	}
    }
}
