package itti.com.pl.arena.cm.dto;

import java.io.Serializable;

/**
 * Abstract class representing geoportal objects (like buildings, infrastructure or vehicles)
 * @author cm-admin
 *
 */
public abstract class GeoObject implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /*
     * ID of the object (unique per module/ontology)
     */
    private String id;
    /*
     * Last known location of the object
     */
    private Location location;

    /*
     * administrative data section
     */
    private String country;
    private String town;
    private String street;
    private int streetNumber;

    /*
     * Boundaries of the objects
     */
    private Location[] boundaries = new Location[0];

    /**
     * Creates new object with given ID
     * @param id ID of the object
     */
    public GeoObject(String id){
        this.id = id;
    }
    /**
     * Returns information about object boundaries
     * @return
     */
    public Location[] getBoundaries() {
        return boundaries.clone();
    }

    /**
     * Sets new boundaries of the object
     * @param boundaries
     */
    public void setBoundaries(Location[] boundaries) {
        if (boundaries != null) {
            this.boundaries = boundaries.clone();
        } else {
            this.boundaries = new Location[0];
        }
    }

    /**
     * Returns information about ID of the object
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Returns information about object location of the object
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Updates information about object location of the object
     * @param location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Returns administrative information about the object (country)
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * Updates administrative information about the object (country)
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns administrative information about the object (town/city)
     * @return
     */
    public String getTown() {
        return town;
    }

    /**
     * Updates administrative information about the object (town/city)
     * @param town
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * Returns administrative information about the object (street)
     * @return
     */
    public String getStreet() {
        return street;
    }

    /**
     * Updates administrative information about the object (street)
     * @param street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Returns administrative information about the object (number)
     * @return
     */
    public int getStreetNumber() {
        return streetNumber;
    }

    /**
     * Updates administrative information about the object (number)
     * @param streetNumber
     */
    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    @Override
    public String toString() {
        return String.format("GeoObject [objectId=%s, location=%s, boundaries=%s]", getId(), getLocation(), getBoundaries());
    }
}
