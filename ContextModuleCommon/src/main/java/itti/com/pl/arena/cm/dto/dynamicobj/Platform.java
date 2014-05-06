package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.OntologyObject;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class representing ContextModule platform (like Truck or Vehicle)
 * 
 * @author cm-admin
 * 
 */
public class Platform extends OntologyObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Supported types of platforms
     * 
     * @author cm-admin
     * 
     */
    public enum Type {

        /**
         * Vehicle with cameras installed on it - default (and currently the only one) Platform Type used by the
         * ContextModule
         */
        Vehicle_with_cameras, ;
        /**
         * Returns default type of the {@link Platform} used by the module
         * 
         * @return default type of the platform
         */
        public static Type getDefaultType() {
            return Vehicle_with_cameras;
        }
    }

    /*
     * Last known location of the platform)
     */
    private Location location;
    /*
     * Type of the platform
     */
    private Type type = Type.getDefaultType();
    /*
     * List of cameras installed on platform
     */
    private Map<String, Camera> cameras = new HashMap<>();
    /*
     * Width of the platform
     */
    private double width;
    /*
     * Height of the platform
     */
    private double height;
    /*
     * Length of the platform
     */
    private double length;

    /**
     * Creates a new basic platform object
     * 
     * @param id
     *            ID of the platform
     */
    public Platform(String id) {
        super(id);
    }

    /**
     * Creates a new complete platform object
     * 
     * @param id
     *            ID of the platform
     * @param location
     *            last known location of the platform
     * @param platformType
     *            type of the platform
     * @param cameras
     *            list of cameras installed on the platform
     */
    public Platform(String id, Location location, Type platformType, Set<Camera> cameras) {
        this(id);
        this.type = platformType;
        this.location = location;
        if (cameras != null) {
            this.cameras.clear();
            for (Camera camera : cameras) {
                addCamera(camera);
            }
        }
    }

    /**
     * Return last know location of the platform
     * 
     * @return location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Updates last know location of the platform
     * 
     * @param location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Returns type of the platform
     * 
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Updates type of the platform
     * 
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns list of cameras installed on the platform
     * 
     * @return list of cameras
     */
    public Map<String, Camera> getCameras() {
        return new HashMap<>(cameras);
    }

    /**
     * Adds a new camera to the platform
     * 
     * @param camera
     */
    public void addCamera(Camera camera) {
        if (camera != null) {
            cameras.put(camera.getId(), camera);
        }
    }

    /**
     * Sets a collection of cameras to the platform
     * 
     * @param cameras
     *            collection of cameras
     */
    public void setCameras(Collection<Camera> cameras) {
        this.cameras.clear();
        if (cameras != null) {
            for (Camera camera : cameras) {
                addCamera(camera);
            }
        }
    }

    /**
     * Removes camera from the platform
     * 
     * @param cameraId
     *            ID of the camera
     */
    public void removeCamera(String cameraId) {
        if (cameraId != null) {
            cameras.remove(cameraId);
        }
    }

    /**
     * Returns width of the platform
     * 
     * @return width width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets width of the platform
     * 
     * @param width
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Returns height of the platform
     * 
     * @return height height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets height of the platform
     * 
     * @param height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Returns length of the platform
     * 
     * @return length
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets length of the platform
     * 
     * @param length
     */
    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, lastLocation=%s, cameras=%s]", Platform.class.getSimpleName(), getId(),
                String.valueOf(getLocation()), getCameras());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cameras == null) ? 0 : cameras.hashCode());
        long temp;
        temp = Double.doubleToLongBits(NumbersHelper.changePrecision(height));
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(NumbersHelper.changePrecision(length));
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        temp = Double.doubleToLongBits(NumbersHelper.changePrecision(width));
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Platform other = (Platform) obj;
        if (cameras == null) {
            if (other.cameras != null)
                return false;
        } else if (!cameras.equals(other.cameras))
            return false;
        if (!NumbersHelper.equals(height, other.height, 0.001))
            return false;
        if (!NumbersHelper.equals(length, other.length, 0.001))
            return false;
        if (!NumbersHelper.equals(width, other.width, 0.001))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}
