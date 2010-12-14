package edu.vt;

import edu.vt.sim.Program;

public class SamplePrograms {

	static Program complexReadProg = new Program(){
		public void execute() {
			log(read(0));
			log(read(1));
			log(read(2));
			log(read(3));
			log(read(0));
			log(read(1));
		}
	};
	
	static Program complexWriteProg = new Program(){
		public void execute() {
			write(0, new byte[] {5, 6});
			write(1, new byte[] {5, 6});
			write(2, new byte[] {5, 6});
			write(3, new byte[] {5, 6});
			write(0, new byte[] {5, 6});
			write(1, new byte[] {5, 6});
		}
	};


	static Program memTestProg = new Program(){
		public void execute() {
			write(0, new byte[] {0, 1});
			write(1, new byte[] {2, 3});
			write(2, new byte[] {4, 5});
			write(3, new byte[] {6, 7});
			write(0, new byte[] {1, 0});
			write(1, new byte[] {3, 2});
			write(2, new byte[] {5, 4});
			write(3, new byte[] {7, 6});
		}
	};
	
	static Program simpleReadProg = new Program(){
		public void execute() {
			read(1);
		}
	};
	
	static Program simpleWriteProg = new Program(){
		public void execute() {
			write(1, new byte[] {7, 8});
		}
	};
	
	static Program TM1_Prog = new edu.vt.sim.tm.Program(){
		public void execute() {
			atomic_begin();
			write(1, new byte[] {1, 2});
			write(2, new byte[] {3, 4});
			atomic_end();
		}
	};
	
	static Program TM2_Prog = new edu.vt.sim.tm.Program(){
		public void execute() {
			atomic_begin();
			write(1, new byte[] {5, 6});
			write(2, new byte[] {7, 8});
			atomic_end();
		}
	};
}
