package com.catandroid.app.common.components;

import com.catandroid.app.common.players.Player;

public class Edge {

	private int index;
	private Vertex[] vertices;
	private Player owner;
	private int lastRoadCountId;
	private Hexagon originHex;
    private int originHexDirect;
    private Harbor myHarbor = null;

	/**
	 * Initialize edge with vertices set to null
	 */
	public Edge(int index) {
		this.index = index;
		vertices = new Vertex[2];
		vertices[0] = vertices[1] = null;
		owner = null;
		lastRoadCountId = 0;
	}

	/**
	 * Set vertices for the edge
	 * 
	 * @param v1
	 *            the first vertices
	 * @param v2
	 *            the second vertices
	 */
	public void setVertices(Vertex v1, Vertex v2) {
		vertices[0] = v1;
		vertices[1] = v2;
		v1.addEdge(this);
		v2.addEdge(this);
	}

	/**
	 * Check if the edge has a given vertices
	 * 
	 * @param v
	 *            the vertices to check for
	 * @return true if v is associated with the edge
	 */
	public boolean hasVertex(Vertex v) {
		return (vertices[0] == v || vertices[1] == v);
	}

	/**
	 * Get the other vertices associated with edge
	 * 
	 * @param v
	 *            one vertices
	 * @return the other associated vertices or null if not completed
	 */
	public Vertex getAdjacent(Vertex v) {
		if (vertices[0] == v) {
			return vertices[1];
		}
		else if (vertices[1] == v) {
			return vertices[0];
		}

		return null;
	}

	/**
	 * Check if a road has been build at the edge
	 * 
	 * @return true if a road was built
	 */
	public boolean hasRoad() {
		return (owner != null);
	}

	/**
	 * Get the owner's player number
	 * 
	 * @return 0 or the owner's player number
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Determine if player can build a road on edge
	 * 
	 * @param player
	 *            the player to check for
	 * @return true if player can build a road on edge
	 */
	public boolean canBuild(Player player) {
		if (owner != null) {
			return false;
		}

		// check for roads to each vertices
		for (int i = 0; i < 2; i++) {
			// the player has a road to an unoccupied vertices,
			// or the player has an adjacent building
			if (vertices[i].hasRoad(player) && !vertices[i].hasBuilding()
					|| vertices[i].hasBuilding(player)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Set the origin hexagon
	 * @param h
	 *            the hex to set
	 * @return
	 */
	public void setOriginHex(Hexagon h) {
		this.originHex = h;
	}


	/**
	 * Get the origin hexagon
	 *
	 * @return the origin hexagon
	 */
	public Hexagon getOriginHex() {
		return originHex;
	}

    /**
     * Set the origin hexagon direction
     * @param direct
     *            the edge direction on origin hexagon
     * @return
     */
    public void setOriginHexDirect(int direct) {
        this.originHexDirect = direct;
    }

    /**
     * Get the origin hexagon direction
     * @return the origin hexagon direction
     */
    public int getOriginHexDirect() {
        return this.originHexDirect;
    }

    /**
     * Get the marginal X sign of the origin hexagon direction
     * @return the marginal X sign of the origin hexagon direction
     */
    public int getOriginHexDirectXsign() {
        switch(this.originHexDirect) {
            case 0:
                return 1;
            case 1:
                return 1;
            case 2:
                return 0;
            case 3:
                return -1;
            case 4:
                return -1;
            case 5:
                return 0;
            default:
                return Integer.MIN_VALUE;
        }
    }

    /**
     * Get the marginal X sign of the origin hexagon direction
     * @return the marginal X sign of the origin hexagon direction
     */
    public int getOriginHexDirectYsign() {
        switch(this.originHexDirect) {
            case 0:
                return 1;
            case 1:
                return -1;
            case 2:
                return -1;
            case 3:
                return -1;
            case 4:
                return 1;
            case 5:
                return 1;
            default:
                return Integer.MIN_VALUE;
        }
    }

    /**
     * Set a harbor on this edge
     * @param harbor
     *            the harbor to set
     * @return
     */
    public void setMyHarbor(Harbor harbor) {
        harbor.setMyEdge(this);
        this.myHarbor = harbor;
    }

    /**
     * Get the harbor on this edge
     * @return the harbor
     */
    public Harbor getMyHarbor() {
        return this.myHarbor;
    }

	/**
	 * Get the first vertices
	 * 
	 * @return the first vertices
	 */
	public Vertex getV0Clockwise() {
		return vertices[0];
	}

	/**
	 * Get the second vertices
	 * 
	 * @return the second vertices
	 */
	public Vertex getV1Clockwise() {
		return vertices[1];
	}

	/**
	 * Get the index of this edge
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Build a road on edge
	 * 
	 * @param player
	 *            the road owner
	 * @return true if player can build a road on edge
	 */
	public boolean build(Player player) {
		if (!canBuild(player)) {
			return false;
		}

		owner = player;
		return true;
	}

	/**
	 * Get the road length through this edge
	 * 
	 * @param player
	 *            player to measure for
	 * @param from
	 *            where we are measuring from
	 * @param countId
	 *            unique id for this count iteration
	 * @return the road length
	 */
	public int getRoadLength(Player player, Vertex from, int countId) {
		if (owner != player || lastRoadCountId == countId) {
			return 0;
		}

		// this ensures that that road isn't counted multiple times (cycles)
		lastRoadCountId = countId;

		// find other vertices
		Vertex to = (from == vertices[0] ? vertices[1] : vertices[0]);

		// return road length
		return to.getRoadLength(player, this, countId) + 1;
	}

	/**
	 * Get the longest road length through this edge
	 * 
	 * @param countId
	 *            unique id for this count iteration
	 * @return the road length
	 */
	public int getRoadLength(int countId) {
		if (owner == null) {
			return 0;
		}

		// this ensures that that road isn't counted multiple times (cycles)
		lastRoadCountId = countId;

		int length1 = vertices[0].getRoadLength(owner, this, countId);
		int length2 = vertices[1].getRoadLength(owner, this, countId);
		return length1 + length2 + 1;
	}
}
