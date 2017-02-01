package com.catandroid.app.common.controllers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.util.DisplayMetrics;

import com.catandroid.app.common.components.Board;
import com.catandroid.app.common.components.BoardGeometry;
import com.catandroid.app.common.components.Edge;
import com.catandroid.app.common.components.Harbor;
import com.catandroid.app.common.components.Hexagon;
import com.catandroid.app.common.components.Vertex;
import com.catandroid.app.common.players.Player;
import com.catandroid.app.common.ui.views.GameView;
import com.catandroid.app.common.ui.resources.Square;
import com.catandroid.app.common.ui.TextureManager;

public class GameRenderer implements Renderer {

	public enum Action {
		NONE, TOWN, CITY, ROAD, ROBBER
	}

	private TextureManager texture;
	private Board board;
	private Player player;
	private int lastRoll;
	private Action action;

	private int width, height;

	private static BoardGeometry boardGeometry = null;

	private static final float[] backgroundColors = { 0, 0.227f, 0.521f, 1,
			0.262f, 0.698f, 0.878f, 1, 0, 0.384f, 0.600f, 1, 0.471f, 0.875f,
			1f, 1 };

	private Square background;
	private GameView view;

	public GameRenderer(GameView gameView) {
		view = gameView;
		
		if (boardGeometry == null)
			boardGeometry = new BoardGeometry();

		action = Action.NONE;
	}

	public void setState(Board board, Player player, TextureManager texture, int lastRoll) {
		this.texture = texture;
		this.board = board;
		this.player = player;
		this.lastRoll = lastRoll;
	}

	public void setSize(DisplayMetrics screen, int width, int height) {
		boardGeometry.setSize(width, height);
		this.width = width;
		this.height = height;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}
	
	public BoardGeometry getGeometry() {
		return boardGeometry;
	}

	public boolean cancel() {
		// TODO: cancel intermediate interactions

		return ((board.isProduction() || board.isBuild()) && action != Action.NONE);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
		this.height = height;
		
		boardGeometry.setSize(width, height);

		float aspect = (float) width / (float) height;
		if (width > height)
			background = new Square(backgroundColors, 0, 0, 0, 2 * aspect, 2);
		else
			background = new Square(backgroundColors, 0, 0, 0, 2, 2 / aspect);

		gl.glViewport(0, 0, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		gl.glShadeModel(GL10.GL_SMOOTH);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_DST_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		texture.initGL(gl);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glColor4f(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		float aspect = (float) width / (float) height;
		if (width > height)
			gl.glOrthof(-aspect, aspect, -1, 1, 0.1f, 40f);
		else
			gl.glOrthof(-1, 1, -1 / aspect, 1 / aspect, 0.1f, 40f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glTranslatef(0, 0, -30);

		// draw background without transformation
		background.render(gl);

		gl.glTranslatef(-boardGeometry.getTranslateX(), -boardGeometry.getTranslateY(), 0);
		gl.glScalef(boardGeometry.getZoom(), boardGeometry.getZoom(), 1);

		// draw the hexangons with backdrop
		for (int i = 0; i < Hexagon.NUM_HEXAGONS; i++)
			texture.draw1(board.getHexagon(i), gl, boardGeometry);

		// draw the roll numbers, robber, and highlighting
		for (int i = 0; i < Hexagon.NUM_HEXAGONS; i++)
			texture.draw2(board.getHexagon(i), gl, boardGeometry);
		for (int i = 0; i < Hexagon.NUM_HEXAGONS; i++)
			texture.draw3(board.getHexagon(i), gl, boardGeometry, lastRoll);
		for (int i = 0; i < Hexagon.NUM_HEXAGONS; i++)
			texture.draw4(board.getHexagon(i), gl, boardGeometry);

		// draw traders
		for (int i = 0; i < Harbor.NUM_HARBORS; i++)
			texture.draw(board.getTrader(i), gl, boardGeometry);

		// draw edges
		for (int i = 0; i < Edge.NUM_EDGES; i++) {
			Edge edge = board.getEdge(i);
			boolean build = action == Action.ROAD && player != null
					&& player.canBuild(edge);

			if (build || edge.getOwner() != null)
				texture.draw(edge, build, gl, boardGeometry);
		}

		// draw vertices
		for (int i = 0; i < Vertex.NUM_VERTICES; i++) {
			Vertex vertex = board.getVertex(i);
			boolean town = player != null && action == Action.TOWN
					&& player.canBuild(vertex, Vertex.TOWN);
			boolean city = player != null && action == Action.CITY
					&& player.canBuild(vertex, Vertex.CITY);

			texture.draw(vertex, town, city, gl, boardGeometry);
		}

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, width, 0, height, 0.1f, 40f);
		gl.glTranslatef(0, 0, -30);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// draw the buttons
		view.placeButtons(width, height);
		view.drawButtons(texture, gl);
	}
}
