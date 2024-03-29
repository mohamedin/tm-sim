package edu.vt.arch.mem;

import java.util.Arrays;

import edu.vt.util.Config;

public class MemoryBlock {
	private byte[] data;
	
	MemoryBlock(){
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
		return Arrays.toString(data);
	}
}
