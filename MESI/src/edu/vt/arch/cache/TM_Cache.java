package edu.vt.arch.cache;

import edu.vt.arch.cache.CacheBlock.State;
import edu.vt.arch.com.AddressBus;
import edu.vt.arch.com.DataBus;
import edu.vt.arch.com.SharedBus;
import edu.vt.arch.com.Signal;
import edu.vt.arch.mem.Stall;
import edu.vt.util.Config;
import edu.vt.util.Logger;

public class TM_Cache extends Cache{

	private TM_CacheBlock[] blocks;
	
	private DataBus dataBus;
	private AddressBus addressBus;
	private SharedBus sharedBus;

	private boolean backoff = false;
	private int workingAddress = -1;
	public int index;
	
	private boolean aborted = false;
	
	public TM_Cache(int index, int size){
		super(index, size);
		this.index = index;
		blocks = new TM_CacheBlock[size];
		for (int i=0; i<blocks.length; i++)
			blocks[i] = new TM_CacheBlock();
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

	private int findBlockForReplacement(){	
		//not cached so find an empty line 
		for (int i=0; i<blocks.length; i++)
			if (blocks[i].tm_state == TM_CacheBlock.TM_State.EMPTY)
				return i;
		//then noraml line
		for (int i=0; i<blocks.length; i++)
			if (blocks[i].tm_state == TM_CacheBlock.TM_State.NORMAL)
				return i;
		//finally xcommit
		for (int i=0; i<blocks.length; i++)
			if (blocks[i].tm_state == TM_CacheBlock.TM_State.XCOMMIT){
				if (blocks[i].state == TM_CacheBlock.State.MODIFIED){//Issue writeback
					addressBus.acquire();
					dataBus.acquire();					
					writeBack(i);
					addressBus.release();
					dataBus.release();					
				}
				return i;
			}
		//There must be something wrong if we reached here (All full)//TODO: must abort
		return -1;
	}
	
	public byte[] read(int address) {
		//return any data if transaction is aborted
		if (aborted) return new byte[]{-128,-128};
		//check if it is cached
		for (int i=0; i<blocks.length; i++){
			if (blocks[i].address == address){
				if (blocks[i].tm_state == TM_CacheBlock.TM_State.XABORT){
					Stall.stall(Config.CACHE_ACCESS_TIME); 
					return blocks[i].getData();
				}
				else if (blocks[i].tm_state == TM_CacheBlock.TM_State.NORMAL){
					Stall.stall(Config.CACHE_ACCESS_TIME); 
					blocks[i].tm_state = TM_CacheBlock.TM_State.XABORT;
					int another = findBlockForReplacement();
					blocks[another].address = address;
					blocks[another].state = TM_CacheBlock.State.EXCLUSIVE;//TODO: Check
					blocks[another].tm_state = TM_CacheBlock.TM_State.XCOMMIT;
					blocks[another].setData(blocks[i].getData());
					return blocks[i].getData();
				}
			}
		}
		//Not found in cache
		addressBus.acquire();
		dataBus.acquire();
		workingAddress = address;
		addressBus.broadcast(this, new Signal(address, Signal.Type.READ));
		Stall.stall(Config.MEM_ACCESS_TIME);
		if(backoff){//Conflict TODO: review
			Stall.stall(Config.MEM_WRITE_BACK_TIME);
			aborted = true;
			for (int i=0; i<blocks.length; i++){
				if (blocks[i].tm_state.equals(TM_CacheBlock.TM_State.XCOMMIT)){
					blocks[i].tm_state = TM_CacheBlock.TM_State.NORMAL;
				}else if (blocks[i].tm_state.equals(TM_CacheBlock.TM_State.XABORT)){
					blocks[i].tm_state = TM_CacheBlock.TM_State.EMPTY;
					blocks[i].state = TM_CacheBlock.State.INVALID;
				}
			}
//			blocks[index(address)].state = State.SHARED;
		}
		backoff = false;
		workingAddress = -1;
		addressBus.release();
		dataBus.release();
		
		return blocks[index(address)].getData();
	}

	public void write(int address, byte data[]) {
		//return if transaction is aborted
		if (aborted) return;
		//check if it is cached
		for (int i=0; i<blocks.length; i++){
			if (blocks[i].address == address){
				if (blocks[i].tm_state == TM_CacheBlock.TM_State.XABORT){
					Stall.stall(Config.CACHE_ACCESS_TIME);
					blocks[i].setData(data);
					blocks[i].state = TM_CacheBlock.State.MODIFIED;
					return;
				}
				else if (blocks[i].tm_state == TM_CacheBlock.TM_State.NORMAL){
					Stall.stall(Config.CACHE_ACCESS_TIME); 
					blocks[i].tm_state = TM_CacheBlock.TM_State.XABORT;
					blocks[i].setData(data);
					blocks[i].state = TM_CacheBlock.State.MODIFIED;					
					int another = findBlockForReplacement();
					blocks[another].address = address;
					blocks[another].state = TM_CacheBlock.State.EXCLUSIVE;//TODO: Check
					blocks[another].tm_state = TM_CacheBlock.TM_State.XCOMMIT;
					blocks[another].setData(blocks[i].getData());
					return;
				}
			}
		}
		//Not found in cache
		addressBus.acquire();
		dataBus.acquire();
		workingAddress = address;
		addressBus.broadcast(this, new Signal(address, Signal.Type.READ_FOR_OWNERSHIP));
		Stall.stall(Config.MEM_ACCESS_TIME);
		if(backoff){//Conflict TODO: review
			Stall.stall(Config.MEM_WRITE_BACK_TIME);
			aborted = true;
			for (int i=0; i<blocks.length; i++){
				if (blocks[i].tm_state.equals(TM_CacheBlock.TM_State.XCOMMIT)){
					blocks[i].tm_state = TM_CacheBlock.TM_State.NORMAL;
				}else if (blocks[i].tm_state.equals(TM_CacheBlock.TM_State.XABORT)){
					blocks[i].tm_state = TM_CacheBlock.TM_State.EMPTY;
					blocks[i].state = TM_CacheBlock.State.INVALID;
				}
			}
//			blocks[index(address)].state = State.SHARED;
		}
		backoff = false;
		workingAddress = -1;
		addressBus.release();
		dataBus.release();
		
		for (int i=0; i<blocks.length; i++){
			if (blocks[i].address == address && blocks[i].tm_state.equals(TM_CacheBlock.TM_State.XABORT)){
				blocks[i].setData(data);
				blocks[i].state = TM_CacheBlock.State.MODIFIED;
				return;
			}
		}
	}
	
	private void writeBack(int index) {
		dataBus.broadcast(this, new Signal(blocks[index].address, Signal.Type.WRITE, blocks[index].getData()));
		blocks[index].state = TM_CacheBlock.State.SHARED;
		Stall.stall(Config.MEM_WRITE_BACK_TIME);
	}
	
	private int index(int address){
		for (int i=0; i<blocks.length; i++)
			if (blocks[i].address == address)
				return i;
		//Not in the cache
		return -1;
	}
	
	/***
	 * 
	 * @param address
	 * @return 	0	not cached and invalid block
	 * 			1	cached
	 */
	private int cached(int address){
		//fully associative so wether it exists or not 
		int i = index(address);
		if (i==-1 || blocks[i].state.equals(TM_CacheBlock.State.INVALID))
			return 0;
		else
			return 1;
	}
	
	private void cache(int index, int address, byte[] data){
		blocks[index].setData(data);
		blocks[index].address = address;
		blocks[index].state = TM_CacheBlock.State.EXCLUSIVE;
		blocks[index].tm_state = TM_CacheBlock.TM_State.XCOMMIT;
		index = findBlockForReplacement();
		blocks[index].setData(data);
		blocks[index].address = address;
		blocks[index].state = TM_CacheBlock.State.EXCLUSIVE;
		blocks[index].tm_state = TM_CacheBlock.TM_State.XABORT;
	}

	public void signal(Signal signal) {
		int res = cached(signal.address);
		if(res==1 || signal.address==workingAddress){
			Logger.debug("Processing " + signal + "@" + index);
			int index = index(signal.address);
			if (index == -1) //not in cache
				index = findBlockForReplacement();
			TM_CacheBlock block = blocks[index];
			switch(signal.type){
				case READ:
					if(block.state.equals(TM_CacheBlock.State.EXCLUSIVE))	// down-grade
						block.state = TM_CacheBlock.State.SHARED;
					else if(block.state.equals(TM_CacheBlock.State.MODIFIED)){	// write back
						sharedBus.broadcast(this, new Signal(signal.address, Signal.Type.BACKOFF));
						writeBack(index);//TODO: check
					}
					break;
				case READ_FOR_OWNERSHIP:
					if(block.state.equals(TM_CacheBlock.State.MODIFIED)){	// write back
						sharedBus.broadcast(this, new Signal(signal.address, Signal.Type.BACKOFF));
						writeBack(index);//TODO: check
					}
					block.state = TM_CacheBlock.State.INVALID;	// down-grade
					break;
				case READ_RESPONSE:
					if(res!=1)
						cache(index, signal.address, signal.data);
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
		for (TM_CacheBlock block : blocks)
			buffer.append("\n").append(block);
		return buffer.toString();
	}

}
