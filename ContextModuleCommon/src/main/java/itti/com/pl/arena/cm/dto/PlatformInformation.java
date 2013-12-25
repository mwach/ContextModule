package itti.com.pl.arena.cm.dto;

import java.util.HashMap;
import java.util.Map;

public class PlatformInformation {

	private String id;
	private Location lastLocation;
	private Integer bearing;
	private Map<String, Camera> cameras = new HashMap<>();

	public PlatformInformation(String id, Location lastLocation, Map<String, Camera> cameras){
		this.id = id;
		this.lastLocation = lastLocation;
		if(cameras != null){
			this.cameras = cameras;
		}
	}

	public void setLastPosition(Location location){
		this.lastLocation = location;
	}

	public void addCamera(Camera camera){
		if(camera != null){
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

	public Camera getCamera(String cameraId){
		return cameras.get(cameraId);
	}

	@Override
	public String toString() {
		return "PlatformInformation [id=" + id + ", lastLocation="
				+ lastLocation + ", cameras=" + cameras + "]";
	}

	public void addBearing(Integer bearing) {
		this.bearing = bearing;
	}
	public Integer getBearing() {
		return bearing;
	}

}

