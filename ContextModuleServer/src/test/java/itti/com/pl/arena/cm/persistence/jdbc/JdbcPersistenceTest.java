package itti.com.pl.arena.cm.persistence.jdbc;

import static org.junit.Assert.*;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.persistence.PersistenceException;
import itti.com.pl.arena.cm.persistence.jdbc.JdbcPersistence;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;

import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JdbcPersistenceTest {

    private static JdbcPersistence persistence = null;

    private static Random random = new Random();

    @BeforeClass
    public static void before() throws PersistenceException, IOHelperException {
	persistence = new JdbcPersistence();
	JdbcProperties properties = new JdbcProperties();
	properties.setPropertiesFile("jdbc.test.properties");
	properties.setSchemaFile("schema.test.properties");
	persistence.setJdbcProperties(properties);
	persistence.init();
    }

    @AfterClass
    public static void afterClass() throws PersistenceException {
	persistence.shutdown();
    }

    @Test
    public void testInsertPosition() throws PersistenceException {

	long timestamp = System.currentTimeMillis();

	Location location = createDummyLocation(timestamp);

	persistence.create(location);
	Location lastLocation = persistence.readLastPosition();

	assertEquals(location, lastLocation);
    }

    @Test
    public void testMultiInsertPosition() throws PersistenceException {

	long timestamp = System.currentTimeMillis();

	Location locationOne = createDummyLocation(timestamp);
	Location locationTwo = createDummyLocation(timestamp + 1);

	persistence.create(locationOne);
	persistence.create(locationTwo);
	Location lastLocation = persistence.readLastPosition();
	assertEquals(locationTwo, lastLocation);

	List<Location> Locations = persistence.readHistory(timestamp);
	assertEquals(2, Locations.size());
	assertEquals(locationOne, Locations.get(0));
	assertEquals(locationTwo, Locations.get(1));

	persistence.delete(timestamp);
	Locations = persistence.readHistory(timestamp);
	assertEquals(1, Locations.size());
	assertEquals(locationTwo, Locations.get(0));

    }

    public static Location createDummyLocation(long timestamp) {
	Location dummyLocation = new Location(random.nextDouble() * 100, // longitude
	        random.nextDouble() * 100, // latitude
	        random.nextDouble() * 100, // altitude
	        random.nextInt(180), // bearing
	        random.nextDouble() * 100, // speed
	        timestamp, // time
	        random.nextDouble() * 100 // accuracy
	);
	return dummyLocation;
    }

}
