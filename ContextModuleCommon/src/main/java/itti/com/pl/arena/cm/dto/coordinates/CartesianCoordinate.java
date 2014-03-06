package itti.com.pl.arena.cm.dto.coordinates;

/**
 * Class representing single point in the Cartesian -D space
 * @author cm-admin
 *
 */
public class CartesianCoordinate {

    //x coordinate
    private double x;
    //y coordinate
    private double y;

    /**
     * Default constructor
     * @param x coordinate
     * @param y coordinate
     */
    public CartesianCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns value of the X coordinate
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Returns value of the Y coordinate
     * @return y
     */
    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("%s [%f, %f]", this.getClass().getSimpleName(), getX(), getY());
    }
}
