package itti.com.pl.arena.cm.server.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.UUID;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.dynamicobj.PlatformStatus;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.server.location.Range;
import itti.com.pl.arena.cm.server.ontology.Ontology;
import itti.com.pl.arena.cm.server.ontology.OntologyException;
import itti.com.pl.arena.cm.server.persistence.Persistence;
import itti.com.pl.arena.cm.server.persistence.PersistenceException;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * test class for the {@link PlatformTracker}
 * @author cm-admin
 *
 */
public class PlatformTrackerTest {

    @Test
    public void testCheckInitNoID(){
        //test case for the init method method
        PlatformTracker pt = new PlatformTracker();
        pt.init();
        //PlatformID is set using 'init' method
        assertNotNull(pt.getId());
    }

    @Test
    public void testCheckInitWithID(){
        //test case for the init method method - ID provided via setter
        String id = UUID.randomUUID().toString();
        PlatformTracker pt = new PlatformTracker();
        pt.setPlatformId(id);
        pt.init();
        //PlatformID is set using 'init' method
        assertEquals(id, pt.getId());
    }

    @Test
    public void testOnLocationChangeNullLocation() throws PersistenceException{
        //test case for the onLocationChange method method - null location provided
        PlatformTracker pt = new PlatformTracker();
        Persistence persistence = Mockito.mock(Persistence.class);
        pt.setPersistence(persistence);
        pt.onLocationChange(null);
        //null was not persisted
        Mockito.verify(persistence, Mockito.times(0)).create(Mockito.anyString(), Mockito.any(Location.class));
    }

    @Test
    public void testOnLocationChangeValidLocation() throws PersistenceException, OntologyException{
        //test case for the onLocationChange method method - valid location provided
        PlatformTracker pt = new PlatformTracker();
        Location location = new Location(343, 3435);
        Persistence persistence = Mockito.mock(Persistence.class);
        pt.setPersistence(persistence);
        Ontology ontology = Mockito.mock(Ontology.class);
        pt.setOntology(ontology);
        pt.onLocationChange(location);
        //value was persisted
        Mockito.verify(persistence, Mockito.times(1)).create(Mockito.anyString(), Mockito.eq(location));
        //ontology was updated
        Mockito.verify(ontology, Mockito.times(1)).updatePlatformPosition(Mockito.anyString(), Mockito.eq(location));
    }

    @Test
    public void testCheckPlatformStoppedEmptyDB() throws OntologyException{
        //test case for the checkPlatformStopped method - no data in db
        Persistence persistence = Mockito.mock(Persistence.class);
        Ontology ontology = Mockito.mock(Ontology.class);
        PlatformTracker pt = new PlatformTracker();
        pt.setPersistence(persistence);
        pt.checkPlatformStopped();
        //ontology was not called
        Mockito.verify(ontology, Mockito.times(0)).
            getGISObjects(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any(String[].class));
    }

    @Test
    public void testCheckPlatformStoppedPlatformMoving() throws OntologyException, PersistenceException{
        //test case for the checkPlatformStopped method - platform is still moving
        Persistence persistence = Mockito.mock(Persistence.class);
        Mockito.when(persistence.getLastPosition(Mockito.anyString())).thenReturn(
                new Location(0, 0, 0, 0, 0, 0, System.currentTimeMillis() / 1000));
        Ontology ontology = Mockito.mock(Ontology.class);
        PlatformTracker pt = new PlatformTracker();
        pt.setMaxIdleTime(1000);
        pt.setPersistence(persistence);
        pt.checkPlatformStopped();
        //ontology was not called
        Mockito.verify(ontology, Mockito.times(0)).
            getGISObjects(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any(String[].class));
        //status is 'moving'
        assertEquals(PlatformStatus.Moving, pt.getStatus());
    }

    @Test
    public void testCheckPlatformStoppedPlatformStoppedOutsideArea() throws OntologyException, PersistenceException{
        //test case for the checkPlatformStopped method - platform is not moving, but it's outside Arena-managed area
        Persistence persistence = Mockito.mock(Persistence.class);
        //10K seconds back
        Mockito.when(persistence.getLastPosition(Mockito.anyString())).thenReturn(
                new Location(0, 0, 0, 0, 0, 0, System.currentTimeMillis() / 1000 - 10000));
        Ontology ontology = Mockito.mock(Ontology.class);
        PlatformTracker pt = new PlatformTracker();
        pt.setOntology(ontology);
        pt.setMaxIdleTime(1);
        pt.setPersistence(persistence);
        pt.checkPlatformStopped();
        //ontology was called just once (widest range)
        Mockito.verify(ontology, Mockito.times(1)).
            getGISObjects(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any(String[].class));
        //status is 'stopped'
        assertEquals(PlatformStatus.StoppedOutsideParking, pt.getStatus());
    }

    @Test
    public void testCheckPlatformStoppedPlatformStoppedInsideArea() throws OntologyException, PersistenceException{
        //test case for the checkPlatformStopped method - platform is not moving, and it's inside Arena-managed area
        Persistence persistence = Mockito.mock(Persistence.class);
        //10K seconds back
        Mockito.when(persistence.getLastPosition(Mockito.anyString())).thenReturn(
                new Location(0, 0, 0, 0, 0, 0, System.currentTimeMillis() / 1000 - 10000));
        Ontology ontology = Mockito.mock(Ontology.class);
        //parking detected
        HashSet<GeoObject> parkingLotsSet = new HashSet<>();
        parkingLotsSet.add(new ParkingLot("dummy"));
        Mockito.when(ontology.getGISObjects(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString())).
            thenReturn(parkingLotsSet);
        PlatformTracker pt = new PlatformTracker();
        pt.setOntology(ontology);
        pt.setMaxIdleTime(1);
        PlatformListener listener = Mockito.mock(PlatformListener.class);
        pt.setPlatformListener(listener);
        pt.setPersistence(persistence);
        pt.checkPlatformStopped();
        //ontology was called as many times as many ranges is defined
        Mockito.verify(ontology, Mockito.times(Range.values().length)).
            getGISObjects(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any(String[].class));
        //listener was notified about platform move
        Mockito.verify(listener, Mockito.times(1)).
            destinationReached(Mockito.anyString(), Mockito.any(Location.class));
        //status is 'stopped'
        assertEquals(PlatformStatus.StoppedOnParking, pt.getStatus());
    }

}
