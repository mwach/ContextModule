package itti.com.pl.arena.cm.geoportal.api;

import static org.junit.Assert.*;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.GeoportalException.GeoportalExceptionCodes;
import itti.com.pl.arena.cm.geoportal.govpl.GeoportalGovPl;
import itti.com.pl.arena.cm.geoportal.govpl.GeoportalService;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalRequestDataObject;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalRequestObject;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@Ignore
public class GeoportalGovPlTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private GeoportalGovPl geoportal = new GeoportalGovPl();

	@Test
	public void testGeoportalAPINullParams() throws GeoportalException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(GeoportalExceptionCodes.VALIDATION_SERVICE_NOT_PROVIDED.getErrorMsg());

		geoportal.getGeoportalStringData(null, null);
	}

	@Test
	public void testGeoportalAPINullUrl() throws GeoportalException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(GeoportalExceptionCodes.VALIDATION_SERVICE_NOT_PROVIDED.getErrorMsg());

		geoportal.getGeoportalStringData(null, new GeoportalRequestDataObject(1,1));
	}

	@Test
	public void testGeoportalAPINullRequest() throws GeoportalException{

		expectedException.expect(GeoportalException.class);
		expectedException.expectMessage(GeoportalExceptionCodes.VALIDATION_REQUEST_DATA_NOT_PROVIDED.getErrorMsg());

		geoportal.getGeoportalStringData(GeoportalService.CATASTRAL_DATA_SERVICE, null);
	}

	@Test
	public void testGeoportalAPIDummyRequest() throws GeoportalException{

		//invalid coordinates in the request data - Geoportal should process the request and return empty data set
		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(-1, -1);

		geoportal.getGeoportalStringData(GeoportalService.BOUNDARIES_REGISTRY_DATA_SERVICE, requestObject);
	}


	@Test
	public void testGeoportalAPIValidCatastralRequest() throws GeoportalException{

		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
		String response = geoportal.getGeoportalStringData(GeoportalService.CATASTRAL_DATA_SERVICE, requestObject);
		assertNotNull(response);
	}

	@Test
	public void testGeoportalAPIValidBoundariesRequest() throws GeoportalException{

		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
		String response = geoportal.getGeoportalStringData(GeoportalService.BOUNDARIES_REGISTRY_DATA_SERVICE, requestObject);
		assertNotNull(response);
	}

	@Test
	public void testGeoportalAPIValidVmapl2Request() throws GeoportalException{

		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
		String response = geoportal.getGeoportalStringData(GeoportalService.TOPOGRAPHIC_VMAPL2_DATA_SERVICE, requestObject);
		assertNotNull(response);
	}

	@Test
	public void testGeoportalAPIValidTopographicRequest() throws GeoportalException{

		GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
		String response = geoportal.getGeoportalStringData(GeoportalService.TOPOGRAPHIC_DATA_SERVICE, requestObject);
		assertNotNull(response);
	}
}
