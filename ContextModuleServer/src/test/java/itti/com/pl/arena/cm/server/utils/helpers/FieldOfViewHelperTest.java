package itti.com.pl.arena.cm.server.utils.helpers;

import static org.junit.Assert.*;

import java.util.Random;

import itti.com.pl.arena.cm.dto.coordinates.FieldOfViewObject;
import itti.com.pl.arena.cm.dto.coordinates.RadialCoordinate;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FieldOfViewHelperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Random random = new Random();

    @Test
    public void testCalculateVisibilityNullRequest() throws FieldOfViewHelperException {

        // null request object provided
        expectedException.expect(FieldOfViewHelperException.class);
        expectedException.expectMessage(ErrorMessages.FIELD_OF_VIEW_HELPER_EMPRY_OBJECT.getMessage());
        FieldOfViewHelper.calculateVisibility(null);
    }

    @Test
    public void testCalculateVisibilityValidRequest() throws FieldOfViewHelperException {

        FieldOfViewObject fov = new FieldOfViewObject("id");
        int visible = 1 + random.nextInt(10);
        int notVisible = random.nextInt(10);

        // add random visible objects
        for (int i = 0; i < visible; i++) {
            fov.addVisibleObject(new RadialCoordinate(0, 0));
        }
        // add random visible objects
        for (int i = 0; i < notVisible; i++) {
            fov.addNotVisibleObject(new RadialCoordinate(0, 0));
        }
        double percentage = (100.0 * visible / (visible + notVisible));
        assertEquals(percentage, FieldOfViewHelper.calculateVisibility(fov), 0.001);
    }

}
