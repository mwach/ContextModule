package itti.com.pl.arena.cm;

/**
 * General interface implemented by all CM service beans Used by the Spring application context
 * 
 * @author mawa
 * 
 */
public interface Service {

    /**
     * Method called during bean initialization If module cannot be initialized (e.g. service cannot connect to the DB)
     * an runtime initialization exception will be thrown
     */
    public void init();

    /**
     * Method called during service shutdown Used to close all the streams, connections and other resources
     */
    public void shutdown();
}
