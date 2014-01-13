package itti.com.pl.arena.cm.geoportal.gov.pl;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.geoportal.Geoportal;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalRequestObject;
import itti.com.pl.arena.cm.utils.helpers.IOHelper;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class providing access to the Geoportal services
 * @author mawa
 *
 */
public final class GeoportalService implements Geoportal{
//TODO: geoportal for other countries
	private static final String REQUEST_METHOD = "GET";
	private static final String PROPERTY_CHARSET = "charset";
	private static final int CONNECTION_TIMEOUT = 10 * 1000;

	@Override
	public String getGeoportalData(String geoportalUrl)
		throws GeoportalException {
		LogHelper.debug(GeoportalService.class, "getGeoportalStringData", "init");
		byte[] serviceSesponse = getGeoportalByteData(geoportalUrl);
		return StringHelper.toUtf8String(serviceSesponse);
	}

	/**
	 * Calls selected Geoportal service and returns returned data
	 * @param serviceUrl URL to the Geoportal service
	 * @param requestData data to be sent in the request
	 * @return data returned from the Geoportal service
	 * @throws GeoportalException could not process request
	 */
	public String getGeoportalData(GeoportalUrls service, GeoportalRequestObject requestObject) throws GeoportalException{

		LogHelper.debug(GeoportalService.class, "getGeoportalStringData", "init");
		//validate request parameters
		validateRequest(service, requestObject);

		//prepare URL
		String requestUrl = prepareRequestUrl(service, requestObject);
		return getGeoportalData(requestUrl);
	}
		
	/**
	 * Calls selected Geoportal service and returns returned data
	 * @param serviceUrl URL to the Geoportal service
	 * @param requestData data to be sent in the request
	 * @return data returned from the Geoportal service
	 * @throws GeoportalException could not process request
	 */
	private byte[] getGeoportalByteData(String requestUrl) throws GeoportalException{

		LogHelper.debug(GeoportalService.class, "getGeoportalData", "init");

		//objects used during connection
		byte[] response = null;
		HttpURLConnection connection = null;

		try{
			URL url = new URL(requestUrl);
			LogHelper.debug(GeoportalService.class, "getGeoportalData", "requestUrl: %s", url);

			//open connection
			connection = (HttpURLConnection) url.openConnection();           
			connection.setConnectTimeout(CONNECTION_TIMEOUT);

			//prepare connection parameters
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod(REQUEST_METHOD); 
			connection.setRequestProperty(PROPERTY_CHARSET, Constants.ENCODING);

			//get the response data stream
			response = IOHelper.readStreamData(connection.getInputStream());

		}catch(IOException | IOHelperException | RuntimeException exc){
			LogHelper.exception(GeoportalService.class, "getGeoportalData", "Could not retrieve Geoportal data", exc);

			throw new GeoportalException(ErrorMessages.GEOPORTAL_REQUEST_FAILED, exc);
		}finally{
			IOHelper.closeConnection(connection);
		}
		LogHelper.debug(GeoportalService.class, "getGeoportalData", "done. Read bytes: %s", response.length);

		return response;
	}

	private String prepareRequestUrl(GeoportalUrls service, GeoportalRequestObject requestObject) 
			throws GeoportalException {
		return String.format("%s%s", service.getServiceURL(), GeoportalHelper.toRequest(requestObject));
	}

	/**
	 * Validates provided request parameters
	 * @param serviceUrl URL to the Geoportal service 
	 * @param requestObject data to be sent in the request
	 * @throws GeoportalException validation failed
	 */
	private void validateRequest(GeoportalUrls service, GeoportalRequestObject requestObject) throws GeoportalException {

		if(service == null){
			LogHelper.error(GeoportalService.class, "validateRequest", "validation failed: service is null");

			throw new GeoportalException(ErrorMessages.GEOPORTAL_SERVICE_NOT_PROVIDED);
		}		

		if(requestObject == null){
			LogHelper.error(GeoportalService.class, "validateRequest", "validation failed: request data not provided");

			throw new GeoportalException(ErrorMessages.GEOPORTAL_REQUEST_DATA_NOT_PROVIDED);
		}		
	}

	@Override
	public void init() {
	    //nothing to be done here for that implementation
	}

	@Override
	public void shutdown() {
	    //nothing to be done here for that implementation
	}
}
