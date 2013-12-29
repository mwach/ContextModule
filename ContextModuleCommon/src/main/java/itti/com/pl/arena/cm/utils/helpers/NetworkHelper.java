package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.ErrorMessages;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Network utilities
 * 
 * @author cm-admin
 * 
 */
public final class NetworkHelper {

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
			throw new NetworkHelperException(exc,
					ErrorMessages.NETWORK_HELPER_CANNOT_OBTAIN_IP,
					exc.getLocalizedMessage());
		}
		return ipAddress;
	}
}
