package edu.vt.arch.com;

import java.util.concurrent.locks.ReentrantLock;

import edu.vt.arch.cache.Cache;
import edu.vt.arch.mem.MainMemory;
import edu.vt.util.Logger;

public abstract class AbstractBus {

	public IBusComponent[] busComponents;
	public ReentrantLock lock = new ReentrantLock(true);
	
	protected AbstractBus(MainMemory memory, Cache[] caches){
		busComponents = new IBusComponent[caches.length + (memory==null ? 0 : 1)];
		int offset = 0;
		if(memory!=null)
			busComponents[offset++] = memory;
		for(int i=0; i<caches.length; i++)
			busComponents[i+offset] = caches[i];
	}
	
	public void acquire(){
		lock.lock();
	}
	
	public void release(){
		lock.unlock();
	}
	
	public void broadcast(IBusComponent sender, final Signal signal) {
		Logger.debug("Broadcasting " + signal);
		
		for (IBusComponent component : busComponents){
			final IBusComponent com = component;
			if(!sender.equals(com))
				new Thread(){
					@Override
					public void run() {
						com.signal(signal);
					}
				}.start();
		}
	}

}
