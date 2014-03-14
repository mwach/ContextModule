package itti.com.pl.arena.cm.dto.staticobj;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

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

        public static Type getType(String type){
            if(StringHelper.hasContent(type)){
                
                for (Type typeEnum : Type.values()) {
                    if(StringHelper.equalsIgnoreCase(typeEnum.name(), type)){
                        return typeEnum;
                    }
                }
            }
            return null;
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of that class
     * @param id ID of the object
     * @param type type of the building
     */
    public Building(String id, Type type){
        super(id);
        setType(type);
    }

    /*
     * type of the building
     */
    private Type type;

    /**
     * returns type of the building
     * @return type of the building
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Building other = (Building) obj;
        if (type != other.type)
            return false;
        if(getId() != other.getId())
            return false;
        return true;
    }

    
}
