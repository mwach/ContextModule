package itti.com.pl.arena.cm.dto.coordinates;

import java.io.Serializable;

/**
 * Class representing single point in the radius 2-D space
 * 
 * @author cm-admin
 * 
 */
public class RadialCoordinate implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // radius (distance between points)
    private double distance;
    // angle between points
    private double angle;

    /**
     * Default constructor
     * 
     * @param distance
     *            radius
     * @param angle
     *            angle
     */
    public RadialCoordinate(double distance, double angle) {
        this.distance = distance;
        this.angle = angle;
    }

    /**
     * Updates angle with the new value (new value is going to be added to the existing one)
     * 
     * @param angle
     */
    public void updateAngle(double angle) {
        this.angle -= angle;
    }

    /**
     * Returns value of the radius
     * 
     * @return radius
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns value of the angle
     * 
     * @return angle
     */
    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return String.format("%s [%f, %f]", this.getClass().getSimpleName(), getDistance(), getAngle());
    }

}
