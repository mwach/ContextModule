package itti.com.pl.arena.cm.server.geoportal.gov.pl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;
import itti.com.pl.arena.cm.server.geoportal.GeoportalException;
import itti.com.pl.arena.cm.server.geoportal.gov.pl.dto.GeoportalRequestImageObject;
import itti.com.pl.arena.cm.server.geoportal.gov.pl.dto.GeoportalRequestObject;
import itti.com.pl.arena.cm.server.geoportal.gov.pl.dto.GeoportalResponse;
import itti.com.pl.arena.cm.utils.helper.JsonHelper;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.LogHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelperException;

public final class GeoportalHelper {

    private GeoportalHelper() {
    }

    /**
     * format of the data request
     */
    private static final String REQUEST_DATA_FORMAT =

    "geometry=%s&" + "geometryType=%s&" + "mapExtent=%s&" + "imageDisplay=%s&" + "sr=%d&" + "returnGeometry=%b&"
            + "tolerance=%d&" + "layers=%s&" + "f=%s";

    /**
     * format of the image request
     */
    private static final String REQUEST_IMAGE_FORMAT =

    "dpi=%s&" + "transparent=%s&" + "format=%s&" + "layers=%s" + "bbox=%s&" + "bboxSR=%s&" + "imageSR=%s&" + "size=%s&" + "f=%s";

    /**
     * Prepares request URL parameters using {@link GeoportalRequestObject}
     * 
     * @param requestObject
     *            data to be used to build a request
     * @return parameters part of the request URL
     * @throws GeoportalException
     *             could not create the request URL
     * @throws StringHelperException
     */
    public static String toRequest(GeoportalRequestObject requestObject) throws GeoportalException {
        // check, if provided object is not empty
        if (requestObject == null) {
            throw new GeoportalException(ErrorMessages.GEOPORTAL_REQUEST_NULL_OBJECT_PROVIDED);
        }
        // prepare some request chunks as JSON strings
        String geometry = null;
        String mapExtent = null;
        try {
            geometry = JsonHelper.toJson(requestObject.getGeometry());
            mapExtent = JsonHelper.toJson(requestObject.getMapExtent());
        } catch (JsonHelperException exc) {
            throw new GeoportalException(ErrorMessages.GEOPORTAL_CANNOT_PREPARE_REQUEST_URL, exc.getLocalizedMessage());
        }
        // prepare request data using object data
        String requestUrl = null;
        try {
            requestUrl = String.format(REQUEST_DATA_FORMAT, StringHelper.encodeUrl(geometry), requestObject.getGeometryType(),
                    StringHelper.encodeUrl(mapExtent), requestObject.getImageDisplay(), requestObject.getSr(),
                    requestObject.isReturnGeometry(), requestObject.getTolerance(), requestObject.getLayers(),
                    requestObject.getFormat());
        } catch (StringHelperException exc) {
            throw new GeoportalException(ErrorMessages.GEOPORTAL_CANNOT_PREPARE_REQUEST_URL, exc, exc.getLocalizedMessage());
        }
        return requestUrl;
    }

    /**
     * Prepares request URL parameters using {@link GeoportalRequestImageObject}
     * 
     * @param requestObject
     *            data to be used to build a request
     * @return parameters part of the request URL
     * @throws GeoportalException
     *             could not create the request URL
     * @throws StringHelperException
     */
    public static String toRequest(GeoportalRequestImageObject requestObject) throws GeoportalException, StringHelperException {
        // check, if provided object is not empty
        if (requestObject == null) {
            throw new GeoportalException(ErrorMessages.GEOPORTAL_REQUEST_NULL_OBJECT_PROVIDED);
        }
        // prepare some request chunks as JSON strings
        String bboxJson = StringHelper.encodeUrl(requestObject.getBboxString());
        String size = StringHelper.encodeUrl(requestObject.getSizeString());
        String layers = StringHelper.encodeUrl("show:" + requestObject.getLayers());
        // prepare request data using object data
        return String.format(REQUEST_IMAGE_FORMAT, requestObject.getDpi(), requestObject.isTransparent(),
                requestObject.getImageFormat(), layers, bboxJson, requestObject.getSr(), requestObject.getSr(), size,
                requestObject.getFormat());
    }

    public static GeoportalResponse fromResponse(String jsonResponse, Collection<String> keys) {

        GeoportalResponse response = new GeoportalResponse();
        if (StringHelper.hasContent(jsonResponse) && keys != null) {

            List<String> layers = splitResponseIntoLayers(jsonResponse, "layerId");

            for (String layerData : layers) {
                try {
                    String layerId = JsonHelper.getJsonValue(layerData, "layerId");
                    response.startNewLayer(layerId);
                    for (String key : keys) {
                        response.addValue(layerId, key, JsonHelper.getJsonValue(layerData, key));
                    }
                } catch (JsonHelperException exc) {
                    LogHelper.warning(GeoportalHelper.class, "fromResponse",
                            "Could not parse data retrieved from the geoportal: '%s'. Details: %s", jsonResponse,
                            exc.getLocalizedMessage());
                }

            }
        }
        return response;
    }

    private static List<String> splitResponseIntoLayers(String string, String token) {

        List<String> response = new ArrayList<>();
        int startPos = string.indexOf(token);
        while (startPos > 0) {
            int endPos = string.indexOf(token, startPos + 1);
            response.add(string.substring(startPos - 1, endPos == -1 ? string.length() : endPos));
            startPos = endPos;
        }
        return response;
    }

    public static Set<GeoObject> parseResponseIntoObjects(GeoportalResponse geoportalData, Location location) {

        Set<GeoObject> geoData = new HashSet<>();
        for (String layerId : geoportalData.getLayersIds()) {
            for (int itemId = 0; itemId < geoportalData.getLayerElementsCount(layerId); itemId++) {
                String layerName = geoportalData.getValue(layerId, itemId, Constants.LAYER_NAME);
                // TODO: doesn't have to be a building
                String objectId = String.format("Building_%f_%f_%d", location.getLongitude(), location.getLatitude(), itemId);
                GeoObject object = GeoObjectFactory.getGeoObject(layerName, objectId);
                if (object != null) {
                    geoData.add(object);
                }
            }
        }
        return geoData;
    }
}
