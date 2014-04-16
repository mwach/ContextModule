package itti.com.pl.arena.cm.client.service;

import java.util.ArrayList;
import java.util.List;

import itti.com.pl.arena.cm.service.LocalContextModule;
import itti.com.pl.arena.cm.service.MessageConstants.ContextModuleRequests;
import itti.com.pl.arena.cm.utils.helper.LocationHelper;

import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.FeatureVector;
import eu.arena_fp7._1.Location;
import eu.arena_fp7._1.RealWorldCoordinate;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * Context Module client facade for {@link ModuleImpl}
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleAdapter {

	private String moduleName;
	private LocalContextModule contextModule;
	private boolean connected = false;

	public ContextModuleAdapter(String moduleName) {
		this.moduleName = moduleName;
	}

	public void connect(String brokerUrl) {
		if (!connected) {
			contextModule = new ContextModuleFacade(moduleName, brokerUrl);
			contextModule.init();
			connected = true;
		}
	}

	public void disconnect() {
		if (connected) {
			contextModule.shutdown();
			connected = false;
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public List<String> getListOfParkingLots() {
		SimpleNamedValue request = contextModule.createSimpleNamedValue(
				moduleName, null);
		request.setHref(ContextModuleRequests.getListOfParkingLots.name());
		eu.arena_fp7._1.Object response = contextModule
				.getListOfParkingLots(request);
		return getStringsFromFeatureVector(response == null ? null : response
				.getFeatureVector());
	}

	public List<String> getListOfZones(String parkingLot) {
		SimpleNamedValue request = contextModule.createSimpleNamedValue(
				moduleName, parkingLot);
		request.setHref(ContextModuleRequests.getListOfZones.name());
		eu.arena_fp7._1.Object response = contextModule.getListOfZones(request);
		return getStringsFromFeatureVector(response == null ? null : response
				.getFeatureVector());
	}

	/**
	 * Parses response stored inside {@link FeatureVector} object
	 * 
	 * @param featureVector
	 *            information stored inside {@link FeatureVector}
	 */
	private List<String> getStringsFromFeatureVector(FeatureVector featureVector) {

		List<String> response = new ArrayList<>();
		if (featureVector != null) {
			for (AbstractDataFusionType feature : featureVector.getFeature()) {
				if (feature instanceof RealWorldCoordinate) {
					response.add(LocationHelper
							.createStringFromLocation(new itti.com.pl.arena.cm.dto.Location(
									((RealWorldCoordinate) feature).getX(),
									((RealWorldCoordinate) feature).getY(), 0,
									((RealWorldCoordinate) feature).getZ())));
				} else {
					if (feature instanceof Location) {
						response.add(LocationHelper
								.createStringFromLocation(new itti.com.pl.arena.cm.dto.Location(
										((Location) feature).getX(),
										((Location) feature).getY())));
					} else {
						response.add(((SimpleNamedValue) feature).getValue());
					}
				}
			}
		}
		return response;
	}

}
