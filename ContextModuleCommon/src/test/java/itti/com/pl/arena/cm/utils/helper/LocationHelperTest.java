package itti.com.pl.arena.cm.utils.helper;

import static org.junit.Assert.*;

import java.util.Random;

import itti.com.pl.arena.cm.dto.Location;

import org.junit.Test;

/**
 * Test class for the {@link LocationHelper} class
 * @author cm-admin
 *
 */
public class LocationHelperTest {

    private static Random random = new Random();

    @Test
    public void testEmptyLocation(){
        Location location = new Location();
        assertEquals("0.0, 0.0, 0.0", LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatLocation(){
        Location location = new Location(random.nextDouble(), random.nextDouble());
        assertEquals(
                String.format("%s, %s, 0.0", location.getLongitude(), location.getLatitude()), 
                LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatBearLocation(){
        //bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(360));
        String expectedFormat = String.format("%s, %s, 0.0", location.getLongitude(), location.getLatitude());
        assertEquals(
                expectedFormat, LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatBearAltLocation(){
        //bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(360), random.nextDouble());
        String expectedFormat = String.format("%s, %s, %s", location.getLongitude(), location.getLatitude(), location.getAltitude());
        assertEquals(
                expectedFormat, LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLocationEndDecode() throws LocationHelperException{
        //bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        assertEquals(
                location, LocationHelper.getLocationFromString(LocationHelper.createStringFromLocation(location)));
    }

    @Test
    public void testLocationListEncDecode() throws LocationHelperException{
        //bearing is going to be ignored
        Location[] arrayOfLocations = new Location[3];
        arrayOfLocations[0] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        arrayOfLocations[1] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        arrayOfLocations[2] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());

        assertArrayEquals(
                arrayOfLocations, LocationHelper.getLocationsFromStrings(LocationHelper.createStringsFromLocations(arrayOfLocations)));
    }

    @Test
    public void testCalculateAngle(){
        Location locationA = new Location(0, 0);
        Location locationB = new Location(1, 1);
        double angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(45.0, angle, 0.0001);

        locationB = new Location(1, 0);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(0.0, angle, 0.0001);

        locationB = new Location(0, 1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(90.0, angle, 0.0001);

        locationB = new Location(0, -1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(-90.0, angle, 0.0001);

        locationB = new Location(-1, 0);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(180.0, angle, 0.0001);
                
        locationB = new Location(-1, -1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(-135.0, angle, 0.0001);

    }
}
