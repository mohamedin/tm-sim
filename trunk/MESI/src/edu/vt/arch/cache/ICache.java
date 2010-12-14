package edu.vt.arch.cache;

import edu.vt.arch.com.AddressBus;
import edu.vt.arch.com.DataBus;
import edu.vt.arch.com.IBusComponent;
import edu.vt.arch.com.SharedBus;

public interface ICache extends IBusComponent{

	public byte[] read(int address);
	public void write(int address, byte data[]);
	
	public void connectToAddressBus(AddressBus addressBus);
	public void connectToSharedBus(SharedBus sharedBus);
	public void connectToDataBus(DataBus dataBus);
	
}
