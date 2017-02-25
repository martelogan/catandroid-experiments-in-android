package com.catandroid.app.common.components;

import com.catandroid.app.common.components.Hexagon.Type;

public class Harbor {

	public static final int NUM_HARBORS = 9;
	
	public enum Position {
		NORTH, SOUTH, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
	}

	// default positions
	private static final Position[] POSITIONS_BY_VDIRECT = {
		Position.NORTHEAST, Position.SOUTHEAST, Position.SOUTH,
			Position.SOUTHWEST, Position.NORTHWEST, Position.NORTH
	};

	private Type type;
	private Position position;
	private int index;
	private Edge myEdge = null;

	public Harbor(Type type, int index) {
		this.type = type;
		this.index = index;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void setPosition(Position p) {
		this.position = p;
	}

	public Position getPosition() {
		return position;
	}

	public void setMyEdge(Edge e) {
		this.myEdge = e;
	}

	public Edge getMyEdge() {
		return myEdge;
	}

	public static Position vdirectToPosition(int vdirect) {
		return POSITIONS_BY_VDIRECT[vdirect];
	}
	
	public int getIndex() {
		return index;
	}
	
	public static Harbor[] initialize() {

		// mark all traders as unassigned
		Harbor[] harbor = new Harbor[NUM_HARBORS];
		boolean[] usedTrader = new boolean[NUM_HARBORS];
		for (int i = 0; i < NUM_HARBORS; i++)
			usedTrader[i] = false;

		// for each harbor type (one of each resource, 4 any 3:1 traders)
		for (int i = 0; i < NUM_HARBORS; i++) {
			while (true) {
				// pick a random unassigned harbor
				int pick = (int) (Math.random() * NUM_HARBORS);
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
		Harbor[] harbor = new Harbor[NUM_HARBORS];
		for (int i = 0; i < harbor.length; i++)
			harbor[i] = new Harbor(types[i], i);
		
		return harbor;
	}
}
