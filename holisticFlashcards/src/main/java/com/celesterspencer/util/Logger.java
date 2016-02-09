package com.celesterspencer.util;

import java.util.Arrays;
import java.util.HashSet;

public class Logger {

	private static HashSet<String> m__flags = new HashSet<String>(Arrays.asList("ALL", "Dictionary", "IOMANAGER"));
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// GETTERS AND SETTERS
	//-------------------------------------------------------------------------------------------------------------------
	public static void setFlag(String flag) {
		m__flags.add(flag);
	}
	
	public static void log(String msg, String flag) {
		if (m__flags.contains(flag) || m__flags.contains("DEBUG") || m__flags.contains("ERROR")) {
			System.out.println("" + flag + ": " + msg);
		}
	}
	
}
