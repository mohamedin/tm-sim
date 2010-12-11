package edu.vt.sim;

import edu.vt.arch.cpu.Core;

public abstract class Program implements Runnable{

	private Core core;
	public void setAffinity(Core core){
		this.core = core;
	}
	
	protected byte[] read(int address){
		return core.read(address);
	}
	protected void write(int address, byte[] data){
		core.write(address, data);
	}
}
