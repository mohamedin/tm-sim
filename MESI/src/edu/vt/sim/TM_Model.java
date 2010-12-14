package edu.vt.sim;

import edu.vt.arch.cache.Cache;
import edu.vt.arch.cache.MESI_Cache;
import edu.vt.arch.cache.TM_Cache;
import edu.vt.arch.com.AddressBus;
import edu.vt.arch.com.DataBus;
import edu.vt.arch.com.SharedBus;
import edu.vt.arch.cpu.Core;
import edu.vt.arch.mem.MainMemory;
import edu.vt.util.Config;

public class TM_Model {

	private Core[] cores;
	private MainMemory memory;
	private Cache[] caches;

	public TM_Model(Program... programs) {
		memory = new MainMemory(Config.MEM_SIZE);
		caches = new Cache[Config.CORES];
		for(int i=0; i<caches.length; i++)
			caches[i] = new TM_Cache(i, Config.MEM_SIZE / Config.BLOCK_SIZE);
		// Connections
		AddressBus addressBus = new AddressBus(memory, caches);
		DataBus dataBus = new DataBus(memory, caches);
		SharedBus sharedBus = new SharedBus(caches);
		memory.connectToDataBus(dataBus);
		for(Cache cache : caches){
			cache.connectToAddressBus(addressBus);
			cache.connectToSharedBus(sharedBus);
			cache.connectToDataBus(dataBus);
		}
		// CPU Cores
		cores = new Core[Config.CORES];
		for(int i=0; i<cores.length; i++){
			Program program = i<programs.length ? programs[i] : null;
			cores[i] = new Core(i, caches[i], program);
			if(program!=null)
				program.setAffinity(cores[i]);
		}
	}
	
	public void start(){
		for(Core core : cores){
			core.start();
		}
		for(Core core : cores){
			try {
				core.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("~~~~~~~~~~~~~~~~~ M O D E L ~~~~~~~~~~~~~~~\n");
		buffer.append("Cores :").append(cores.length).append("\n");
		for (Cache cache : caches)
			buffer.append(cache).append("\n");
		buffer.append(memory);
		buffer.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		return buffer.toString();
	}

}