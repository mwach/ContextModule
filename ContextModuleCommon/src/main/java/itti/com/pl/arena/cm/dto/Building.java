package itti.com.pl.arena.cm.dto;

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
        ATM, Cafe_Restaurant, Car_services_facilities, Caretaker_cabin, Hotel, Other_undefined, Pertrol_station, Toilets;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
