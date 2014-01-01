package itti.com.pl.arena.cm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.Platform;
import itti.com.pl.arena.cm.dto.PlatformLocation;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalService;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalUrls;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalHelper;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalKeys;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestDataObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.location.LocationListener;
import itti.com.pl.arena.cm.ontology.Ontology;
import itti.com.pl.arena.cm.ontology.OntologyException;
import itti.com.pl.arena.cm.persistence.Persistence;
import itti.com.pl.arena.cm.persistence.PersistenceException;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;

public class DefaultArenaTracker implements ContextManagerService, LocationListener{

	private Persistence persistence = null;
	private Ontology ontology = null;
	private GeoportalService geoportal = null;

	/*
	 * ID of the listener (random UUID generated during initialization)
	 */
	private String id;

	private Persistence getPersistence() {
		return persistence;
	}

	@Required
	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	private Ontology getOntology() {
		return ontology;
	}

	@Required
	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	private GeoportalService getGeoportal() {
		return geoportal;
	}

	@Required
	public void setGeoportal(GeoportalService geoportal) {
		this.geoportal = geoportal;
	}

	private double locationDelta = 0.02;

	public void setLocationDelta(double delta)
	{
		this.locationDelta = delta;
	}

	private double getLocationDelta()
	{
		return locationDelta;
	}

	public Location getLastPosition(){
		Location lastPos = null;
		try {
			lastPos = getPersistence().readLastPosition();
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		return lastPos;
	}

	@Override
	public void startTracking() {
	}

	@Override
	public void stopTracking() {
	}

	@Override
	public void onLocationChange(PlatformLocation newLocation) {

		//first, try to persist latest location in the database
		try {
			getPersistence().create(newLocation);
		} catch (PersistenceException e) {
			LogHelper.debug(DefaultArenaTracker.class, "onLocationChange", "Could not persist location data");
		}

		//if object is no moving, get info from the ontology
		if(newLocation.getSpeed() == 0){
			//
		}
		try {
			getOntology().getPlatform(newLocation.getId());
		} catch (OntologyException e) {
			LogHelper.exception(DefaultArenaTracker.class, "onLocationChange", e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Platform getPlatformData(String platformId) {
		try {
			return getOntology().getPlatform(platformId);
		} catch (OntologyException e) {
			LogHelper.exception(DefaultArenaTracker.class, "getPlatformData", e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public List<Platform> getPlatformsData(double x, double y) {
		List<Platform> platformsInformation = new ArrayList<>();
		try {
			List<String> platformNames = getOntology().getPlatforms(x, y, getLocationDelta());
			for (String platformId : platformNames) {
				platformsInformation.add(getOntology().getPlatform(platformId));
			}
		} catch (OntologyException e) {
			LogHelper.exception(DefaultArenaTracker.class, "getPlatformsData", e.getLocalizedMessage(), e);
		}
		return platformsInformation;
	}

	@Override
	public List<GeoObject> getGISData(double x, double y) {

		List<GeoObject> gisInformation = new ArrayList<>();
		try {
			List<String> platformNames = getOntology().getGISObjects(x, y, getLocationDelta());
			for (String gisObjectId : platformNames) {
				gisInformation.add(getOntology().getGISObject(gisObjectId));
			}
		} catch (OntologyException | RuntimeException e) {
			LogHelper.exception(DefaultArenaTracker.class, "getGISData", e.getLocalizedMessage(), e);
		}
		return gisInformation;
	}

	@Override
	public List<GeoObject> getGeoportalData(double x, double y) {

		try {
			String geoportalData = getGeoportal().getGeoportalData(
					GeoportalUrls.TOPOGRAPHIC_DATA_SERVICE, 
					new GeoportalRequestDataObject(x, y));
			GeoportalResponse response = 
					GeoportalHelper.fromResponse(geoportalData, GeoportalKeys.getTopographyKeys());
			getOntology().addGeoportalData(x, y, response);
		} catch (GeoportalException | RuntimeException e) {
			LogHelper.exception(DefaultArenaTracker.class, "getgeoportalData", e.getLocalizedMessage(), e);
		}
		return getGISData(x, y);
	}

	@Override
	public String getId() {
	    return id;
	}

	@Override
	public void init() {
	    this.id = UUID.randomUUID().toString();
	}

	@Override
	public void shutdown() {
	    //not used by this servive
	}
}
