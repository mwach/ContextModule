package itti.com.pl.arena.cm.location;

import itti.com.pl.arena.cm.Service;

public interface LocationPublisher extends Service{

	public void setLocationListener(LocationListener locationListener);
}
