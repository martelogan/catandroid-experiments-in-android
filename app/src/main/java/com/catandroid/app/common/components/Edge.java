package com.catandroid.app.common.components;

import com.catandroid.app.common.players.Player;

public class Edge {

	private int id;
	private int[] vertexIds;
	private Player owner;
	private int lastRoadCountId;
	private int originHexId;
    private int originHexDirect;
    private int myHarborId = -1;
	private Board board;

	/**
	 * Initialize edge with vertexIds set to null
	 */
	public Edge(Board board, int id) {
		this.id = id;
		vertexIds = new int[2];
		vertexIds[0] = vertexIds[1] = -1;
		owner = null;
		lastRoadCountId = 0;
		this.board = board;
	}

	/**
	 * Set the board
	 *
	 * @param board
	 *
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Set vertices for the edge
	 *
	 * @param v0
	 *            the first vertex
	 * @param v1
	 *            the second vertex
	 */
	public void setVertices(Vertex v0, Vertex v1) {
		vertexIds[0] = v0.getId();
		vertexIds[1] = v1.getId();
		v0.addEdge(this);
		v1.addEdge(this);
	}

	/**
	 * Set vertices for the edge by id
	 * 
	 * @param v0_Id
	 *            the id of the first vertex
	 * @param v1_Id
	 *            the id of the second vertex
	 */
	public void setVerticesById(int v0_Id, int v1_Id) {
		vertexIds[0] = v0_Id;
		vertexIds[1] = v1_Id;
		board.getVertexById(v0_Id).addEdge(this);
		board.getVertexById(v1_Id).addEdge(this);
	}

	/**
	 * Check if the edge has a given vertexIds
	 * 
	 * @param v
	 *            the vertexIds to check for
	 * @return true if v is associated with the edge
	 */
	public boolean hasVertex(int v) {
		return (vertexIds[0] == v || vertexIds[1] == v);
	}

	/**
	 * Get the other vertex associated with edge
	 * 
	 * @param v
	 *            one vertex
	 * @return the other associated vertex or null if not completed
	 */
	public Vertex getAdjacent(Vertex v) {
		if (board.getVertexById(vertexIds[0]) == v) {
			return board.getVertexById(vertexIds[1]);
		}
		else if (board.getVertexById(vertexIds[1]) == v) {
			return board.getVertexById(vertexIds[0]);
		}

		return null;
	}

	/**
	 * Get the other vertex associated with edge
	 *
	 * @param vertexId
	 *            id of the vertex
	 * @return the other associated vertexIds or null if not completed
	 */
	public Vertex getAdjacentById(int vertexId) {
		if (vertexIds[0] == vertexId) {
			return board.getVertexById(vertexIds[1]);
		}
		else if (vertexIds[1] == vertexId) {
			return board.getVertexById(vertexIds[0]);
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

		// check for roads to each vertexIds
		for (int i = 0; i < 2; i++) {
			// the player has a road to an unoccupied vertexIds,
			// or the player has an adjacent building
			if (board.getVertexById(vertexIds[i]).hasRoad(player) && !board.getVertexById(vertexIds[i]).hasBuilding()
					|| board.getVertexById(vertexIds[i]).hasBuilding(player)) {
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
		this.originHexId = h.getId();
	}

	/**
	 * Set the origin hexagon
	 * @param hexId
	 *            the id of hex to set
	 * @return
	 */
	public void setOriginHexById(int hexId) {
		this.originHexId = hexId;
	}


	/**
	 * Get the origin hexagon
	 *
	 * @return the origin hexagon
	 */
	public Hexagon getOriginHex() {
		return board.getHexagonById(originHexId);
	}

	/**
	 * Get the origin hexagon id
	 *
	 * @return the origin hexagon id
	 */
	public int getOriginHexId() {
		return originHexId;
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
		harbor.setEdgeById(this.getId());
		this.myHarborId = harbor.getId();
	}

	/**
	 * Set a harbor on this edge by id
	 * @param harborId
	 *            the id of harbor to set
	 * @return
	 */
	public void setMyHarborById(int harborId) {
		board.getHarborById(harborId).setEdgeById(this.getId());
		this.myHarborId = harborId;
	}

    /**
     * Get the harbor on this edge
     * @return the harbor
     */
    public int getMyHarborId() {
        return this.myHarborId;
    }

	/**
	 * Get the first vertex
	 * 
	 * @return the first vertex
	 */
	public Vertex getV0Clockwise() {

		return board.getVertexById(vertexIds[0]);
	}

	/**
	 * Get the second vertex
	 * 
	 * @return the second vertex
	 */
	public Vertex getV1Clockwise() {
		return board.getVertexById(vertexIds[1]);
	}

	/**
	 * Get the id of this edge
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
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
	public int getRoadLength(Player player, int from, int countId) {
		if (owner != player || lastRoadCountId == countId) {
			return 0;
		}

		// this ensures that that road isn't counted multiple times (cycles)
		lastRoadCountId = countId;

		// find other vertexIds
		int to = (from == vertexIds[0] ? vertexIds[1] : vertexIds[0]);

		// return road length
		return board.getVertexById(to).getRoadLength(player, id, countId) + 1;
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

		int length1 = board.getVertexById(vertexIds[0]).getRoadLength(owner, id, countId);
		int length2 = board.getVertexById(vertexIds[1]).getRoadLength(owner, id, countId);
		return length1 + length2 + 1;
	}
}
