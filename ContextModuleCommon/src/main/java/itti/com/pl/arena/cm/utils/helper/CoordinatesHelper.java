package itti.com.pl.arena.cm.utils.helper;

/**
 * Coordinate utilities
 * 
 * @author cm-admin
 * 
 */
public final class CoordinatesHelper {

    private CoordinatesHelper() {
    }

    /**
     * Calculates value of the X coordinate using radial coordinate parameters
     * See http://www.mathsisfun.com/polar-cartesian-coordinates.html
     * @param radius radius
     * @param angle angle
     * @return value of the corresponding X coordinate
     */
    public static double getXFromRadial(double radius, double angle){
        return radius * Math.cos(angle);
    }

    /**
     * Calculates value of the Y coordinate using radial coordinate parameters
     * See http://www.mathsisfun.com/polar-cartesian-coordinates.html
     * @param radius radius
     * @param angle angle
     * @return value of the corresponding Y coordinate
     */
    public static double getYFromRadial(double radius, double angle){
        return radius * Math.sin(angle);
    }

}
