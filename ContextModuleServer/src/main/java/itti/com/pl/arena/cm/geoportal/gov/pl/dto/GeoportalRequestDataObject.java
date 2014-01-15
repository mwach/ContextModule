package itti.com.pl.arena.cm.geoportal.gov.pl.dto;

public class GeoportalRequestDataObject extends GeoportalRequestObject {

    public GeoportalRequestDataObject(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public GeoportalRequestDataObject(double longitude, double latitude, Wkid wkid) {
        super(longitude, latitude, wkid);
    }
}
