package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.exception.ErrorMessages;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Network utilities
 * 
 * @author cm-admin
 * 
 */
public final class NetworkHelper {

    private static final String PROPERTY_CHARSET = "charset";
    private static final int CONNECTION_TIMEOUT = 10 * 1000;

    private NetworkHelper() {
    }

    /**
     * Returns IP of the current host
     * 
     * @return IP address of the host
     * @throws NetworkHelperException
     */
    public static String getIpAddress() throws NetworkHelperException {

        String ipAddress = null;
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (RuntimeException | UnknownHostException exc) {
            throw new NetworkHelperException(ErrorMessages.NETWORK_HELPER_CANNOT_OBTAIN_IP, exc, exc.getLocalizedMessage());
        }
        return ipAddress;
    }

    /**
     * Calls web resource and returns binary data
     * 
     * @param requestUrl
     *            URL to the web resource
     * @param requestMethod
     *            HTTP request method (POST or GET)
     * @return data returned from the Geoportal service
     * @throws GeoportalException
     *             could not process request
     */
    public static byte[] doHttpRequestData(String requestUrl, String requestMethod) throws NetworkHelperException {

        // objects used during connection
        byte[] response = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(requestUrl);

            // open connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            // prepare connection parameters
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty(PROPERTY_CHARSET, Constants.ENCODING);

            // get the response data stream
            response = IOHelper.readStreamData(connection.getInputStream());

        } catch (IOException | IOHelperException | RuntimeException exc) {

            throw new NetworkHelperException(ErrorMessages.NETWORK_HELPER_CANNOT_DO_HTTP_REQUEST, exc, requestUrl, requestMethod,
                    exc.getLocalizedMessage());
        } finally {
            IOHelper.closeConnection(connection);
        }

        return response;
    }

}
