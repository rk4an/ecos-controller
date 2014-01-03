package com.ecos.train.object;

import java.util.Comparator;

public class Train implements Comparable<Train> {

	private int id;
	private String name;
	private String address;
	private int speedIndicator = 0;
	private int symbol = 3;

	public Train(int id, String name, String address){
		this.id = id;
		this.name = name;
		this.address = address;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getSpeedIndicator() {
		return speedIndicator;
	}

	public void setSpeedIndicator(int speedIndicator) {
		this.speedIndicator = speedIndicator;
	}

	public int getSymbol() {
		return symbol;
	}

	public void setSymbol(int symbol) {
		this.symbol = symbol;
	}

	public String toString() {
		return name + " (" + id + ")";
	}

	@Override
	public int compareTo(Train t) {
		return name.compareTo(t.getName());
	}

	public static Comparator<Train> TrainNameComparator = new Comparator<Train>() {

		public int compare(Train train1, Train train2) {

			String trainName1 = train1.getName().toUpperCase();
			String trainName2 = train2.getName().toUpperCase();

			return trainName1.compareTo(trainName2);
		}
	};
	
	public static Comparator<Train> TrainIdComparator = new Comparator<Train>() {

		public int compare(Train train1, Train train2) {

			int trainId1 = train1.getId();
			int trainId2 = train2.getId();

			return trainId1 - trainId2;
		}
	};
}
