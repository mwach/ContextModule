package itti.com.pl.arena.cm.persistence;

import java.util.Random;

import itti.com.pl.arena.cm.dto.Location;

public class PersistenceTestHelper {

	private static Random random = new Random();

	public static Location createDummyLocation(){
		return createDummyLocation(random.nextLong());
	}

	public static Location createDummyLocation(long timestamp){
		Location dummyLocation = new Location(
				random.nextDouble() * 100,	//longitude
				random.nextDouble() * 100,	//latitude
				random.nextDouble() * 100,	//altitude
				random.nextInt(180),		//bearing
				random.nextDouble() * 100,	//speed
				timestamp,					//time
				random.nextDouble() * 100	//accuracy
		);
		return dummyLocation;
	}
}
