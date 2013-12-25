package itti.com.pl.arena.cm.location;

import itti.com.pl.arena.cm.dto.PlatformLocation;

public interface LocationListener {

	void onLocationChange(PlatformLocation newLocation);
}
