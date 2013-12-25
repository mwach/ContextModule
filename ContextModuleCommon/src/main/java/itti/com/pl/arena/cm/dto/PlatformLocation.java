package itti.com.pl.arena.cm.dto;

public class PlatformLocation extends Location{

	private static final long serialVersionUID = 1L;
	private String id;

	public PlatformLocation(String platformId, double longitude, double latitude, double altitude, int bearing, double speed, long time, double accuracy){
		super(longitude, latitude, altitude, bearing, speed, time, accuracy);
		this.id = platformId;
	}

	public PlatformLocation(String platformId, double longitude, double latitude, double altitude){
		super(longitude, latitude, altitude, 0, 0, 0, 0);
		this.id = platformId;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "PlatformLocation [id=" + id
				+ ", longitude=" + getLongitude() + ", Latitude="
				+ getLatitude() + ", Altitude=" + getAltitude()
				+ ", bearing=" + getBearing() + ", Speed="
				+ getSpeed() + ", Time()=" + getTime() + ", Accuracy="
				+ getAccuracy() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlatformLocation other = (PlatformLocation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
