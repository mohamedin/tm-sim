package edu.vt.arch.com;

import edu.vt.arch.cache.ICache;
import edu.vt.arch.mem.MainMemory;

public class DataBus extends AbstractBus{

	public DataBus(MainMemory memory, ICache[] caches) {
		super(memory, caches);
	}
}
