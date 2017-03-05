package com.catandroid.app.common.ui.graphics_controllers;

import java.util.Hashtable;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLUtils;

import com.catandroid.app.common.components.BoardGeometry;
import com.catandroid.app.common.components.Edge;
import com.catandroid.app.common.components.Harbor;
import com.catandroid.app.common.components.Hexagon;
import com.catandroid.app.common.components.Resource;
import com.catandroid.app.common.ui.resources.Square;
import com.catandroid.app.common.ui.resources.UIButton;
import com.catandroid.app.R;
import com.catandroid.app.common.components.Vertex;
import com.catandroid.app.common.players.Player;

public class TextureManager {

	private enum Type {
		NONE, BACKGROUND, SHORE, TILE, ROBBER, LIGHT, HARBOR, RESOURCE, NUMBER_TOKEN, ROAD, TOWN, CITY, ORNAMENT, BUTTONBG, BUTTON
	}

	public enum Location {
		BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT
	}

	public enum Background {
		NONE, WAVES, WAVES_HORIZONTAL
	}

	private Hashtable<Integer, Bitmap> bitmap;
	private Hashtable<Integer, Integer> resource;
	private Hashtable<Integer, Square> square;

	private static int hash(Type type, int variant) {
		return variant << 6 | type.ordinal();
	}

	private Bitmap get(Type type, int variant) {
		return bitmap.get(hash(type, variant));
	}

	private void add(Type type, int variant, int id, Resources res) {
		int key = hash(type, variant);
		Bitmap bitmap = BitmapFactory.decodeResource(res, id, new Options());
		this.bitmap.put(key, bitmap);
		this.resource.put(key, id);
		this.square.put(key, new Square(id, 0, 0, type.ordinal(),
				(float) bitmap.getWidth() / (float) BoardGeometry.TILE_SIZE,
				(float) bitmap.getHeight() / (float) BoardGeometry.TILE_SIZE));
	}

