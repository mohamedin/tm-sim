package edu.vt.arch.com;

import edu.vt.arch.cache.Cache;

public class SharedBus extends AbstractBus{

	public SharedBus(Cache[] caches) {
		super(null, caches);
	}

}
