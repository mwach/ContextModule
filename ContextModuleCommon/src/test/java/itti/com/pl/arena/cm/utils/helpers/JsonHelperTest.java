package itti.com.pl.arena.cm.utils.helpers;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JsonHelperTest {

	private static final String JSON_STRING_VALID = "{\"results\":[{\"layerId\":62,\"layerName\":\"Budynek\",\"value\":\"BBBD02\",\"displayFieldName\":\"X_KOD_TBD\",\"attributes\":{\"\"Object ID\"\":\"17852568\",\"X_KOD_TBD\":\"BBBD02\",\"X_KOD_VMAP\":\"Null\"}";

	@Test
	public void testGetJsonValueNulls(){
		Assert.assertNull(JsonHelper.getJsonValue(null, null));
	}

	@Test
	public void testGetJsonValueEmptyNulls(){
		Assert.assertNull(JsonHelper.getJsonValue("", null));
	}

	@Test
	public void testGetJsonValueEmpties(){
		Assert.assertNull(JsonHelper.getJsonValue("", ""));
	}

	@Test
	public void testGetJsonValueRandoms(){
		Assert.assertNull(JsonHelper.getJsonValue(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
	}

	@Test
	public void testGetJsonValueValid(){
		Assert.assertEquals("17852568", JsonHelper.getJsonValue(JSON_STRING_VALID, "Object ID"));
	}

	@Test
	public void testGetJsonValueValidCaseInsensitive(){
		//case sensitivity is ignored - return value
		Assert.assertEquals("17852568", JsonHelper.getJsonValue(JSON_STRING_VALID, "object id", false));
	}

	@Test
	public void testGetJsonValueValidCaseSensitive(){
		//nothing should be returned - case matters
		Assert.assertNull(JsonHelper.getJsonValue(JSON_STRING_VALID, "object id"));
	}

	@Test
	public void testGetJsonValueValidCorruptedJson(){
		
		//some part of the JSON was removed: ':' char
		String response = JsonHelper.getJsonValue(JSON_STRING_VALID.replace("Object ID\"\":\"", "Object ID\"\"\""), "Object ID");
		Assert.assertNotNull(response);
		Assert.assertNotEquals("17852568", response);
	}

	@Test
	@Ignore
	public void testGetJsonValueGeoportalData() throws IOHelperException{

		String geoportalData = IOHelper.readDataFromFile("src/test/resources/geoportal/responses/BOUNDARIES_REGISTRY_DATA_SERVICE");
		String response = JsonHelper.getJsonValue(geoportalData, "");
		Assert.assertNull(response);
	}

}
