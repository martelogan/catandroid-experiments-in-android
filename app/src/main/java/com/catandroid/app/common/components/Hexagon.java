package com.catandroid.app.common.components;

import com.catandroid.app.common.components.hexGridUtils.AxialHexLocation;
import com.catandroid.app.common.players.Player;

import java.util.HashMap;
import java.util.Vector;

public class Hexagon {

	private NumberToken numberToken;
    private Resource resourceProduced;
    private TerrainType terrainType;
	private Vertex[] vertices;
	private Edge[] edges;
	private AxialHexLocation coord;
    private boolean hasRobber = false;
	private int id;

    public enum TerrainType {
        FOREST, PASTURE, FIELDS, HILLS, MOUNTAINS, DESERT, SEA, GOLD_FIELD, LIGHT, DIM, SHORE
    }

	/**
	 * Initialize the hexagon with a resource resourceType and numberToken number
	 * 
	 * @param terrainType
	 *            terrainType of hexagon
	 * @param index
	 *            id number for the hexagon
	 */
	public Hexagon(TerrainType terrainType, int index) {
        this.terrainType = terrainType;
		this.resourceProduced = getResource(terrainType);
		this.numberToken = new NumberToken(0);
		vertices = new Vertex[6];
		edges = new Edge[6];
		id = index;
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
     * Get the hexagon's produced resource
     *
     * @return the hexagon's produced resource
     */
    public Resource getResource() {
        return resourceProduced;
    }

	/**
	 * Get the hexagon's produced resourceType
	 * 
	 * @return the hexagon's produced resourceType
	 */
	public Resource.ResourceType getResourceType()
    {
		if (resourceProduced == null) {
            return null;
        }
        return resourceProduced.getResourceType();
	}

    /**
     * Get the hexagon's terrainType
     *
     * @return the hexagon's terrainType
     */
    public TerrainType getTerrainType() { return terrainType; }

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
	 * Get integer representation of the number token
	 * currently placed on this hexagon
	 *
	 * @return integer representation of number token
	 */
	public int getNumberTokenAsInt() {
		return this.numberToken.getTokenNum();
	}

    /**
     * Get the number token object placed on this hexagon
     *
     * @return number token currently placed on this hexagon
     */
    public NumberToken getNumberTokenAsObject() {
        return this.numberToken;
    }

	/**
	 * Place number token on this hexagon (pass by int)
	 * 
	 * @param tokenNum
	 *            integer rep of number token to place
	 */
	public void placeNumberToken(int tokenNum) {
		this.numberToken = new NumberToken(tokenNum);
	}

    /**
     * Place number token on this hexagon (pass by obect)
     *
     * @param token
     *            object rep of number token to place
     */
    public void placeNumberToken(NumberToken token) {
        this.numberToken = token;
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
	 * Get a hexagon's axial coordinate
	 *
	 * @param
	 * @return the axial coordinate of the hexagon
	 */
	public AxialHexLocation getCoord() {
		return coord;
	}

	/**
	 * Distribute resources from this hexagon
	 * 
	 * @param diceRoll
	 *            the current dice sum
	 */
	public void distributeResources(int diceRoll) {
		if (diceRoll != this.numberToken.getTokenNum() || hasRobber()) {
			return;
		}

		for (int i = 0; i < 6; i++)
		{
			vertices[i].distributeResources(resourceProduced.getResourceType());
		}
	}

	/**
	 * Check if a given player owns land adjacent to the hexagon
	 * 
	 * @param player
	 *            the player to check
	 * @return true iff player has a settlement adjacent to the hexagon
	 */
	public boolean adjacentToPlayer(Player player) {
		for (int i = 0; i < 6; i++) {
			if (vertices[i].getOwner() == player) {
                return true;
            }
		}
		return false;
	}

	/**
	 * Get all players owning a settlement adjacent to the hexagon
	 * 
	 * @return a vector of players
	 */
	public Vector<Player> getPlayers() {
		Vector<Player> players = new Vector<Player>();
		for (int i = 0; i < 6; i++) {
			Player owner = vertices[i].getOwner();
			if (owner != null && !players.contains(owner)) {
                players.add(owner);
            }
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
				if (adjacent == null || adjacent == this) {
                    continue;
                }
				else if (hexagon == adjacent) {
                    return true;
                }
			}
		}
		return false;
	}

	/**
	 * Set the robber on this hexagon
	 *
	 * @return true if the hexagon now has the robber
	 */
	public boolean setRobber() {
		this.hasRobber = true;
		return true;
	}

	/**
	 * Remove the robber from this hexagon
	 *
	 * @return true iff robber was indeed removed
	 */
	public boolean removeRobber() {
		if (this.hasRobber) {
			this.hasRobber = false;
			return true;
		}
		// robber is not on this hex
		return false;
	}

	/**
	 * Check if the hexagon has the robber
	 * 
	 * @return true iff the hexagon has the robber
	 */
	public boolean hasRobber() {
		return (this.hasRobber);
	}

	/**
	 * Get the hexagon id
	 * 
	 * @return the hexagon id number
	 */
	public int getId() {
		return id;
	}

    private static final HashMap<TerrainType, Resource> terrainTypeToResourceMap =
            initTerrainTypeToResourceMap();
    private static HashMap<TerrainType, Resource> initTerrainTypeToResourceMap()
    {
        HashMap<TerrainType, Resource> terrainToResourceMap =
                new HashMap<TerrainType, Resource>();
        Resource lumber, wool, grain, brick, ore;
        lumber = new Resource(Resource.ResourceType.LUMBER);
        wool = new Resource(Resource.ResourceType.WOOL);
        grain = new Resource(Resource.ResourceType.GRAIN);
        brick = new Resource(Resource.ResourceType.BRICK);
        ore = new Resource(Resource.ResourceType.ORE);
        terrainToResourceMap.put(TerrainType.FOREST, lumber);
        terrainToResourceMap.put(TerrainType.PASTURE, wool);
        terrainToResourceMap.put(TerrainType.FIELDS, grain);
        terrainToResourceMap.put(TerrainType.HILLS, brick);
        terrainToResourceMap.put(TerrainType.MOUNTAINS, ore);
        return terrainToResourceMap;
    }

    public static Resource getResource(TerrainType terrainType) {
        return terrainTypeToResourceMap.get(terrainType);
    }

}
