package itti.com.pl.arena.cm.utils;

import java.util.UUID;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.PlatformLocation;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanInitializationException;

public class GpsMockTest {

	private static final double ASSERT_DELTA = 0.0001;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testDefaults(){
		
		expectedException.expect(BeanInitializationException.class);

		//this one has invalid params, so use default ones
		GpsListenerMock mockInvalidDefault = new GpsListenerMock();
		mockInvalidDefault.init();
	}

	@Test
	public void testCustomObject(){

		int noOfSteps = 100;
		//create custom, valid object
		PlatformLocation startLocation = new PlatformLocation(UUID.randomUUID().toString(), 1, 1, 1, 0, 0, 0, 0);
		Location endLocation = new Location(2, 2, 2, 0, 0, 0, 0);
		GpsListenerMock mockDefault = new GpsListenerMock();
		mockDefault.setStart(startLocation);
		mockDefault.setDestination(endLocation);
		mockDefault.setSteps(noOfSteps);
		mockDefault.init();

		//check all locations until destination will be reached (speed == 0)
		Location defaultLocation = null;
		int stepNo = 0;
		do{
			defaultLocation = mockDefault.updateLocation();
			double latitudeDelta = endLocation.getLatitude() - startLocation.getLatitude();
			double delta = stepNo * latitudeDelta / noOfSteps;
			Assert.assertEquals(startLocation.getLatitude() + delta, defaultLocation.getLatitude(), ASSERT_DELTA);
			stepNo++;
		}while(defaultLocation.getSpeed() != 0);
	}

}
