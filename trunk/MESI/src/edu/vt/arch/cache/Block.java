package edu.vt.arch.cache;

public class Block {
	public static enum State { INVALID, EXCLUSIVE, SHARED, MODIFIED }
	
	State state = State.INVALID;
	int tag;
	byte[] data;
}
