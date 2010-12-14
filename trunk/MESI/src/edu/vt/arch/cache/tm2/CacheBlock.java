package edu.vt.arch.cache.tm2;

import java.util.Arrays;

import edu.vt.util.Config;

public class CacheBlock {
	public static enum State { INVALID, EXCLUSIVE, SHARED, MODIFIED}
	public static enum TM_State {EMPTY, NORMAL, XCOMMIT, XABORT}
	
	State state;
	TM_State tm_state;
	int address;
	private byte[] data;
	
	CacheBlock() {
		state = State.INVALID;
		tm_state = TM_State.EMPTY;
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
		return "<" + state.toString() + "> <"+tm_state.toString()+"> [" + address + "] " + Arrays.toString(data);
	}
}