	public TextureManager(Resources res) {
		// initialize hash table
		bitmap = new Hashtable<Integer, Bitmap>();
		resource = new Hashtable<Integer, Integer>();
		square = new Hashtable<Integer, Square>();

		// load large tile textures
		add(Type.SHORE, 0, R.drawable.tile_shore, res);
		add(Type.TILE, Hexagon.TerrainType.DESERT.ordinal(), R.drawable.tile_desert,
				res);
		add(Type.TILE, Hexagon.TerrainType.PASTURE.ordinal(), R.drawable.tile_wool, res);
		add(Type.TILE, Hexagon.TerrainType.FIELDS.ordinal(), R.drawable.tile_grain, res);
		add(Type.TILE, Hexagon.TerrainType.FOREST.ordinal(), R.drawable.tile_lumber,
				res);
		add(Type.TILE, Hexagon.TerrainType.HILLS.ordinal(), R.drawable.tile_brick, res);
		add(Type.TILE, Hexagon.TerrainType.MOUNTAINS.ordinal(), R.drawable.tile_ore, res);
		add(Type.TILE, Hexagon.TerrainType.SEA.ordinal(), R.drawable.tile_sea, res);
		add(Type.TILE, Hexagon.TerrainType.GOLD_FIELD.ordinal(), R.drawable.tile_gold, res);
		add(Type.TILE, Hexagon.TerrainType.DIM.ordinal(), R.drawable.tile_dim, res);
		add(Type.LIGHT, 0, R.drawable.tile_light, res);

		// load number token textures
		add(Type.NUMBER_TOKEN, 2, R.drawable.num_2, res);
		add(Type.NUMBER_TOKEN, 3, R.drawable.num_3, res);
		add(Type.NUMBER_TOKEN, 4, R.drawable.num_4, res);
		add(Type.NUMBER_TOKEN, 5, R.drawable.num_5, res);
		add(Type.NUMBER_TOKEN, 6, R.drawable.num_6, res);
		add(Type.NUMBER_TOKEN, 8, R.drawable.num_8, res);
		add(Type.NUMBER_TOKEN, 9, R.drawable.num_9, res);
		add(Type.NUMBER_TOKEN, 10, R.drawable.num_10, res);
		add(Type.NUMBER_TOKEN, 11, R.drawable.num_11, res);
		add(Type.NUMBER_TOKEN, 12, R.drawable.num_12, res);

		// load robber textures
		add(Type.ROBBER, 0, R.drawable.tile_robber, res);

		// load button textures
		add(Type.BUTTONBG, UIButton.Background.BACKDROP.ordinal(),
				R.drawable.button_backdrop, res);
		add(Type.BUTTONBG, UIButton.Background.PRESSED.ordinal(),
				R.drawable.button_press, res);
		add(Type.BUTTON, UIButton.Type.INFO.ordinal(),
				R.drawable.button_status, res);
		add(Type.BUTTON, UIButton.Type.ROLL.ordinal(), R.drawable.button_roll,
				res);
		add(Type.BUTTON, UIButton.Type.ROAD.ordinal(), R.drawable.button_road,
				res);
		add(Type.BUTTON, UIButton.Type.TOWN.ordinal(),
				R.drawable.button_settlement, res);
		add(Type.BUTTON, UIButton.Type.CITY.ordinal(), R.drawable.button_city,
				res);
		add(Type.BUTTON, UIButton.Type.DEVCARD.ordinal(),
				R.drawable.button_development_cards, res);
		add(Type.BUTTON, UIButton.Type.TRADE.ordinal(),
				R.drawable.button_trade, res);
		add(Type.BUTTON, UIButton.Type.ENDTURN.ordinal(),
				R.drawable.button_endturn, res);
		add(Type.BUTTON, UIButton.Type.CANCEL.ordinal(),
				R.drawable.button_cancel, res);

		add(Type.ROAD, 0, R.drawable.road, res);
				
		// load large town textures
		add(Type.TOWN, Player.Color.SELECT.ordinal(),
				R.drawable.settlement_grey, res);
		add(Type.TOWN, Player.Color.RED.ordinal(), R.drawable.settlement_red,
				res);
		add(Type.TOWN, Player.Color.BLUE.ordinal(),
				R.drawable.settlement_blue, res);
		add(Type.TOWN, Player.Color.GREEN.ordinal(),
				R.drawable.settlement_green, res);
		add(Type.TOWN, Player.Color.ORANGE.ordinal(),
				R.drawable.settlement_yellow, res);

		// load large city textures
		add(Type.CITY, Player.Color.SELECT.ordinal(), R.drawable.city_grey,
				res);
		add(Type.CITY, Player.Color.RED.ordinal(), R.drawable.city_red, res);
		add(Type.CITY, Player.Color.BLUE.ordinal(), R.drawable.city_blue, res);
		add(Type.CITY, Player.Color.GREEN.ordinal(), R.drawable.city_green, res);
		add(Type.CITY, Player.Color.ORANGE.ordinal(), R.drawable.city_yellow,
				res);

		// load large resource icons
		add(Type.RESOURCE, Resource.ResourceType.LUMBER.ordinal(),
				R.drawable.res_lumber, res);
		add(Type.RESOURCE, Resource.ResourceType.WOOL.ordinal(), R.drawable.res_wool,
				res);
		add(Type.RESOURCE, Resource.ResourceType.GRAIN.ordinal(), R.drawable.res_grain,
				res);
		add(Type.RESOURCE, Resource.ResourceType.BRICK.ordinal(), R.drawable.res_brick,
				res);
		add(Type.RESOURCE, Resource.ResourceType.ORE.ordinal(), R.drawable.res_ore, res);
		add(Type.RESOURCE, Resource.ResourceType.ANY.ordinal(), R.drawable.harbor_special,
				res);

		// load large harbor textures
		add(Type.HARBOR, Harbor.Position.NORTH.ordinal(),
				R.drawable.harbor_north, res);
		add(Type.HARBOR, Harbor.Position.SOUTH.ordinal(),
				R.drawable.harbor_south, res);
		add(Type.HARBOR, Harbor.Position.NORTHEAST.ordinal(),
				R.drawable.harbor_northeast, res);
		add(Type.HARBOR, Harbor.Position.NORTHWEST.ordinal(),
				R.drawable.harbor_northwest, res);
		add(Type.HARBOR, Harbor.Position.SOUTHEAST.ordinal(),
				R.drawable.harbor_southeast, res);
		add(Type.HARBOR, Harbor.Position.SOUTHWEST.ordinal(),
				R.drawable.harbor_southwest, res);

		// load corner ornaments
		add(Type.ORNAMENT, Location.BOTTOM_LEFT.ordinal(),
				R.drawable.bl_corner, res);
		add(Type.ORNAMENT, Location.TOP_LEFT.ordinal(), R.drawable.tl_corner,
				res);
		add(Type.ORNAMENT, Location.TOP_RIGHT.ordinal(), R.drawable.tr_corner,
				res);
	}

