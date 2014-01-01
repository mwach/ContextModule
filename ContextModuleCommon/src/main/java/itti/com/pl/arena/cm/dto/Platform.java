package itti.com.pl.arena.cm.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Platform {

    private String id;
    private Location lastLocation;
    private Map<String, Camera> cameras = new HashMap<>();

    public Platform(String id, Location lastLocation, Set<Camera> cameras) {
	this.id = id;
	this.lastLocation = lastLocation;
	if (cameras != null) {
	    for (Camera camera : cameras) {
	        this.cameras.put(camera.getId(), camera);
            }
	}
    }

    public abstract PlatformType getType();

    public void setLastPosition(Location location) {
	this.lastLocation = location;
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
	return lastLocation;
    }

    public Map<String, Camera> getCameras() {
	return cameras;
    }

    public Camera getCamera(String cameraId) {
	return cameras.get(cameraId);
    }

    @Override
    public String toString() {
	return "PlatformInformation [id=" + id + ", lastLocation=" + lastLocation + ", cameras=" + cameras + "]";
    }
}
