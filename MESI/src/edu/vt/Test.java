package edu.vt;

import edu.vt.sim.Model;
import edu.vt.util.Config;

public class Test {
	
	public static void main(String[] args) {
		// configuration
		System.setProperty("verbose", "true");
		
		Config.CORES = 2;
		Config.MEM_SIZE = 8;
		Config.BLOCK_SIZE = 2;
		Model model = new Model(new SamplePrograms.LockCounter(), new SamplePrograms.LockCounter());
		model.start();
		
		Config.CACHE_CLASS = edu.vt.arch.cache.tm1.Cache.class;
		Config.MEM_SIZE /=2;
		Model tmModel = new Model(new SamplePrograms.TMCounter(), new SamplePrograms.TMCounter());
		tmModel.start();

		// output
		System.out.println(model);
		System.out.println(tmModel);
	}
	
}
