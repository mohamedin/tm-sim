package edu.vt;

import edu.vt.sim.Model;
import edu.vt.util.Config;

public class Test {
	
	public static void main(String[] args) {
		// configuration
		System.setProperty("verbose", "true");
		Config.CORES = 2;
		Config.MEM_SIZE = 4;
		
		// running program on 4 cores
		Model model = new Model(SamplePrograms.complexWriteProg, SamplePrograms.complexReadProg);
		model.start();

		// output
		System.out.println(model);
	}
	
}
