package edu.vt.arch.com;

import edu.vt.arch.cache.ICache;
import edu.vt.arch.mem.MainMemory;

public class AddressBus extends AbstractBus{

	public AddressBus(MainMemory memory, ICache[] caches) {
		super(memory, caches);
	}
	
}
