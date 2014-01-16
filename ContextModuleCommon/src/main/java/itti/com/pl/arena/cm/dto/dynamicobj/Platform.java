package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.dto.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Platform {

    public enum Type {

        /**
         * Vehicle with cameras installed on it - default Platform Type used by the ContextModule
         */
        Vehicle_with_cameras,
    }

    private String id;
    private Location location;
    private Type type;
    private Map<String, Camera> cameras = new HashMap<>();

    public Platform(String id, Location location, Type platformType, Set<Camera> cameras) {
        this.id = id;
        this.type = platformType;
        this.location = location;
        if (cameras != null) {
            for (Camera camera : cameras) {
                this.cameras.put(camera.getId(), camera);
            }
        }
    }

    public Type getType(){
        return type;
    }

    public void setLastPosition(Location location) {
        this.location = location;
    }

    public void addCamera(Camera camera) {
        if (camera != null) {
            cameras.put(camera.getId(), camera);
        }
    }

    public String getId() {
        return id;
    }

    public Location getLastLocation() {
        return location;
    }

    public Map<String, Camera> getCameras() {
        return cameras;
    }

    public Camera getCamera(String cameraId) {
        return cameras.get(cameraId);
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, lastLocation=%s, cameras=%s]", Platform.class.getSimpleName(), getId(),
                getLastLocation(), getCameras());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cameras == null) ? 0 : cameras.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
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
        } else if (cameras.size() != other.cameras.size())
            return false;
        else {
            for (Camera camera : cameras.values()) {
                if (!other.cameras.containsValue(camera)) {
                    return false;
                }
            }
        }

        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        return true;
    }

}
