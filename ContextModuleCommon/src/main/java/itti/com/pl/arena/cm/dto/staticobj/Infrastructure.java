package itti.com.pl.arena.cm.dto.staticobj;

import itti.com.pl.arena.cm.dto.GeoObject;

/**
 * Implementation of the {@link GeoObject} representing infrastructure object
 * 
 * @author cm-admin
 *
 */
public class Infrastructure extends GeoObject {

    /**
     * Available infrastructure types
     * @author cm-admin
     *
     */
    public enum Type {
        Cafe_Restaurant_Zone,
        Car_Parking,
        Entrance,
        Fence,
        Lawn,
        LPG_auto_gas_refuelling_area,
        Petrol_refuelling_area,
        Service_area,
        Sidewalk,
        Smoking_zone,
        Toilets_zone,
        Track_and_lorry_parking,
        Trees
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
    public Infrastructure(String id){
        super(id);
    }

    /*
     * type of the infrastructure
     */
    private Type type;

    /**
     * returns type of the infrastructure
     * @return type of the infrastructure
     */
    public Type getType() {
        return type;
    }

    /**
     * updates type of the infrastructure
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }
}
