package itti.com.pl.arena.cm.dto;

import java.util.Set;

public class VehicleWithCamaras extends Platform{

    public VehicleWithCamaras(String id, Location lastLocation, Set<Camera> cameras) {
	super(id, lastLocation, cameras);
    }

    @Override
    public PlatformType getType() {
	return PlatformType.Vehicle_with_cameras;
    }
}
