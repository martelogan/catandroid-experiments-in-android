package com.catandroid.app.common.components;

import com.catandroid.app.common.components.hexGridUtils.AxialHexLocation;
import com.catandroid.app.R;
import com.catandroid.app.common.players.Player;

import java.util.Vector;

public class Hexagon {

	public enum Type {
		LUMBER, WOOL, GRAIN, BRICK, ORE, DESERT, SEA, GOLD, ANY, LIGHT, DIM, SHORE
	}

	public static final Type[] TYPES = { Type.LUMBER, Type.WOOL, Type.GRAIN,
			Type.BRICK, Type.ORE };

	private final static int[] PROBABILITY = { 0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3,
			2, 1 };

	private Board board;
	private int roll;
	private Type type;
	private Vertex[] vertices;
	private Edge[] edges;
	private AxialHexLocation coord;
	private int id;

	/**
	 * Initialize the hexagon with a resource type and roll number
	 * 
	 * @param type
	 *            resource type
	 * @param index
	 *            id number for the hexagon
	 */
	public Hexagon(Board board, Type type, int index) {
		this.board = board;
		this.type = type;
		this.roll = 0;
		vertices = new Vertex[6];
		edges = new Edge[6];
		id = index;
	}

	/**
	 * Set the connected vertices for hexagon
	 * 
	 * @param v1
	 *            first vertices
	 * @param v2
	 *            second vertices
	 * @param v3
	 *            third vertices
	 * @param v4
	 *            fourth vertices
	 * @param v5
	 *            fifth vertices
	 * @param v6
	 *            sixth vertices
	 */
	public void setVertices(Vertex v1, Vertex v2, Vertex v3, Vertex v4,
			Vertex v5, Vertex v6) {
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		vertices[3] = v4;
		vertices[4] = v5;
		vertices[5] = v6;

		for (int i = 0; i < 6; i++)
			vertices[i].addHexagon(this);
	}

	/**
	 * Set a vertex of the hexagon
	 *
	 * @param index
	 *            the index to set
	 * @return
	 */
	public void setVertex(Vertex v, int index) {
		vertices[index] = v;
		v.addHexagon(this);
	}

	/**
	 * Get a vertices of the hexagon
	 *
	 * @param index
	 *            the index of the vertices
	 * @return the vertices
	 */
	public Vertex getVertex(int index) {
		return vertices[index];
	}

	/**
	 * Get a vertex index from vertices
	 *
	 * @param v
	 *            the vertex to find
	 * @return index of the vertex
	 */
	public int findVertex(Vertex v) {
		for (int i = 0; i < 6; i++) {
			if (vertices[i] == v) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get an edge index from edges
	 *
	 * @param e
	 *            the edge to find
	 * @return index of the edge
	 */
	public int findEdge(Edge e) {
		for (int i = 0; i < 6; i++) {
			if (edges[i] == e) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the resource type
	 * 
	 * @return the resource type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the hexagon type retrospectively
	 * 
	 * @param type
	 *            the hexagon type
	 */
	public void setType(Type type) {
		this.type = type;
	}


	/**
	 * Get an edge of the hexagon
	 *
	 * @param direction
	 *            the index of the edge
	 * @return the edge
	 */
	public Edge getEdge(int direction) {
		return edges[direction];
	}

	/**
	 * Set an edge of the hexagon
	 *
	 * @param direction
	 *            index to set the edge
	 * @return
	 */
	public void setEdge(Edge edge, int direction) {
		edge.setOriginHexDirect(direction);
		edges[direction] = edge;
	}

	/**
	 * Get the roll number for this resource
	 * 
	 * @return the roll sum corresponding to this resource
	 */
	public int getRoll() {
		return roll;
	}

	/**
	 * Set the roll number for this resource
	 * 
	 * @param roll
	 *            the dice sum for this resource
	 */
	public void setRoll(int roll) {
		this.roll = roll;
	}

	/**
	 * Get the probability of rolling this number
	 * 
	 * @return the number of possible rolls which give this number
	 */
	public int getProbability() {
		return PROBABILITY[roll];
	}

	/**
	 * Set hexagon's axial coordinate
	 *
	 * @param coord
	 *            axial coordinate to set
	 * @return
	 */
	public void setCoord(AxialHexLocation coord) {
		this.coord = coord;
	}

	/**
	 * Get hexagon's axial coordinate
	 *
	 * @param
	 * @return the vertices
	 */
	public AxialHexLocation getCoord() {
		return coord;
	}

	/**
	 * Distribute resources from this hexagon
	 * 
	 * @param roll
	 *            the dice sum
	 */
	public void distributeResources(int roll) {
		if (roll != this.roll || hasRobber()) {
			return;
		}

		for (int i = 0; i < 6; i++)
		{
			vertices[i].distributeResources(type);
		}
	}

	/**
	 * Check if a player has a town or city adjacent to the hexagon
	 * 
	 * @param player
	 *            the player to check for
	 * @return true if player has a town or city adjacent to the hexagon
	 */
	public boolean hasPlayer(Player player) {
		for (int i = 0; i < 6; i++) {
			if (vertices[i].getOwner() == player)
				return true;
		}

		return false;
	}

	/**
	 * Get the players the own a settlement adjacent to the hexagon
	 * 
	 * @return a Vector of players
	 */
	public Vector<Player> getPlayers() {
		Vector<Player> players = new Vector<Player>();
		for (int i = 0; i < 6; i++) {
			Player owner = vertices[i].getOwner();
			if (owner != null && !players.contains(owner))
				players.add(owner);
		}

		return players;
	}

	/**
	 * Check if this hexagon is adjacent to a given hexagon
	 * 
	 * @param hexagon
	 *            the hexagon to check for
	 * @return true if hexagon is adjacent to this hexagon
	 */
	public boolean isAdjacent(Hexagon hexagon) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				Hexagon adjacent = vertices[i].getHexagon(j);
				if (adjacent == null || adjacent == this)
					continue;

				if (hexagon == adjacent)
					return true;
			}
		}

		return false;
	}

	/**
	 * Check if the hexagon has the robber
	 * 
	 * @return true if the hexagon has the robber
	 */
	public boolean hasRobber() {
		return (board.getRobber() == this);
	}

	/**
	 * Get the hexagon id
	 * 
	 * @return the hexagon id number
	 */
	public int getId() {
		return id;
	}

	public static int getTypeStringResource(Type type) {
		switch (type) {
		case LUMBER:
			return R.string.lumber;
		case WOOL:
			return R.string.wool;
		case GRAIN:
			return R.string.grain;
		case BRICK:
			return R.string.brick;
		case ORE:
			return R.string.ore;
		default:
			return R.string.nostring;
		}
	}
}
