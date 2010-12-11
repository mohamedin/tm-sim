package edu.vt.arch.mem;

public class Stall {

	public static void stall(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
