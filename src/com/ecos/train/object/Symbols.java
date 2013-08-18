package com.ecos.train.object;

import android.util.SparseArray;

public class Symbols {

	private static Symbols INSTANCE = null;
	private  SparseArray<String> symbols;

	private Symbols(){
		symbols = new  SparseArray<String>();
		symbols.put(2, "Function");
		symbols.put(3, "Head light");
		symbols.put(4, "Light 1");
		symbols.put(5, "Light 2");
		symbols.put(6, "Function"); //Fix Function
		symbols.put(7, "Sound");
		symbols.put(8, "Music");
		symbols.put(9, "Announce");
		symbols.put(10, "Routing speed");
		symbols.put(11, "ABV");
		symbols.put(32, "Coupler");
		symbols.put(33, "Steam");
		symbols.put(34, "Panto");
		symbols.put(35, "Hight beam");
		symbols.put(36, "Bell");
		symbols.put(37, "Horn");
		symbols.put(38, "Whistle");
		symbols.put(39, "Door sound");
		symbols.put(40, "Fan");
		symbols.put(42, "Shovel work sound");
		symbols.put(44, "Shift");
		symbols.put(260, "Interior lighting");
		symbols.put(261, "Plate light");
		symbols.put(263, "Brake sound");
		symbols.put(299, "Crane raise lower");
		symbols.put(555, "Hook up down");
		symbols.put(773, "Wheel light");
		symbols.put(811, "Turn crane");
		symbols.put(1031, "Steam blow");
		symbols.put(1033, "Radio sound");
		symbols.put(1287, "Coupler sound");
		symbols.put(1543, "Track sound");
		symbols.put(1607, "Diesel Engine Notch Up");
		symbols.put(1608, "Diesel Engine Notch Down");
		symbols.put(2055, "Thunderer whistle");
		symbols.put(3847, "Buffer sound");
		/*symbols.put(9001, "Curve sound");
		symbols.put(9002, "Relief valve");
		symbols.put(9003, "Steam blow off");
		symbols.put(9004, "Air pump");
		symbols.put(9005, "Water pump");
		symbols.put(9006, "Sand");
		symbols.put(9007, "Drain valve");
		symbols.put(9008, "Set barke");
		symbols.put(9009, "Set barke");
		symbols.put(9010, "Board lighting");*/
	}

	public static synchronized Symbols getInstance(){			
		if (INSTANCE == null){ 	
			INSTANCE = new Symbols();	
		}
		return INSTANCE;
	}

	public SparseArray<String> getSymbols() {
		return symbols;
	}
}