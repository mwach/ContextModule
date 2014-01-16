package itti.com.pl.arena.cm.dto.staticobj;

import itti.com.pl.arena.cm.dto.GeoObject;

/**
 * Implementation of the {@link GeoObject} representing building
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

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}