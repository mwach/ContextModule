package itti.com.pl.arena.cm.dto;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class GeoObjectTest {

    @Test
    public void testSetBoundaries(){
        //check default value
        GeoObject geoObject = new TestGeoObject(UUID.randomUUID().toString());
        Assert.assertArrayEquals(new Location[0], geoObject.getBoundaries());

        //verify, cannot be overwrite by null
        geoObject.setBoundaries(null);
        Assert.assertArrayEquals(new Location[0], geoObject.getBoundaries());

        //verify getter
        Location[] locations = new Location[]{new Location(1.11, 2.22), new Location(3.33, 4.44)};
        geoObject.setBoundaries(locations);
        Assert.assertArrayEquals(locations, geoObject.getBoundaries());
    }


    private static class TestGeoObject extends GeoObject{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public TestGeoObject(String id){
            super(id);
        }
    }
}
