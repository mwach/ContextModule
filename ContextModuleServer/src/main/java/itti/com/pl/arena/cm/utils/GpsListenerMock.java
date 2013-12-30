package itti.com.pl.arena.cm.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.PlatformLocation;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.location.LocationListener;
import itti.com.pl.arena.cm.location.LocationPublisher;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;

/**
 * Class mocking GPS behavior Produces GPS coordinates compatible with the Context Manager module, used for testing and
 * development purposes
 * 
 * @author mawa
 * 
 */
public class GpsListenerMock implements LocationPublisher {

    /*
     * Default value - number of GPS updates before reaching the target
     */
    private static final int DEFAULT_STEPS = 10000;

    /*
     * GPS attributes: accuracy, bearing and speed
     */
    private static final double DEFAULT_ACCURACY = 10;
    private static final int DEFAULT_BEARING = 30;
    private static final int DEFAULT_SPEED = 50;

    // Current step.
    private int currentStep = 0;
    // ID of the mocked object
    private String id;
    // Total number of steps
    private int steps;
    // Initial location
    private Location start = null;
    // Destination
    private Location destination = null;
    // delta between consecutive steps, each call, (until destination will be
    // reached) result will be incremented using this delta
    private PlatformLocation deltaLocation = null;

    // GPS location listeners
    private Map<String, LocationListener> locationListeners = new HashMap<>();

    private String getId() {
	return id;
    }

    @Required
    public void setId(String id) {
	this.id = id;
    }

    private int getSteps() {
	return steps;
    }

    @Required
    public void setSteps(int steps) {
	this.steps = steps;
    }

    private Location getStart() {
	return start;
    }

    @Required
    public void setStart(Location start) {
	this.start = start;
    }

    private Location getDestination() {
	return destination;
    }

    @Required
    public void setDestination(Location destination) {
	this.destination = destination;
    }

    private synchronized Map<String, LocationListener> getLocationListeners() {
	return locationListeners;
    }

    @Override
    public void registerListener(LocationListener listener) {
	getLocationListeners().put(listener.getId(), listener);
    }

    @Override
    public void setListeners(LocationListener... listeners) {

	//remove existing listeners
	getLocationListeners().clear();
	//add new listeners
	for (LocationListener listener : listeners) {
	    registerListener(listener);
	}
    }

    @Override
    public void deregisterListener(LocationListener listener) {
	if(listener != null){
	    getLocationListeners().remove(listener.getId());
	}
    }

    @Override
    public void init() {

	try {
	    if (getStart() == null) {
		throw new GeoportalException(ErrorMessages.GEOPORTAL_NULL_START_LOCATION);
	    }
	    if (getStart() == null) {
		throw new GeoportalException(ErrorMessages.GEOPORTAL_NULL_START_LOCATION);
	    }
	} catch (GeoportalException exc) {
	    LogHelper.exception(GpsListenerMock.class, "init", "Could not initialize component", exc);
	    throw new BeanInitializationException("Could not initialize component", exc);
	}

	// validation: if values are invalid use default ones
	if (steps <= 0) {
	    steps = DEFAULT_STEPS;
	}

	// prepare delta object and fill it with values which will change during
	// object 'movement'
	double altitude = (getDestination().getAltitude() - getStart().getAltitude()) / steps;
	double latitude = (getDestination().getLatitude() - getStart().getLatitude()) / steps;
	double longitude = (getDestination().getLongitude() - getStart().getLongitude()) / steps;

	this.deltaLocation = new PlatformLocation(getId(), longitude, latitude, altitude, 0, 0, 0, 0);
    }

    @Override
    public void shutdown() {
    }

    /**
     * Returns 'current' object location When object reach destination (steps counter will reach defined number of total
     * steps) location will stop changing
     */
    public PlatformLocation updateLocation() {

	// prepare return object
	// add deltas depending on current step
	double altitude = getStart().getAltitude() + currentStep * deltaLocation.getAltitude();
	double longitude = getStart().getLongitude() + currentStep * deltaLocation.getLongitude();
	double latitude = getStart().getLatitude() + currentStep * deltaLocation.getLatitude();
	long time = System.currentTimeMillis();

	int speed = 0;
	// if destination reached, object 'stops', otherwise 'object' is still
	// moving
	if (currentStep < getSteps()) {
	    speed = DEFAULT_SPEED;
	    currentStep++;
	}

	PlatformLocation returnLocation = new PlatformLocation(getId(), longitude, latitude, altitude,
	        DEFAULT_BEARING, speed, time, DEFAULT_ACCURACY);

	for (LocationListener listener : getLocationListeners().values()) {
	    listener.onLocationChange(returnLocation);
	}
	return returnLocation;
    }
}
