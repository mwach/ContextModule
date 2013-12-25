package itti.com.pl.arena.cm.utils.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class NetworkHelper {

	private NetworkHelper(){
	}

	public static String getIpAddress() throws NetworkHelperException{
		String ipAddress = null;
		try{
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		}catch(RuntimeException | UnknownHostException exc){
			throw new NetworkHelperException(exc, "Could not determine IP address of the host: %s", exc.getLocalizedMessage());
		}
		return ipAddress;
	}
}
