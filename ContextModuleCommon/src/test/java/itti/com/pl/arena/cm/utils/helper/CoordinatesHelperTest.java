package itti.com.pl.arena.cm.utils.helper;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the {@link CoordinatesHelper} helper class
 * 
 * @author cm-admin
 * 
 */
public class CoordinatesHelperTest {

    private Random random = new Random();

    @Test
    public void testAngle0() {
        // angle = 0, so Y=0
        double radius = random.nextDouble() + 1;
        double angle = 0;
        Assert.assertEquals(radius, CoordinatesHelper.getXFromRadial(radius, angle), 0.001);
        Assert.assertEquals(0, CoordinatesHelper.getYFromRadial(radius, angle), 0.001);
    }

    @Test
    public void testAngle90() {
        // angle = 90 = pi/2, so X=0
        double radius = random.nextDouble() + 1;
        double angle = Math.PI * 1 / 2;
        Assert.assertEquals(0, CoordinatesHelper.getXFromRadial(radius, angle), 0.001);
        Assert.assertEquals(radius, CoordinatesHelper.getYFromRadial(radius, angle), 0.001);
    }

    @Test
    public void testAngle180() {
        // angle = 180 = pi, so Y=0 and X=-x
        double radius = random.nextDouble() + 1;
        double angle = Math.PI;
        Assert.assertEquals(-radius, CoordinatesHelper.getXFromRadial(radius, angle), 0.001);
        Assert.assertEquals(0, CoordinatesHelper.getYFromRadial(radius, angle), 0.001);
    }

    @Test
    public void testAngle270() {
        // angle = 270 = 3/2pi, so Y=-y and X=0
        double radius = random.nextDouble() + 1;
        double angle = Math.PI * 3 / 2;
        Assert.assertEquals(0, CoordinatesHelper.getXFromRadial(radius, angle), 0.001);
        Assert.assertEquals(-radius, CoordinatesHelper.getYFromRadial(radius, angle), 0.001);
    }

    @Test
    public void testAngle360() {
        // angle = 260 = 2pi, so Y=0 and X=x
        double radius = random.nextDouble() + 1;
        double angle = Math.PI * 2;
        Assert.assertEquals(radius, CoordinatesHelper.getXFromRadial(radius, angle), 0.001);
        Assert.assertEquals(0, CoordinatesHelper.getYFromRadial(radius, angle), 0.001);
    }
}
