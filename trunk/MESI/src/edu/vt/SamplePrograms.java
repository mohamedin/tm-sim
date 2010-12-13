package edu.vt;

import edu.vt.sim.Program;

public class SamplePrograms {

	static Program complexReadProg = new Program(){
		@Override
		public void run() {
			log(read(0));
			log(read(1));
			log(read(2));
			log(read(3));
			log(read(0));
			log(read(1));
		}
	};
	
	static Program complexWriteProg = new Program(){
		@Override
		public void run() {
			write(0, new byte[] {5, 6});
			write(1, new byte[] {5, 6});
			write(2, new byte[] {5, 6});
			write(3, new byte[] {5, 6});
			write(0, new byte[] {5, 6});
			write(1, new byte[] {5, 6});
		}
	};


	static Program memTestProg = new Program(){
		@Override
		public void run() {
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
		@Override
		public void run() {
			log(read(0));
		}
	};
	
	static Program simpleWriteProg = new Program(){
		@Override
		public void run() {
			write(0, new byte[] {7, 8});
		}
	};
}
