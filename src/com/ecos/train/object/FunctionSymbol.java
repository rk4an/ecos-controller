package com.ecos.train.object;

import android.util.SparseArray;

public class FunctionSymbol {

	private static FunctionSymbol INSTANCE = null;
	private SparseArray<String> symbols;

	private FunctionSymbol(){
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
		symbols.put(11015, "Curve sound");
		symbols.put(11271, "Relief valve");
		symbols.put(11527, "Steam blow off");
		symbols.put(553, "Air pump");
		symbols.put(1321, "Water pump");
		symbols.put(11783, "Sand");
		symbols.put(1799, "Drain valve");
		symbols.put(12039, "Set barke");
		symbols.put(1029, "Set barke");
		symbols.put(772, "Board lighting");
		symbols.put(5639, "Diesel Engine Notch Up");
		symbols.put(5640, "Diesel Engine Notch Down");
	}

	public static synchronized FunctionSymbol getInstance(){			
		if (INSTANCE == null){ 	
			INSTANCE = new FunctionSymbol();	
		}
		return INSTANCE;
	}

	public SparseArray<String> getSymbols() {
		return symbols;
	}
}
