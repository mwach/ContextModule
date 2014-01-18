package itti.com.pl.arena.cm.dto;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class LocationTest {

    private Random random = new Random();

    @Test
    public void testLocationNonArgConstructor() {
        Location location = new Location();
        compareAllLocationParams(location, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0);
    }

    @Test
    public void testLocationTwoArgConstructor() {
        double longitude = random.nextDouble();
        double latitude = random.nextDouble();
        Location location = new Location(longitude, latitude);
        compareAllLocationParams(location, longitude, latitude, 0, 0.0, 0.0, 0.0, 0);
    }

    @Test
    public void testLocationThreeArgConstructor() {
        double longitude = random.nextDouble();
        double latitude = random.nextDouble();
        int bearing = random.nextInt(100);
        Location location = new Location(longitude, latitude, bearing);
        compareAllLocationParams(location, longitude, latitude, bearing, 0.0, 0.0, 0.0, 0);
    }

    @Test
    public void testLocationAllArgConstructor() {
        double longitude = random.nextDouble();
        double latitude = random.nextDouble();
        int bearing = random.nextInt(100);
        double altitude = random.nextDouble();
        double accuracy = random.nextDouble();
        long speed = random.nextLong();
        long time = random.nextLong();

        Location location = new Location(longitude, latitude, bearing, altitude, accuracy, speed, time);
        compareAllLocationParams(location, longitude, latitude, bearing, accuracy, altitude, speed, time);
    }

    private void compareAllLocationParams(Location location, double longitude, double latitude, int bearing, double accuracy,
            double altitude, double speed, long time) {
        Assert.assertEquals(longitude, location.getLongitude(), 0.001);
        Assert.assertEquals(latitude, location.getLatitude(), 0.001);
        Assert.assertEquals(bearing, location.getBearing());
        Assert.assertEquals(accuracy, location.getAccuracy(), 0.001);
        Assert.assertEquals(altitude, location.getAltitude(), 0.001);
        Assert.assertEquals(speed, location.getSpeed(), 0.001);
        Assert.assertEquals(time, location.getTime(), 0.001);
    }

}
