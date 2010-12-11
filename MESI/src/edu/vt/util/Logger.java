package edu.vt.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static Boolean verbose = Boolean.getBoolean("verbose");
	private static SimpleDateFormat formatter = new SimpleDateFormat("mm:ss SSS");
	private static void log(String level, String msg){
		if(!verbose)
			return;
		
		String fullMsg = level + " " +  Thread.currentThread().getName() + "@" + formatter.format(new Date()) +" :" + msg;
		if(level.equals("ERROR")) System.err.println(fullMsg);
		else System.out.println(fullMsg);
	}

	public static void fetal(String msg){
		log("FETAL", msg);
	}

	public static void info(String msg){
		log("INFO", msg);
	}

	public static void debug(String msg){
		log("DEBUG", msg);
	}
	
	public static void error(String msg){
		log("ERROR", msg);
	}

	public static void init(String string) throws IOException {
  		File outFile = new File("logs/" + string + ".out.txt");
  		if(!outFile.exists()) outFile.createNewFile(); 
  		System.setOut(new PrintStream(new FileOutputStream(outFile,true)));
  		File errFile = new File("logs/" + string + ".err.txt");
  		if(!errFile.exists()) errFile.createNewFile(); 
  		System.setErr(new PrintStream(new FileOutputStream(errFile,true)));
  		
  		Logger.error("- - - - - - - - - - [ Start ] - - - - - - - - - -");
  		Logger.info("- - - - - - - - - - [ Start ] - - - - - - - - - -");
	}
}
