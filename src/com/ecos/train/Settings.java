package com.ecos.train;

import java.util.ArrayList;
import java.util.List;

import com.ecos.train.object.Train;

public class Settings {

	public static final int CONSOLE_PORT = 15471;

	public static String consoleIp = "";
	public static int consolePort = CONSOLE_PORT;
	
	public static List<Train> allTrains = new ArrayList<Train>();
	public static Train currentTrain = new Train(-1, "", "");
	
	public static State state = State.NONE;

	public static boolean fullVersion = false;
	
	public static final int SPEED_MIN = 0;
	public static final int SPEED_MAX = 127;
	public static final int SPEED_STEP = 10;
	
	public static boolean sortById = false;
	public static String protocolVersion = "0.2";
	
	public enum State {
		NONE, 
		INIT_GET_CONSOLE, 
		INIT_GET_TRAINS, 
		GET_TRAIN_MAIN_STATE,
		GET_TRAIN_BUTTON_STATE, 
		IDLE;
	}
	
}
