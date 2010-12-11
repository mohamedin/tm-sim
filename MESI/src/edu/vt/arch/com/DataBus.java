package edu.vt.arch.com;

import edu.vt.arch.cache.Cache;
import edu.vt.arch.mem.MainMemory;

public class DataBus extends AbstractBus{

	public DataBus(MainMemory memory, Cache[] caches) {
		super(memory, caches);
	}
}
