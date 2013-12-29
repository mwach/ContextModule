package itti.com.pl.arena.cm.geoportal.api;

import static org.junit.Assert.*;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestDataObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestObject.Wkid;

import java.util.Random;

import org.junit.Test;

public class GeoportalRequestObjectTest {

	private Random random = new Random();
	private static final double MAX_DOUBLE_DELTA = 0.0001;

	@Test
	public void tetsGeoportalRequestObjectBasic(){

		double longitude = random.nextFloat();
		double latitude = random.nextFloat();
		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(
				longitude, latitude);

		assertEquals(longitude, requestObject.getLongitude(), MAX_DOUBLE_DELTA);
		assertEquals(latitude, requestObject.getLatitude(), MAX_DOUBLE_DELTA);
	}

	@Test
	public void tetsGeoportalRequestObjectExt(){

		double longitude = random.nextFloat();
		double latitude = random.nextFloat();
		Wkid wkid = Wkid.W_2180;
		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(
				longitude, latitude, wkid);

		assertEquals(longitude, requestObject.getLongitude(), MAX_DOUBLE_DELTA);
		assertEquals(latitude, requestObject.getLatitude(), MAX_DOUBLE_DELTA);
		assertEquals(String.valueOf(wkid.getValue()), requestObject.getWkid());
	}

}
