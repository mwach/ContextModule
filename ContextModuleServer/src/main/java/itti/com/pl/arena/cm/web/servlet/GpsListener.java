package itti.com.pl.arena.cm.web.servlet;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.location.LocationListener;
import itti.com.pl.arena.cm.location.LocationPublisher;
import itti.com.pl.arena.cm.utils.helpers.IOHelper;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;
import itti.com.pl.arena.cm.utils.helpers.LogHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

/**
 * Servlet implementation class GpsListener
 */
/**
 * Accepts position updates from connected GPS device
 * Example input data:
 * {"Latitude":53.11581257265061,"Longitude":17.9995668400079,"Altitude":108.0999755859375,"Bearing":89.0,"Speed":0.5004000067710876,"Time":1376733045000,"Accuracy":20.0}
 * 
 * @author mawa
 *
 */
public class GpsListener implements HttpRequestHandler, LocationPublisher{

	static final byte[] GET_REQUEST_ANY = StringHelper.getUtf8ByteArray("Only POST requests are supported");
	static final byte[] POST_RESPONSE_OK = StringHelper.getUtf8ByteArray("OK");
	static final byte[] POST_RESPONSE_FAIL = StringHelper.getUtf8ByteArray("EMPTY");

	private LocationListener locationListener = null;

	private LocationListener getLocationListener() {
		return locationListener;
	}

	@Override
	public void setLocationListener(LocationListener locationListener) {
		this.locationListener = locationListener;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LogHelper.debug(GpsListener.class, "doPost", "POST request received");

		//read input data
		String newLocation = null;
		try{
			newLocation = IOHelper.readStreamData(request.getInputStream(), Constants.ENCODING);
			LogHelper.debug(GpsListener.class, "doPost", "Received data: '%s'", newLocation);

			//write confirmation to the output
			response.getOutputStream().write(
					StringHelper.hasContent(newLocation) ? POST_RESPONSE_OK : POST_RESPONSE_FAIL);

			notifyListener(newLocation);

		} catch (IOHelperException e) {
			LogHelper.warning(GpsListener.class, "doPost", "Could not process the request: %s", e.getLocalizedMessage());
		}finally{
			IOHelper.closeStream(request.getInputStream());
			IOHelper.closeStream(response.getOutputStream());
		}

	}

//	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//GET requests are not supported
		LogHelper.debug(GpsListener.class, "doGet", "GET request detected from address: '%s'", req.getRemoteAddr());
		resp.getOutputStream().write(GET_REQUEST_ANY);
		IOHelper.closeStream(req.getInputStream());
		IOHelper.closeStream(resp.getOutputStream());
	}

	public void notifyListener(String newLocation) {
		if(getLocationListener() != null)
		{
//			Location location = JsonHelper.getJsonParser().fromJson(newLocation, Location.class);
//			PlatformLocation platformLocation = new PlatformLocation(pla)
//			getLocationListener().onLocationChange(location);
		}
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if(request.getMethod() != null && request.getMethod().equalsIgnoreCase("POST")){
			doPost(request, response);
		}else{
			doGet(request, response);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void shutdown() {
	}
}
