package itti.com.pl.arena.cm.utils.helper;

import static org.junit.Assert.*;

import java.util.Random;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.exception.ErrorMessages;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for the {@link LocationHelper} class
 * 
 * @author cm-admin
 * 
 */
public class LocationHelperTest {

    private static Random random = new Random();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testEmptyLocation() {
        Location location = new Location();
        assertEquals("0.0, 0.0, 0.0", LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatLocation() {
        Location location = new Location(random.nextDouble(), random.nextDouble());
        assertEquals(String.format("%s, %s, 0.0", location.getLongitude(), location.getLatitude()),
                LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatBearLocation() {
        // bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(360));
        String expectedFormat = String.format("%s, %s, 0.0", location.getLongitude(), location.getLatitude());
        assertEquals(expectedFormat, LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLonLatBearAltLocation() {
        // bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), random.nextInt(360), random.nextDouble());
        String expectedFormat = String.format("%s, %s, %s", location.getLongitude(), location.getLatitude(),
                location.getAltitude());
        assertEquals(expectedFormat, LocationHelper.createStringFromLocation(location));
    }

    @Test
    public void testLocationEndDecode() throws LocationHelperException {
        // bearing is going to be ignored
        Location location = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        assertEquals(location, LocationHelper.getLocationFromString(LocationHelper.createStringFromLocation(location)));
    }

    @Test
    public void testLocationListEncDecode() throws LocationHelperException {
        // bearing is going to be ignored
        Location[] arrayOfLocations = new Location[3];
        arrayOfLocations[0] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        arrayOfLocations[1] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());
        arrayOfLocations[2] = new Location(random.nextDouble(), random.nextDouble(), 0, random.nextDouble());

        assertArrayEquals(arrayOfLocations,
                LocationHelper.getLocationsFromStrings(LocationHelper.createStringsFromLocations(arrayOfLocations)));
    }

    @Test
    public void testCalculateAngle() {
        Location locationA = new Location(0, 0);
        Location locationB = new Location(1, 1);
        double angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(45.0, angle, 0.0001);

        locationB = new Location(1, 0);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(90.0, angle, 0.0001);

        locationB = new Location(0, 1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(0.0, angle, 0.0001);

        locationB = new Location(-1, 1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(-45, angle, 0.0001);

        locationB = new Location(-1, 0);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(-90.0, angle, 0.0001);

        locationB = new Location(-1, -1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(-135.0, angle, 0.0001);

        locationB = new Location(0, -1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(180.0, angle, 0.0001);

        locationB = new Location(1, -1);
        angle = LocationHelper.calculateAngle(locationA, locationB);
        assertEquals(135.0, angle, 0.0001);

    }

    @Test
    public void testGetCoordinateToDoubleNull() throws LocationHelperException{

        expectedException.expect(LocationHelperException.class);
        expectedException.expectMessage(ErrorMessages.LOCATION_HELPER_NULL_LOCATION.getMessage());
        
        LocationHelper.convertCoordinateToDouble(null);
    }
    
    @Test
    public void testGetCoordinateToDoubleInvalidFormat() throws LocationHelperException{
        expectedException.expect(LocationHelperException.class);
        expectedException.expectMessage(String.format(ErrorMessages.LOCATION_HELPER_COULD_NOT_PARSE.getMessage(), "dummy"));
        
        LocationHelper.convertCoordinateToDouble("dummy");
    }

    @Test
    public void testGetCoordinateToDoubleInvalidFormatTwo() throws LocationHelperException{

        String location ="234'234'eewr";
        expectedException.expect(LocationHelperException.class);
        expectedException.expectMessage(String.format(ErrorMessages.LOCATION_HELPER_COULD_NOT_PARSE.getMessage(), "eewr"));
        
        LocationHelper.convertCoordinateToDouble(location);
    }

    @Test
    public void testGetCoordinateToDoubleInvalidDoubleApostrophe() throws LocationHelperException{

        String location ="234'234''435";
        double value = LocationHelper.convertCoordinateToDouble(location);
        assertEquals((234.0 + 234.0/60 + 435.0/(60*60)), value, 0.001);

        String location2 ="234'234'435";
        double value2 = LocationHelper.convertCoordinateToDouble(location2);
        assertEquals(value2, value, 0.001);
    }

    @Test
    @Ignore("Only for ontology data gen purposes")
    public void testGetCoordinateToDoubleSetOfLocations() throws LocationHelperException{

        String[] realLocations = new String[]{
                //parking lot
                "53'7'26.67",
                "17'58'22.91",
                "53'7'26.97",
                "17'58'23.46",
                "53'7'27.38",
                "17'58'21.95",
                "53'7'27.07",
                "17'58'21.68",
                //building
                "53'7'26.38",
                "17'58'22.86",
                "53'7'26.81",
                "17'58'23.08",
                "53'7'26.92",
                "17'58'22.5",
                "53'7'26.53",
                "17'58'22.34",
                //garage
                "53'7'26.85",
                "17'58'22.22",
                "53'7'26.97",
                "17'58'22.1",
                "53'7'26.89",
                "17'58'21.84",
                "53'7'26.78",
                "17'58'21.93",
                //vehicle
                //P1
                "53'7'25.53",
                "17'58'23.18",
              //P2
                "53'7'26.37",
                "17'58'23.58",
              //P3
                "53'7'27.11",
                "17'58'23.83",

        };
        for (int i=0 ; i<realLocations.length ; i+=2) {
            double valueY = LocationHelper.convertCoordinateToDouble(realLocations[i]);
            double valueX = LocationHelper.convertCoordinateToDouble(realLocations[i+1]);
            System.out.println(valueX + "," + valueY);
        }
    }
}
