package itti.com.pl.arena.cm.dto;

import java.util.HashMap;
import java.util.Map;

public class Parking extends GeoObject {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private Map<String, GeoObject> staticObjects = new HashMap<String, GeoObject>();
    private Map<String, GeoObject> mobilities = new HashMap<String, GeoObject>();

    public Map<String, GeoObject> getStaticObjects() {
        return staticObjects;
    }

    public void addStaticObject(GeoObject staticObject) {
        this.staticObjects.put(staticObject.getId(), staticObject);
    }

    public Map<String, GeoObject> getMobilities() {
        return mobilities;
    }

    public void addMobility(GeoObject mobility) {
        this.mobilities.put(mobility.getId(), mobility);
    }

}
