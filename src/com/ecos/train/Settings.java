package com.ecos.train;

import java.util.ArrayList;
import java.util.List;

import com.ecos.train.object.Train;

public class Settings {

	public static final int CONSOLE_PORT = 15471;

	public static String consoleIp = "";
	public static int consolePort = CONSOLE_PORT;

	public static List<Train> allTrains = new ArrayList<Train>();
	public static int currentTrainIndex = -1;

	public static boolean fullVersion = false;

	public static final int SPEED_MIN = 0;
	public static final int SPEED_MAX = 127;
	public static final int SPEED_STEP = 10;
	public static final int FUNCTION_BUTTONS = 24;

	public static boolean sortById = false;
	public static String protocolVersion = "0.2";

	public static Train getCurrentTrain() {
		if(currentTrainIndex != -1) {
			return allTrains.get(currentTrainIndex);
		}
		else {
			return new Train(-1, "", "");
		}
	}
}
