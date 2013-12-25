package itti.com.pl.arena.cm.geoportal.govpl;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.geoportal.Geoportal;
import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.GeoportalException.GeoportalExceptionCodes;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalRequestObject;
import itti.com.pl.arena.cm.utils.helpers.IOHelper;
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
public final class GeoportalGovPl implements Geoportal{
//TODO: geoportal for other countries
	private static final String REQUEST_METHOD = "GET";
	private static final String PROPERTY_CHARSET = "charset";
	private static final int CONNECTION_TIMEOUT = 10 * 1000;

	/**
	 * Calls selected Geoportal service and returns returned data
	 * @param serviceUrl URL to the Geoportal service
	 * @param requestData data to be sent in the request
	 * @return data returned from the Geoportal service
	 * @throws GeoportalException could not process request
	 */
	public String getGeoportalStringData(GeoportalService service, GeoportalRequestObject requestObject) throws GeoportalException{

		LogHelper.debug(GeoportalGovPl.class, "getGeoportalStringData", "init");
		byte[] serviceSesponse = getGeoportalData(service, requestObject);
		return StringHelper.toUtf8String(serviceSesponse);
	}

		
	/**
	 * Calls selected Geoportal service and returns returned data
	 * @param serviceUrl URL to the Geoportal service
	 * @param requestData data to be sent in the request
	 * @return data returned from the Geoportal service
	 * @throws GeoportalException could not process request
	 */
	private byte[] getGeoportalData(GeoportalService service, GeoportalRequestObject requestObject) throws GeoportalException{

		LogHelper.debug(GeoportalGovPl.class, "getGeoportalData", "init");

		//validate request parameters
		validateRequest(service, requestObject);

		//objects used during connection
		byte[] response = null;
		HttpURLConnection connection = null;

		try{
			//prepare URL
			String requestUrl = prepareRequestUrl(service, requestObject);

			URL url = new URL(requestUrl);
			LogHelper.debug(GeoportalGovPl.class, "getGeoportalData", "requestUrl: %s", url);

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

		}catch(IOException | RuntimeException exc){
			LogHelper.exception(GeoportalGovPl.class, "getGeoportalData", "Could not retrieve Geoportal data", exc);

			throw new GeoportalException(GeoportalExceptionCodes.API_GET_FAILED.getErrorMsg(), (Throwable)exc);
		}finally{
			IOHelper.closeConnection(connection);
		}
		LogHelper.debug(GeoportalGovPl.class, "getGeoportalData", "done. Read bytes: %s", response.length);

		return response;
	}

	private String prepareRequestUrl(GeoportalService service, GeoportalRequestObject requestObject) 
			throws GeoportalException {
		return String.format("%s%s", service.getServiceURL(), GeoportalHelper.toRequest(requestObject));	
	}

	/**
	 * Validates provided request parameters
	 * @param serviceUrl URL to the Geoportal service 
	 * @param requestObject data to be sent in the request
	 * @throws GeoportalException validation failed
	 */
	private void validateRequest(GeoportalService service, GeoportalRequestObject requestObject) throws GeoportalException {

		if(service == null){
			LogHelper.error(GeoportalGovPl.class, "validateRequest", "validation failed: service is null");

			throw new GeoportalException(
					GeoportalExceptionCodes.VALIDATION_SERVICE_NOT_PROVIDED);
		}		

		if(requestObject == null){
			LogHelper.error(GeoportalGovPl.class, "validateRequest", "validation failed: request data not provided");

			throw new GeoportalException(
					GeoportalExceptionCodes.VALIDATION_REQUEST_DATA_NOT_PROVIDED);
		}		
	}
}
