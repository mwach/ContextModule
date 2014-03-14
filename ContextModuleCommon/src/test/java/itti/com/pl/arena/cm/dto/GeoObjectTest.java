package itti.com.pl.arena.cm.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class GeoObjectTest {

    @Test
    public void testSetBoundaries(){
        //check default value
        GeoObject geoObject = new TestGeoObject(UUID.randomUUID().toString());
        Assert.assertEquals(new HashSet<>(), geoObject.getBoundaries());

        //verify, cannot be overwrite by null
        geoObject.setBoundaries((Set<Location>)null);
        Assert.assertEquals(new HashSet<>(), geoObject.getBoundaries());

        //verify getter
        Location[] locations = new Location[]{new Location(1.11, 2.22), new Location(3.33, 4.44)};
        geoObject.setBoundaries(locations);
        Assert.assertEquals(new HashSet<>(Arrays.asList(locations)), geoObject.getBoundaries());
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
