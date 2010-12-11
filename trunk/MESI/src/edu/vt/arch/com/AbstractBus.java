package edu.vt.arch.com;

import edu.vt.arch.cache.Cache;
import edu.vt.arch.mem.MainMemory;
import edu.vt.arch.mem.Stall;

public abstract class AbstractBus {

	public MainMemory memory;
	public Cache[] caches;
	protected AbstractBus(MainMemory memory, Cache[] caches) {
		this.memory = memory;
		this.caches = caches;
	}
	
	public synchronized void broadcast(Signal signal, long busy) {
		Stall.stall(busy);
		
		if(memory!=null)
			memory.signal(signal);
		for (Cache cache : caches)
			cache.signal(signal);
	}

}
