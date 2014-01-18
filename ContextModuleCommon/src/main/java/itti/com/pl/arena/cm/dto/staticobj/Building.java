package itti.com.pl.arena.cm.dto.staticobj;

import itti.com.pl.arena.cm.dto.GeoObject;

/**
 * Implementation of the {@link GeoObject} representing building
 * 
 * @author cm-admin
 * 
 */
public class Building extends GeoObject {

    /**
     * Available building types
     * 
     * @author cm-admin
     * 
     */
    public enum Type {
        ATM, 
        Cafe_Restaurant, 
        Car_services_facilities, 
        Caretaker_cabin, 
        Hotel, 
        Other_undefined, 
        Pertrol_station, 
        Toilets
        ;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of that class
     * @param id ID of the object
     */
    public Building(String id){
        super(id);
    }

    /*
     * type of the building
     */
    private Type type;

    /**
     * returns type of the building
     * @return 
     */
    public Type getType() {
        return type;
    }

    /**
     * updates type of the building
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }
}
