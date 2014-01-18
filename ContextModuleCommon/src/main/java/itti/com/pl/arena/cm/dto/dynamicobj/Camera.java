package itti.com.pl.arena.cm.dto.dynamicobj;

import java.io.Serializable;

public class Camera implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * ID of the camera
     */
    private String id;
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

    public Camera(String id, String type, double angleX, double angleY, RelativePosition position) {
        this.id = id;
        this.type = type;
        this.angleX = angleX;
        this.angleY = angleY;
        this.onPlatformPosition = position;
    }

    /**
     * Returns ID of the camera
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Returns type of the camera
     * 
     * @return
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
     * @return
     */
    public double getAngleX() {
        return angleX;
    }

    /**
     * Updates horizontal angle of the camera (area of view in X axis)
     * 
     * @param angleX
     */
    public void setAngleX(double angleX) {
        this.angleX = angleX;
    }

    /**
     * Returns vertical angle of the camera (area of view in Y axis)
     * 
     * @return
     */
    public double getAngleY() {
        return angleY;
    }

    /**
     * Updates vertical angle of the camera (area of view in Y axis)
     * 
     * @param angleY
     */
    public void setAngleY(double angleY) {
        this.angleY = angleY;
    }

    /**
     * Returns position of the camera on platform
     * 
     * @return
     */
    public RelativePosition getOnPPlatformPosition() {
        return onPlatformPosition;
    }

    /**
     * Updates position of the camera on platform
     * 
     * @param onPlatformPosition
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
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