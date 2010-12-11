package edu.vt;

import edu.vt.sim.Model;
import edu.vt.sim.Program;
import edu.vt.util.Config;

public class Test {
	
	public static void main(String[] args) {
		// configuration
		System.setProperty("verbose", "true");
		Config.CORES = 2;
		Config.MEM_SIZE = 4;
		// simple program
		Program readProg1 = new Program(){
			@Override
			public void run() {
				log(read(0));
			}
		};
		Program writeProg1 = new Program(){
			@Override
			public void run() {
				write(0, new byte[] {5, 6});
			}
		};
		Program writeProg2 = new Program(){
			@Override
			public void run() {
				write(0, new byte[] {7, 8});
			}
		};
		
		// running program on 4 cores
		Model model = new Model(writeProg1, readProg1);
		model.start();
		
		System.out.println(model);
	}
	
}
