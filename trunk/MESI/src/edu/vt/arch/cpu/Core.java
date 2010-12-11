package edu.vt.arch.cpu;

import edu.vt.arch.cache.Cache;

public class Core extends Thread{

	private Cache cache;
	public Core(Cache cache, Runnable runnable){
		super(runnable);
		this.cache = cache;
	}
	public byte[] read(int address) {
		return cache.read(address);
	}
	public void write(int address, byte data[]) {
		cache.write(address, data);
	}
}
