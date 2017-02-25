package com.catandroid.app.common.components;

import android.util.Log;

import com.catandroid.app.common.components.hexGridUtils.AxialHexLocation;
import com.catandroid.app.common.components.hexGridUtils.HexGridLayout;
import com.catandroid.app.common.components.hexGridUtils.HexGridUtils;
import com.catandroid.app.common.components.hexGridUtils.HexPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BoardGeometry {

	public static final int TILE_SIZE = 256;
	public static final int BUTTON_SIZE = 128;

	private static final float MAX_PAN = 2.5f;

    private static HexPoint size = HexGridLayout.size_default;

	private int width, height;
	private float cx, cy, zoom;
	private float minZoom, maxZoom, highZoom;

	public BoardGeometry() {
		cx = cy = 0;
		width = height = 480;
		zoom = minZoom = maxZoom = highZoom = 1;
	}

	public void setSize(int w, int h) {
		width = w;
		height = h;

		float aspect = (float) width / (float) height;
		float minZoomX = 0.5f * (float) width / (5.5f * TILE_SIZE);
		float minZoomY = 0.5f * (float) width / aspect / (5.1f * TILE_SIZE);

		minZoom = min(minZoomX, minZoomY);
		highZoom = 2 * minZoom;
		maxZoom = 3 * minZoom;

		setZoom(minZoom);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private int getMinimalSize() {
		return width < height ? width : height;
	}

	public float getZoom() {
		return zoom;
	}

	public void zoomOut() {
		cx = cy = 0;
		zoom = minZoom;
	}

	public void zoomTo(int userX, int userY) {
		cx = translateScreenX(userX);
		cy = translateScreenY(userY);
		zoom = highZoom;
		translate(0, 0);
	}

	public void toggleZoom(int userX, int userY) {
		if (zoom > (highZoom - 0.01f))
			zoomOut();
		else
			zoomTo(userX, userY);
	}

	public void zoomBy(float z) {
		setZoom(zoom * z);
	}

	public void setZoom(float z) {
		zoom = z;

		if (zoom > maxZoom)
			zoom = maxZoom;
		else if (zoom < minZoom)
			zoom = minZoom;

		translate(0, 0);
	}

	public void translate(float dx, float dy) {
		float halfMin = (float) getMinimalSize() / 2.0f;

		cx += dx / halfMin;
		cy -= dy / halfMin;

		float radius = (float) Math.sqrt(cx * cx + cy * cy);
		float maxRadius = MAX_PAN * (zoom - minZoom) / (maxZoom - minZoom);

		if (radius > maxRadius) {
			cx *= maxRadius / radius;
			cy *= maxRadius / radius;
		}
	}

	public float getTranslateX() {
		return cx;
	}

	public float getTranslateY() {
		return cy;
	}

	private float translateScreenX(int x) {
		float halfMin = (width < height ? width : height) / 2f;
		return ((x - width / 2) / halfMin + cx) / zoom;
	}

	private float translateScreenY(int y) {
		float halfMin = (width < height ? width : height) / 2f;
		return ((height / 2 - y) / halfMin + cy) / zoom;
	}

	private int getNearest(int userX, int userY, float[] rx, float[] ry, int length) {
		float x = translateScreenX(userX);
		float y = translateScreenY(userY);

		int best = -1;
		double dist2 = zoom * zoom / 4;
		for (int i = 0; i < length; i++) {
			double x2 = Math.pow(x - rx[i], 2);
			double y2 = Math.pow(y - ry[i], 2);
			if (x2 + y2 < dist2) {
				dist2 = x2 + y2;
				best = i;
			}
		}
		return best;
	}


	// TODO: Move helper functions to HexGridUtils
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

	public int getNearestHexagon(int userX, int userY) {
		return getNearest(userX, userY, HEXAGONS_X, HEXAGONS_Y, Hexagon.NUM_HEXAGONS);
	}

	public int getNearestEdge(int userX, int userY) {
		return getNearest(userX, userY, EDGES_X, EDGES_Y, Edge.NUM_EDGES);
	}

	public int getNearestVertex(int userX, int userY) {
		return getNearest(userX, userY, VERTICES_X, VERTICES_Y, Vertex.NUM_VERTICES);
	}

	public float getHexagonX(int index) {
		return HEXAGONS_X[index];
	}

	public float getHexagonY(int index) {
		return HEXAGONS_Y[index];
	}

	public float getEdgeX(int index) {
		return EDGES_X[index];
	}

	public float getEdgeY(int index) {
		return EDGES_Y[index];
	}

	public float getVertexX(int index) {
		return VERTICES_X[index];
	}

	public float getVertexY(int index) {
		return VERTICES_Y[index];
	}

	public float getHarborX(int index) {
		return HEXAGONS_X[HARBOR_HEXES[index]];
	}

	public float getHarborY(int index) {
		return HEXAGONS_Y[HARBOR_HEXES[index]];
	}

	public float getHarborIconX(int index, Edge e) {
        float edgeX = EDGES_X[HARBOR_EDGES[index]];
		return edgeX + ((float) Math.abs(size.x)) * (e.getOriginHexDirectXsign());
	}

	public float getHarborIconY(int index, Edge e) {
        float edgeY = EDGES_Y[HARBOR_EDGES[index]];
        return edgeY + ((float) Math.abs(size.y)) * (e.getOriginHexDirectYsign());
	}

	public static void setAssociations(Hexagon[] hexagon, Vertex[] vertex,
			Edge[] edge, Harbor[] harbor) {
		// associate vertices with hexagons
		hexagon[0].setVertices(vertex[6], vertex[7], vertex[12], vertex[13],
				vertex[18], vertex[19]);
		hexagon[1].setVertices(vertex[18], vertex[19], vertex[24], vertex[25],
				vertex[30], vertex[31]);
		hexagon[2].setVertices(vertex[30], vertex[31], vertex[36], vertex[37],
				vertex[42], vertex[43]);
		hexagon[3].setVertices(vertex[2], vertex[3], vertex[7], vertex[8],
				vertex[13], vertex[14]);
		hexagon[4].setVertices(vertex[13], vertex[14], vertex[19], vertex[20],
				vertex[25], vertex[26]);
		hexagon[5].setVertices(vertex[25], vertex[26], vertex[31], vertex[32],
				vertex[37], vertex[38]);
		hexagon[6].setVertices(vertex[37], vertex[38], vertex[43], vertex[44],
				vertex[48], vertex[49]);
		hexagon[7].setVertices(vertex[0], vertex[1], vertex[3], vertex[4],
				vertex[8], vertex[9]);
		hexagon[8].setVertices(vertex[8], vertex[9], vertex[14], vertex[15],
				vertex[20], vertex[21]);
		hexagon[9].setVertices(vertex[20], vertex[21], vertex[26], vertex[27],
				vertex[32], vertex[33]);
		hexagon[10].setVertices(vertex[32], vertex[33], vertex[38], vertex[39],
				vertex[44], vertex[45]);
		hexagon[11].setVertices(vertex[44], vertex[45], vertex[49], vertex[50],
				vertex[52], vertex[53]);
		hexagon[12].setVertices(vertex[4], vertex[5], vertex[9], vertex[10],
				vertex[15], vertex[16]);
		hexagon[13].setVertices(vertex[15], vertex[16], vertex[21], vertex[22],
				vertex[27], vertex[28]);
		hexagon[14].setVertices(vertex[27], vertex[28], vertex[33], vertex[34],
				vertex[39], vertex[40]);
		hexagon[15].setVertices(vertex[39], vertex[40], vertex[45], vertex[46],
				vertex[50], vertex[51]);
		hexagon[16].setVertices(vertex[10], vertex[11], vertex[16], vertex[17],
				vertex[22], vertex[23]);
		hexagon[17].setVertices(vertex[22], vertex[23], vertex[28], vertex[29],
				vertex[34], vertex[35]);
		hexagon[18].setVertices(vertex[34], vertex[35], vertex[40], vertex[41],
				vertex[46], vertex[47]);

		// associate vertices with edges
		edge[0].setVertices(vertex[0], vertex[1]);
		edge[1].setVertices(vertex[0], vertex[3]);
		edge[2].setVertices(vertex[1], vertex[4]);
		edge[3].setVertices(vertex[2], vertex[3]);
		edge[4].setVertices(vertex[2], vertex[7]);
		edge[5].setVertices(vertex[3], vertex[8]);
		edge[6].setVertices(vertex[4], vertex[5]);
		edge[7].setVertices(vertex[4], vertex[9]);
		edge[8].setVertices(vertex[5], vertex[10]);
		edge[9].setVertices(vertex[6], vertex[7]);
		edge[10].setVertices(vertex[6], vertex[12]);
		edge[11].setVertices(vertex[7], vertex[13]);
		edge[12].setVertices(vertex[8], vertex[9]);
		edge[13].setVertices(vertex[8], vertex[14]);
		edge[14].setVertices(vertex[9], vertex[15]);
		edge[15].setVertices(vertex[10], vertex[11]);
		edge[16].setVertices(vertex[10], vertex[16]);
		edge[17].setVertices(vertex[11], vertex[17]);
		edge[18].setVertices(vertex[12], vertex[18]);
		edge[19].setVertices(vertex[13], vertex[14]);
		edge[20].setVertices(vertex[13], vertex[19]);
		edge[21].setVertices(vertex[14], vertex[20]);
		edge[22].setVertices(vertex[15], vertex[16]);
		edge[23].setVertices(vertex[15], vertex[21]);
		edge[24].setVertices(vertex[16], vertex[22]);
		edge[25].setVertices(vertex[17], vertex[23]);
		edge[26].setVertices(vertex[18], vertex[19]);
		edge[27].setVertices(vertex[18], vertex[24]);
		edge[28].setVertices(vertex[19], vertex[25]);
		edge[29].setVertices(vertex[20], vertex[21]);
		edge[30].setVertices(vertex[20], vertex[26]);
		edge[31].setVertices(vertex[21], vertex[27]);
		edge[32].setVertices(vertex[22], vertex[23]);
		edge[33].setVertices(vertex[22], vertex[28]);
		edge[34].setVertices(vertex[23], vertex[29]);
		edge[35].setVertices(vertex[24], vertex[30]);
		edge[36].setVertices(vertex[25], vertex[26]);
		edge[37].setVertices(vertex[25], vertex[31]);
		edge[38].setVertices(vertex[26], vertex[32]);
		edge[39].setVertices(vertex[27], vertex[28]);
		edge[40].setVertices(vertex[27], vertex[33]);
		edge[41].setVertices(vertex[28], vertex[34]);
		edge[42].setVertices(vertex[29], vertex[35]);
		edge[43].setVertices(vertex[30], vertex[31]);
		edge[44].setVertices(vertex[30], vertex[36]);
		edge[45].setVertices(vertex[31], vertex[37]);
		edge[46].setVertices(vertex[32], vertex[33]);
		edge[47].setVertices(vertex[32], vertex[38]);
		edge[48].setVertices(vertex[33], vertex[39]);
		edge[49].setVertices(vertex[34], vertex[35]);
		edge[50].setVertices(vertex[34], vertex[40]);
		edge[51].setVertices(vertex[35], vertex[41]);
		edge[52].setVertices(vertex[36], vertex[42]);
		edge[53].setVertices(vertex[37], vertex[38]);
		edge[54].setVertices(vertex[37], vertex[43]);
		edge[55].setVertices(vertex[38], vertex[44]);
		edge[56].setVertices(vertex[39], vertex[40]);
		edge[57].setVertices(vertex[39], vertex[45]);
		edge[58].setVertices(vertex[40], vertex[46]);
		edge[59].setVertices(vertex[41], vertex[47]);
		edge[60].setVertices(vertex[42], vertex[43]);
		edge[61].setVertices(vertex[43], vertex[48]);
		edge[62].setVertices(vertex[44], vertex[45]);
		edge[63].setVertices(vertex[44], vertex[49]);
		edge[64].setVertices(vertex[45], vertex[50]);
		edge[65].setVertices(vertex[46], vertex[47]);
		edge[66].setVertices(vertex[46], vertex[51]);
		edge[67].setVertices(vertex[48], vertex[49]);
		edge[68].setVertices(vertex[49], vertex[52]);
		edge[69].setVertices(vertex[50], vertex[51]);
		edge[70].setVertices(vertex[50], vertex[53]);
		edge[71].setVertices(vertex[52], vertex[53]);

		// associate vertices with traders
		for (int i = 0; i < HARBOR_EDGES.length; i++) {
			edge[HARBOR_EDGES[i]].getV0Clockwise().setHarbor(harbor[i]);
			edge[HARBOR_EDGES[i]].getV1Clockwise().setHarbor(harbor[i]);
		}
	}

    public static Edge resolveNeighborHex(HashSet<Edge> portEdges, Hexagon myHex,
                                          Hexagon myNeighbor, int myV0index, int myV1index) {
        Vertex myClockwiseV0, myClockwiseV1;
        int neighborEdgeDirect = AxialHexLocation.complementAxialDirection(myV0index);
        Edge neighborEdge = myNeighbor.getEdge(neighborEdgeDirect);
        // forfeit neighborEdge candidacy for harbor
        try {
            // should fail silently if edge not present
            portEdges.remove(neighborEdge);
        } catch (Exception e) {
            // Warn exception (in case an intended removal failed)
            System.out.println("WARNING: failed to remove edge. Exception:\n");
            e.printStackTrace();
        }
        // axialDirection is reversed to that of edge creator
        myClockwiseV0 = neighborEdge.getV1Clockwise();
        myClockwiseV1 = neighborEdge.getV0Clockwise();
        myHex.setEdge(neighborEdge, myV0index);
        myHex.setVertex(myClockwiseV0, myV0index);
        myHex.setVertex(myClockwiseV1, myV1index);

        return neighborEdge;
    }

    public static void placeClockwiseEdge(Edge clockwiseEdge, Vertex clockwiseV0,
                                          Vertex clockwiseV1, Hexagon curHex, int vDirect) {
        clockwiseEdge.setVertices(clockwiseV0, clockwiseV1);
        curHex.setEdge(clockwiseEdge, vDirect);
        curHex.setVertex(clockwiseV0, vDirect);
        curHex.setVertex(clockwiseV1, (vDirect + 1) % 6);
    }

	public static void populateBoard(Hexagon[] hexagons, Vertex[] vertices,
									 Edge[] edges, Harbor[] harbors, HashMap<Long, Hexagon> hexMap)  {

		// edges available to neighbour harbors
		HashSet<Edge> portEdges = new HashSet<Edge>();

        // shuffled array of hexagons
		Hexagon[] randomHexes = Arrays.copyOf(hexagons, Hexagon.NUM_HEXAGONS);
        Collections.shuffle(Arrays.asList(randomHexes));

		// TODO: remove debugging
		HashSet<Hexagon> successfullyHashed = new HashSet<Hexagon>();

        // variables for hexagon population logic
		int map_radius = Hexagon.RADIUS;
		int hexagonIndex = 0, edgeIndex = 0, vertexIndex = 0;
        Long hexLocationHash;
		Hexagon curHex = null, curNeighborHex = null;
        AxialHexLocation curHexLocation = null, neighborLocation = null;
        Boolean hadClockwiseNeighbor = false, hadAntiClockwiseNeighbor = false;
		Edge clockwiseEdge = null, neighborEdge = null;

        // iteration to generate perfectly-centered hex shape
        for (int q = -map_radius; q <= map_radius; q++) {
            int r1 = max(-map_radius, -q - map_radius);
            int r2 = min(map_radius, -q + map_radius);
            for (int r = r1; r <= r2; r++) { // for each (q, r) axial coordinate

                // for each hexagon we seek to place
                curHex = randomHexes[hexagonIndex];
                curHexLocation = new AxialHexLocation(q, r);

                // iterate clockwise from top vertex around hexagon
                Vertex clockwiseV0 = null, clockwiseV1 = null;
                for (int vDirect = 0; vDirect < 6; vDirect++) {
                    //TODO: remove debugging
                    if (vertexIndex >= 51) {
                        Log.d("t", "here");
                    }
                    hadClockwiseNeighbor = false;
                    hadAntiClockwiseNeighbor = false;
                    // is there a clockwise axialNeighbor w.r.t vDirect?
                    neighborLocation = AxialHexLocation.axialNeighbor(curHexLocation, vDirect);
                    hexLocationHash = HexGridUtils.perfectHash(neighborLocation);
                    curNeighborHex = hexMap.get(hexLocationHash);
                    if (curNeighborHex != null) { // we have a clockwise axialNeighbor
                        hadClockwiseNeighbor = true;
                        resolveNeighborHex(portEdges, curHex, curNeighborHex,
                                vDirect, (vDirect + 1) % 6);
                    } else { // there was no clockwise axialNeighbor
                        // get and increment next available edge
                        clockwiseEdge = edges[edgeIndex];
						clockwiseEdge.setOriginHex(curHex);
                        edgeIndex += 1;
                        // new edge is candidate for harbor
                        portEdges.add(clockwiseEdge);
                        // use current hex's next vertex if already placed
                        clockwiseV1 = curHex.getVertex((vDirect + 1) % 6);
                        if (clockwiseV1 == null) {
                            // is there a clockwise axialNeighbor w.r.t vDirect + 1?
                            neighborLocation = AxialHexLocation.axialNeighbor(
                                    curHexLocation, (vDirect + 1) % 6);
                            hexLocationHash = HexGridUtils.perfectHash(neighborLocation);
                            curNeighborHex = hexMap.get(hexLocationHash);
                            if (curNeighborHex != null) { // we have a clockwise axialNeighbor
                                neighborEdge = resolveNeighborHex(portEdges, curHex, curNeighborHex,
                                        (vDirect + 1) % 6, (vDirect + 2) % 6);
                                clockwiseV1 = neighborEdge.getV1Clockwise();
                                curHex.setVertex(clockwiseV0, (vDirect + 1) % 6);
                            } else { // there was no clockwise axialNeighbor
                                // place new vertex at clockwiseV1
                                clockwiseV1 = vertices[vertexIndex];
                                vertexIndex += 1;
                            }
                        }
                    }
                    // is there a counter-clockwise axialNeighbor w.r.t vDirect?
                    neighborLocation =
							AxialHexLocation.axialNeighbor(curHexLocation,
									Math.abs(((((vDirect - 1)  % 6) + 6) % 6)));
                    hexLocationHash = HexGridUtils.perfectHash(neighborLocation);
                    curNeighborHex = hexMap.get(hexLocationHash);
                    if (curNeighborHex != null) { // we have an anti-clockwise axialNeighbor
                        hadAntiClockwiseNeighbor = true;
                        neighborEdge = resolveNeighborHex(portEdges, curHex, curNeighborHex,
                                Math.abs(((((vDirect - 1)  % 6) + 6) % 6)), vDirect);
                        if (!hadClockwiseNeighbor) {
                            // reuse less-clockwise vertex of anti-clockwise axialNeighbor
                            clockwiseV0 = neighborEdge.getV0Clockwise();
                            curHex.setVertex(clockwiseV0, vDirect);

                            placeClockwiseEdge(clockwiseEdge, clockwiseV0,
                                    clockwiseV1, curHex, vDirect);
                        }
                    } else { // we did not have an anti-clockwise axialNeighbor
                        if (!hadClockwiseNeighbor) { // vertex and edge are both new in vDirect
                            // use current hex's vertex if already placed
                            clockwiseV0 = curHex.getVertex(vDirect);
                            if (clockwiseV0 == null) {
                                // get next available vertex
                                clockwiseV0 = vertices[vertexIndex];
                                vertexIndex += 1;
                            }

                            placeClockwiseEdge(clockwiseEdge, clockwiseV0,
                                    clockwiseV1, curHex, vDirect);
                        }
                    }
                }
                // hexagon is ready to place
				curHex.setCoord(curHexLocation);
                hexLocationHash = HexGridUtils.perfectHash(curHexLocation);
                hexMap.put(hexLocationHash, curHex);
                successfullyHashed.add(curHex);
                hexagonIndex += 1;
            }
        }

		if (successfullyHashed.size() != Hexagon.NUM_HEXAGONS) {
			Log.d("WARNING","Some hexes were not hashed!");
		}


        // shuffled array of edges
        ArrayList<Edge> randomPortEdges = new ArrayList<Edge>(portEdges);
        Collections.shuffle(randomPortEdges);

        // associate vertices with harbors
        Harbor harbor;
        int neighborDirect, forbiddenNeighborEdgeDirect;
        HashSet<Edge> forbiddenPortEdges = new HashSet<Edge>();
        for (int i = 0, j = 0; i < harbors.length; i++, j++) {
            while (j < randomPortEdges.size()) {
                clockwiseEdge = randomPortEdges.get(j);
                if (forbiddenPortEdges.contains(clockwiseEdge)) {
                    j++;
                    continue;
                }
                curHex = clockwiseEdge.getOriginHex();
                curHexLocation = curHex.getCoord();
                //TODO: modularize
                // add clockwise forbidden edge
                neighborDirect = (clockwiseEdge.getOriginHexDirect() + 1) % 6;
                neighborLocation =
                        AxialHexLocation.axialNeighbor(curHexLocation, neighborDirect);
                curNeighborHex = hexMap.get(HexGridUtils.perfectHash(neighborLocation));
                if (curNeighborHex != null) {
                    forbiddenNeighborEdgeDirect =
                            (AxialHexLocation.complementAxialDirection(neighborDirect) + 1) % 6;
                    forbiddenPortEdges.add(curNeighborHex.getEdge(forbiddenNeighborEdgeDirect));
                }
                // add counter-clockwise forbidden edge
                neighborDirect = (((((clockwiseEdge.getOriginHexDirect() - 1)  % 6) + 6) % 6));
                neighborLocation =
                        AxialHexLocation.axialNeighbor(curHexLocation, neighborDirect);
                curNeighborHex = hexMap.get(HexGridUtils.perfectHash(neighborLocation));
                if (curNeighborHex != null) {
                    forbiddenNeighborEdgeDirect =
                            ((((AxialHexLocation.complementAxialDirection(neighborDirect) - 1)
                                    % 6) + 6) % 6);
                    forbiddenPortEdges.add(curNeighborHex.getEdge(forbiddenNeighborEdgeDirect));
                }
                // track current edge
                forbiddenPortEdges.add(clockwiseEdge);
                break;
            }
            if (j > randomPortEdges.size()) {
                Log.d("ERROR", "insufficient port edges");
                break;
            }
            harbor = harbors[i];
            edgeIndex = curHex.findEdge(clockwiseEdge);
            harbor.setPosition(Harbor.vdirectToPosition(edgeIndex));
            clockwiseEdge.setMyHarbor(harbor);
            clockwiseEdge.getV0Clockwise().setHarbor(harbor);
            clockwiseEdge.getV1Clockwise().setHarbor(harbor);
			HARBOR_EDGES[i] = clockwiseEdge.getIndex();
			HARBOR_HEXES[i] = clockwiseEdge.getOriginHex().getId();
        }

		initCoordinates(hexagons, vertices, edges, harbors, hexMap);

	}

	public static void initCoordinates(Hexagon[] hexagons, Vertex[] vertices,
									 Edge[] edges, Harbor[] harbors, HashMap<Long, Hexagon> hexMap) {
		HexGridLayout layout = new HexGridLayout(HexGridLayout.flat, HexGridLayout.size_default, HexGridLayout.origin_default);
		AxialHexLocation axialCoord;
		float center_X, center_Y;
		HexPoint cartesianCoord, vertexOffset;
		int index;
		for (Hexagon h : hexagons) {
			// set hex cartesian coordinates
			index = h.getId();
			axialCoord = h.getCoord();
			cartesianCoord = HexGridLayout.hexToPixel(layout, axialCoord);
			center_X = (float) cartesianCoord.x;
			center_Y = (float) cartesianCoord.y;
			HEXAGONS_X[index] = center_X;
			HEXAGONS_Y[index] = center_Y;
		}
        Hexagon myHex;
        int myHexIndex, angleDirection;
        for (Vertex v: vertices)
        {
            // set vertex cartesian coordinates
            index = v.getIndex();
            myHex = v.getHexagon(0);
            myHexIndex = myHex.getId();
            angleDirection = -((((myHex.findVertex(v) - 1)  % 6) + 6) % 6);
            vertexOffset = HexGridLayout.hexCornerOffset(layout, angleDirection);
            VERTICES_X[index] = HEXAGONS_X[myHexIndex] + ((float) vertexOffset.x);
            VERTICES_Y[index] = HEXAGONS_Y[myHexIndex] + ((float) vertexOffset.y);
        }
		int v0_index, v1_index;
		for (Edge e : edges) {
			// set edge cartesian coordinates
			index = e.getIndex();
			v0_index = e.getV0Clockwise().getIndex();
			v1_index = e.getV1Clockwise().getIndex();
			EDGES_X[index] = (float) ((VERTICES_X[v0_index] + VERTICES_X[v1_index])/2.0);
			EDGES_Y[index] = (float) ((VERTICES_Y[v0_index] + VERTICES_Y[v1_index])/2.0);
		}
	}

	// OLD GEOMETRY CONSTANTS

	private static final float[] HEXAGONS_X_o = { -1.44f, -1.44f, -1.44f, -0.72f,
			-0.72f, -0.72f, -0.72f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.72f, 0.72f,
			0.72f, 0.72f, 1.44f, 1.44f, 1.44f };

	private static final float[] HEXAGONS_Y_o = { 0.84f, -0.0f, -0.84f, 1.26f,
			0.42f, -0.42f, -1.26f, 1.68f, 0.84f, -0.0f, -0.84f, -1.68f, 1.26f,
			0.42f, -0.42f, -1.26f, 0.84f, -0.0f, -0.84f };

	private static final float[] VERTICES_X_o = { -0.24f, 0.24f, -0.96f, -0.5f,
			0.48f, 0.96f, -1.68f, -1.22f, -0.24f, 0.22f, 1.2f, 1.68f, -1.94f,
			-0.96f, -0.5f, 0.48f, 0.94f, 1.94f, -1.68f, -1.22f, -0.24f, 0.22f,
			1.2f, 1.68f, -1.94f, -0.96f, -0.5f, 0.48f, 0.94f, 1.94f, -1.68f,
			-1.22f, -0.24f, 0.22f, 1.2f, 1.68f, -1.94f, -0.96f, -0.5f, 0.48f,
			0.94f, 1.94f, -1.68f, -1.22f, -0.24f, 0.22f, 1.2f, 1.68f, -0.96f,
			-0.5f, 0.48f, 0.96f, -0.24f, 0.24f };

	private static final float[] VERTICES_Y_o = { 2.1f, 2.1f, 1.68f, 1.68f, 1.68f,
			1.68f, 1.26f, 1.26f, 1.26f, 1.26f, 1.26f, 1.26f, 0.84f, 0.84f,
			0.84f, 0.84f, 0.84f, 0.84f, 0.42f, 0.42f, 0.42f, 0.42f, 0.42f,
			0.42f, -0.0f, -0.0f, -0.0f, -0.0f, -0.0f, -0.0f, -0.42f, -0.42f,
			-0.42f, -0.42f, -0.42f, -0.42f, -0.84f, -0.84f, -0.84f, -0.84f,
			-0.84f, -0.84f, -1.26f, -1.26f, -1.26f, -1.26f, -1.26f, -1.26f,
			-1.68f, -1.68f, -1.68f, -1.68f, -2.1f, -2.1f };

	private static final float[] EDGES_X_o = { 0.0f, -0.37f, 0.36f, -0.73f,
			-1.09f, -0.37f, 0.72f, 0.35f, 1.08f, -1.45f, -1.81f, -1.09f,
			-0.01f, -0.37f, 0.35f, 1.44f, 1.07f, 1.81f, -1.81f, -0.73f, -1.09f,
			-0.37f, 0.71f, 0.35f, 1.07f, 1.81f, -1.45f, -1.81f, -1.09f, -0.01f,
			-0.37f, 0.35f, 1.44f, 1.07f, 1.81f, -1.81f, -0.73f, -1.09f, -0.37f,
			0.71f, 0.35f, 1.07f, 1.81f, -1.45f, -1.81f, -1.09f, -0.01f, -0.37f,
			0.35f, 1.44f, 1.07f, 1.81f, -1.81f, -0.73f, -1.09f, -0.37f, 0.71f,
			0.35f, 1.07f, 1.81f, -1.45f, -1.09f, -0.01f, -0.37f, 0.35f, 1.44f,
			1.08f, -0.73f, -0.37f, 0.72f, 0.36f, 0.0f };

	private static final float[] EDGES_Y_o = { 2.1f, 1.89f, 1.89f, 1.68f, 1.47f,
			1.47f, 1.68f, 1.47f, 1.47f, 1.26f, 1.05f, 1.05f, 1.26f, 1.05f,
			1.05f, 1.26f, 1.05f, 1.05f, 0.63f, 0.84f, 0.63f, 0.63f, 0.84f,
			0.63f, 0.63f, 0.63f, 0.42f, 0.21f, 0.21f, 0.42f, 0.21f, 0.21f,
			0.42f, 0.21f, 0.21f, -0.21f, -0.0f, -0.21f, -0.21f, -0.0f, -0.21f,
			-0.21f, -0.21f, -0.42f, -0.63f, -0.63f, -0.42f, -0.63f, -0.63f,
			-0.42f, -0.63f, -0.63f, -1.05f, -0.84f, -1.05f, -1.05f, -0.84f,
			-1.05f, -1.05f, -1.05f, -1.26f, -1.47f, -1.26f, -1.47f, -1.47f,
			-1.26f, -1.47f, -1.68f, -1.89f, -1.68f, -1.89f, -2.1f };

	private static final int[] HARBOR_EDGES_o = { 0, 4, 8, 27, 34, 52, 59, 67, 69 };

	private static final int[] HARBOR_HEXES_o = { 7, 3, 12, 1, 17, 2, 18, 6, 15 };

	private static final float[] HARBORS_OFFSET_X = { 0.0f, -0.16f, 0.15f,
			-0.15f, 0.15f, -0.15f, 0.15f, 0.0f, 0.0f };

	private static final float[] HARBORS_OFFSET_Y = { 0.18f, 0.1f, 0.1f, 0.1f,
			0.1f, -0.1f, -0.1f, -0.16f, -0.16f };

	// NEW GEOMETRY ARRAYS

	//TODO: replace hard-coded constants with below dynamic arrays

	private static final float[] HEXAGONS_X = new float[Hexagon.NUM_HEXAGONS];

	private static final float[] HEXAGONS_Y = new float[Hexagon.NUM_HEXAGONS];

	private static final float[] VERTICES_X = new float[Vertex.NUM_VERTICES];

	private static final float[] VERTICES_Y =  new float[Vertex.NUM_VERTICES];

	private static final float[] EDGES_X = new float[Edge.NUM_EDGES];

	private static final float[] EDGES_Y = new float[Edge.NUM_EDGES];

	private static final int[] HARBOR_EDGES = new int[Harbor.NUM_HARBORS];

	private static final int[] HARBOR_HEXES = new int[Harbor.NUM_HARBORS];

	private static final float[] HARBORS_OFFSET_X_n = new float[Harbor.NUM_HARBORS];

	private static final float[] HARBORS_OFFSET_Y_n = new float[Harbor.NUM_HARBORS];

}
