package itti.com.pl.arena.cm.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing ARENA Zone object
 * 
 * @author cs-admin
 * 
 */
public class Zone extends OntologyObject implements Iterable<Location> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    // <Zone ident="54" name="Zn54_gr4" plane_name="ground">
    private long ident;
    private String planeName;

    /*
     * list of zone coordinates
     */
    private List<Location> coordinates = new ArrayList<>();

    /**
     * Creates a new zone
     * 
     * @param name
     *            ID/name of the zone
     */
    public Zone(String name) {
        super(name);
    }

    /**
     * Adds a new coordinate to the zone
     * 
     * @param coordinate
     *            new coordinate
     */
    public void addCoordinate(Location coordinate) {
        if (coordinate != null) {
            coordinates.add(coordinate);
        }
    }

    /**
     * Adds a list of new coordinates to the zone
     * 
     * @param coordinates
     *            list of coordinates
     */
    public void addCoordinates(Collection<Location> coordinates) {
        if (coordinates != null) {
            this.coordinates.addAll(coordinates);
        }
    }

    /**
     * Removes coordinate from the zone
     * 
     * @param coordinate
     *            coordinate to remove
     * @return true, if coordinate was removed, false otherwise
     */
    public boolean removeCoordinate(Location coordinate) {
        if (coordinate != null) {
            return coordinates.remove(coordinate);
        }
        return false;
    }

    @Override
    public Iterator<Location> iterator() {
        return coordinates.iterator();
    }

    /**
     * Returns unique identifier of the zone
     * 
     * @return zone Id
     */
    public long getIdent() {
        return ident;
    }

    /**
     * Sets unique identifier of the zone
     * 
     * @param ident
     *            zone ID
     */
    public void setIdent(long ident) {
        this.ident = ident;
    }

    /**
     * Returns name of the zone
     * 
     * @return zone name
     */
    public String getName() {
        return getId();
    }

    /**
     * Returns name of the plane
     * 
     * @return plane name
     */
    public String getPlaneName() {
        return planeName;
    }

    /**
     * Sets name of the plane
     * 
     * @param planeName
     *            plane name
     */
    public void setPlaneName(String planeName) {
        this.planeName = planeName;
    }

    public Location[] getLocations() {
        return coordinates.toArray(new Location[coordinates.size()]);
    }
}
