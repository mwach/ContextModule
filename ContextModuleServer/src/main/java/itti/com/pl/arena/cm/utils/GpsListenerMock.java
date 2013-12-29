package itti.com.pl.arena.cm.utils;

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
 * Class mocking GPS behaviour Produces GPS coordinates compatible with the
 * Context Manager module, used for testing and development purposes
 * 
 * @author mawa
 * 
 */
public class GpsListenerMock implements LocationPublisher {

	private static final int DEFAULT_STEPS = 10000;

	private static final double DEFAULT_ACCURACY = 10;
	private static final int DEFAULT_BEARING = 30;
	private static final int DEFAULT_SPEED = 50;


	// number of steps (calls) before 'destination' will be reached
	private int currentStep = 0;
	// total number of steps
	private int steps;
	// initial location
	private PlatformLocation startLocation = null;
	// end location
	private Location endLocation = null;
	// delta between consecutive steps, each call, (until finish will be
	// reached) result will be incremented using this delta
	private PlatformLocation deltaLocation = null;

	//GPS location listener
	private LocationListener locationListener = null;

	private LocationListener getLocationListener() {
		return locationListener;
	}

	@Override
	public void setLocationListener(LocationListener locationListener) {
		this.locationListener = locationListener;
	}

	private int getSteps() {
		return steps;
	}

	@Required
	public void setSteps(int steps) {
		this.steps = steps;
	}

	private PlatformLocation getStartLocation() {
		return startLocation;
	}

	@Required
	public void setStartLocation(PlatformLocation startLocation) {
		this.startLocation = startLocation;
	}

	private Location getEndLocation() {
		return endLocation;
	}

	@Required
	public void setEndLocation(Location endLocation) {
		this.endLocation = endLocation;
	}

	@Override
	public void init() {

		try{
		if(getStartLocation() == null){
			throw new GeoportalException(ErrorMessages.GEOPORTAL_NULL_START_LOCATION);
		}
		if(getStartLocation() == null){
			throw new GeoportalException(ErrorMessages.GEOPORTAL_NULL_START_LOCATION);
		}
		}catch(GeoportalException exc){
			LogHelper.exception(GpsListenerMock.class, "init", "Could not initialize component", exc);
			throw new BeanInitializationException("Could not initialize component", exc);
		}

		// validation: if values are invalid use default ones
		if (steps <= 0) {
			steps = DEFAULT_STEPS;
		}

		// prepare delta object and fill it with values which will change during
		// object 'movement'
		double altitude = (getEndLocation().getAltitude() - getStartLocation()
						.getAltitude()) / steps;
		double latitude = (getEndLocation().getLatitude() - getStartLocation()
						.getLatitude()) / steps;
		double longitude = (getEndLocation().getLongitude() - getStartLocation()
						.getLongitude()) / steps;

		this.deltaLocation = new PlatformLocation(startLocation.getId(), longitude, latitude, altitude, 0, 0, 0, 0);
	}

	@Override
	public void shutdown() {
	}

	/**
	 * Returns 'current' object location When object reach destination (steps
	 * counter will reach defined number of total steps) location will stop
	 * changing
	 */
	public PlatformLocation updateLocation() {

		// prepare return object
		// add deltas depending on current step
		double altitude = getStartLocation().getAltitude()
				+ currentStep * deltaLocation.getAltitude();
		double longitude = getStartLocation().getLongitude()
				+ currentStep * deltaLocation.getLongitude();
		double latitude = getStartLocation().getLatitude()
				+ currentStep * deltaLocation.getLatitude();
		long time = System.currentTimeMillis();

		int speed = 0;
		// if destination reached, object 'stops', otherwise 'object' is still
		// moving
		if (currentStep < getSteps()) {
			speed = DEFAULT_SPEED;
			currentStep++;
		}

		PlatformLocation returnLocation = new PlatformLocation(getStartLocation().getId(), 
				longitude, latitude, altitude, DEFAULT_BEARING, speed, time, DEFAULT_ACCURACY);

		if (getLocationListener() != null) {
			getLocationListener().onLocationChange(returnLocation);
		}
		return returnLocation;
	}
}
