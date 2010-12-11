package edu.vt.arch.com;

public class Signal {
	public enum Type { READ, WRITE, READ_RESPONSE, BACKOFF };
	
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
}
