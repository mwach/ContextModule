package itti.com.pl.arena.cm.service;

import java.util.List;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.PlatformInformation;

public interface ContextManagerService {

	public void startTracking();
	
	public void stopTracking();

	public PlatformInformation getPlatformData(String platformId);

	public List<PlatformInformation> getPlatformsData(double x, double y);

	public List<GeoObject> getGISData(double x, double y);

	public List<GeoObject> getGeoportalData(double x, double y);
}
