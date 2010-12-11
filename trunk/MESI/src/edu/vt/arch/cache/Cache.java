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

	public synchronized byte[] read(int address) {
		if(!cached(address)){
			addressBus.broadcast(new Signal(address, Signal.Type.READ), Config.MEM_ACCESS_TIME);
			try { wait(); } catch (InterruptedException e) {}
		}
		else
			Stall.stall(Config.CACHE_ACCESS_TIME);
		return blocks[index(address)].data;
	}

	public void write(int address, byte data[]) {
		// TODO		
	}
	
	private int tag(int address){
		return address/blocks.length;
	}
	
	private int index(int address){
		return address%blocks.length;
	}
	
	private boolean cached(int address){
		Block block = blocks[index(address)];
		return !block.state.equals(Block.State.INVALID) && (block.tag == tag(address));
	}
	
	private void cache(int address, byte[] data){
		int index = index(address);
		if(blocks[index].state.equals(Block.State.MODIFIED)){	// write back
			dataBus.broadcast(new Signal(index|blocks[index].tag, Signal.Type.WRITE, blocks[index].data), Config.MEM_WRITE_BACK_TIME);	
		}
		blocks[index].data = data;
		blocks[index].tag = tag(address);
		blocks[index].state = Block.State.EXCLUSIVE;
	}

	public synchronized void signal(Signal signal) {
		if(cached(signal.address)){
			int index = index(signal.address);
			Block block = blocks[index];
			if(signal.type.equals(Signal.Type.READ)){
				if(block.state.equals(Block.State.EXCLUSIVE))
					block.state = Block.State.SHARED;
				else if(block.state.equals(Block.State.MODIFIED))	// write back
					dataBus.broadcast(new Signal(index|block.tag, Signal.Type.WRITE, blocks[index].data), Config.MEM_WRITE_BACK_TIME);
			}else if(signal.type.equals(Signal.Type.WRITE)){
				// TODO
			}else if(signal.type.equals(Signal.Type.READ_RESPONSE)){
				cache(signal.address, signal.data);
				notifyAll();
			}
		}
	}
}
