package itti.com.pl.arena.cm.geoportal.gov.pl;

import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.util.Arrays;
import java.util.Collection;

public final class GeoportalKeys {

	public static final String LayerName = "layerName";

	public static Collection<String> getTopographyKeys(){
		return Arrays.asList(
				new String[]{
						LayerName, 				// "layerName":"Budynek"
						"X_AKTUALNOSC_G",		// "X_AKTUALNOSC_G":"2011-01-05"
						"FUNKCJA_OGOLNA",		// "FUNKCJA_OGOLNA":"p"
						"FUNKCJA_SZCZEGOLOWA",	// "FUNKCJA_SZCZEGOLOWA":"Pr"
						"L_KONDYGNACJI",		// "L_KONDYGNACJI":"1"
						"Shape",				// "Shape":"Polygon"
						"SHAPE.AREA",			// "SHAPE.AREA":"76,8036"
						"SHAPE.LEN",			// "SHAPE.LEN":"36,798332"
						"INFORM_DODATKOWA",		// "INFORM_DODATKOWA":"magazyn i biuro"
												// "INFORM_DODATKOWA":"Bydgoszcz"
						"RODZAJ_ZABUDOWY",		// "RODZAJ_ZABUDOWY":"4"
						"CHARAKTER_ZABUDOWY",	// "CHARAKTER_ZABUDOWY":"2"
						"ROSLINNOSC"			// "ROSLINNOSC":"Bl"
				});
	}

	
	public static String getOntlogyClass(String geoportalLayerName){
		if(StringHelper.equalsIgnoreCase("Budynek", geoportalLayerName)){
			return "Other_building";
		}
		return null;
	}
}
