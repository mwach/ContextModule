package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.OntologyObject;

public class Camera extends OntologyObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * Type of the camera (like infrared, thermal, normal...)
     */
    private String type;
    /*
     * Camera horizontal angle (view angle)
     */
    private double angleX;
    /*
     * Camera vertical angle (view angle)
     */
    private double angleY;
    /*
     * position of the camera on platform
     */
    private RelativePosition onPlatformPosition;

    /**
     * Default camera object constructor
     * 
     * @param id
     *            ID of the camera
     * @param type
     *            type of the camera (any string accepted but should be some common, easy to recognize value like
     *            infrared, fisheye)
     *            TODO: should we create an enum to keep acceptable values
     * @param angleX camera horizontal angle 
     * @param angleY camera vertical angle
     * @param position position of the camera on truck
     */
    public Camera(String id, String type, double angleX, double angleY, RelativePosition position) {
        super(id);
        this.type = type;
        this.angleX = angleX;
        this.angleY = angleY;
        this.onPlatformPosition = position;
    }

    /**
     * Returns type of the camera
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Updates type of the camera
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns horizontal angle of the camera (area of view in X axis)
     * 
     * @return horizontal angle
     */
    public double getAngleX() {
        return angleX;
    }

    /**
     * Updates horizontal angle of the camera (area of view in X axis)
     * 
     * @param angleX vertical angle
     */
    public void setAngleX(double angleX) {
        this.angleX = angleX;
    }

    /**
     * Returns vertical angle of the camera (area of view in Y axis)
     * 
     * @return vertical angle
     */
    public double getAngleY() {
        return angleY;
    }

    /**
     * Updates vertical angle of the camera (area of view in Y axis)
     * 
     * @param angleY vertical angle
     */
    public void setAngleY(double angleY) {
        this.angleY = angleY;
    }

    /**
     * Returns position of the camera on platform
     * 
     * @return position on platform
     */
    public RelativePosition getOnPPlatformPosition() {
        return onPlatformPosition;
    }

    /**
     * Updates position of the camera on platform
     * 
     * @param onPlatformPosition position on platform
     */
    public void setOnPlatformPosition(RelativePosition onPlatformPosition) {
        this.onPlatformPosition = onPlatformPosition;
    }

    @Override
    public String toString() {
        return String.format("Camera [id=%s, type=%s, angleX=%f, angleY=%f, onPlatformPosition=%s]", getId(),
                String.valueOf(getType()), getAngleX(), getAngleY(), String.valueOf(getOnPPlatformPosition()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(angleX);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(angleY);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((onPlatformPosition == null) ? 0 : onPlatformPosition.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Camera other = (Camera) obj;
        if (Double.doubleToLongBits(angleX) != Double.doubleToLongBits(other.angleX))
            return false;
        if (Double.doubleToLongBits(angleY) != Double.doubleToLongBits(other.angleY))
            return false;
        if (onPlatformPosition != other.onPlatformPosition)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}