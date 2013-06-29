package com.ecos.train;

public class Settings {

	public static final int CONSOLE_PORT = 15471;
	
	public static String consoleIp = "";
	public static int consolePort = CONSOLE_PORT;
	public static int trainId = -1;
	
	public static State state = State.NONE;

	public static boolean fullVersion = false;
	
	public enum State {
		NONE, 
		INIT_GET_EMERGENCY, 
		INIT_GET_TRAINS, 
		GET_TRAIN_MAIN_STATE, 
		GET_TRAIN_BUTTON_STATE, 
		GET_TRAIN_BUTTON_STATE_EXTRA, 
		IDLE;
	}
}
