package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.dto.OntologyObject;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;

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
     * name of the platform, where camera is installed
     */
    private String platformName;
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
    private CartesianCoordinate onPlatformPosition;

    /*
     * angle (measured in degrees) is used to determine camera angle against main truck axis (Y)
     * - if camera is directed to the front, then angle is 0
     * - if camera is directed to the back, then angle is 180
     * - if camera is directed to the left side of the scene, then angle is 270
     * - if camera is directed to the right side of the scene, then angle is 90
     */
    private int directionAngle;

    /**
     * Default camera object constructor
     * 
     * @param id
     *            ID of the camera
     * @param type
     *            type of the camera (any string accepted but should be some common, easy to recognize value like
     *            infrared, fisheye)
     * @param angleX camera horizontal angle 
     * @param angleY camera vertical angle
     * @param cameraPosition position of the camera on truck measured in meters. 
     * Point (0,0) of the Cartesian axis should be located at the front of the truck, in the middle width
     * @param directionAngle angle (measured in radians) which determines main camera direction axis against main truck axis (Y)
     */
    public Camera(String id, String platformName, String type, double angleX, double angleY, CartesianCoordinate cameraPosition, int directionAngle) {
        super(id);
        this.platformName = platformName;
        this.type = type;
        this.angleX = angleX;
        this.angleY = angleY;
        this.onPlatformPosition = cameraPosition;
        this.directionAngle = directionAngle;
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
     * Returns name of the platform
     * 
     * @return name of the platform
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * Updates name of the platform
     * 
     * @param platformName name of the platform
     */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    /**
     * Updates type of the camera
     * 
     * @param type
     */
    public void setType(CameraType type) {
        this.type = type.name();
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
     * Returns main camera direction axis
     * 
     * @return vertical angle
     */
    public int getDirectionAngle() {
        return directionAngle;
    }

    /**
     * Updates main camera direction axis 
     * 
     * @param directionAngle vertical angle
     */
    public void setDirectionAngle(int directionAngle) {
        this.directionAngle = directionAngle;
    }

    /**
     * Returns position of the camera on platform
     * 
     * @return position on platform
     */
    public CartesianCoordinate getOnPlatformPosition() {
        return onPlatformPosition;
    }

    /**
     * Updates position of the camera on platform
     * 
     * @param onPlatformPosition position on platform
     */
    public void setOnPlatformPosition(CartesianCoordinate onPlatformPosition) {
        this.onPlatformPosition = onPlatformPosition;
    }

    @Override
    public String toString() {
        return String.format("Camera [id=%s, type=%s, angleX=%f, angleY=%f, onPlatformPosition=%s]", getId(),
                String.valueOf(getType()), getAngleX(), getAngleY(), String.valueOf(getOnPlatformPosition()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(NumbersHelper.changePrecision(angleX));
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(NumbersHelper.changePrecision(angleY));
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(directionAngle);
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
        if (!NumbersHelper.equals(angleX, other.angleX))
            return false;
        if (!NumbersHelper.equals(angleY, other.angleY))
            return false;
        if (!NumbersHelper.equals(directionAngle, other.directionAngle))
            return false;
        if (onPlatformPosition == null) {
            if (other.onPlatformPosition != null)
                return false;
        } else if (!onPlatformPosition.equals(other.onPlatformPosition))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}