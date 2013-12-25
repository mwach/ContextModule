package itti.com.pl.arena.cm;

/**
 * General interface implemented by all CM service beans
 * Used by the Spring application context
 * @author mawa
 *
 */
public interface Service {

	/**
	 * Method called during bean initialization
	 * If module cannot be initialized (e.g. cannot connect to the DB) an {@link BeanInitializationException} is thrown
	 */
	public void init();

	/**
	 * Method called service shutdown
	 */
	public void shutdown();
}