	public static int getColor(Player.Color color) {
		switch (color) {
		case RED:
			return Color.rgb(0xBE, 0x28, 0x20);
		case BLUE:
			return Color.rgb(0x37, 0x57, 0xB3);
		case GREEN:
			return Color.rgb(0x13, 0xA6, 0x19);
		case ORANGE:
			return Color.rgb(0xE9, 0xD3, 0x03);
		default:
			return Color.rgb(0x87, 0x87, 0x87);
		}
	}
	
	public static float[] getColorArray(int color) {
		float[] array = new float[4];
		array[0] = (float) Color.red(color) / 255.0f;
		array[1] = (float) Color.green(color) / 255.0f;
		array[2] = (float) Color.blue(color) / 255.0f;
		array[3] = 1f;
		return array;
	}

	public static int darken(int color, double factor) {
		return Color.argb(Color.alpha(color),
				(int) (Color.red(color) * factor),
				(int) (Color.green(color) * factor),
				(int) (Color.blue(color) * factor));
	}

	public static void setPaintColor(Paint paint, Player.Color color) {
		paint.setColor(getColor(color));
	}

	public void draw(UIButton button, GL10 gl) {
		float factor = 2 * BoardGeometry.TILE_SIZE / BoardGeometry.BUTTON_SIZE;
		
		gl.glPushMatrix();
		gl.glTranslatef(button.getX(), button.getY(), 10);
		gl.glScalef(button.getWidth() * factor, button.getHeight() * factor, 1);
		
		square.get(hash(Type.BUTTONBG, UIButton.Background.BACKDROP.ordinal())).render(gl);
		
		if (button.isPressed())
			square.get(hash(Type.BUTTONBG, UIButton.Background.PRESSED.ordinal())).render(gl);

		square.get(hash(Type.BUTTON, button.getType().ordinal())).render(gl);

//		if (!button.isEnabled())
//			square.get(hash(ResourceType.BUTTONBG, UIButton.Background.ACTIVATED.ordinal())).render(gl);
		
		gl.glPopMatrix();
	}

	public void draw(Location location, int x, int y, GL10 gl) {
		Bitmap image = get(Type.ORNAMENT, location.ordinal());

		int dx = x;
		int dy = y;

		if (location == Location.BOTTOM_RIGHT || location == Location.TOP_RIGHT)
			dx -= image.getWidth() / 2;

		if (location == Location.BOTTOM_LEFT
				|| location == Location.BOTTOM_RIGHT)
			dy -= image.getHeight() / 2;

		gl.glPushMatrix();
		gl.glTranslatef(dx, dy, 0);
		square.get(location.ordinal()).render(gl);
		gl.glPopMatrix();
	}

	public void draw1(Hexagon hexagon, GL10 gl, BoardGeometry boardGeometry) {
		gl.glPushMatrix();

		int id = hexagon.getId();
		gl.glTranslatef(boardGeometry.getHexagonX(id), boardGeometry.getHexagonY(id), 0);

		square.get(hash(Type.SHORE, 0)).render(gl);
		square.get(hash(Type.TILE, hexagon.getTerrainType().ordinal())).render(gl);

		gl.glPopMatrix();
	}

	public void draw2(Hexagon hexagon, GL10 gl, BoardGeometry boardGeometry) {
		gl.glPushMatrix();

		int id = hexagon.getId();
		gl.glTranslatef(boardGeometry.getHexagonX(id), boardGeometry.getHexagonY(id), 0);

		if (hexagon.hasRobber())
			square.get(hash(Type.ROBBER, 0)).render(gl);

		gl.glPopMatrix();
	}

	public void draw3(Hexagon hexagon, GL10 gl, BoardGeometry boardGeometry, int lastRoll) {
		gl.glPushMatrix();

		int id = hexagon.getId();
		gl.glTranslatef(boardGeometry.getHexagonX(id), boardGeometry.getHexagonY(id), 0);

		int roll = hexagon.getNumberTokenAsInt();
		
		if (!hexagon.hasRobber() && lastRoll != 0 && roll == lastRoll)
			square.get(hash(Type.LIGHT, 0)).render(gl);

		gl.glPopMatrix();
	}

