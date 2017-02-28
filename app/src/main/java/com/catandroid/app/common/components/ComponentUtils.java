package com.catandroid.app.common.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ComponentUtils
{
    public ComponentUtils()
    {
    }

    /**
     * Initialize the hexagons randomly
     *
     * @param board
     *            the board
     * @return a hexagon array
     */
    public static Hexagon[] initRandomHexes(Board board) {
        int hexCount, curTerrainTypeCount, typeIndex;
        hexCount = board.getBoardGeometry().getHexCount();
        Hexagon[] hexagons = new Hexagon[hexCount];
        Hexagon.TerrainType[] terrainTypes = Hexagon.TerrainType.values();
        Hexagon.TerrainType terrainType;
        // generate random board layout
        for (typeIndex = 0; typeIndex < terrainTypes.length; typeIndex++) {
            terrainType = terrainTypes[typeIndex];
            curTerrainTypeCount = board.getTerrainCount(terrainType);
            for (int count = 0; count < curTerrainTypeCount; count++) {
                // pick hexagon index (location)
                while (true) {
                    int index = (int) (hexCount * Math.random());
                    if (hexagons[index] == null) {
                        hexagons[index] = new Hexagon(terrainType, index);

                        if (terrainType == Hexagon.TerrainType.DESERT) {
                            hexagons[index].placeNumberToken(7);
                            board.setCurRobberHex(hexagons[index]);
                        }

                        break;
                    }
                }
            }
        }

        return hexagons;
    }

    public static Harbor[] initRandomHarbors(int harborCount) {

        // mark all harbors as unassigned
        Harbor[] harbors = new Harbor[harborCount];
        boolean[] usedHarbor = new boolean[harborCount];
        for (int i = 0; i < harborCount; i++)
        {
            usedHarbor[i] = false;
        }

        // for each harbor type (one of each resource, 4 any 3:1 harbors)
        for (int i = 0; i < harborCount; i++) {
            while (true) {
                // pick a random unassigned harbor
                int pick = (int) (Math.random() * harborCount);
                if (!usedHarbor[pick]) {
                    Resource.ResourceType resourceType;
                    if (i >= Resource.RESOURCE_TYPES.length)
                    {
                        resourceType = Resource.ResourceType.ANY;
                    }
                    else
                    {
                        resourceType = Resource.ResourceType.values()[i];
                    }

                    harbors[pick] = new Harbor(resourceType, pick);
                    usedHarbor[pick] = true;
                    break;
                }
            }
        }

        return harbors;
    }

    public static Harbor[] generateHarbors(Resource.ResourceType[] resourceTypes) {
        Harbor[] harbors = new Harbor[resourceTypes.length];
        for (int i = 0; i < harbors.length; i++) {
            harbors[i] = new Harbor(resourceTypes[i], i);
        }

        return harbors;
    }

    /**
     * Initialize the hexagons based on a predefined board layout
     *
     * @param hexCount
     *            the number of hexagons to generate
     * @param terrainTypes
     *            an array of hexagon resourceTypes
     * @return a hexagon array
     */
    public static Hexagon[] generateHexes(int hexCount, Hexagon.TerrainType[] terrainTypes) {
        Hexagon[] hexagons = new Hexagon[hexCount];
        for (int i = 0; i < hexagons.length; i++)
        {
            hexagons[i] = new Hexagon(terrainTypes[i], i);
        }

        return hexagons;
    }


    /**
     * Generate vertices
     * @param vertexCount
     *          number of vertices to generate
     * @return array of vertices
     */
    public static Vertex[] generateVertices(int vertexCount) {
        Vertex[] vertices = new Vertex[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = new Vertex(i);
        }

        return vertices;
    }

    /**
     * Generate edges
     * @param edgeCount
     *          number of edges to generate
     * @return array of edges
     */
    public static Edge[] generateEdges(int edgeCount) {
        Edge[] edges = new Edge[edgeCount];
        for (int i = 0; i < edgeCount; i++) {
            edges[i] = new Edge(i);
        }

        return edges;
    }

    /**
     * Assign executeDiceRoll numbers to the hexagons randomly
     *
     * @param hexagons
     *            the hexagon array
     */
    public static void assignRoles(Hexagon[] hexagons) {

        int [] countPerDiceSum = Board.COUNT_PER_DICE_SUM;
        int hexCount = hexagons.length;

        // initialize counts used to allocate number tokens
        int[] curTokenCount = new int[countPerDiceSum.length];
        for (int i = 0; i < curTokenCount.length; i++) {
            curTokenCount[i] = 0;
        }

        Hexagon curHex;
        Hexagon.TerrainType terrainType;

        // place 6s and 8s (high probability rolls)
        int numHighRollers = countPerDiceSum[6] + countPerDiceSum[8];
        Hexagon[] highRollers = new Hexagon[numHighRollers];
        for (int i = 0; i < numHighRollers; i++) {
            // pick a random hexagon
            int pick = -1;
            while (pick < 0) {
                pick = (int) (hexCount * Math.random());
                curHex = hexagons[pick];
                // make sure it isn't adjacent to another high roller
                for (int j = 0; j < i; j++) {
                    if (curHex.isAdjacent(highRollers[j])) {
                        pick = -1;
                        break;
                    }
                }

                // make sure it wasn't already picked
                terrainType = curHex.getTerrainType();
                if (pick >= 0 && curHex.getNumberTokenAsInt() > 0 || pick >= 0
                        && (terrainType == Hexagon.TerrainType.DESERT ||
                        terrainType == Hexagon.TerrainType.SEA)) {
                    pick = -1;
                }
            }

            // assign the tokenNum value
            int tokenNum = (i < countPerDiceSum[6] ? 6 : 8);
            highRollers[i] = hexagons[pick];
            highRollers[i].placeNumberToken(tokenNum);
            curTokenCount[tokenNum] += 1;
        }

        // generate random placement of executeDiceRoll numbers
        for (int i = 0; i < hexCount; i++) {
            curHex = hexagons[i];
            terrainType = curHex.getTerrainType();
            // skip hexagons that already have a tokenNum number
            if (curHex.getNumberTokenAsInt() > 0 ||
                    (terrainType == Hexagon.TerrainType.DESERT ||
                            terrainType == Hexagon.TerrainType.SEA)) {
                continue;
            }

            // pick tokenNum
            int tokenNum = 0;
            while (true) {
                tokenNum = (int) (countPerDiceSum.length * Math.random());
                if (curTokenCount[tokenNum] < countPerDiceSum[tokenNum]) {
                    break;
                }
            }

            hexagons[i].placeNumberToken(tokenNum);
            curTokenCount[tokenNum] += 1;
        }
    }

    public static Resource.ResourceType getType(String string) {
        for (int i = 0; i < Resource.RESOURCE_TYPES.length; i++) {
            if (string == Resource.RESOURCE_TYPES[i].toString().toLowerCase()) {
                return Resource.RESOURCE_TYPES[i];
            }
        }

        return null;
    }

    public static <E> List<E> pickNRandomElements(List<E> list, int n, Random r) {
        int length = list.size();

        if (length < n) {
            return null;
        }

        //We don't need to shuffle the whole list
        for (int i = length - 1; i >= length - n; --i)
        {
            Collections.swap(list, i , r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

}

