package itti.com.pl.arena.cm.persistence;

import itti.com.pl.arena.cm.dto.Location;

import java.util.List;

public interface Persistence {

	public void init() throws PersistenceException;
	public void shutdown() throws PersistenceException;

	public void create(Location point) throws PersistenceException;
	public void delete(long oldestTimestamp) throws PersistenceException;
	public Location readLastPosition() throws PersistenceException;
	public List<Location> readHistory(long initialTimestamp) throws PersistenceException;
}
