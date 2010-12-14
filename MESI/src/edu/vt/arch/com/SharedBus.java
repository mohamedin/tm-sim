package edu.vt.arch.com;

import edu.vt.arch.cache.ICache;

public class SharedBus extends AbstractBus{

	public SharedBus(ICache[] caches) {
		super(null, caches);
	}

}
