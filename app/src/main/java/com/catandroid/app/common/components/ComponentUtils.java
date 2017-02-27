package com.catandroid.app.common.components;

import java.util.Collections;
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
        int hexCount = board.getBoardGeometry().getHexCount();
        Hexagon[] hexagons = new Hexagon[hexCount];
        int [] countPerResource = Board.COUNT_PER_TERRAIN;
        // generate random board layout
        for (int type = 0; type < countPerResource.length; type++) {
            for (int count = 0; count < countPerResource[type]; count++) {

                // pick hexagon index (location)
                while (true) {
                    int index = (int) (hexCount * Math.random());
                    if (hexagons[index] == null) {
                        Hexagon.Type hexType = Hexagon.Type.values()[type];
                        hexagons[index] = new Hexagon(board, hexType, index);

                        if (hexType == Hexagon.Type.DESERT) {
                            hexagons[index].setRoll(7);
                            board.setRobber(index);
                        }

                        break;
                    }
                }
            }
        }

        return hexagons;
    }

    public static Harbor[] initRandomHarbors(int harborCount) {

        // mark all traders as unassigned
        Harbor[] harbor = new Harbor[harborCount];
        boolean[] usedTrader = new boolean[harborCount];
        for (int i = 0; i < harborCount; i++)
            usedTrader[i] = false;

        // for each harbor type (one of each resource, 4 any 3:1 harbors)
        for (int i = 0; i < harborCount; i++) {
            while (true) {
                // pick a random unassigned harbor
                int pick = (int) (Math.random() * harborCount);
                if (!usedTrader[pick]) {
                    Hexagon.Type type;
                    if (i >= Hexagon.TYPES.length)
                        type = Hexagon.Type.ANY;
                    else
                        type = Hexagon.Type.values()[i];

                    harbor[pick] = new Harbor(type, pick);
                    usedTrader[pick] = true;
                    break;
                }
            }
        }

        return harbor;
    }

    public static Harbor[] generateHarbors(Hexagon.Type[] types) {
        Harbor[] harbor = new Harbor[types.length];
        for (int i = 0; i < harbor.length; i++) {
            harbor[i] = new Harbor(types[i], i);
        }

        return harbor;
    }

    /**
     * Initialize the hexagons based on a predefined board layout
     *
     * @param board
     *            the board
     * @param types
     *            an array of hexagon types
     * @return a hexagon array
     */
    public static Hexagon[] generateHexes(Board board, Hexagon.Type[] types) {
        Hexagon[] hexagons = new Hexagon[board.getBoardGeometry().getHexCount()];
        for (int i = 0; i < hexagons.length; i++)
            hexagons[i] = new Hexagon(board, types[i], i);

        return hexagons;
    }


    /**
     * Generate vertices
     * @param vertexCount
     *          number of vertices to generate
     * @return array of vertices
     */
    public static Vertex[] generateVertices(int vertexCount) {
        Vertex[] vertex = new Vertex[vertexCount];
        for (int i = 0; i < vertexCount; i++)
            vertex[i] = new Vertex(i);

        return vertex;
    }

    /**
     * Generate edges
     * @param edgeCount
     *          number of edges to generate
     * @return array of edges
     */
    public static Edge[] generateEdges(int edgeCount) {
        Edge[] edge = new Edge[edgeCount];
        for (int i = 0; i < edgeCount; i++)
            edge[i] = new Edge(i);

        return edge;
    }

    /**
     * Assign roll numbers to the hexagons randomly
     *
     * @param hexagons
     *            the hexagon array
     */
    public static void assignRoles(Hexagon[] hexagons, int numHighRollers) {

        int [] countPerDiceSum = Board.COUNT_PER_DICE_SUM;
        int hexCount = hexagons.length;

        // initialize count of dice sums used to allocate roll numbers
        int[] rollCount = new int[countPerDiceSum.length];
        for (int i = 0; i < rollCount.length; i++) {
            rollCount[i] = 0;
        }

        // place 6s and 8s (high probability rolls)
        Hexagon[] highRollers = new Hexagon[numHighRollers];
        for (int i = 0; i < numHighRollers; i++) {
            // pick a random hexagon
            int pick = -1;
            while (pick < 0) {
                pick = (int) (hexCount * Math.random());

                // make sure it isn't adjacent to another high roller
                for (int j = 0; j < i; j++) {
                    if (hexagons[pick].isAdjacent(highRollers[j])) {
                        pick = -1;
                        break;
                    }
                }

                // make sure it wasn't already picked
                if (pick >= 0 && hexagons[pick].getRoll() > 0 || pick >= 0
                        && hexagons[pick].getType() == Hexagon.Type.DESERT)
                    pick = -1;
            }

            // assign the roll value
            int roll = (i < 2 ? 6 : 8);
            highRollers[i] = hexagons[pick];
            highRollers[i].setRoll(roll);
            rollCount[roll] += 1;
        }

        // generate random placement of roll numbers
        for (int i = 0; i < hexCount; i++) {
            // skip hexagons that already have a roll number
            if (hexagons[i].getRoll() > 0 || hexagons[i].getType() == Hexagon.Type.DESERT)
                continue;

            // pick roll
            int roll = 0;
            while (true) {
                roll = (int) (countPerDiceSum.length * Math.random());
                if (rollCount[roll] < countPerDiceSum[roll])
                    break;
            }

            hexagons[i].setRoll(roll);
            rollCount[roll] += 1;
        }
    }

    public static Hexagon.Type getType(String string) {
        for (int i = 0; i < Hexagon.TYPES.length; i++) {
            if (string == Hexagon.TYPES[i].toString().toLowerCase())
                return Hexagon.TYPES[i];
        }

        return null;
    }

    public static <E> List<E> pickNRandomElements(List<E> list, int n, Random r) {
        int length = list.size();

        if (length < n) return null;

        //We don't need to shuffle the whole list
        for (int i = length - 1; i >= length - n; --i)
        {
            Collections.swap(list, i , r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

}

