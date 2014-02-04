/**
 * 
 */
package com.safran.arena.stubs;

/**
 * @author F270116
 *
 */
public interface SchedulerThreadListener {
	public enum ThreadStatus {
		RUNNING,
		PAUSED,
		STOPPED;
	}
	void moduleAdded(String name);
	void moduleRemoved(String name);
	void moduleStatusChanged(String name);
	void moduleTSChanged(String name);
	void moduleGroupChanged(String name);
	void currentDateChanged(long timeStamp);
	void statusChanged(ThreadStatus status);
	
}
