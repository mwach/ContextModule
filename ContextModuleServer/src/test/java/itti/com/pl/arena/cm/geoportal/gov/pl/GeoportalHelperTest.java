package itti.com.pl.arena.cm.geoportal.gov.pl;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.UUID;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalHelper;
import itti.com.pl.arena.cm.geoportal.gov.pl.GeoportalKeys;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestDataObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestImageObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestObject.Wkid;
import itti.com.pl.arena.cm.utils.helpers.IOHelper;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.StringHelperException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GeoportalHelperTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Random random = new Random();

	@Test
	public void testToJsonObjectNull() throws GeoportalException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(ErrorMessages.GEOPORTAL_SERIALIZE_NULL_OBJECT_PROVIDED.getMessage());
		
		GeoportalHelper.toJson(null);
	}

	@Test
	public void testFromJsonStringNull() throws GeoportalException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(ErrorMessages.GEOPORTAL_DESERIALIZE_NULL_JSON_PROVIDED.getMessage());
		
		GeoportalHelper.fromJson(null);
	}

	@Test
	public void testFromJsonStringEmpty() throws GeoportalException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(ErrorMessages.GEOPORTAL_DESERIALIZE_NULL_JSON_PROVIDED.getMessage());
		
		GeoportalHelper.fromJson("");
	}

	@Test
	public void testFromJsonStringInvalid() throws GeoportalException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(ErrorMessages.GEOPORTAL_DESERIALIZE_INVALID_JSON_PROVIDED.getMessage());
		
		GeoportalHelper.fromJson(UUID.randomUUID().toString());
	}


	@Test
	public void testToFromObjectValid() throws GeoportalException{

		GeoportalRequestObject requestObject = 
				new GeoportalRequestDataObject(random.nextDouble(), random.nextDouble());
		String jsonObject = GeoportalHelper.toJson(requestObject);
		assertNotNull(jsonObject);
		GeoportalRequestObject restoredObject = GeoportalHelper.fromJson(jsonObject);
		assertEquals(requestObject,  restoredObject);
	}

	@Test
	public void testToFromObjectinvalid() throws GeoportalException{

		//some values were changed

		GeoportalRequestObject requestObject = 
				new GeoportalRequestDataObject(random.nextDouble(), random.nextDouble());
		String jsonObject = GeoportalHelper.toJson(requestObject);
		assertNotNull(jsonObject);

		for(int i=0 ; i<9;i++){
			if(jsonObject.contains("" + i)){
				jsonObject = jsonObject.replace("" + i, "" + (i+1));
				break;
			}
		}
		GeoportalRequestObject restoredObject = GeoportalHelper.fromJson(jsonObject);
		assertFalse(requestObject.equals(restoredObject));
	}

	@Test
	public void testToRequestNull() throws GeoportalException, StringHelperException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(ErrorMessages.GEOPORTAL_REQUEST_NULL_OBJECT_PROVIDED.getMessage());
		
		GeoportalHelper.toRequest(null);
	}


	@Test
	public void testToRequestValid() throws GeoportalException, StringHelperException{

		String refJsonRequest="geometry=%7B%22x%22%3A" +
				"17.972947" +
				"%2C%22y%22%3A" +
				"53.123998" +
				"%2C%22spatialReference%22%3A%7B%22wkid%22%3A%224326%22%7D%7D&geometryType=esriGeometryPoint&mapExtent=%7B%22xmin%22%3A431250.6065813396%2C+%22ymin%22%3A584736.727175701%2C+%22xmax%22%3A431411.7381536028%2C+%22ymax%22%3A584858.171168589%2C+%22spatialReference%22%3A%7B%22wkid%22%3A%222180%22%7D%7D&imageDisplay=609,459,96&sr=2180&returnGeometry=false&tolerance=5&layers=visible:1,3,4,6,7,8,9,11,12,13,14,16,17,18,19,20,22,23,27,28,29,30,31,32,33,34,37,38,39,41,42,43,44,45,47,48,50,51,52,53,54,55,57,58,59,60,61,62,63,64,65,66,70,71,72,73,74,77,78,80,81,82,83,84,85,86,87,88,89,90,91,92,93,96&f=json";

		double longitude = 17.972947;
		double latitude = 53.123998;
		Wkid wkid = Wkid.W_4326;
		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(longitude, latitude, wkid);
		String request = GeoportalHelper.toRequest(requestObject);
		assertNotNull(request);
		assertEquals(refJsonRequest, request);
	}


	@Test
	public void testToImageRequestValid() throws GeoportalException, StringHelperException{

		String refJsonRequest="dpi=96&transparent=true&format=PNG8" +
				"&layers=show%3A0%2C1%2C2&bbox=17.974734282593246%2C53.12344164937794%2C17.97981294467757%2C53.12567982988655" +
				"&bboxSR=4326&imageSR=4326&size=1366%2C602&f=image";

		double longitude = 17.972947;
		double latitude = 53.123998;
		Wkid wkid = Wkid.W_4326;
		GeoportalRequestImageObject requestObject = new GeoportalRequestImageObject(longitude, latitude, wkid);
		String request = GeoportalHelper.toRequest(requestObject);
		assertNotNull(request);
		assertEquals(refJsonRequest, request);
	}


	/*********************************************************
	 * RESPONSES
	 * @throws IOHelperException 
	 *********************************************************/
	
	@Test
	public void testParseTopologyResponse() throws GeoportalException, IOHelperException{
		
		String responseString = IOHelper.readDataFromFile(
				"src/test/resources/geoportal/responses/TopographicDataServiceResponse.json");
		GeoportalResponse response = 
				GeoportalHelper.fromResponse(responseString, GeoportalKeys.getTopographyKeys());

		assertNotNull(response);
	}
}
