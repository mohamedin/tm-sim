package edu.vt.sim;

import java.util.Arrays;

import edu.vt.arch.cpu.Core;

public abstract class Program implements Runnable{

	protected Core core;
	public void setAffinity(Core core){
		this.core = core;
	}
	
	protected byte[] read(int address){
		return core.read(address);
	}
	protected void write(int address, byte[] data){
		core.write(address, data);
	}

	protected byte[] test_and_set(int address, byte[] data){
		byte[] old = read(address).clone();
		write(address, data);
		return old;
	}
	
	protected void log(byte[] data){
		System.out.println(Arrays.toString(data));
	}
	
	@Override
	public void run() {
		execute();
	}

	protected abstract void execute();
}
