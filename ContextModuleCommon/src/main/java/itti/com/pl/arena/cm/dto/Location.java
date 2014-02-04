package itti.com.pl.arena.cm.dto;

import java.io.Serializable;

/**
 * Location object used by the ContextModule
 * 
 * @author cm-admin
 * 
 */
public class Location implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private double longitude;
    private double latitude;
    private double altitude;
    private int bearing;
    private double speed;
    private long time;
    private double accuracy;

    /**
     * Constructor for basic location object containing no location information
     */
    public Location() {
    }

    /**
     * Constructor for basic location object containing information about longitude and latitude only This is a location
     * object used by ContenxtModule when interacting with other Arena modules It can be easy mapped to other Location
     * objects (like Location use by Arena)
     * 
     * @param longitude
     *            longitude
     * @param latitude
     *            latitude
     */
    public Location(double longitude, double latitude) {
        this();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Constructor for basic location object containing information about longitude, latitude and bearing This is a
     * basic location object used by ContenxtModule managed platforms like Trucks or Vehicles
     * 
     * @param longitude
     *            longitude
     * @param latitude
     *            latitude
     * @param bearing
     *            bearing
     */
    public Location(double longitude, double latitude, int bearing) {
        this(longitude, latitude);
        this.bearing = bearing;
    }

    /**
     * Creates a complete location object Used by ContextModule listeners (contains GPS data collected from external
     * device/module about last platform location)
     * 
     * @param longitude
     *            longitude
     * @param latitude
     *            latitude
     * @param bearing
     *            bearing
     * @param altitude
     *            altitude
     * @param accuracy
     *            accuracy
     * @param speed
     *            speed
     * @param time
     *            time
     */
    public Location(double longitude, double latitude, int bearing, double altitude, double accuracy, double speed, long time) {
        this(longitude, latitude, bearing);
        this.altitude = altitude;
        this.speed = speed;
        this.time = time;
        this.accuracy = accuracy;
    }

    /**
     * returns value of the longitude
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * returns value of the latitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * returns value of the altitude
     * @return altitude
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * returns value of the bearing
     * @return bearing
     */
    public int getBearing() {
        return bearing;
    }

    /**
     * returns value of the speed
     * @return speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * returns value of the location update timestamp
     * @return time
     */
    public long getTime() {
        return time;
    }

    /**
     * returns value of the accuracy
     * @return accuracy
     */
    public double getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString() {
        return String.format("Location [longitude=%f, latitude=%f, altitude=%f, "
                + "bearing=%d, speed=%f, time=%d, accuracy=%f]", getLongitude(), getLatitude(), getAltitude(),
                getBearing(), getSpeed(), getTime(), getAccuracy());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(accuracy);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + bearing;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(speed);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (time ^ (time >>> 32));
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
        Location other = (Location) obj;
        if (Double.doubleToLongBits(accuracy) != Double.doubleToLongBits(other.accuracy))
            return false;
        if (Double.doubleToLongBits(altitude) != Double.doubleToLongBits(other.altitude))
            return false;
        if (bearing != other.bearing)
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (Double.doubleToLongBits(speed) != Double.doubleToLongBits(other.speed))
            return false;
        if (time != other.time)
            return false;
        return true;
    }

}
