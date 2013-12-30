package itti.com.pl.arena.cm.dto;

public class Camera {

	private String id;
	private String type;
	private double angleX;
	private double angleY;
	private RelativePosition position;

	public Camera(String id, String type, double angleX, double angleY, RelativePosition position){
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
		return "Camera [id=" + id + ", type=" + type + ", angleX="
				+ angleX + ", angleY=" + angleY + ", position=" + position
				+ "]";
	}
	
}