package edu.vt.arch.mem;

import edu.vt.arch.com.DataBus;
import edu.vt.arch.com.IBusComponent;
import edu.vt.arch.com.Signal;
import edu.vt.util.Config;
import edu.vt.util.Logger;


public class MainMemory implements IBusComponent{
	private MemoryBlock[] blocks;
	
	private DataBus dataBus;
	
	public MainMemory(int size){
		blocks = new MemoryBlock[size];
		for (int i=0; i<blocks.length; i++)
			blocks[i] = new MemoryBlock();
	}

	public void connectToDataBus(DataBus dataBus) {
		this.dataBus = dataBus;
	}

	public void signal(Signal signal) {
		Logger.debug("Processing " + signal + " @Memory");
		switch(signal.type){
			case READ_FOR_OWNERSHIP:
			case READ:
				Stall.stall(Config.MEM_ACCESS_TIME - Config.TIME_SAFTY_MARGIN);
				dataBus.broadcast(this, new Signal(signal.address, Signal.Type.READ_RESPONSE, blocks[signal.address].getData()));
				break;
			case WRITE:
				Logger.debug("Effective data change [" + signal.address + "]");
				blocks[signal.address].setData(signal.data);
				break;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Main Memory");
		for (MemoryBlock block : blocks)
			buffer.append("\n").append(block);
		return buffer.toString();
	}
}
