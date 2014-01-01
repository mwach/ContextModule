package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.dto.Platform;
import itti.com.pl.arena.cm.dto.PlatformType;
import itti.com.pl.arena.cm.dto.Truck;

public final class PlatformFactory {

    public static Platform getPlatform(String platformClass, String platformId){

	PlatformType platformType = PlatformType.valueOf(platformClass);
	switch (platformType) {
	case Truck:
	    return new Truck(platformId, null, null);

	default:
	    return null;
	}
    }
}
