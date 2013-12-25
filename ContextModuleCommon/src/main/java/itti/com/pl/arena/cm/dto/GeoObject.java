package itti.com.pl.arena.cm.dto;

import java.io.Serializable;

public abstract class GeoObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private Location location;
	private String country;
	private String town;
	private String street;
	private int streetNumber;
	private String cadastralId;

	private String[] gpsCoordinates;

	public String[] getGpsCoordinates() {
		return gpsCoordinates;
	}
	public void setGpsCoordinates(String[] gpsCoordinates) {
		this.gpsCoordinates = gpsCoordinates;
	}
	public String getId() {
		return id;
	}
	public void setId(String objectId) {
		this.id = objectId;
	}

	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public int getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(int streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getCadastralId() {
		return cadastralId;
	}
	public void setCadastralId(String cadastralId) {
		this.cadastralId = cadastralId;
	}

	@Override
	public String toString() {
		return "GeoObject [objectId=" + id
				+ ", location=" + location + ", country=" + country + ", town="
				+ town + ", street=" + street + ", streetNumber="
				+ streetNumber + ", cadastralId=" + cadastralId + "]";
	}
}
