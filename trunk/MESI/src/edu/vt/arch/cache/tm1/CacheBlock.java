package edu.vt.arch.cache.tm1;

import java.util.Arrays;

import edu.vt.util.Config;

public class CacheBlock {
	public static enum State { INVALID, EXCLUSIVE, SHARED, MODIFIED }
	
	State state;
	int address;
	private byte[] data;
	
	CacheBlock() {
		state = State.INVALID;
		address = -1;
		data = new byte[Config.BLOCK_SIZE];
	}
	
	public byte[] getData(){
		return data;
	}
	
	public void setData(byte[] data){
		for (int i=0; i<data.length; i++)
			this.data[i] = data[i];
	}
	
	@Override
	public String toString() {
		return "<" + state.toString() + "> [" + address + "] " + Arrays.toString(data);
	}
}
