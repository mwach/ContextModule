/**
 * 
 */
package com.safran.arena.stubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;
import com.safran.arena.impl.SimpleMessageFilter;
import com.safran.arena.impl.TimeSteppedModuleProxy;
import com.safran.arena.stubs.SchedulerThreadListener.ThreadStatus;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.FinishedUpTo;
import eu.arena_fp7._1.Module;
import eu.arena_fp7._1.TimeSteppedModule;

/**
 * @author F270116
 * 
 */
public class SchedulerThread extends ModuleImpl implements Runnable {
	private Client _client;
	private ArrayList<SchedulerThreadListener> _listeners = new ArrayList<SchedulerThreadListener>();
	private boolean cont = true;
	private boolean _pause = false;
	private SimpleMessageFilter _messageFilter;

	/**
	 * The perfect example of what should not be done. :D These items are the
	 * groups used by the scheduler to group TimeStepped modules. Group X is
	 * notified to continue when Group X+1 has finished its processing. By using
	 * an enum, we prevent the extension of the quantity of groups by the used,
	 * which is bad. This should be user defined Id.
	 * 
	 * @author F270116
	 * 
	 */
	public enum Group {
		UNHANDLED, GROUP_1, GROUP_2, GROUP_LAST;
	}

	public enum Status {
		/**
		 * The process has been notified.
		 */
		PROCESSING,
		/**
		 * The process is ready to be notified.
		 */
		WAITING,
		/**
		 * 
		 */
		PAUSED;
	}

	/**
	 * Structure to keep data on managed modules.
	 * 
	 * @author F270116
	 * 
	 */
	private class Scheduled {
		TimeSteppedModuleProxy tsProxy;
		Status status;
		Group group = Group.UNHANDLED;
		long timeStamp;
	}

	private class ScheduledGroup {
		Long targetTimeStamp;
		Long reachedTimeStamp;
		Status status;

		/**
		 * @param targetTimeStamp
		 * @param reachedTimeStamp
		 * @param status
		 */
		public ScheduledGroup(Long targetTimeStamp, Long reachedTimeStamp,
				Status status) {
			super();
			this.targetTimeStamp = targetTimeStamp;
			this.reachedTimeStamp = reachedTimeStamp;
			this.status = status;
		}

	}

	private enum EventType {
		ModuleFinished, ModuleAdded, ModuleRemoved, Terminate, GroupChanged, Start, Stop, Pause, Continue, SetEnd, SetStep;
	}

	/**
	 * Event incoming to the machine.
	 * 
	 * @author F270116
	 * 
	 */
	private class Event {
		EventType type;
		String moduleId;
		Group newGroup;
		long timeStamp;
		long timeStamp2;

		/**
		 * @param type
		 * @param moduleId
		 */
		public Event(EventType type, String moduleId) {
			super();
			this.type = type;
			this.moduleId = moduleId;
		}

		/**
		 * @param type
		 * @param moduleId
		 * @param newGroup
		 */
		public Event(EventType type, String moduleId, Group newGroup) {
			super();
			this.type = type;
			this.moduleId = moduleId;
			this.newGroup = newGroup;
		}

		/**
		 * @param type
		 * @param moduleId
		 * @param timeStamp
		 */
		public Event(EventType type, String moduleId, long timeStamp) {
			super();
			this.type = type;
			this.moduleId = moduleId;
			this.timeStamp = timeStamp;
		}

		/**
		 * @param type
		 * @param moduleId
		 * @param timeStamp
		 */
		public Event(EventType type, String moduleId, long timeStamp,
				long timeStamp2) {
			super();
			this.type = type;
			this.moduleId = moduleId;
			this.timeStamp = timeStamp;
			this.timeStamp2 = timeStamp2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			return "Event : " + type + " " + moduleId + " newGroup=" + newGroup
					+ ", ts1=" + timeStamp + ", ts2=" + timeStamp2;
		}

	}

	private ArrayList<Scheduled> _modules = new ArrayList<SchedulerThread.Scheduled>();
	private HashMap<String, Scheduled> _modulesMap = new HashMap<String, SchedulerThread.Scheduled>();
	private LinkedBlockingQueue<Event> _events = new LinkedBlockingQueue<Event>();
	private HashMap<Group, ScheduledGroup> _groups = new HashMap<SchedulerThread.Group, ScheduledGroup>();
	private long _timeStep = 300;
	private long _target = 0l;

