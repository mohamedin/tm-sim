package edu.vt.arch.cpu;

import edu.vt.arch.cache.ICache;
import edu.vt.arch.cache.tm1.Cache;

public class Core extends Thread{

	private ICache cache;
	public Core(int index, ICache cache, Runnable runnable){
		super(runnable);
		setName("CPU " + index);
		this.cache = cache;
	}
	public byte[] read(int address) {
		return cache.read(address);
	}
	public void write(int address, byte data[]) {
		cache.write(address, data);
	}
	
	public void atomic_begin(){
		((Cache)cache).startTM();
	}
	
	public void atomic_end(){
		((Cache)cache).tryCommit();
	}

}
