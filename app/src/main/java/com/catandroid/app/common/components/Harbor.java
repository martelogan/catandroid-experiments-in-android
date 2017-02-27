package com.catandroid.app.common.components;

import com.catandroid.app.common.components.Hexagon.Type;

public class Harbor {
	
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
}
