package itti.com.pl.arena.cm.geoportal.gov.pl;

import java.util.Arrays;
import java.util.Collection;

/**
 * Constants used by Geoportal service
 * @author cm-admin
 *
 */
public final class Constants {

    public static final String LAYER_NAME = "layerName";

    public static final String LAYER_BUILDING = "Budynek";

    public static Collection<String> getTopographyKeys() {
	return GEOPORTAL_OBJECT_CONSTAINTS;
    }

    private static Collection<String> GEOPORTAL_OBJECT_CONSTAINTS = Arrays.asList(
		new String[] { 
		LAYER_NAME, // "layerName":"Budynek"
	        "X_AKTUALNOSC_G", // "X_AKTUALNOSC_G":"2011-01-05"
	        "FUNKCJA_OGOLNA", // "FUNKCJA_OGOLNA":"p"
	        "FUNKCJA_SZCZEGOLOWA", // "FUNKCJA_SZCZEGOLOWA":"Pr"
	        "L_KONDYGNACJI", // "L_KONDYGNACJI":"1"
	        "Shape", // "Shape":"Polygon"
	        "SHAPE.AREA", // "SHAPE.AREA":"76,8036"
	        "SHAPE.LEN", // "SHAPE.LEN":"36,798332"
	        "INFORM_DODATKOWA", // "INFORM_DODATKOWA":"magazyn i biuro"
	                            // "INFORM_DODATKOWA":"Bydgoszcz"
	        "RODZAJ_ZABUDOWY", // "RODZAJ_ZABUDOWY":"4"
	        "CHARAKTER_ZABUDOWY", // "CHARAKTER_ZABUDOWY":"2"
	        "ROSLINNOSC" // "ROSLINNOSC":"Bl"
	});
}
