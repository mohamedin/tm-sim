package edu.vt.sim.tm;

import edu.vt.util.Logger;


public abstract class Program extends edu.vt.sim.Program{

	public void atomic_begin(){
		core.atomic_begin();
	}
	
	public void atomic_end(){
		core.atomic_end();
	}
	
	@Override
	public void run() {
		while(true){
			try {
				execute();
			} catch (TransactionConflictException e) {
				Logger.info("-- RETRY --");
				continue;
			}
			break;
		}
	}
}
