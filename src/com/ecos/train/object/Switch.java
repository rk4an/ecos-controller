package com.ecos.train.object;

import android.util.SparseArray;

public class Switch {

	private static Switch INSTANCE = null;
	private SparseArray<String> symbols;

	private Switch(){
		symbols = new  SparseArray<String>();
		symbols.put(0, "Turnout left");
		symbols.put(1, "Turnout right");
		symbols.put(4, "Double slip turnout ");
		symbols.put(5, "Semaphore hp01");
		symbols.put(6, "Semaphore hp02");
		symbols.put(7, "Semaphore hp012");
		symbols.put(12, "Light signal");
		symbols.put(36, "General function");
		symbols.put(38, "Dummy 2-states");
		symbols.put(39, "Dummy 3-states");
		symbols.put(40, "Dummy 4-states");
	}

	public static synchronized Switch getInstance(){			
		if (INSTANCE == null){ 	
			INSTANCE = new Switch();	
		}
		return INSTANCE;
	}

	public SparseArray<String> getSymbols() {
		return symbols;
	}
}
