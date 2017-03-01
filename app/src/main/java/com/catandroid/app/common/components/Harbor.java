package com.catandroid.app.common.components;

import com.catandroid.app.common.components.Resource.ResourceType;

public class Harbor {

	public enum Position {
		NORTH, SOUTH, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
	}

	// default positions
	private static final Position[] POSITIONS_BY_VDIRECT = {
			Position.NORTHEAST, Position.SOUTHEAST, Position.SOUTH,
			Position.SOUTHWEST, Position.NORTHWEST, Position.NORTH
	};

	private ResourceType resourceType;
	private Position position;
	private int id;
	private int edgeId = -1;

	private transient Board board;

	public Harbor(Board board, ResourceType resourceType, int id) {
		this.resourceType = resourceType;
		this.id = id;
		this.board = board;
	}


	public void setBoard(Board board) {
		this.board = board;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setPosition(Position p) {
		this.position = p;
	}

	public Position getPosition() {
		return position;
	}

	public void setEdge(Edge edge) {
		this.edgeId = edge.getId();
	}

	public void setEdgeById(int edgeId) {
		this.edgeId = edgeId;
	}

	public Edge getEdge() {
		return board.getEdgeById(edgeId);
	}

	public static Position vdirectToPosition(int vdirect) {
		return POSITIONS_BY_VDIRECT[vdirect];
	}

	public int getId() {
		return id;
	}
}
