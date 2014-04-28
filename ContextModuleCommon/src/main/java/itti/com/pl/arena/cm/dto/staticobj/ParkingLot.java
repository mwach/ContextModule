package itti.com.pl.arena.cm.dto.staticobj;

import itti.com.pl.arena.cm.dto.GeoObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link GeoObject} representing parking lot
 * 
 * @author cm-admin
 * 
 */
public class ParkingLot extends GeoObject {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /*
     * list if infrastructure objects located on given parking
     */
    private Map<String, Infrastructure> infrastructures = new HashMap<String, Infrastructure>();
    /*
     * list if buildings located on given parking
     */
    private Map<String, Building> buildings = new HashMap<String, Building>();

    /**
     * Constructor of that class
     * 
     * @param id
     *            ID of the object
     */
    public ParkingLot(String id) {
        super(id);
    }

    /**
     * Returns list of infrastructure objects located on given parking lot
     * @return list of the infrastructure objects detected on the parking lot
     */
    public Map<String, Infrastructure> getInfrastructure() {
        return new HashMap<>(infrastructures);
    }

    /**
     * Adds a new infrastructure object located on given parking
     * @param infrastructure adds a new infrastructure object to the parking lot object
     */
    public void addIntrastructure(Infrastructure infrastructure) {
        this.infrastructures.put(infrastructure.getId(), infrastructure);
    }

    /**
     * Removes existing infrastructure object from given parking
     * 
     * @param infrastructureId
     */
    public void removeIntrastructure(String infrastructureId) {
        this.infrastructures.remove(infrastructureId);
    }

    /**
     * Returns list of buildings located on given parking
     * 
     * @return list of buildings
     */
    public Map<String, Building> getBuildings() {
        return new HashMap<>(buildings);
    }

    /**
     * Adds a new building object located on given parking
     * 
     * @param building building
     */
    public void addBuilding(Building building) {
        this.buildings.put(building.getId(), building);
    }

    /**
     * Sets a new list of buildings for given parking
     * 
     * @param buildings list of buildings
     */
    public void setBuildings(Collection<Building> buildings) {
        this.buildings.clear();
        if(buildings != null){
            for (Building building : buildings) {
                addBuilding(building);
            }
        }
    }

    /**
     * Sets a new list of infrastructures for given parking
     * 
     * @param infrastructures list of infrastructures
     */
    public void setInfrastructures(Collection<Infrastructure> infrastructures) {
        this.infrastructures.clear();
        if(infrastructures != null){
            for (Infrastructure infrastructure : infrastructures) {
                addIntrastructure(infrastructure);
            }
        }
    }

    /**
     * Removes existing building object from given parking
     * 
     * @param buildingId
     */
    public void removeBuilding(String buildingId) {
        this.buildings.remove(buildingId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((buildings == null) ? 0 : buildings.hashCode());
        result = prime * result + ((infrastructures == null) ? 0 : infrastructures.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParkingLot other = (ParkingLot) obj;
        if(getId() != other.getId())
            return false;
        if (buildings == null) {
            if (other.buildings != null)
                return false;
        } else if (!buildings.equals(other.buildings))
            return false;
        if (infrastructures == null) {
            if (other.infrastructures != null)
                return false;
        } else if (!infrastructures.equals(other.infrastructures))
            return false;
        return true;
    }
}
