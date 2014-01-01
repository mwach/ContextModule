package itti.com.pl.arena.cm.service;

import java.util.List;

import itti.com.pl.arena.cm.Service;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Platform;

public interface ContextManagerService extends Service{

	public void startTracking();
	
	public void stopTracking();

	public Platform getPlatformData(String platformId);

	public List<Platform> getPlatformsData(double x, double y);

	public List<GeoObject> getGISData(double x, double y);

	public List<GeoObject> getGeoportalData(double x, double y);
}
