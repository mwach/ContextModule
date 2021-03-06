package itti.com.pl.arena.cm.server.geoportal.gov.pl;

import static org.junit.Assert.*;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.geoportal.GeoportalException;
import itti.com.pl.arena.cm.server.geoportal.gov.pl.GeoportalService;
import itti.com.pl.arena.cm.server.geoportal.gov.pl.GeoportalUrls;
import itti.com.pl.arena.cm.server.geoportal.gov.pl.dto.GeoportalRequestDataObject;
import itti.com.pl.arena.cm.server.geoportal.gov.pl.dto.GeoportalRequestObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GeoportalServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private GeoportalService geoportal = new GeoportalService();

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP", justification = "NULL passed for testing purposes only")
    @Test
    public void testGeoportalAPINullParams() throws GeoportalException {

        expectedException.expect(GeoportalException.class);
        expectedException.expectMessage(ErrorMessages.GEOPORTAL_SERVICE_NOT_PROVIDED.getMessage());

        geoportal.getGeoportalData(null, null);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP", justification = "NULL passed for testing purposes only")
    @Test
    public void testGeoportalAPINullUrl() throws GeoportalException {

        expectedException.expect(GeoportalException.class);
        expectedException.expectMessage(ErrorMessages.GEOPORTAL_SERVICE_NOT_PROVIDED.getMessage());

        geoportal.getGeoportalData(null, new GeoportalRequestDataObject(1, 1));
    }

    @Test
    public void testGeoportalAPINullRequest() throws GeoportalException {

        expectedException.expect(GeoportalException.class);
        expectedException.expectMessage(ErrorMessages.GEOPORTAL_REQUEST_DATA_NOT_PROVIDED.getMessage());

        geoportal.getGeoportalData(GeoportalUrls.CATASTRAL_DATA_SERVICE, null);
    }

    @Test
    public void testGeoportalAPIDummyRequest() throws GeoportalException {

        // invalid coordinates in the request data - Geoportal should process the request and return empty data set
        GeoportalRequestObject requestObject = new GeoportalRequestDataObject(-1, -1);

        geoportal.getGeoportalData(GeoportalUrls.BOUNDARIES_REGISTRY_DATA_SERVICE, requestObject);
    }

    @Test
    public void testGeoportalAPIValidCatastralRequest() throws GeoportalException {

        GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
        String response = geoportal.getGeoportalData(GeoportalUrls.CATASTRAL_DATA_SERVICE, requestObject);
        assertNotNull(response);
    }

    @Test
    public void testGeoportalAPIValidBoundariesRequest() throws GeoportalException {

        GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
        String response = geoportal.getGeoportalData(GeoportalUrls.BOUNDARIES_REGISTRY_DATA_SERVICE, requestObject);
        assertNotNull(response);
    }

    @Test
    public void testGeoportalAPIValidVmapl2Request() throws GeoportalException {

        GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
        String response = geoportal.getGeoportalData(GeoportalUrls.TOPOGRAPHIC_VMAPL2_DATA_SERVICE, requestObject);
        assertNotNull(response);
    }

    @Test
    public void testGeoportalAPIValidTopographicRequest() throws GeoportalException {

        GeoportalRequestObject requestObject = new GeoportalRequestDataObject(17.972946559166793, 53.124318916278824);
        String response = geoportal.getGeoportalData(GeoportalUrls.TOPOGRAPHIC_DATA_SERVICE, requestObject);
        assertNotNull(response);
    }
}
