package itti.com.pl.arena.cm.utils.helper;

import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import itti.com.pl.arena.cm.dto.coordinates.ArenaObjectCoordinate;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.coordinates.RadialCoordinate;

public class ArenaObjectCoordinateTest {

    private Random random = new Random();

    @Test
    public void testAddGetCoordinate() {

        // test to verify that basic get/set functionality is working properly
        String objectId = UUID.randomUUID().toString();
        int noOfPairs = 5;

        double[][] testCoordinates = new double[noOfPairs][];
        ArenaObjectCoordinate objectCoordinate = new ArenaObjectCoordinate(objectId);
        for (int i = 0; i < noOfPairs; i++) {
            double radius = random.nextDouble();
            double angle = random.nextDouble();
            testCoordinates[i] = new double[2];
            testCoordinates[i][0] = radius;
            testCoordinates[i][1] = angle;
            objectCoordinate.addRadialCoordinates(radius, angle);
        }
        Assert.assertEquals(objectId, objectCoordinate.getId());
        Assert.assertEquals(noOfPairs, objectCoordinate.getRadialCoordinates().size());
        int itemPos = 0;
        for (RadialCoordinate radialCoordinate : objectCoordinate) {
            Assert.assertEquals(testCoordinates[itemPos][0], radialCoordinate.getDistance(), 0.0001);
            Assert.assertEquals(testCoordinates[itemPos][1], radialCoordinate.getAngle(), 0.0001);
            itemPos++;
        }
    }

    @Test
    public void testUpdateCoordinate() {

        // test to verify, if object update is working correctly
        ArenaObjectCoordinate objectCoordinate = new ArenaObjectCoordinate("objectId");
        double radius = random.nextDouble();
        double angle = random.nextDouble();
        double angleUpdate = random.nextDouble();
        objectCoordinate.addRadialCoordinates(radius, angle);
        // set new value of the angle
        objectCoordinate.iterator().next().updateAngle(angleUpdate);
        // verify new value
        Assert.assertEquals(angle + angleUpdate, objectCoordinate.iterator().next().getAngle(), 0.0001);
    }

    @Test
    public void testTranslateCoordinates() {

        // test to verify, if objects translation (radial into Cartesian) is working correctly

        ArenaObjectCoordinate objectCoordinate = new ArenaObjectCoordinate("objectId");
        double radius = random.nextDouble();
        double angle = random.nextDouble();
        objectCoordinate.addRadialCoordinates(radius, angle);

        for (CartesianCoordinate cartesianCoordinate : objectCoordinate.getCartesianCoordinates()) {
            Assert.assertEquals(CoordinatesHelper.getXFromRadial(radius, angle), cartesianCoordinate.getX(), 0.0001);
            Assert.assertEquals(CoordinatesHelper.getYFromRadial(radius, angle), cartesianCoordinate.getY(), 0.0001);
        }
    }
}