	public void draw4(Hexagon hexagon, GL10 gl, BoardGeometry boardGeometry) {
		gl.glPushMatrix();

		int id = hexagon.getId();
		gl.glTranslatef(boardGeometry.getHexagonX(id), boardGeometry.getHexagonY(id), 0);

		int roll = hexagon.getNumberTokenAsInt();

		if (roll != 0 && roll != 7) {
			gl.glScalef(1.5f, 1.5f, 1);
			square.get(hash(Type.NUMBER_TOKEN, roll)).render(gl);
		}

		gl.glPopMatrix();
	}

	public void draw(Harbor harbor, GL10 gl, BoardGeometry boardGeometry) {
		int id = harbor.getId();

		// draw shore access notches
		gl.glPushMatrix();
		gl.glTranslatef(boardGeometry.getHarborX(id), boardGeometry.getHarborY(id), 0);
		square.get(hash(Type.HARBOR, harbor.getPosition().ordinal()))
				.render(gl);
		gl.glPopMatrix();

		// draw type icon+
		gl.glPushMatrix();
		gl.glTranslatef(boardGeometry.getHarborIconX(id, harbor.getEdge()),
				boardGeometry.getHarborIconY(id, harbor.getEdge()), 0);
		square.get(hash(Type.RESOURCE, harbor.getResourceType().ordinal())).render(gl);
		gl.glPopMatrix();
	}

	public void draw(Edge edge, boolean build, GL10 gl, BoardGeometry boardGeometry) {
		float[] x = new float[2];
		float[] y = new float[2];
		x[0] = boardGeometry.getVertexX(edge.getV0Clockwise().getId());
		x[1] = boardGeometry.getVertexX(edge.getV1Clockwise().getId());
		y[0] = boardGeometry.getVertexY(edge.getV0Clockwise().getId());
		y[1] = boardGeometry.getVertexY(edge.getV1Clockwise().getId());

		Player owner = edge.getOwnerPlayer();
		float[] color;
		if (owner != null)
			color = getColorArray(getColor(owner.getColor()));
		else
			color = getColorArray(getColor(Player.Color.SELECT));
		
		float dx = x[1] - x[0];
		float dy = y[1] - y[0];
		
		gl.glColor4f(color[0], color[1], color[2], color[3]);

		gl.glPushMatrix();
		
		gl.glTranslatef(boardGeometry.getEdgeX(edge.getId()), boardGeometry.getEdgeY(edge.getId()), Type.ROAD.ordinal());
		gl.glRotatef((float) (180 / Math.PI * Math.atan(dy / dx)), 0, 0, 1);
		
		square.get(hash(Type.ROAD, 0)).render(gl);
		
		gl.glPopMatrix();
		
		gl.glColor4f(1, 1, 1, 1);
	}

	public void draw(Vertex vertex, boolean buildTown, boolean buildCity,
					 GL10 gl, BoardGeometry boardGeometry) {

		Type type = Type.NONE;
		if (vertex.getBuilding() == Vertex.CITY || buildCity)
		{
			type = Type.CITY;
		}
		else if (vertex.getBuilding() == Vertex.TOWN || buildTown)
		{
			type = Type.TOWN;
		}

		Player.Color color;
		Player owner = vertex.getOwner();
		if (buildTown || buildCity)
		{
			color = Player.Color.SELECT;
		}
		else if (owner != null)
		{
			color = owner.getColor();
		}
		else
		{
			color = Player.Color.NONE;
		}

		Square object = square.get(hash(type, color.ordinal()));
		if (object != null) {
			gl.glPushMatrix();
			int id = vertex.getId();
			gl.glTranslatef(boardGeometry.getVertexX(id), boardGeometry.getVertexY(id), Type.TOWN.ordinal());
			object.render(gl);
			gl.glPopMatrix();
		}
	}

	public Bitmap get(UIButton.Type type) {
		return get(Type.BUTTON, type.ordinal());
	}

	public Bitmap get(Resource.ResourceType resourceType) {
		return get(Type.RESOURCE, resourceType.ordinal());
	}

	public void initGL(GL10 gl) {
		for (Integer key : bitmap.keySet()) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, resource.get(key));

			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_REPEAT);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_REPEAT);
			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL10.GL_MODULATE);

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap.get(key), 0);
		}
	}
}