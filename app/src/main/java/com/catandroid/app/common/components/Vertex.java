package com.catandroid.app.common.components;

import com.catandroid.app.common.players.Player;

public class Vertex {

	public static final int NONE = 0;
	public static final int TOWN = 1;
	public static final int CITY = 2;

	private int index;
	private int building;

	private Player owner;

	private Edge[] edges;
	private Hexagon[] hexagons;
	private Harbor harbors;

	/**
	 * Initialize a vertex with edges set to null
	 * 
	 * @param index
	 *            the vertex index for drawing
	 */
	public Vertex(int index) {
		this.index = index;
		owner = null;
		building = NONE;

		edges = new Edge[3];
		edges[0] = edges[1] = edges[2] = null;

		hexagons = new Hexagon[3];
		hexagons[0] = hexagons[1] = hexagons[2] = null;
		setHarbor(null);
	}

	/**
	 * Associate an edges with vertex
	 * 
	 * @param e
	 *            the edges to addCubic (ignored if already associated)
	 */
	public void addEdge(Edge e) {
		for (int i = 0; i < 3; i++) {
			if (edges[i] == null) {
				edges[i] = e;
				return;
			} else if (edges[i] == e) {
				return;
			}
		}
	}

	/**
	 * Associate an hexagons with vertex
	 * 
	 * @param h
	 *            the hexagons to addCubic (ignored if already associated)
	 */
	public void addHexagon(Hexagon h) {
		for (int i = 0; i < 3; i++) {
			if (hexagons[i] == null) {
				hexagons[i] = h;
				return;
			} else if (hexagons[i] == h) {
				return;
			}
		}
	}

	/**
	 * Get the hexagons at the given index
	 * 
	 * @param index
	 *            the hexagons index (0, 1, or 2)
	 * @return the hexagons or null
	 */
	public Hexagon getHexagon(int index) {
		return hexagons[index];
	}

	/**
	 * Get the hexagons's index for drawing
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Determine if an edges is connected to vertex
	 * 
	 * @param e
	 *            the edges to check for
	 * @return true if e is connected to the vertex
	 */
	public boolean hasEdge(Edge e) {
		return (edges[0] == e || edges[1] == e || edges[2] == e);
	}

	/**
	 * Get an edges
	 * 
	 * @param index
	 *            the edges index [0, 2]
	 * @return the edges or null
	 */
	public Edge getEdge(int index) {
		return edges[index];
	}

	/**
	 * Check if vertex has a building for any player
	 * 
	 * @return true if there is a town or city for any player
	 */
	public boolean hasBuilding() {
		return (building != NONE);
	}

	/**
	 * Check if vertex has a building for a player
	 * 
	 * @param player
	 *            the player to check for
	 * @return true if player has a building on the vertexS
	 */
	public boolean hasBuilding(Player player) {
		return (owner == player);
	}

	/**
	 * Get the type of building at vertex
	 * 
	 * @return the type of building at the vertex (equal to the number of
	 *         points)
	 */
	public int getBuilding() {
		return building;
	}

	/**
	 * Get the player number of the owner of a building at vertex
	 * 
	 * @return the Player that owns it, or null
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Check for adjacent roads
	 * 
	 * @param player
	 *            the player to check for
	 * @return true if one of the adjacent edges has a road for player
	 */
	public boolean hasRoad(Player player) {
		for (int i = 0; i < 3; i++) {
			if (edges[i] != null && edges[i].getOwner() == player)
				return true;
		}

		return false;
	}

	public void distributeResources(Resource.ResourceType resourceType) {
		if (owner == null)
		{
			return;
		}

		if (resourceType != null) {
			owner.addResources(resourceType, building);
		}
	}

	/**
	 * Check if there are no adjacent settlements
	 * 
	 * @return true if there are no adjacent settlements
	 */
	public boolean couldBuild() {
		// check for adjacent buildings
		for (int i = 0; i < 3; i++) {
			if (edges[i] != null && edges[i].getAdjacent(this).hasBuilding())
				return false;
		}

		return true;
	}

	/**
	 * Check if player can build at vertex
	 * 
	 * @param player
	 *            player to check for
	 * @param setup
	 *            setup condition allows player to build without a road
	 * @return true if player can build at vertex
	 */
	public boolean canBuild(Player player, int type, boolean setup) {
		if (!couldBuild()) {
			return false;
		}

		// only allow building towns
		if (setup) {
			return (owner == null);
		}

		// check if owner has road to vertex
		if (!this.hasRoad(player)) {
			return false;
		}

		// can build town
		if (owner == null && type == TOWN) {
			return true;
		}
		// can build city
		else {
			return owner == player && type == CITY && building == TOWN;
		}
	}

	/**
	 * Simple version of canBuild(player, setup) where setup is false
	 * 
	 * @param player
	 *            player to check for
	 * @return true if player can build at vertex
	 */
	public boolean canBuild(Player player, int type) {
		return this.canBuild(player, type, false);
	}

	/**
	 * Build at vertex for player
	 * 
	 * @param player
	 *            which player intends to build
	 * @param setup
	 *            setup condition allows player to build without a road
	 */
	public boolean build(Player player, int type, boolean setup) {
		if (!this.canBuild(player, type, setup))
			return false;

		switch (building) {
			case NONE:
				owner = player;
				building = TOWN;
				break;
			case TOWN:
				building = CITY;
				break;
			case CITY:
				return false;
		}

		if (harbors != null)
		{
			player.setTradeValue(harbors.getResourceType());
		}

		return true;
	}

	public void setHarbor(Harbor harbor) {
		this.harbors = harbor;
	}

	public Harbor getHarbor() {
		return harbors;
	}

	/**
	 * Find the longest road passing through this vertex for the given player
	 * 
	 * @param player
	 *            the player
	 * @param omit
	 *            omit an edges already considered
	 * @return the road length
	 */
	public int getRoadLength(Player player, Edge omit, int countId) {
		int longest = 0;

		// FIXME: if two road paths diverge and re-converge, the result may be
		// calculated with whichever happens to be picked first

		// another player's road breaks the road chain
		if (owner != null && owner != player)
		{
			return 0;
		}

		// find the longest road aside from one passing through the given edges
		for (int i = 0; i < 3; i++) {
			if (edges[i] == null || edges[i] == omit)
			{
				continue;
			}

			int length = edges[i].getRoadLength(player, this, countId);
			if (length > longest)
			{
				longest = length;
			}
		}

		return longest;
	}
}