	/**
	 * @param moduleName
	 */
	public SchedulerThread(String moduleName, Client client) {
		super(moduleName);
		for (Group g : Group.values()) {
			_groups.put(g, new ScheduledGroup(0l, 0l, Status.WAITING));
		}
		_client = client;
		_client.registerModule(this);
		_messageFilter = new SimpleMessageFilter(true);
		_messageFilter.addClass(FinishedUpTo.class);
		_client.registerModuleAsDataConsumer(this, _messageFilter);
		_client.registerModuleAsManager(this);
		List<Module> modules = _client.getModuleList(getModuleName());
		for (Module module : modules) {
			if (module instanceof TimeSteppedModule) {
				onTimeSteppedModuleRegistered((TimeSteppedModule) module);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.impl.ModuleImpl#onTimeSteppedModuleRegistered(eu.arena_fp7
	 * ._1.TimeSteppedModule)
	 */
	@Override
	public void onTimeSteppedModuleRegistered(TimeSteppedModule module) {
		super.onTimeSteppedModuleRegistered(module);
		try {
			_events.put(new Event(EventType.ModuleAdded, module.getId()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.impl.ModuleImpl#onModuleUnregistered(eu.arena_fp7._1
	 * .Module)
	 */
	@Override
	public void onModuleUnregistered(Module module) {

		super.onModuleUnregistered(module);

		try {
			_events.put(new Event(EventType.ModuleRemoved, module.getId()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.impl.ModuleImpl#onTimeSteppedModuleUnregistered(eu.arena_fp7
	 * ._1.TimeSteppedModule)
	 */
	@Override
	public void onTimeSteppedModuleUnregistered(TimeSteppedModule module) {
		super.onTimeSteppedModuleUnregistered(module);
		try {
			_events.put(new Event(EventType.ModuleRemoved, module.getId()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.impl.ModuleImpl#onDataAvailable(java.lang.Class,
	 * java.lang.String, eu.arena_fp7._1.AbstractDataFusionType)
	 */
	@Override
	public void onDataAvailable(
			Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {
		super.onDataAvailable(dataType, dataSourceId, data);
		if (data instanceof FinishedUpTo) {
			try {
				FinishedUpTo finished = (FinishedUpTo) data;
				System.out.println("Received FinishedUpTo ts="
						+ finished.getEndValidityPeriod() + " for "
						+ finished.getDataSourceId());
				_events.put(new Event(EventType.ModuleFinished, data
						.getDataSourceId(), finished.getEndValidityPeriod()));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.impl.ModuleImpl#onDataChanged(java.lang.Class,
	 * java.lang.String, eu.arena_fp7._1.AbstractDataFusionType)
	 */
	@Override
	public void onDataChanged(Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {
		// TODO Auto-generated method stub
		super.onDataChanged(dataType, dataSourceId, data);
		onDataAvailable(dataType, dataSourceId, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Event e;
		Scheduled moduleStruct;

		while (cont) {
			updateCurrentTS();

			try {
				e = _events.take();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				continue;
			}
			switch (e.type) {
			case Terminate:
				cont = false;
				break;
			case ModuleFinished:
				synchronized (_modulesMap) {
					/*
					 * Module pretends to be finiseh. Register its new time
					 * stamp, but verify that it matches the target before
					 * declaring it finished and scanning all group.
					 */
					Scheduled module = _modulesMap.get(e.moduleId);
					if (module == null) {
						System.err.println("Error : module not found "
								+ e.moduleId);
					} else {
						module.timeStamp = e.timeStamp;
						Group g = _modulesMap.get(e.moduleId).group;
						if (e.timeStamp >= _groups.get(g).reachedTimeStamp) {
							module.status = Status.WAITING;
							scanGroup(g, e.timeStamp);
						}
					}
				}
				fireModuleStatusChanged(e.moduleId);
				fireModuleTSChanged(e.moduleId);
				break;
			case ModuleAdded:
				synchronized (_modulesMap) {
					moduleStruct = _modulesMap.get(e.moduleId);
					if (moduleStruct == null) {
						moduleStruct = new Scheduled();
						_modulesMap.put(e.moduleId, moduleStruct);
						_modules.add(moduleStruct);
					}
					// refresh these datas
					moduleStruct.status = Status.WAITING;
					moduleStruct.tsProxy = _client
							.getTimeSteppedModuleProxy(e.moduleId);
				}
				fireModuleAdded(e.moduleId);
				break;
			case ModuleRemoved:
				synchronized (_modulesMap) {
					Group g = null;
					moduleStruct = _modulesMap.get(e.moduleId);
					if (moduleStruct != null) {
						g = _modulesMap.get(e.moduleId).group;
						_modules.remove(moduleStruct);
						_modulesMap.remove(e.moduleId);
					}
					// unlock modules if the removed one was blocking the group
					if (g != null) {
						scanGroup(g, -1);
					}
				}
				fireModuleRemoved(e.moduleId);
				break;
			case GroupChanged:
				synchronized (_modulesMap) {
					moduleStruct = _modulesMap.get(e.moduleId);
					if (moduleStruct != null) {
						moduleStruct.group = e.newGroup;
					}
				}
				fireModuleGroupChanged(e.moduleId);
				break;
			case Pause:
				_pause = true;
				fireThreadChanged(ThreadStatus.PAUSED);
				break;
			case Continue:
				_pause = false;
				for (Group g : Group.values()) {
					resumeGroup(g);
				}
				fireThreadChanged(ThreadStatus.RUNNING);
				break;
			case Start:
				_pause = false;
				synchronized (_modulesMap) {
					for (Scheduled module : _modulesMap.values()) {
						if (module != null) {
							module.status = Status.PROCESSING;
							module.timeStamp = e.timeStamp;
							module.tsProxy.start(e.timeStamp);
							module.tsProxy.timeStep(e.timeStamp + _timeStep);
							fireModuleStatusChanged(module.tsProxy
									.getModuleName());
						}
					}
					for (ScheduledGroup group : _groups.values()) {
						group.status = Status.WAITING;
						group.targetTimeStamp = e.timeStamp + _timeStep;
						group.reachedTimeStamp = e.timeStamp;
					}
					_target = e.timeStamp2;
				}
				fireThreadChanged(ThreadStatus.RUNNING);
				break;
			case Stop:
				_pause = true;
				fireThreadChanged(ThreadStatus.STOPPED);
				break;
			case SetStep:
				if (e.timeStamp > 0) {
					_timeStep = e.timeStamp;
				}
				break;
			case SetEnd:
				if (e.timeStamp > 0) {
					_target = e.timeStamp;
				}
				break;
			}
		}

	}

	/**
	 * Scans the group G. If the group is waiting and its TS is not lower than
	 * the previous group TS, send processing of previous group. If group is
	 * group 1, no scan to perform. If group is last group, send signal also to
	 * this group.
	 * 
	 * @param g
	 */
	private void scanGroup(Group g, long lastReached) {
		if (g == Group.UNHANDLED) {
			return;
		}
		boolean send = true;
		long lowerTs = Long.MAX_VALUE;
		for (Scheduled module : _modules) {
			if (module.group == g) {
				if (module.timeStamp < lowerTs) {
					lowerTs = module.timeStamp;
				}
				if (module.status == Status.PROCESSING) {
					send = false;
				}
			}

		}
		_groups.get(g).reachedTimeStamp = lowerTs;
		if (lastReached >= 0 && lastReached != lowerTs) {
			send = false;
			System.out.println(g + " not notified because lastReached ("
					+ lastReached + ") is not lowerTs (" + lowerTs + ")");

		}
		if (send) {
			System.out.println(g + " finished at "
					+ _groups.get(g).reachedTimeStamp);
			// GROUP_LAST does not wait except its input, as it is last
			if (g == Group.GROUP_LAST) {
				Long ts = _groups.get(g).targetTimeStamp;
				if (_pause) {
					pauseGroup(g);
				} else if (ts < _target) {
					sendSignal(g);
				} else {
					System.out.println(g + "Target reached");
					pauseGroup(g);
				}

			}
			// GROUP_1 does not unlock other groups, as it is first
			if (g != Group.GROUP_1) {
				Group previous = Group.values()[g.ordinal() - 1];
				Long tsNMinusOne = _groups.get(previous).targetTimeStamp;
				Long ts = _groups.get(g).targetTimeStamp;
				if (tsNMinusOne <= ts) {
					if (_pause) {
						pauseGroup(g);
					} else {
						System.out.println("Group " + g
								+ " finished, starting group " + previous);
						sendSignal(previous);
					}
				} else {
					System.out
							.println("Group " + g + " at " + ts
									+ ", previous (" + previous + ") at "
									+ tsNMinusOne);
				}
			} else {
				// GROUP_1
				// nothing, group_1 is started when group_2 is finished
			}

		}
	}

	private void pauseGroup(Group g) {
		for (Scheduled module : _modules) {
			if (module.group == g) {
				module.status = Status.PAUSED;
				fireModuleStatusChanged(module.tsProxy.getModuleName());
			}
		}
		ScheduledGroup scG = _groups.get(g);
		scG.status = Status.PAUSED;

	}

	private void resumeGroup(Group g) {
		ScheduledGroup scG = _groups.get(g);
		//if (scG.status == Status.PAUSED) {
		//   sendSignal(g);
			//scanGroup(g, -1);
		//}
		for (Scheduled module : _modules) {
			if (module.group == g) {
				module.tsProxy.timeStep(_groups.get(g).targetTimeStamp);
			}
		}
	}

	private void sendSignal(Group g) {
		Long timeStamp = _groups.get(g).targetTimeStamp;
		timeStamp += _timeStep;
		_groups.get(g).targetTimeStamp = timeStamp;

		for (Scheduled module : _modules) {
			if (module.group == g) {
				module.tsProxy.timeStep(timeStamp);

				module.status = Status.PROCESSING;
				fireModuleStatusChanged(module.tsProxy.getModuleName());
				System.out.println("Sending signal to module "
						+ module.tsProxy.getModuleName() + " as in group " + g
						+ " for TS " + timeStamp);
			}
		}
	}

	/**
	 * Changes the group of one module.
	 * 
	 * @param moduleId
	 * @param group
	 */
	public void changeGroup(String moduleId, Group group) {
		try {
			_events.put(new Event(EventType.GroupChanged, moduleId, group));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addListener(SchedulerThreadListener listener) {
		synchronized (_listeners) {
			_listeners.add(listener);
		}
	}

	public void removeListener(SchedulerThreadListener listener) {
		synchronized (_listeners) {
			_listeners.remove(listener);
		}
	}

	public Long getModuleTS(String moduleId) {
		synchronized (_modulesMap) {
			Scheduled module = _modulesMap.get(moduleId);
			if (module != null) {
				return module.timeStamp;
			} else {
				return null;
			}
		}
	}

	public Group getModuleGroup(String moduleId) {
		synchronized (_modulesMap) {
			Scheduled module = _modulesMap.get(moduleId);
			if (module != null) {
				return module.group;
			} else {
				return null;
			}
		}
	}

	/**
	 * Compute current time stamp as the minimum target timestamp and propagate
	 * ths value.
	 */
	private void updateCurrentTS() {
		long ts = 0;
		synchronized (_modulesMap) {
			for (ScheduledGroup group : _groups.values()) {
				if (group.reachedTimeStamp > ts) {
					ts = group.reachedTimeStamp;
				}
			}
		}
		synchronized (_listeners) {
			for (SchedulerThreadListener listener : _listeners) {
				listener.currentDateChanged(ts);
			}
		}
	}

	public Status getModuleStatus(String moduleId) {
		synchronized (_modulesMap) {
			Scheduled module = _modulesMap.get(moduleId);
			if (module != null) {
				return module.status;
			} else {
				return null;
			}
		}
	}

	public void startScheduling(long startDate, long endDate) {
		try {
			_events.put(new Event(EventType.Start, null, startDate, endDate));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void stopScheduling() {
		try {
			_events.put(new Event(EventType.Stop, null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setStepAndEnd(long step, long end) {
		try {
			_events.put(new Event(EventType.SetStep, null, step));
			_events.put(new Event(EventType.SetEnd, null, end));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops the thread
	 */
	public void stop() {
		try {
			_events.put(new Event(EventType.Terminate, null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		try {
			_events.put(new Event(EventType.Pause, null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void unPause() {
		try {
			_events.put(new Event(EventType.Continue, null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	protected void fireModuleStatusChanged(String name) {
		synchronized (_listeners) {
			for (SchedulerThreadListener listener : _listeners) {
				listener.moduleStatusChanged(name);
			}
		}
	}

	protected void fireModuleTSChanged(String name) {
		synchronized (_listeners) {
			for (SchedulerThreadListener listener : _listeners) {
				listener.moduleTSChanged(name);
			}
		}
	}

	protected void fireModuleAdded(String name) {
		synchronized (_listeners) {
			for (SchedulerThreadListener listener : _listeners) {
				listener.moduleAdded(name);
			}
		}
	}

	protected void fireModuleRemoved(String name) {
		synchronized (_listeners) {
			for (SchedulerThreadListener listener : _listeners) {
				listener.moduleRemoved(name);
			}
		}
	}

	protected void fireModuleGroupChanged(String name) {
		synchronized (_listeners) {
			for (SchedulerThreadListener listener : _listeners) {
				listener.moduleGroupChanged(name);
			}
		}
	}

	protected void fireThreadChanged(ThreadStatus status) {
		synchronized (_listeners) {
			for (SchedulerThreadListener listener : _listeners) {
				listener.statusChanged(status);
			}
		}
	}
}
