package itti.com.pl.arena.cm.dto;

public class Building extends GeoObject{

	public enum Type{
		ATM,
		Cafe_Restaurant,
		Car_services_facilities,
		Caretaker_cabin,
		Hotel,
		Other_undefined,
		Pertrol_station,
		Toilets
		;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
