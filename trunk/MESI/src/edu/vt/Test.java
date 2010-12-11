package edu.vt;

import edu.vt.sim.Model;
import edu.vt.sim.Program;
import edu.vt.util.Config;

public class Test {
	
	public static void main(String[] args) {
		// configuration
		Config.CORES = 8;
		Config.MEM_SIZE = 1024;
		// simple program
		Program simpleProgram = new Program(){
			@Override
			public void run() {
				
			}
		};
		// running program on 4 cores
		new Model(simpleProgram, simpleProgram, simpleProgram, simpleProgram).start();
	}
	
}
