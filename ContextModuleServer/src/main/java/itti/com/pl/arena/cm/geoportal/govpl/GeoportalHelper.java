package itti.com.pl.arena.cm.geoportal.govpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import itti.com.pl.arena.cm.geoportal.GeoportalException;
import itti.com.pl.arena.cm.geoportal.GeoportalException.GeoportalExceptionCodes;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalRequestImageObject;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalRequestObject;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.utils.helpers.JsonHelper;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

public final class GeoportalHelper {

	private GeoportalHelper() {
	}

	/**
	 * format of the data request
	 */
	private static final String REQUEST_DATA_FORMAT =

	"geometry=%s&" + "geometryType=%s&" + "mapExtent=%s&" + "imageDisplay=%s&"
			+ "sr=%d&" + "returnGeometry=%b&" + "tolerance=%d&" + "layers=%s&"
			+ "f=%s";

	/**
	 * format of the image request
	 */
	private static final String REQUEST_IMAGE_FORMAT =

	"dpi=%s&" + "transparent=%s&" + "format=%s&" + "layers=%s" + "bbox=%s&"
			+ "bboxSR=%s&" + "imageSR=%s&" + "size=%s&" + "f=%s";

	/**
	 * Serializes {@link GeoportalRequestObject} into string
	 * 
	 * @param requestObject
	 *            object to serialize
	 * @return serialized object
	 * @throws GeoportalException
	 *             could not serialize object
	 */
	public static String toJson(GeoportalRequestObject requestObject)
			throws GeoportalException {
		// check, if provided object is not null
		if (requestObject == null) {
			throw new GeoportalException(
					GeoportalExceptionCodes.HELPER_SERIALIZE_NULL_OBJECT_PROVIDED
							.getErrorMsg());
		}
		return JsonHelper.toJson(requestObject);
	}

	/**
	 * Deserializes JSON string into {@link GeoportalRequestObject}
	 * 
	 * @param jsonRequestObject
	 *            JSON string
	 * @return instance of {@link GeoportalRequestObject}
	 * @throws GeoportalException
	 *             could not construct instance of
	 *             {@link GeoportalRequestObject}
	 */
	public static GeoportalRequestObject fromJson(String jsonRequestObject)
			throws GeoportalException {
		// check, if provided string contains data
		if (!StringHelper.hasContent(jsonRequestObject)) {
			throw new GeoportalException(
					GeoportalExceptionCodes.HELPER_DESERIALIZE_NULL_JSON_PROVIDED
							.getErrorMsg());
		}
		GeoportalRequestObject object = null;
		// try to deserialize string into object
		try {
			object = JsonHelper.fromJson(jsonRequestObject,
					GeoportalRequestObject.class);
		} catch (RuntimeException exc) {
			LogHelper
					.exception(
							GeoportalHelper.class,
							"fromJson",
							String.format(
									"Could not deserialize object into JSON. Object: '%s'",
									jsonRequestObject), exc);
			throw new GeoportalException(
					GeoportalExceptionCodes.HELPER_DESERIALIZE_INVALID_JSON_PROVIDED
							.getErrorMsg(), exc);
		}
		return object;
	}

	/**
	 * Prepares request URL parameters using {@link GeoportalRequestObject}
	 * 
	 * @param requestObject
	 *            data to be used to build a request
	 * @return parameters part of the request URL
	 * @throws GeoportalException
	 *             could not create the request URL
	 */
	public static String toRequest(GeoportalRequestObject requestObject)
			throws GeoportalException {
		// check, if provided object is not empty
		if (requestObject == null) {
			throw new GeoportalException(
					GeoportalExceptionCodes.HELPER_REQUEST_NULL_OBJECT_PROVIDED
							.getErrorMsg());
		}
		// prepare some request chunks as JSON strings
		String geometry = JsonHelper.toJson(requestObject.getGeometry());
		String mapExtent = JsonHelper.toJson(requestObject.getMapExtent());
		// prepare request data using object data
		return String.format(REQUEST_DATA_FORMAT,
				StringHelper.encodeUrl(geometry),
				requestObject.getGeometryType(),
				StringHelper.encodeUrl(mapExtent),
				requestObject.getImageDisplay(), requestObject.getSr(),
				requestObject.isReturnGeometry(), requestObject.getTolerance(),
				requestObject.getLayers(), requestObject.getFormat());
	}

	/**
	 * Prepares request URL parameters using {@link GeoportalRequestImageObject}
	 * 
	 * @param requestObject
	 *            data to be used to build a request
	 * @return parameters part of the request URL
	 * @throws GeoportalException
	 *             could not create the request URL
	 */
	public static String toRequest(GeoportalRequestImageObject requestObject)
			throws GeoportalException {
		// check, if provided object is not empty
		if (requestObject == null) {
			throw new GeoportalException(
					GeoportalExceptionCodes.HELPER_REQUEST_NULL_OBJECT_PROVIDED
							.getErrorMsg());
		}
		// prepare some request chunks as JSON strings
		String bboxJson = StringHelper.encodeUrl(requestObject.getBboxString());
		String size = StringHelper.encodeUrl(requestObject.getSizeString());
		String layers = StringHelper.encodeUrl("show:"
				+ requestObject.getLayers());
		// prepare request data using object data
		return String.format(REQUEST_IMAGE_FORMAT, requestObject.getDpi(),
				requestObject.isTransparent(), requestObject.getImageFormat(),
				layers, bboxJson, requestObject.getSr(), requestObject.getSr(),
				size, requestObject.getFormat());
	}

	
	public static GeoportalResponse fromResponse(String jsonResponse,
			Collection<String> keys) {

		GeoportalResponse response = new GeoportalResponse();
		if (StringHelper.hasContent(jsonResponse) && keys != null) {

			List<String> layers = splitIntoLayers(jsonResponse, "layerId");

			for (String layerData : layers) {
				String layerId = JsonHelper.getJsonValue(layerData, "layerId");
				response.startNewLayer(layerId);
				for (String key : keys) {
					response.addValue(layerId, key,
						JsonHelper.getJsonValue(layerData, key));
				}
			}
		}
		return response;
	}

	private static List<String> splitIntoLayers(String string, String token) {

		List<String> response = new ArrayList<>();
		int startPos = string.indexOf(token);
		while(startPos > 0){
			int endPos = string.indexOf(token, startPos+1);
			response.add(string.substring(startPos-1, endPos == -1 ? string.length() : endPos));
			startPos = endPos;	
		}
		return response;
	}
}
