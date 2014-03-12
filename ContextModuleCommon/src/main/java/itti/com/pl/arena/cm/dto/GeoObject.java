package itti.com.pl.arena.cm.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Abstract class representing geoportal objects (like buildings, infrastructure or vehicles)
 * @author cm-admin
 *
 */
public abstract class GeoObject extends OntologyObject {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

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
    private List<Location> boundaries = new ArrayList<>();

    /**
     * Creates new object with given ID
     * @param id ID of the object
     */
    public GeoObject(String id){
        super(id);
    }
    /**
     * Returns information about object boundaries
     * @return list of the object boundaries
     */
    public Location[] getBoundaries() {
        return boundaries.toArray(new Location[boundaries.size()]);
    }

    /**
     * Returns number of boundary objects
     * @return number (count) of the object boundaries
     */
    private int getBoundariesLength(){
        return boundaries.size();
    }

    /**
     * Sets new boundaries of the object
     * @param boundaries
     */
    public void setBoundaries(Location[] boundaries) {
        if (boundaries != null) {
            this.boundaries = Arrays.asList(boundaries);
        } else {
            this.boundaries.clear();
        }
    }

    /**
     * Returns information about object location of the object
     * @return location of the object
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Updates information about object location of the object
     * @param location location of the object
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Returns administrative information about the object (country)
     * @return country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Updates administrative information about the object (country)
     * @param country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns administrative information about the object (town/city)
     * @return town name
     */
    public String getTown() {
        return town;
    }

    /**
     * Updates administrative information about the object (town/city)
     * @param town town name
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * Returns administrative information about the object (street)
     * @return street name
     */
    public String getStreet() {
        return street;
    }

    /**
     * Updates administrative information about the object (street)
     * @param street street name
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Returns administrative information about the object (number)
     * @return street number
     */
    public int getStreetNumber() {
        return streetNumber;
    }

    /**
     * Updates administrative information about the object (number)
     * @param streetNumber street number
     */
    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    @Override
    public String toString() {
        return String.format("GeoObject [objectId=%s, location=%s, boundaries size=%d]", getId(), getLocation(), getBoundariesLength());
    }
}
