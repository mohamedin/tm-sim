package edu.vt;

import edu.vt.arch.cache.tm1.Cache;
import edu.vt.sim.Model;
import edu.vt.util.Config;

public class Test {
	
	public static void main(String[] args) {
		// configuration
		System.setProperty("verbose", "true");
		Config.CORES = 2;
		Config.MEM_SIZE = 4;
		Config.BLOCK_SIZE = 2;
		Config.CACHE_CLASS = Cache.class;
		
		// running program on 4 cores
//		Model model = new Model(SamplePrograms.simpleWriteProg, SamplePrograms.simpleReadProg);
		Model model = new Model(SamplePrograms.TM1_Prog, SamplePrograms.TM2_Prog);
		model.start();

		// output
		System.out.println(model);
	}
	
}
