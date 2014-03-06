package itti.com.pl.arena.cm.dto.coordinates;

/**
 * Class representing single point in the radius 2-D space
 * @author cm-admin
 *
 */
public class RadialCoordinate {

    //radius (distance between points)
    private double radius;
    //angle between points
    private double angle;

    /**
     * Default constructor
     * @param radius radius
     * @param angle angle
     */
    public RadialCoordinate(double radius, double angle) {
        this.radius = radius;
        this.angle = angle;
    }

    /**
     * Updates angle with the new value (new value is going to be added to the existing one)
     * @param angle
     */
    public void updateAngle(double angle) {
        this.angle += angle;
    }

    /**
     * Returns value of the radius
     * @return radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Returns value of the angle
     * @return angle
     */
    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return String.format("%s [%f, %f]", this.getClass().getSimpleName(), getRadius(), getAngle());
    }

}
