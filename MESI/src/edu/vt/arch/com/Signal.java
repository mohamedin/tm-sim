package edu.vt.arch.com;

import java.util.Arrays;

public class Signal {
	public enum Type { READ, WRITE, READ_RESPONSE, READ_FOR_OWNERSHIP, BACKOFF, NON_EXCLUSIVE };
	
	public Type type;
	public int address;
	public byte[] data;

	public Signal(int address, Type type){
		this.address = address;
		this.type = type;
	}
	
	public Signal(int address, Type type, byte[] data){
		this(address, type);
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "<" + type.name() + "> [" + address + "] " + Arrays.toString(data);
	}
}
