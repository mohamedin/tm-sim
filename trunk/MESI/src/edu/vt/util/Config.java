package edu.vt.util;

import edu.vt.arch.cache.mesi.Cache;

public class Config {

	public static int CORES = 8;
	public static int MEM_SIZE = 16;
	public static int BLOCK_SIZE = 2;

	public static long MEM_ACCESS_TIME = 5000;
	public static long MEM_WRITE_BACK_TIME = 1000;
	public static long CACHE_ACCESS_TIME = 100;
	
	public static long TIME_SAFTY_MARGIN = 50;
	
	public static Class CACHE_CLASS = Cache.class;
	
}
