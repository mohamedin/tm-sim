package edu.vt.arch.cache;

import edu.vt.arch.com.AddressBus;
import edu.vt.arch.com.DataBus;
import edu.vt.arch.com.SharedBus;
import edu.vt.arch.com.Signal;
import edu.vt.arch.mem.Stall;
import edu.vt.util.Config;

public class Cache {

	private Block[] blocks;
	
	private DataBus dataBus;
	private AddressBus addressBus;
	private SharedBus sharedBus;

	private boolean backoff = false;
	private int workingAddress = -1;
	
	public Cache(int size){
		blocks = new Block[size];
		for (int i=0; i<blocks.length; i++)
			blocks[i] = new Block();
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
		switch(cached(address)){
			case 1:	// cache read hit 
				Stall.stall(Config.CACHE_ACCESS_TIME); 
				break;
			case -1:  // uncached & write back needed
				writeBack(address);
			case 0: // uncached
				workingAddress = address;
				addressBus.broadcast(new Signal(address, type), Config.MEM_ACCESS_TIME);
				if(backoff)
					Stall.stall(Config.MEM_WRITE_BACK_TIME);
				backoff = false;
				workingAddress = -1;
				break;
		}
	}
	
	public byte[] read(int address) {
		access(address, Signal.Type.READ);
		return blocks[index(address)].data;
	}

	public void write(int address, byte data[]) {
		access(address, Signal.Type.WRITE);
		blocks[index(address)].data = data;
	}
	
	private void writeBack(int index) {
		dataBus.broadcast(new Signal(index|blocks[index].tag, Signal.Type.WRITE, blocks[index].data), Config.MEM_WRITE_BACK_TIME);
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
		Block block = blocks[index(address)];
		if(block.state.equals(Block.State.INVALID))
			return 0;
		if(block.tag == tag(address))
			return 1;
		return block.state.equals(Block.State.MODIFIED) ? -1 : 0;
	}
	
	private void cache(int address, byte[] data){
		int index = index(address);
		if(cached(address)==-1)	// write back
			writeBack(index);
		blocks[index].data = data;
		blocks[index].tag = tag(address);
		blocks[index].state = Block.State.EXCLUSIVE;
	}

	public void signal(Signal signal) {
		if(cached(signal.address)==1){
			int index = index(signal.address);
			Block block = blocks[index];
			switch(signal.type){
				case READ:
					if(block.state.equals(Block.State.EXCLUSIVE))	// down-grade
						block.state = Block.State.SHARED;
					else if(block.state.equals(Block.State.MODIFIED)){	// write back
						sharedBus.broadcast(new Signal(signal.address, Signal.Type.BACKOFF), 0);
						writeBack(index|block.tag);
					}
					break;
				case WRITE:
					if(block.state.equals(Block.State.MODIFIED)){	// write back
						sharedBus.broadcast(new Signal(signal.address, Signal.Type.BACKOFF), 0);
						writeBack(index|block.tag);
					}else
						block.state = Block.State.INVALID;	// down-grade
					break;
				case READ_RESPONSE:
					cache(signal.address, signal.data);
					break;
				case BACKOFF:
					if(workingAddress == signal.address)
						backoff = true;
					break;
			}
		}
	}
}
