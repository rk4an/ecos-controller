package com.ecos.train;

public class Settings {

	public static String consoleIp = "";
	public static int consolePort = 15471;
	public static int trainId = -1;
	public static State state = State.NONE;
	
	protected enum State {
		NONE, INIT_GET_EMERGENCY, INIT_GET_TRAINS, GET_TRAIN_MAIN_STATE, GET_TRAIN_BUTTON_STATE, IDLE;
	  }
	
}
