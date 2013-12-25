package itti.com.pl.arena.cm.persistence.jdbc;

import static org.junit.Assert.*;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.persistence.PersistenceException;
import itti.com.pl.arena.cm.persistence.PersistenceTestHelper;
import itti.com.pl.arena.cm.persistence.jdbc.JdbcPersistence;
import itti.com.pl.arena.cm.utils.helpers.IOHelperException;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class HsqlDbPersistenceTest {

	private static JdbcPersistence persistence = null;

	@BeforeClass
	public static void before() throws PersistenceException, IOHelperException{
		persistence = new JdbcPersistence();
		persistence.setPropertiesFile("jdbc.test.properties");
		persistence.setSchemaFile("schema.test.properties");
		persistence.init();	
	}

	@AfterClass
	public static void afterClass() throws PersistenceException{
		persistence.shutdown();
	}

	@Test
	@Ignore
	public void testInsertPosition() throws PersistenceException {

		long timestamp = System.currentTimeMillis();

		Location location = PersistenceTestHelper.createDummyLocation(timestamp);

		persistence.create(location);
		Location lastLocation = persistence.readLastPosition();
	
		assertEquals(location, lastLocation);
	}

	@Test
	public void testMultiInsertPosition() throws PersistenceException {

		long timestamp = System.currentTimeMillis();

		Location locationOne = PersistenceTestHelper.createDummyLocation(timestamp);
		Location locationTwo = PersistenceTestHelper.createDummyLocation(timestamp +1);

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
}
