package edu.vt.arch.mem;

import edu.vt.arch.com.DataBus;
import edu.vt.arch.com.Signal;


public class MainMemory {
	private MemoryBlock[] memory;
	
	private DataBus dataBus;
	
	public MainMemory(int size){
		memory = new MemoryBlock[size];
	}

	public void connectToDataBus(DataBus dataBus) {
		this.dataBus = dataBus;
	}

	public void signal(Signal signal) {
		if(signal.type.equals(Signal.Type.READ))
			dataBus.broadcast(new Signal(signal.address, Signal.Type.READ_RESPONSE, memory[signal.address].data), 0);
		else if(signal.type.equals(Signal.Type.WRITE))
			memory[signal.address].data = signal.data;
	}
}
