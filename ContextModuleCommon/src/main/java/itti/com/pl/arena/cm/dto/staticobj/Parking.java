package itti.com.pl.arena.cm.dto.staticobj;

import itti.com.pl.arena.cm.dto.GeoObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing parking lot object
 * @author cm-admin
 *
 */
public class Parking extends GeoObject {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private Map<String, Infrastructure> infrastructures = new HashMap<String, Infrastructure>();
    private Map<String, Building> buildings = new HashMap<String, Building>();

    public Map<String, Infrastructure> getInfrastructure() {
        return new HashMap<>(infrastructures);
    }

    public void addIntrastructure(Infrastructure infrastructure) {
        this.infrastructures.put(infrastructure.getId(), infrastructure);
    }
    public void removeIntrastructure(GeoObject infrastructure){
        this.infrastructures.remove(infrastructure.getId());
    }

    public Map<String, Building> getBuildings() {
        return new HashMap<>(buildings);
    }

    public void addBuildings(Building building) {
        this.buildings.put(building.getId(), building);
    }
    public void removeBuilding(GeoObject building) {
        this.buildings.remove(building.getId());
    }

}
