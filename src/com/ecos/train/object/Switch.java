package com.ecos.train.object;

import android.util.SparseArray;

public class Switch {

	private static Switch INSTANCE = null;
	private SparseArray<String> symbols;

	private Switch(){
		symbols = new  SparseArray<String>();
		symbols.put(0, "Turnout left");
		symbols.put(1, "Turnout right");
		symbols.put(2, "Turnout 3-way");
		symbols.put(4, "Double slip turnout 4-way");
		symbols.put(5, "Semaphore hp01");
		symbols.put(6, "Semaphore hp02");
		symbols.put(7, "Semaphore hp012");
		symbols.put(8, "Switch signal");
		symbols.put(9, "Light signal");
		symbols.put(10, "Light signal");
		symbols.put(12, "Light signal");
		symbols.put(15, "Streetlight");
		symbols.put(27, "Boom");
		symbols.put(29, "Distant signal");
		symbols.put(30, "Distant signal");
		symbols.put(36, "General function");
		symbols.put(38, "Dummy 2-states");
		symbols.put(39, "Dummy 3-states");
		symbols.put(40, "Dummy 4-states");
		
		/*
		symbols.put(0, "Light signal");
		symbols.put(0, "Switch signal");
		symbols.put(0, "Distant signal");
		symbols.put(0, "Double slip tournout 2-way");
		symbols.put(0, "Curved turnout left");
		symbols.put(0, "Curved turnout right");
		symbols.put(0, "Double crossower turnout 2-way");
		symbols.put(0, "Double crossower turnout 4-way left");
		symbols.put(0, "Double crossower turnout 4-way right");
		symbols.put(0, "Function with track");
		symbols.put(0, "Lattice mast lightning");
		symbols.put(0, "Decoupling track");
		symbols.put(0, "Lightning");
		symbols.put(0, "Engine Shed");
		symbols.put(0, "Start goal unidirectional");
		symbols.put(0, "Start goal bidirectional");
		symbols.put(0, "Signal SNCB 2 states");
		symbols.put(0, "Signal NS 2 states");
		symbols.put(0, "Signal CFL 3 states");
		symbols.put(0, "Signal SNCF 3 states");
		symbols.put(0, "Signal SNCB 4 states");
		symbols.put(0, "Signal NS 3 states");
		*/
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
