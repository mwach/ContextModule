package itti.com.pl.arena.cm.dto.coordinates;

import itti.com.pl.arena.cm.utils.helper.NumbersHelper;

import java.io.Serializable;

/**
 * Class representing single point in the Cartesian -D space
 * @author cm-admin
 *
 */
public class CartesianCoordinate implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CartesianCoordinate other = (CartesianCoordinate) obj;
        if (!NumbersHelper.equals(x, other.x, 0.001))
            return false;
        if (!NumbersHelper.equals(y, other.y, 0.001))
            return false;
        return true;
    }

    
}
