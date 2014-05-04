package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing information about camera and its field of view
 * It provides all camera information (extends base {@link Camera} class)
 * Also provides a list of objects in the camera field of view
 * @author cm-admin
 *
 */
public class CameraFieldOfView extends Camera{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // list of objects in the camera field of view
    //TODO: in the future, replace that list with more complex object:
    // provide information about objects location in the view
    // information about distance
    // if one building is visible, or behind another one
    // others???
    private Set<GeoObject> fieldOfViewObjects = new HashSet<>();

    /**
     * adds a new {@link GeoObject } to the camera field of view
     * @param object geoportal object
     */
    public void addFieldOfViewObject(GeoObject object){
        if(object != null){
            fieldOfViewObjects.add(object);
        }
    }

    /**
     * Returns a list of objects in the camera field of view
     * @return list of objects in the camera field of view
     */
    public Set<GeoObject> getFieldOfViewObjects(){
        return fieldOfViewObjects;
    }

    /**
     * Default constructor. See {@link Camera} for more details
     * @param id ID of the camera
     * @param type type of the camera
     * @param angleX camera horizontal angle
     * @param angleY camera vertical angle
     */
    public CameraFieldOfView(String id, String type, double angleX, double angleY) {
        super(id, type, angleX, angleY, new CartesianCoordinate(0, 0), 0);
    }

    @Override
    public String toString() {
        return String.format("%s, %s", super.toString(), getFieldOfViewObjects());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fieldOfViewObjects == null) ? 0 : fieldOfViewObjects.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CameraFieldOfView other = (CameraFieldOfView) obj;
        if (fieldOfViewObjects == null) {
            if (other.fieldOfViewObjects != null)
                return false;
        } else if (!fieldOfViewObjects.equals(other.fieldOfViewObjects))
            return false;
        return true;
    }
}
