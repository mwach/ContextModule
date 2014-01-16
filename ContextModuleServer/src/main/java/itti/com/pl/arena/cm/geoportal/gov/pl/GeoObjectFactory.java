package itti.com.pl.arena.cm.geoportal.gov.pl;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

public final class GeoObjectFactory {

    public static GeoObject getGeoObject(String ontologyLayerName) {
        if (StringHelper.equalsIgnoreCase(Constants.LAYER_BUILDING, ontologyLayerName)) {
            return new Building();
        }
        return null;
    }
}