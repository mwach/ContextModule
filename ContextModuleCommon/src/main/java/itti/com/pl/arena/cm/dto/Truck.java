package itti.com.pl.arena.cm.dto;

import java.util.Set;

public class Truck extends Platform{

    public Truck(String id, Location lastLocation, Set<Camera> cameras) {
	super(id, lastLocation, cameras);
    }

    @Override
    public PlatformType getType() {
	return PlatformType.Truck;
    }
}
