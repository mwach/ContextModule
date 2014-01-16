package itti.com.pl.arena.cm.dto;

import java.io.Serializable;

/**
 * Location object used by the ContextModule
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

    public Location() {
    }

    public Location(double longitude, double latitude) {
        this();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location(double longitude, double latitude, int bearing) {
        this(longitude, latitude);
        this.bearing = bearing;
    }

    public Location(double longitude, double latitude, double altitude, int bearing, double speed, long time, double accuracy) {
        this(longitude, latitude, bearing);
        this.altitude = altitude;
        this.speed = speed;
        this.time = time;
        this.accuracy = accuracy;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public int getBearing() {
        return bearing;
    }

    public double getSpeed() {
        return speed;
    }

    public long getTime() {
        return time;
    }

    public double getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString() {
        return "Location [longitude=" + longitude + ", latitude=" + latitude + ", altitude=" + altitude + ", bearing=" + bearing
                + ", speed=" + speed + ", time=" + time + ", accuracy=" + accuracy + "]";
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
