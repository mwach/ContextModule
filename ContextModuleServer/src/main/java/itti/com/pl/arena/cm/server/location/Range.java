package itti.com.pl.arena.cm.server.location;

/**
 * Used to convert degrees into kilometers parameter 'rangeInKms' defines fraction of the one degree occupied by given
 * distance One degree is about 110km
 * 
 * @author cm-admin
 * 
 */
public enum Range {

    /**
     * ten kilometers
     */
    Km10(0.091),
    /**
     * One kilometer
     */
    Km1(0.0091),
    /**
     * One hundred meters
     */
    Km01(0.00091),
    ;

    private double rangeInKms = 0;

    private Range(double rangeInKms) {
        this.rangeInKms = rangeInKms;
    }

    public double getRangeInKms() {
        return rangeInKms;
    }

    public static Range getClosestRange(){
        return Km01;
    }
}
