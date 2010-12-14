package edu.vt;

import edu.vt.sim.Model;
import edu.vt.util.Config;

public class Test {
	
	public static void main(String[] args) {
		// configuration
//		System.setProperty("verbose", "true");
	
		Config.BLOCK_SIZE = 2;
		
		for(int i=1; i<=16; i*=2){
			System.out.println("I=" + i);
			Config.CORES = i;
			
			Config.CACHE_CLASS = edu.vt.arch.cache.mesi.Cache.class;
			Config.MEM_SIZE = 8;
			SamplePrograms.TASLockCounter[] tasCounters = new SamplePrograms.TASLockCounter[i];
			for(int j=0; j<i; j++)
				tasCounters[j] = new SamplePrograms.TASLockCounter();
			Model model = new Model(tasCounters);
			model.start();
			
			System.out.println(model);

			Config.CACHE_CLASS = edu.vt.arch.cache.tm1.Cache.class;
			Config.MEM_SIZE /=2;
			SamplePrograms.TMCounter[] tmCounters = new SamplePrograms.TMCounter[i];
			for(int j=0; j<i; j++)
				tmCounters[j] = new SamplePrograms.TMCounter();
			Model tmModel = new Model(tmCounters);
			tmModel.start();

			// output
			System.out.println(tmModel);
		}
	}
	
}
