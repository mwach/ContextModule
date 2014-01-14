package itti.com.pl.arena.cm.geoportal.gov.pl;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.geoportal.Geoportal;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestDataObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestObject;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.NetworkHelper;
import itti.com.pl.arena.cm.utils.helpers.NetworkHelperException;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.util.Set;

/**
 * Class providing access to the Geoportal services
 * 
 * @author mawa
 * 
 */
public final class GeoportalService implements Geoportal {

    private static final String REQUEST_METHOD = "GET";

    @Override
    public Set<GeoObject> getGeoportalData(Location location, double radius) throws GeoportalException {

	LogHelper.debug(GeoportalService.class, "getGeoportalData", "get data for location: %s", location);

	String geoportalDataStr = getGeoportalData(GeoportalUrls.TOPOGRAPHIC_DATA_SERVICE, new GeoportalRequestDataObject(
	        location.getLongitude(), location.getLatitude()));
	LogHelper.debug(GeoportalService.class, "getGeoportalData", "geoportal data received: %s", geoportalDataStr);

	GeoportalResponse geoportalData = GeoportalHelper.fromResponse(geoportalDataStr, Constants.getTopographyKeys());

	Set<GeoObject> geoObjects = GeoportalHelper.parseResponseIntoObjects(geoportalData);

	return geoObjects;
    }

    /**
     * Calls selected Geoportal service and returns returned data
     * 
     * @param serviceUrl
     *            URL to the Geoportal service
     * @param requestData
     *            data to be sent in the request
     * @return data returned from the Geoportal service
     * @throws GeoportalException
     *             could not process request
     */
    public String getGeoportalData(GeoportalUrls service, GeoportalRequestObject requestObject) throws GeoportalException {

	LogHelper.debug(GeoportalService.class, "getGeoportalStringData", "init");
	// validate request parameters
	validateRequest(service, requestObject);

	// prepare URL
	String requestUrl = prepareRequestUrl(service, requestObject);
	byte[] serviceSesponse = null;
	try {
	    serviceSesponse = NetworkHelper.doHttpRequestData(requestUrl, REQUEST_METHOD);
	} catch (NetworkHelperException e) {
	    LogHelper.exception(GeoportalService.class, "getGeoportalData", "Could not retrieve geoportal data", e);
	    throw new GeoportalException(ErrorMessages.GEOPORTAL_REQUEST_FAILED);
	}
	return StringHelper.toUtf8String(serviceSesponse);
    }

    private String prepareRequestUrl(GeoportalUrls service, GeoportalRequestObject requestObject) throws GeoportalException {
	return String.format("%s%s", service.getServiceURL(), GeoportalHelper.toRequest(requestObject));
    }

    /**
     * Validates provided request parameters
     * 
     * @param serviceUrl
     *            URL to the Geoportal service
     * @param requestObject
     *            data to be sent in the request
     * @throws GeoportalException
     *             validation failed
     */
    private void validateRequest(GeoportalUrls service, GeoportalRequestObject requestObject) throws GeoportalException {

	if (service == null) {
	    LogHelper.error(GeoportalService.class, "validateRequest", "validation failed: service is null");

	    throw new GeoportalException(ErrorMessages.GEOPORTAL_SERVICE_NOT_PROVIDED);
	}

	if (requestObject == null) {
	    LogHelper.error(GeoportalService.class, "validateRequest", "validation failed: request data not provided");

	    throw new GeoportalException(ErrorMessages.GEOPORTAL_REQUEST_DATA_NOT_PROVIDED);
	}
    }

    @Override
    public void init() {
	// nothing to be done here for that implementation
    }

    @Override
    public void shutdown() {
	// nothing to be done here for that implementation
    }
}
