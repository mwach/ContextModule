package itti.com.pl.arena.cm.geoportal.gov.pl;

import itti.com.pl.arena.cm.dto.Building;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

public final class GeoObjectFactory {

    public static GeoObject getGeoObject(String ontologyLayerName){
	if(StringHelper.equalsIgnoreCase(Constants.LAYER_BUILDING, ontologyLayerName)){
	    return new Building();
	}
	return null;
    }
}
