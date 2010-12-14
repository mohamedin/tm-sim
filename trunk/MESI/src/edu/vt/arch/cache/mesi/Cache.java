package edu.vt.arch.cache.mesi;

import edu.vt.Test;
import edu.vt.arch.cache.ICache;
import edu.vt.arch.cache.mesi.CacheBlock.State;
import edu.vt.arch.com.AddressBus;
import edu.vt.arch.com.DataBus;
import edu.vt.arch.com.IBusComponent;
import edu.vt.arch.com.SharedBus;
import edu.vt.arch.com.Signal;
import edu.vt.arch.com.Signal.Type;
import edu.vt.arch.mem.Stall;
import edu.vt.util.Config;
import edu.vt.util.Logger;

public class Cache implements ICache{

	private final int index;
	private final CacheBlock[] blocks;
	private final int tagShift;
	
	private DataBus dataBus;
	private AddressBus addressBus;
	private SharedBus sharedBus;

	private boolean backoff = false;
	private boolean nonExclusive = false;
	private int workingAddress = -1;
	
	public Cache(int index, int size){
		this.index = index;
		blocks = new CacheBlock[size];
		for (int i=0; i<blocks.length; i++)
			blocks[i] = new CacheBlock();
		tagShift = (int)(Math.log(Config.BLOCK_SIZE)/Math.log(2));
	}
	
	public void connectToDataBus(DataBus dataBus) {
		this.dataBus = dataBus;
	}
	public void connectToAddressBus(AddressBus addressBus) {
		this.addressBus = addressBus;
	}
	public void connectToSharedBus(SharedBus sharedBus) {
		this.sharedBus = sharedBus;
	}

	private void access(int address, Signal.Type type){
		int cacheStatus = cached(address);
		if(cacheStatus==1 && blocks[index(address)].state.equals(State.SHARED) && type.equals(Type.READ_FOR_OWNERSHIP))
			cacheStatus = 0;
		switch(cacheStatus){
			case 1:	// cache read hit 
				Stall.stall(Config.CACHE_ACCESS_TIME); 
				break;
			case -1:  // uncached & write back needed
				writeBack(index(address));
			case 0: // uncached
				workingAddress = address;
				addressBus.broadcast(this, new Signal(address, type));
				Stall.stall(Config.MEM_ACCESS_TIME);
				if(backoff){
					Stall.stall(Config.MEM_WRITE_BACK_TIME);
					blocks[index(address)].state = State.SHARED;
				}
				backoff = false;
				nonExclusive = false;
				workingAddress = -1;
				break;
		}
	}
	
	private void busAcquire(int address, Signal.Type type){
		addressBus.acquire();
		dataBus.acquire();
		access(address, type);
		addressBus.release();
		dataBus.release();
	}

	@Override
	public byte[] test_and_set(int address, byte[] data) {
		addressBus.acquire();
		dataBus.acquire();
		access(address, Signal.Type.READ);
		byte[] old = blocks[index(address)].getData().clone();
		access(address, Signal.Type.READ_FOR_OWNERSHIP);
		blocks[index(address)].setData(data);
		blocks[index(address)].state = State.MODIFIED;
		addressBus.release();
		dataBus.release();
		return old;
	}

	public byte[] read(int address) {
		busAcquire(address, Signal.Type.READ);
		return blocks[index(address)].getData();
	}

	public void write(int address, byte data[]) {
		busAcquire(address, Signal.Type.READ_FOR_OWNERSHIP);
		blocks[index(address)].setData(data);
		blocks[index(address)].state = State.MODIFIED;
	}
	
	private void writeBack(int index) {
		dataBus.broadcast(this, new Signal(index|blocks[index].tag<<tagShift, Signal.Type.WRITE, blocks[index].getData()));
		blocks[index].state = State.SHARED;
		Stall.stall(Config.MEM_WRITE_BACK_TIME);
	}
	
	private int tag(int address){
		return address/blocks.length;
	}
	
	private int index(int address){
		return address%blocks.length;
	}
	
	/***
	 * 
	 * @param address
	 * @return 	0	not cached and invalid block
	 * 			1	cached
	 * 			-1	not cache and needs write back 
	 */
	private int cached(int address){
		CacheBlock block = blocks[index(address)];
		if(block.state.equals(CacheBlock.State.INVALID))
			return 0;
		if(block.tag == tag(address))
			return 1;
		return block.state.equals(CacheBlock.State.MODIFIED) ? -1 : 0;
	}
	
	private void cache(int address, byte[] data){
		int index = index(address);
		if(cached(address)==-1)	// write back
			writeBack(index);
		blocks[index].setData(data);
		blocks[index].tag = tag(address);
		blocks[index].state = nonExclusive ? CacheBlock.State.SHARED : CacheBlock.State.EXCLUSIVE;
	}

	public void signal(Signal signal) {
		int res = cached(signal.address);
		if(res==1 || signal.address==workingAddress){
			Logger.debug("Processing " + signal + "@" + index);
			int index = index(signal.address);
			CacheBlock block = blocks[index];
			switch(signal.type){
				case READ:
					if(block.state.equals(CacheBlock.State.EXCLUSIVE)){	// down-grade
						sharedBus.broadcast(this, new Signal(signal.address, Signal.Type.NON_EXCLUSIVE));
						block.state = CacheBlock.State.SHARED;
					}else if(block.state.equals(CacheBlock.State.MODIFIED)){	// write back
						sharedBus.broadcast(this, new Signal(signal.address, Signal.Type.BACKOFF));
						writeBack(index);
					}
					break;
				case READ_FOR_OWNERSHIP:
					if(block.state.equals(CacheBlock.State.MODIFIED)){	// write back
						sharedBus.broadcast(this, new Signal(signal.address, Signal.Type.BACKOFF));
						writeBack(index);
					}
					block.state = CacheBlock.State.INVALID;	// down-grade
					break;
				case READ_RESPONSE:
					if(res!=1)
						cache(signal.address, signal.data);
					break;
				case NON_EXCLUSIVE:
					if(workingAddress == signal.address)
						nonExclusive = true;
					break;
				case BACKOFF:
					if(workingAddress == signal.address)
						backoff = true;
					break;
			}
		}else
			Logger.debug("Discard " + signal + "@" + index + " reason:" + res);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Cache");
		for (CacheBlock block : blocks)
			buffer.append("\n").append(block);
		return buffer.toString();
	}
}
