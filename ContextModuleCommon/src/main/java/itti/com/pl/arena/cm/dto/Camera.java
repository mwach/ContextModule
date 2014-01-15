package itti.com.pl.arena.cm.dto;

public class Camera {

    private String id;
    private String type;
    private double angleX;
    private double angleY;
    private RelativePosition position;

    public Camera(String id, String type, double angleX, double angleY, RelativePosition position) {
        this.id = id;
        this.type = type;
        this.angleX = angleX;
        this.angleY = angleY;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getAngleX() {
        return angleX;
    }

    public double getAngleY() {
        return angleY;
    }

    public RelativePosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Camera [id=" + id + ", type=" + type + ", angleX=" + angleX + ", angleY=" + angleY + ", position=" + position
                + "]";
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
        result = prime * result + ((position == null) ? 0 : position.hashCode());
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
        if (position != other.position)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}