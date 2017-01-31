package com.settlers.hd;

import com.settlers.hd.Hexagon.Type;

public class Harbor {

	public static final int NUM_HARBOR = 9;
	
	public enum Position {
		NORTH, SOUTH, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
	}
	
	private static final Position[] POSITION_LIST = {
		Position.NORTH, Position.NORTHWEST, Position.NORTHEAST, 
		Position.NORTHWEST, Position.NORTHEAST, Position.SOUTHWEST, 
		Position.SOUTHEAST, Position.SOUTH, Position.SOUTH 
	};

	private Type type;
	private Position position;
	private int index;

	public Harbor(Type type, int index) {
		this.type = type;
		this.index = index;
		position = POSITION_LIST[index];
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static Harbor[] initialize() {

		// mark all traders as unassigned
		Harbor[] harbor = new Harbor[NUM_HARBOR];
		boolean[] usedTrader = new boolean[NUM_HARBOR];
		for (int i = 0; i < NUM_HARBOR; i++)
			usedTrader[i] = false;

		// for each harbor type (one of each resource, 4 any 3:1 traders)
		for (int i = 0; i < NUM_HARBOR; i++) {
			while (true) {
				// pick a random unassigned harbor
				int pick = (int) (Math.random() * NUM_HARBOR);
				if (!usedTrader[pick]) {
					Type type;
					if (i >= Hexagon.TYPES.length)
						type = Type.ANY;
					else
						type = Type.values()[i];

					harbor[pick] = new Harbor(type, pick);
					usedTrader[pick] = true;
					break;
				}
			}
		}
		
		return harbor;
	}
	
	public static Harbor[] initialize(Type[] types) {
		Harbor[] harbor = new Harbor[NUM_HARBOR];
		for (int i = 0; i < harbor.length; i++)
			harbor[i] = new Harbor(types[i], i);
		
		return harbor;
	}
}
