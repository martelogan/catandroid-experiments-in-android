package com.catandroid.app.common.components;

import com.catandroid.app.R;
import com.catandroid.app.common.logistics.AppSettings;
import com.catandroid.app.common.players.AutomatedPlayer;
import com.catandroid.app.common.players.BalancedAI;
import com.catandroid.app.common.players.Player;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class Board {


	public enum Cards {
		SOLDIER, PROGRESS, HARVEST, MONOPOLY, VICTORY
	}

	public final static int[] COUNT_PER_DICE_SUM = { 0, 0, 2, 3, 3, 3, 3, 0, 3, 3, 3, 3, 2 };

	private final HashMap<Hexagon.TerrainType, Integer> terrainTypeToCountMap;
	private HashMap<Hexagon.TerrainType, Integer> initTerrainTypeToCountMap(int boardSize)
	{
		HashMap<Hexagon.TerrainType, Integer> terrainTypeToCountMap =
				new HashMap<Hexagon.TerrainType, Integer>();
		terrainTypeToCountMap.put(Hexagon.TerrainType.DESERT, 2);
		terrainTypeToCountMap.put(Hexagon.TerrainType.GOLD_FIELD, 2);
		terrainTypeToCountMap.put(Hexagon.TerrainType.HILLS, 3);
		terrainTypeToCountMap.put(Hexagon.TerrainType.FOREST, 4);
		terrainTypeToCountMap.put(Hexagon.TerrainType.PASTURE, 4);
		terrainTypeToCountMap.put(Hexagon.TerrainType.MOUNTAINS, 4);
		terrainTypeToCountMap.put(Hexagon.TerrainType.FIELDS, 5);
		switch (boardSize) {
			case 0:
				terrainTypeToCountMap.put(Hexagon.TerrainType.SEA, 13);
				break;
			case 1:
				terrainTypeToCountMap.put(Hexagon.TerrainType.SEA, 37);
				break;
		}
		return terrainTypeToCountMap;
	}

	public Integer getTerrainCount(Hexagon.TerrainType terrainType) {
		Integer count = terrainTypeToCountMap.get(terrainType);
		if (count == null) {
			return 0;
		}
		return count;
	}

	private static final int NUM_SOLDIER = 14;
	private static final int NUM_PROGRESS = 2;
	private static final int NUM_HARVEST = 2;
	private static final int NUM_MONOPOLY = 2;
	private static final int NUM_VICTORY = 5;

	private enum Phase {
		SETUP_SETTLEMENT, SETUP_FIRST_R, SETUP_CITY, SETUP_SECOND_R,
		PRODUCTION, BUILD, PROGRESS_1, PROGRESS_2, ROBBER, DONE
	}

	private Phase phase, returnPhase;

	private Hexagon[] hexagons;
	private Vertex[] vertices;
	private Edge[] edges;
	private Player[] players;
	private Harbor[] harbors;
	private int[] cards;
	private Stack<Player> playersYetToDiscard;
	private BoardGeometry boardGeometry;
	private HashMap<Long, Hexagon> hexMap;

	private Hexagon curRobberHex, prevRobberHex;
	private int turn, turnNumber, roadCountId, longestRoad,
			largestArmy, maxPoints, humans, lastDiceRollNumber;
	private Player longestRoadOwner, largestArmyOwner, winner;

	private boolean autoDiscard;

	/**
	 * Create new board layout
	 * 
	 * @param names
	 *            array of players names
	 * @param human
	 *            whether each players is human
	 */
	public Board(String[] names, boolean[] human, int maxPoints, BoardGeometry boardGeometry,
			boolean autoDiscard) {
		this.maxPoints = maxPoints;
		this.boardGeometry = boardGeometry;
		this.terrainTypeToCountMap = initTerrainTypeToCountMap(boardGeometry.getBoardSize());
		commonInit();

		this.autoDiscard = autoDiscard;

		// initialise players
		players = new Player[4];
		for (int i = 0; i < 4; i++)
			players[i] = null;

		humans = 0;
		for (int i = 0; i < 4; i++) {
			while (true) {
				int pick = (int) (Math.random() * 4);
				if (players[pick] != null)
					continue;

				Player.Color color = Player.Color.values()[i];

				if (human[i]) {
					humans += 1;
					String participantId = "";
					players[pick] = new Player(this, pick, participantId, color, names[i],
							Player.PLAYER_HUMAN);
				} else {
					players[pick] = new BalancedAI(this, pick, color, names[i]);
				}

				break;
			}
		}
	}

	private void commonInit() {
		turn = 0;
		turnNumber = 1;
		phase = Phase.SETUP_SETTLEMENT;
		roadCountId = 0;
		longestRoad = 4;
		largestArmy = 2;
		longestRoadOwner = null;
		largestArmyOwner = null;
		hexagons = null;
		winner = null;

		playersYetToDiscard = new Stack<Player>();
		hexMap = new HashMap<Long, Hexagon>();

		// initialize development cards
		cards = new int[Cards.values().length];
		cards[Cards.SOLDIER.ordinal()] = NUM_SOLDIER;
		cards[Cards.PROGRESS.ordinal()] = NUM_PROGRESS;
		cards[Cards.VICTORY.ordinal()] = NUM_VICTORY;
		cards[Cards.HARVEST.ordinal()] = NUM_HARVEST;
		cards[Cards.MONOPOLY.ordinal()] = NUM_MONOPOLY;

		// randomly initialize hexagons
		hexagons = ComponentUtils.initRandomHexes(this);
		harbors = ComponentUtils.initRandomHarbors(boardGeometry.getHarborCount());
		vertices = ComponentUtils.generateVertices(boardGeometry.getVertexCount());
		edges = ComponentUtils.generateEdges(boardGeometry.getEdgeCount());

		// populate board map with starting parameters
		boardGeometry.populateBoard(hexagons, vertices, edges, harbors, hexMap);

		// TODO: remove hard-coding / replace this function
		// assign executeDiceRoll numbers randomly
		ComponentUtils.assignRoles(hexagons);
	}

	/**
	 * Get a reference to the board's geometry
	 *
	 * @return the board's geometry
	 */
	public BoardGeometry getBoardGeometry() {
		if (boardGeometry == null) {
			return null;
		}

		return boardGeometry;
	}

	/**
	 * Get a reference to the current players
	 * 
	 * @return the current players
	 */
	public Player getCurrentPlayer() {
		if (players == null)
			return null;

		return players[turn];
	}

	/**
	 * Get a players by index
	 * 
	 * @param index
	 *            players index [0, 3]
	 * @return the players
	 */
	public Player getPlayer(int index) {
		return players[index];
	}

	/**
	 * Distribute resources for a given dice roll number
	 * 
	 * @param diceRollNumber
	 *            the dice roll number to execute
	 */
	public void executeDiceRoll(int diceRollNumber) {
		if (diceRollNumber == 7) {
			// reduce each players to 7 cards
			for (int i = 0; i < 4; i++) {
				int cards = players[i].getResourceCount();
				int extra = cards > 7 ? cards / 2 : 0;

				if (extra == 0)
					continue;

				if (autoDiscard) {
					// discard randomly
					for (int j = 0; j < extra; j++)
						players[i].discard(null);
				}
				if (players[i].isBot()) {
					// instruct the ai to discard
					AutomatedPlayer bot = (AutomatedPlayer) players[i];
					bot.discard(extra);
				} else if (players[i].isHuman()) {
					// queue human players to discard
					playersYetToDiscard.add(players[i]);
				}
			}

			// enter robberIndex phase
			startRobberPhase();
		} else {
			// distribute resources
			for (int i = 0; i < hexagons.length; i++)
			{
				hexagons[i].distributeResources(diceRollNumber);
			}
		}

		lastDiceRollNumber = diceRollNumber;
	}

	/**
	 * Get the last executeDiceRoll
	 * 
	 * @return the last executeDiceRoll, or 0
	 */
	public int getLastDiceRollNumber() {
		if (isSetupPhase() || isProgressPhase())
		{
			return 0;
		}

		return lastDiceRollNumber;
	}

	/**
	 * Run the AI's robberIndex methods
	 * 
	 * @param current
	 *            current ai players
	 */
	private void startAIRobberPhase(AutomatedPlayer current) {
		int hex = current.placeRobber(hexagons, prevRobberHex);
		setRobber(hex);

		int count = 0;
		for (int i = 0; i < 4; i++)
		{
			if (players[i] != players[turn] && hexagons[hex].adjacentToPlayer(players[i]))
			{
				count++;
			}
		}

		if (count > 0) {
			Player[] stealList = new Player[count];
			for (int i = 0; i < 4; i++)
				if (players[i] != players[turn]
						&& hexagons[hex].adjacentToPlayer(players[i]))
				{
					stealList[--count] = players[i];
				}

			int who = current.steal(stealList);
			players[turn].steal(stealList[who]);
		}

		phase = returnPhase;
	}

	/**
	 * Start a players's turn
	 */
	public void runTurn() {
		// process ai turn
		if (players[turn].isBot()) {
			AutomatedPlayer current = (AutomatedPlayer) players[turn];
			switch (phase) {

			case SETUP_SETTLEMENT:
			case SETUP_CITY:
				current.setupTown(vertices);
				break;

			case SETUP_FIRST_R:
			case SETUP_SECOND_R:
				current.setupRoad(edges);
				break;

			case PRODUCTION:
				current.productionPhase();
				players[turn].roll();
				break;

			case BUILD:
				current.buildPhase();
				break;

			case PROGRESS_1:
				current.progressRoad(edges);
			case PROGRESS_2:
				current.progressRoad(edges);
				phase = returnPhase;
				return;

			case ROBBER:
				startAIRobberPhase(current);
				return;

			case DONE:
				return;

			}

			nextPhase();
		}
	}

	/**
	 * Proceed to the next phase or next turn
	 * 
	 * My initial reaction was to treat it as a state machine
	 */
	public boolean nextPhase() {
		boolean turnChanged = false;

		switch (phase) {
		case SETUP_SETTLEMENT:
			phase = Phase.SETUP_FIRST_R;
			break;
		case SETUP_FIRST_R:
			if (turn < 3) {
				turn++;
				turnChanged = true;
				phase = Phase.SETUP_SETTLEMENT;
			} else {
				phase = Phase.SETUP_CITY;
			}
			break;
		case SETUP_CITY:
			phase = Phase.SETUP_SECOND_R;
			break;
		case SETUP_SECOND_R:
			if (turn > 0) {
				turn--;
				turnChanged = true;
				phase = Phase.SETUP_CITY;
			} else {
				phase = Phase.PRODUCTION;
			}
			break;
		case PRODUCTION:
			phase = Phase.BUILD;
			break;
		case BUILD:
			if (turn == 3)
				turnNumber += 1;
			players[turn].endTurn();
			phase = Phase.PRODUCTION;
			turn++;
			turn %= 4;
			turnChanged = true;
			players[turn].beginTurn();
			lastDiceRollNumber = 0;
			break;
		case PROGRESS_1:
			phase = Phase.PROGRESS_2;
			break;
		case PROGRESS_2:
			phase = returnPhase;
			break;
		case ROBBER:
			phase = returnPhase;
			break;
		case DONE:
			return false;
		}

		return turnChanged;
	}

	/**
	 * Enter progress phase 1 (road building)
	 */
	public void startProgressPhase1() {
		returnPhase = phase;
		phase = Phase.PROGRESS_1;
		runTurn();
	}

	/**
	 * Enter the robber placement phase
	 */
	public void startRobberPhase() {
		this.prevRobberHex= this.curRobberHex;
		this.returnPhase = phase;
		this.curRobberHex = null;
		phase = Phase.ROBBER;
		runTurn();
	}

	/**
	 * Determine if we're in a setup phase
	 * 
	 * @return true if the game is in setup phase
	 */
	public boolean isSetupPhase() {
		return (phase == Phase.SETUP_SETTLEMENT || phase == Phase.SETUP_FIRST_R
				|| phase == Phase.SETUP_CITY || phase == Phase.SETUP_SECOND_R);
	}

	public boolean isSetupTown() {
		return (phase == Phase.SETUP_SETTLEMENT || phase == Phase.SETUP_CITY);
	}

	public boolean isSetupRoad() {
		return (phase == Phase.SETUP_FIRST_R || phase == Phase.SETUP_SECOND_R);
	}

	public boolean isSetupPhase2() {
		return (phase == Phase.SETUP_CITY || phase == Phase.SETUP_SECOND_R);
	}

	public boolean isRobberPhase() {
		return (phase == Phase.ROBBER);
	}

	public boolean isProduction() {
		return (phase == Phase.PRODUCTION);
	}

	public boolean isBuild() {
		return (phase == Phase.BUILD);
	}

	public boolean isProgressPhase() {
		return (phase == Phase.PROGRESS_1 || phase == Phase.PROGRESS_2);
	}

	public boolean isProgressPhase1() {
		return (phase == Phase.PROGRESS_1);
	}

	public boolean isProgressPhase2() {
		return (phase == Phase.PROGRESS_2);
	}

	/**
	 * Get the dice executeDiceRoll value for a hexagons
	 * 
	 * @param index
	 *            the index of the hexagons
	 * @return the executeDiceRoll value
	 */
	public int getLastRoll(int index) {
		return hexagons[index].getNumberTokenAsInt();
	}

	/**
	 * Get the resource produced by a particular hexagon
	 * 
	 * @param index
	 *            the index of the hexagon
	 * @return the resource produced by that hexagon
	 */
	public Resource getResource(int index) {
		return hexagons[index].getResource();
	}

	/**
	 * Get indexed hexToTerrainTypes mapping
	 * 
	 * @return array of terrain type ordinals
	 * @note this is intended only to be used to stream out the board layout
	 */
	public int[] getHexToTerrainTypesMapping() {
		int hexMapping[] = new int[hexagons.length];
		for (int i = 0; i < hexagons.length; i++) {
			hexMapping[i] = hexagons[i].getResourceType().ordinal();
		}

		return hexMapping;
	}

	/**
	 * Get a given hexagons
	 * 
	 * @param index
	 *            the index of the hexagons
	 * @return the hexagons
	 */
	public Hexagon getHexagon(int index) {
		if (index < 0 || index >= boardGeometry.getHexCount())
			return null;

		return hexagons[index];
	}
	
	public Hexagon[] getHexagons() {
		return hexagons;
	}

	/**
	 * Get a given harbors
	 * 
	 * @param index
	 *            the index of the harbors
	 * @return the harbors
	 */
	public Harbor getHarbor(int index) {
		if (index < 0 || index >= boardGeometry.getHarborCount())
			return null;

		return harbors[index];
	}

	/**
	 * Get the given edges
	 * 
	 * @param index
	 *            the index of the edges
	 * @return the edges
	 */
	public Edge getEdge(int index) {
		if (index < 0 || index >= boardGeometry.getEdgeCount())
			return null;

		return edges[index];
	}
	
	public Edge[] getEdges() {
		return edges;
	}

	/**
	 * Get the given vertices
	 * 
	 * @param index
	 *            the index of the vertices
	 * @return the vertices
	 */
	public Vertex getVertex(int index) {
		if (index < 0 || index >= boardGeometry.getEdgeCount())
			return null;

		return vertices[index];
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}

	/**
	 * Get a development card
	 * 
	 * @return the type of development card or null if that stack is empty
	 */
	public Cards getDevelopmentCard() {
		int soldiers = cards[Cards.SOLDIER.ordinal()];
		int progress = cards[Cards.PROGRESS.ordinal()];
		int victory = cards[Cards.VICTORY.ordinal()];
		int harvest = cards[Cards.HARVEST.ordinal()];
		int monopoly = cards[Cards.MONOPOLY.ordinal()];
		int number = soldiers + progress + victory + harvest + monopoly;

		if (number == 0)
			return null;

		int pick = (int) (Math.random() * number);

		Cards card;
		if (pick < soldiers)
			card = Cards.SOLDIER;
		else if (pick < soldiers + progress)
			card = Cards.PROGRESS;
		else if (pick < soldiers + progress + victory)
			card = Cards.VICTORY;
		else if (pick < soldiers + progress + victory + harvest)
			card = Cards.HARVEST;
		else
			card = Cards.MONOPOLY;

		cards[card.ordinal()] -= 1;
		return card;
	}

	/**
	 * Get the number of points required to win
	 * 
	 * @return the number of points required to win
	 */
	public int getMaxPoints() {
		return maxPoints;
	}

	/**
	 * Update the longest road owner and length
	 */
	public void checkLongestRoad() {
		Player previousOwner = longestRoadOwner;

		// reset road length in case a road was split
		longestRoad = 4;
		longestRoadOwner = null;

		// reset players' road lengths to 0
		for (int i = 0; i < 4; i++)
		{
			players[i].cancelRoadLength();
		}

		// find longest road
		for (int i = 0; i < edges.length; i++) {
			if (edges[i].hasRoad()) {
				int length = edges[i].getRoadLength(++roadCountId);

				Player owner = edges[i].getOwner();
				owner.setRoadLength(length);
				if (length > longestRoad) {
					longestRoad = length;
					longestRoadOwner = owner;
				}
			}
		}

		// the same players keeps the longest road if length doesn't change
		if (previousOwner != null
				&& previousOwner.getRoadLength() == longestRoad)
		{
			longestRoadOwner = previousOwner;
		}
	}

	/**
	 * Determine if players has the longest road
	 * 
	 * @param player
	 *            the players
	 * @return true if players had the longest road
	 */
	public boolean hasLongestRoad(Player player) {
		return (longestRoadOwner != null && player == longestRoadOwner);
	}

	/**
	 * Get the length of the longest road
	 * 
	 * @return the length of the longest road
	 */
	public int getLongestRoad() {
		return longestRoad;
	}

	/**
	 * Get the owner of the longest road
	 * 
	 * @return the players with the longest road
	 */
	public Player getLongestRoadOwner() {
		return longestRoadOwner;
	}

	/**
	 * Update the largest army if the given size is larger than the current size
	 * 
	 * @param player
	 *            the players owning the army
	 * @param size
	 *            the number of soldiers
	 */
	public void checkLargestArmy(Player player, int size) {
		if (size > largestArmy) {
			largestArmyOwner = player;
			largestArmy = size;
		}
	}

	/**
	 * Determine if players has the largest army
	 * 
	 * @param player
	 *            the players
	 * @return true if players has the largest army
	 */
	public boolean hasLargestArmy(Player player) {
		return (largestArmyOwner != null && player == largestArmyOwner);
	}

	/**
	 * Get the size of the largest army
	 * 
	 * @return the size of the largest army
	 */
	public int getLargestArmy() {
		return largestArmy;
	}

	/**
	 * Get the owner of the largest army
	 * 
	 * @return the players with the largest army
	 */
	public Player getLargestArmyOwner() {
		return largestArmyOwner;
	}

	/**
	 * Check if any players need to discard
	 * 
	 * @return true if one or more players need to discard
	 */
	public boolean checkPlayerToDiscard() {
		return !playersYetToDiscard.empty();
	}

	/**
	 * Get the next players queued for discarding
	 * 
	 * @return a players or null
	 */
	public Player getPlayerToDiscard() {
		try {
			return playersYetToDiscard.pop();
		} catch (EmptyStackException e) {
			return null;
		}
	}

	/**
	 * Get an instruction string for the current phase
	 * 
	 * @return the instruction string resource or 0
	 */
	public int getPhaseResource() {
		switch (phase) {
		case SETUP_SETTLEMENT:
			return R.string.phase_first_town;
		case SETUP_FIRST_R:
			return R.string.phase_first_road;
		case SETUP_CITY:
			return R.string.phase_second_town;
		case SETUP_SECOND_R:
			return R.string.phase_second_road;
		case PRODUCTION:
			return R.string.phase_roll_production;
		case BUILD:
			return R.string.phase_build;
		case PROGRESS_1:
			return R.string.phase_progress1;
		case PROGRESS_2:
			return R.string.phase_progress2;
		case ROBBER:
			return R.string.phase_move_robber;
		case DONE:
			return R.string.phase_game_over;
		}

		return 0;
	}

	/**
	 * Get the global turn number
	 * 
	 * @return the turn number (starting at 1, after setup)
	 */
	public int getTurnNumber() {
		return turnNumber;
	}

	/**
	 * Get the winner
	 * 
	 * @return the winning players or null
	 */
	public Player getWinner(AppSettings appSettings) {
		// winner already found or we just want to check what was already found
		if (winner != null || appSettings == null)
		{
			return winner;
		}

		// check for winner
		for (int i = 0; i < 4; i++) {
			if (players[i].getVictoryPoints() >= maxPoints) {
				phase = Phase.DONE;
				winner = players[i];
				break;
			}
		}

		// save game stats
		if (winner != null)
		{
			appSettings.addScore(humans, maxPoints, winner.getName(), turnNumber);
		}

		return winner;
	}

	/**
	 * Get the hexagons with the robberIndex
	 * 
	 * @return the hexagons with the robberIndex
	 */
	public Hexagon getCurRobberHex() {
		return curRobberHex;
	}

	/**
	 * If the robber is being moved, return the last hexagons where it last
	 * resided, or otherwise the current location
	 * 
	 * @return the last location of the robber
	 */
	public Hexagon getPrevRobberHex() {
		int hexCount = this.boardGeometry.getHexCount();
		int curRobberId = this.curRobberHex.getId();
		int prevRobberId = this.prevRobberHex.getId();
		if (this.phase == Phase.ROBBER && prevRobberId >= 0 && prevRobberId < hexCount)
			return this.prevRobberHex;
		else if (curRobberId >= 0 && curRobberId < hexCount) {
			return this.curRobberHex;
		}
		else {
			return null;
		}
	}

	/**
	 * Set the current robber hexagon
	 *
	 * @param curRobberHex
	 *            current robber hexagon
	 * @return true iff the currebt robber hex was set
	 */
	public boolean setCurRobberHex(Hexagon curRobberHex) {
		if (this.curRobberHex != null) {
			this.curRobberHex.removeRobber();
		}
		this.curRobberHex = curRobberHex;
		this.curRobberHex.setRobber();
		return true;
	}

	/**
	 * Set the index for the robber
	 * 
	 * @param robberIndex
	 *            id of the hexagon with the robber
	 * @return true if the robber was placed
	 */
	public boolean setRobber(int robberIndex) {
		if (this.curRobberHex != null) {
			this.curRobberHex.removeRobber();
		}
		this.curRobberHex = this.hexagons[robberIndex];
		this.curRobberHex.setRobber();
		return true;
	}

	/**
	 * Get the string resource for a card type
	 * 
	 * @param card
	 *            the card type
	 * @return the string resource
	 */
	public static int getCardStringResource(Cards card) {
		switch (card) {
		case SOLDIER:
			return R.string.soldier;
		case PROGRESS:
			return R.string.progress;
		case VICTORY:
			return R.string.victory;
		case HARVEST:
			return R.string.harvest;
		case MONOPOLY:
			return R.string.monopoly;
		default:
			return R.string.nostring;
		}
	}

}
