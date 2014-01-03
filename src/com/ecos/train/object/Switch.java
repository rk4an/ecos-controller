package com.ecos.train.object;

import android.util.SparseArray;

public class Switch {

	private static Switch INSTANCE = null;
	private SparseArray<String> symbols;

	private Switch(){
		symbols = new  SparseArray<String>();
		symbols.put(0, "Empty");
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
