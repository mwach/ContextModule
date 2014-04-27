package itti.com.pl.arena.cm.dto.staticobj;

import java.util.ArrayList;
import java.util.List;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

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
        Bus_stop_area,
        Cafe_Restaurant_zone,
        Car_parking_zone,
        Car_wash_area,
        Entrance,
        Fence,
        General_parking_area,
        Lawn,
        LPG_auto_gas_refuelling_area,
        Petrol_refuelling_area,
        Road,
        Service_area,
        Sidewalk,
        Smoking_zone,
        Toilets_zone,
        Track_and_lorry_parking,
        Trees
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

        public static List<String> asList() {
            List<String> list = new ArrayList<>();
            for (Type type : values()) {
                list.add(type.name());
            }
            return list;
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of that class
     * @param id ID of the object
     * @param type type of the infrastructure
     */
    public Infrastructure(String id, Type type){
        super(id);
        setType(type);
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
        Infrastructure other = (Infrastructure) obj;
        if (type != other.type)
            return false;
        if(getId() != other.getId())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Infrastructure [type=" + type + ", getId()=" + getId() + "]";
    }


}
