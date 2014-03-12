package itti.com.pl.arena.cm;

import java.util.Random;

public class TestHelper {

    private static Random random = new Random();

    public static String getOntologyName(){
        return String.format("className_%d", random.nextInt(10000));
    }

    public static double getCoordinate() {
        return 360 * random.nextDouble();
    }

    public static int getBearing() {
        return random.nextInt(360);
    }

}
