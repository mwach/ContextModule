package itti.com.pl.arena.cm.utils.helper;

import java.util.Random;

import itti.com.pl.arena.cm.dto.Location;

import org.junit.Assert;
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
        Assert.assertEquals("0.0, 0.0, 0.0", LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatLocation(){
        Location location = new Location(random.nextDouble(), random.nextDouble());
        Assert.assertEquals(
                String.format("%s, %s, 0.0", location.getLongitude(), location.getLatitude()), 
                LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatBearLocation(){
        //bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(360));
        String expectedFormat = String.format("%s, %s, 0.0", location.getLongitude(), location.getLatitude());
        Assert.assertEquals(
                expectedFormat, LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatBearAltLocation(){
        //bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(360), random.nextDouble());
        String expectedFormat = String.format("%s, %s, %s", location.getLongitude(), location.getLatitude(), location.getAltitude());
        Assert.assertEquals(
                expectedFormat, LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLocationEndDecode() throws LocationHelperException{
        //bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        Assert.assertEquals(
                location, LocationHelper.getLocationFromString(LocationHelper.createStringFromLocation(location)));
    }

    @Test
    public void testLocationListEncDecode() throws LocationHelperException{
        //bearing is going to be ignored
        Location[] arrayOfLocations = new Location[3];
        arrayOfLocations[0] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        arrayOfLocations[1] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        arrayOfLocations[2] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());

        Assert.assertArrayEquals(
                arrayOfLocations, LocationHelper.getLocationsFromStrings(LocationHelper.createStringsFromLocations(arrayOfLocations)));
    }

}
