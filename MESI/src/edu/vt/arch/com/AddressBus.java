package edu.vt.arch.com;

import edu.vt.arch.cache.Cache;
import edu.vt.arch.mem.MainMemory;

public class AddressBus extends AbstractBus{

	public AddressBus(MainMemory memory, Cache[] caches) {
		super(memory, caches);
	}
	
}
