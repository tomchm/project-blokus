/*
 * GameMode.java
 *
 * This is the primary class file for running the game.  You should study this file for
 * ideas on how to structure your own root class. This class follows a 
 * model-view-controller pattern fairly strictly.
 *
 * Author: Walker M. White
 * Based on original GameX Ship Demo by Rama C. Hoetzlein, 2002
 * LibGDX version, 1/16/2015
 */
package edu.cornell.blokus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import java.util.Random;


/**
 * The primary controller class for the game.
 *
 * While GDXRoot is the root class, it delegates all of the work to the player mode
 * classes. This is the player mode class for running the game. In initializes all 
 * of the other classes in the game and hooks them together.  It also provides the
 * basic game loop (update-draw).
 */
public class GameMode implements ModeController {
	// GRAPHICS AND SOUND RESOURCES
	// Pathnames to texture  and sound assets
	/** The background image for the battle */
	private static final String BACKGROUND_FILE = "images/backdrop.png";
	private static final String PLAYERAREA_FILE = "images/playerArea.png";

	// Asset loading is handled statically, so these are static variables
	/** The background image for the battle */
	private static Texture background; 
	private static Texture redTile, blueTile, greenTile, yellowTile, blankTile;
	private static Texture playerArea;


	public enum Tile {
		BLUE, RED, GREEN, YELLOW, BLANK
	}

	public static final int H = Gdx.graphics.getHeight();
	public static final int W = Gdx.graphics.getWidth();
	public static final int GRID_HEIGHT = 20;
	public static final int GRID_WIDTH = 20;
	public static final int TILE_SIZE = 23;
	public static final int GRID_X = (W - GRID_WIDTH * TILE_SIZE) / 2, GRID_Y = (H - GRID_HEIGHT * TILE_SIZE)/2;

	public static final int P_XMARGIN = 24;
	public static final int P_YMARGIN = 24;

	public Board board;

	public PlayerArea p1_area;
	public PlayerArea ai1_area;
	public PlayerArea ai2_area;
	public PlayerArea ai3_area;

	public AIController[] aiControllers;
	public static final int NUM_AIS = 4;
	public boolean AllAI = true;

	public GamePiece selected = null;
	public GamePiece mousePiece = null;

	public int turn = 0;
	public boolean hasRotated = false;

	public int endCondition = 0;
	public boolean end = false;
	public int[] scores = new int[NUM_AIS];
    public boolean lookAt = false;

	public float[][] weights;
	public static final int NUM_WEIGHTS = 5;
	public float var = 0.07f;

	protected Array<GamePiece> allGamePieces = new Array <GamePiece>();

	/** 
	 * Preloads the texture and sound information for the game.
	 * 
	 * All instance of the game use the same assets, so this is a static method.  
	 * This keeps us from loading the assets multiple times.
	 *
	 * The asset manager for LibGDX is asynchronous.  That means that you
	 * tell it what to load and then wait while it loads them.  This is 
	 * the first step: telling it what to load.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public static void PreLoadContent(AssetManager manager) {
		manager.load(BACKGROUND_FILE,Texture.class);
		manager.load(PLAYERAREA_FILE,Texture.class);
		manager.load("images/redtile.png",Texture.class);
		manager.load("images/bluetile.png",Texture.class);
		manager.load("images/greentile.png",Texture.class);
		manager.load("images/yellowtile.png",Texture.class);
		manager.load("images/blanktile.png",Texture.class);

	}

	/** 
	 * Loads the texture information for the ships.
	 * 
	 * All instance of the game use the same assets, so this is a static method.  
	 * This keeps us from loading the assets multiple times.
	 *
	 * The asset manager for LibGDX is asynchronous.  That means that you
	 * tell it what to load and then wait while it loads them.  This is 
	 * the second step: extracting assets from the manager after it has
	 * finished loading them.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public static void LoadContent(AssetManager manager) {
		background    = manager.get(BACKGROUND_FILE, Texture.class);
		playerArea = manager.get(PLAYERAREA_FILE, Texture.class);
		redTile = manager.get("images/redtile.png", Texture.class);
		blueTile = manager.get("images/bluetile.png", Texture.class);
		greenTile = manager.get("images/greentile.png", Texture.class);
		yellowTile = manager.get("images/yellowtile.png", Texture.class);
		blankTile = manager.get("images/blanktile.png", Texture.class);
	}

	/** 
	 * Unloads the texture information for the ships.
	 * 
	 * This method erases the static variables.  It also deletes the
	 * associated textures from the assert manager.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public static void UnloadContent(AssetManager manager) {
		manager.unload(BACKGROUND_FILE);
		manager.unload(PLAYERAREA_FILE);
		manager.unload("images/redtile.png");
		manager.unload("images/bluetile.png");
		manager.unload("images/greentile.png");
		manager.unload("images/yellowtile.png");
		manager.unload("images/blanktile.png");


	}


	public Texture getTexture(Tile tile) {
		if (tile == Tile.BLANK) {
			return blankTile;
		}
		else if (tile == Tile.BLUE) {
			return blueTile;
		}
		else if (tile == Tile.RED) {
			return redTile;
		}
		else if (tile == Tile.GREEN) {
			return greenTile;
		}
		else if (tile == tile.YELLOW) {
			return yellowTile;
		}
		return null;
	}


    // Instance variables
	/** Read input for blue player from keyboard or game pad (CONTROLLER CLASS) */
	protected InputController inputController;

	/**
	 * Creates a new game with a playing field of the given size.
	 *
	 * This constructor initializes the models and controllers for the game.  The
	 * view has already been initialized by the root class.
	 *
	 * @param width The width of the game window
	 * @param height The height of the game window
	 */
	public GameMode(float width, float height) {
		inputController  = new InputController();
		Gdx.input.setInputProcessor(inputController);
		board = new Board(GRID_X, GRID_Y,GRID_WIDTH, GRID_HEIGHT,TILE_SIZE);

		initializeWeights();
		initializePlayerArea();
		if(!AllAI) {
			initializeAIControllers();
		}
		else {
			initializeAllAIControllers();
		}
	}

	public void reset() {
		recalculateWeights();

		board = new Board(GRID_X, GRID_Y,GRID_WIDTH, GRID_HEIGHT,TILE_SIZE);
        endCondition = 0;
        end = false;
        scores = new int[NUM_AIS];
        allGamePieces.clear();
        turn = 0;

        initializePlayerArea();
        if (!AllAI) {
            initializeWeights();
			initializeAIControllers();
		}
		else {
			initializeAllAIControllers();
		}
    }

    public void initializeWeights() {
		Random rnd = new Random();
		weights = new float[NUM_AIS][NUM_WEIGHTS];
		for (int i = 0; i < NUM_AIS; i ++){
			float weightSum = 0;
			for (int j = 0; j < NUM_WEIGHTS; j++){
				weights[i][j] = rnd.nextFloat();
				weightSum = weightSum + Math.abs(weights[i][j]);
			}
			for (int j = 0; j < NUM_WEIGHTS; j++){
				weights[i][j] = weights[i][j]/weightSum;
			}
		}
//        weights[0] = new float[]{0.2f, 0.16f, 0.5f, 0.13f};
//        weights[1] = new float[]{0.05f, 0.316f, 0.04f, 0.593f};
//        weights[2] = new float[]{0.65f, 0.05f, 0.25f, 0.05f};

	}

	public void recalculateWeights() {
//		int sumScores = scores[0] + scores[1] + scores[2] + scores[3];
//		float[] scoreWeights = new float[]{scores[0]/sumScores, scores[1]/sumScores, scores[2]/sumScores, scores[3]/sumScores };

		int maxScore = 0;
		int winner = 0;
		for (int i = 0; i < scores.length; i++){
			int score = scores[i];
			if (score > maxScore){
				winner = i;
				maxScore = score;
			}
		}

		Random rnd = new Random();
		float[][] newWeights = new float[NUM_AIS][NUM_WEIGHTS];
		for (int i = 0; i < NUM_AIS-2; i ++){

//			float selectWinner = rnd.nextFloat();
//			int winner;
//			if(selectWinner < scoreWeights[0]) winner = 0;
//			else if(selectWinner < scoreWeights[1]) winner = 1;
//			else if(selectWinner < scoreWeights[2]) winner = 2;
//			else winner = 3;

			float weightSum = 0;
			for (int j = 0; j < NUM_WEIGHTS; j++){
				newWeights[i][j] = (float)(rnd.nextGaussian() * var + weights[winner][j]);
				weightSum = weightSum + Math.abs(newWeights[i][j]);
			}
			for (int j = 0; j < NUM_WEIGHTS; j++){
				newWeights[i][j] = newWeights[i][j]/weightSum;
			}
		}

		for (int i = 2; i < NUM_AIS; i ++) {
			float weightSum = 0;
			for (int j = 0; j < NUM_WEIGHTS; j++) {
				newWeights[i][j] = rnd.nextFloat();
				weightSum = weightSum + Math.abs(newWeights[i][j]);
			}
			for (int j = 0; j < NUM_WEIGHTS; j++) {
				newWeights[i][j] = newWeights[i][j] / weightSum;
			}
		}
//		newWeights[3] = new float[]{0.65f, 0.05f, 0.25f, 0.05f};
		weights = newWeights;
	}




	public void initializePlayerArea(){
		p1_area = new PlayerArea( (W - GRID_WIDTH * TILE_SIZE) / 4, 3*H/4,(W - GRID_WIDTH * TILE_SIZE) / 2 - 2*P_XMARGIN, (H - 2*P_YMARGIN)/2 , TILE_SIZE, Tile.BLUE);
		p1_area.setTexture(playerArea);
		for (int i = 0; i < p1_area.gamePieces.length; i++) {
		    allGamePieces.add(p1_area.gamePieces[i]);
        }

		ai1_area = new PlayerArea( (W - GRID_WIDTH * TILE_SIZE) / 4, H/4,(W - GRID_WIDTH * TILE_SIZE) / 2 - 2*P_XMARGIN, (H - 2*P_YMARGIN)/2 , TILE_SIZE, Tile.GREEN);
		ai1_area.setTexture(playerArea);
		for (int i = 0; i < ai1_area.gamePieces.length; i++) {
			allGamePieces.add(ai1_area.gamePieces[i]);
		}

		ai2_area = new PlayerArea( W - (W - GRID_WIDTH * TILE_SIZE) / 4 , 3*H/4,(W - GRID_WIDTH * TILE_SIZE) / 2 - 2*P_XMARGIN, (H - 2*P_YMARGIN)/2 , TILE_SIZE, Tile.RED);
		ai2_area.setTexture(playerArea);
        for (int i = 0; i < ai2_area.gamePieces.length; i++) {
            allGamePieces.add(ai2_area.gamePieces[i]);
        }

		ai3_area = new PlayerArea( W - (W - GRID_WIDTH * TILE_SIZE) / 4 , H/4,(W - GRID_WIDTH * TILE_SIZE) / 2 - 2*P_XMARGIN, (H - 2*P_YMARGIN)/2 , TILE_SIZE, Tile.YELLOW);
		ai3_area.setTexture(playerArea);
		for (int i = 0; i < ai3_area.gamePieces.length; i++) {
			allGamePieces.add(ai3_area.gamePieces[i]);
		}
	}

	public void initializeAIControllers() {
		aiControllers = new AIController[3];
		aiControllers[0] = new AIController(ai1_area, board, weights[0]);
		aiControllers[1] = new AIController(ai2_area, board, weights[1]);
		aiControllers[2] = new AIController(ai3_area, board, weights[2]);
	}


	public void initializeAllAIControllers() {
		aiControllers = new AIController[4];
		//blue
		aiControllers[0] = new AIController(p1_area, board, weights[0]);
		//green
		aiControllers[1] = new AIController(ai1_area, board, weights[1]);
		//red
		aiControllers[2] = new AIController(ai2_area, board, weights[2]);
		//yellow
		aiControllers[3] = new AIController(ai3_area, board, weights[3]);
	}


	/** 
	 * Read user input, calculate physics, and update the models.
	 *
	 * This method is HALF of the basic game loop.  Every graphics frame 
	 * calls the method update() and the method draw().  The method update()
	 * contains all of the calculations for updating the world, such as
	 * checking for collisions, gathering input, and playing audio.  It
	 * should not contain any calls for drawing to the screen.
	 */
	@Override
	public void update() {
		// Read the keyboard for each controller.
        Pair pos = new Pair(inputController.pos.x , H - inputController.pos.y);

        if (inputController.reset) {
            reset();
        }
        lookAt = inputController.lookAt;

        if (!AllAI && turn % (NUM_AIS) == 0) {
        	if (!canPlace(p1_area) || inputController.giveUp){
        		turn++;
        		endCondition ++;
			}
			else {
				// process player input
				if (inputController.clicked) {
					if (board.inGrid(pos.x, pos.y) && mousePiece != null && board.checkValidPlacement(mousePiece)) {
						board.putPieceOnGrid(mousePiece);
						p1_area.removePiece(selected);
						allGamePieces.removeValue(selected, true);
						endCondition = 0;
						turn++;

					}
					selected = p1_area.getPieceAt(pos.x, pos.y);
					if (selected != null) {
						mousePiece = new GamePiece(pos.x, pos.y, 0, selected.template, selected.tile);
					} else {
						mousePiece = null;
					}
				}

				if (mousePiece != null) {
					if (inputController.rotated && !hasRotated) {
						mousePiece.rotation = (mousePiece.rotation + 1) % mousePiece.template.rotations;
						hasRotated = true;
					}
					if (!inputController.rotated) {
						hasRotated = false;
					}
					mousePiece.setXY(pos.x, pos.y);
				}
			}
		}

        // process ai input
		else {
        	int loopInt = (!AllAI) ? 1 : 0;
			AIController ai = aiControllers[(turn % (NUM_AIS)) - (loopInt) ];

			if(!canPlace(ai.pa)){
				turn ++;
				endCondition ++;
			}
			else {
				GamePiece aiPiece = ai.choosePiece();
				if (aiPiece != null) {
					board.putPieceOnGrid(aiPiece);
					ai.pa.removePiece(ai.selected);
					allGamePieces.removeValue(ai.selected, true);
				}
				turn++;
				endCondition = 0;
			}
		}

		if (endCondition == NUM_AIS){
        	calculateScores();
        	end = true;
		}
		// both inputs
	}

	public boolean canPlace(PlayerArea pa){
		for(GamePiece gp: pa.gamePieces) {
			if (gp == null) continue;
			for (int i = 0; i < board.height; i ++) {
				for (int j = 0; j < board.width; j++) {
					for (int rot = 0; rot < gp.template.rotations; rot ++){
						if (board.checkValidPlacement(gp.template, rot, j, i, gp.tile)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void calculateScores() {
		scores[0] = countTiles(p1_area.tile) + ((p1_area.allRemoved()) ? 15 : 0) + ((p1_area.lastOne && p1_area.allRemoved()) ? 5 : 0);
		scores[1] = countTiles(ai1_area.tile) + ((ai1_area.allRemoved()) ? 15 : 0) + ((ai1_area.lastOne && ai1_area.allRemoved()) ? 5 : 0);
		scores[2] = countTiles(ai2_area.tile) + ((ai2_area.allRemoved()) ? 15 : 0) + ((ai2_area.lastOne && ai2_area.allRemoved()) ? 5 : 0);
		scores[3] = countTiles(ai3_area.tile) + ((ai3_area.allRemoved()) ? 15 : 0) + ((ai3_area.lastOne && ai3_area.allRemoved()) ? 5 : 0);
	}

	public int countTiles(Tile tile) {
		int count = 0;
		for (int i = 0; i < board.height; i ++) {
			for (int j = 0; j < board.width; j++) {
				if (board.grid[i][j] == tile){
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Draw the game on the provided GameCanvas
	 *
	 * There should be no code in this method that alters the game state.  All 
	 * assignments should be to local variables or cache fields only.
	 *
	 * @param canvas The drawing context
	 */
	@Override
	public void draw(GameCanvas canvas) {
		canvas.drawOverlay(background,true);
		p1_area.draw(canvas);
		ai1_area.draw(canvas);
		ai2_area.draw(canvas);
		ai3_area.draw(canvas);

		for(int i=0; i<GRID_HEIGHT; i++){
			for(int j=0; j<GRID_WIDTH; j++){
				switch(board.grid[i][j]){
					case BLANK:
						canvas.draw(blankTile, Color.WHITE,0,0, GRID_X+j*TILE_SIZE, GRID_Y+i*TILE_SIZE, 0, TILE_SIZE/32.0f, TILE_SIZE/32.0f);
						break;
					case BLUE:
						canvas.draw(blueTile, Color.WHITE,0,0, GRID_X+j*TILE_SIZE, GRID_Y+i*TILE_SIZE, 0, TILE_SIZE/32.0f, TILE_SIZE/32.0f);
						break;
					case GREEN:
						canvas.draw(greenTile, Color.WHITE,0,0, GRID_X+j*TILE_SIZE, GRID_Y+i*TILE_SIZE, 0, TILE_SIZE/32.0f, TILE_SIZE/32.0f);
						break;
					case RED:
						canvas.draw(redTile, Color.WHITE,0,0, GRID_X+j*TILE_SIZE, GRID_Y+i*TILE_SIZE, 0, TILE_SIZE/32.0f, TILE_SIZE/32.0f);
						break;
					case YELLOW:
						canvas.draw(yellowTile, Color.WHITE,0,0, GRID_X+j*TILE_SIZE, GRID_Y+i*TILE_SIZE, 0, TILE_SIZE/32.0f, TILE_SIZE/32.0f);
						break;
				}
			}
		}

		for (GamePiece gp: allGamePieces) {
			if (gp != null){
				drawGamePiece(canvas,gp,true);
			}
		}
		if (selected != null) {
            drawGamePiece(canvas, mousePiece, false);
        }

        if (end && !lookAt) {
			canvas.drawOverlay(background,true);
			canvas.drawTextCentered("SCORES", 100);
			canvas.drawTextCentered(((!AllAI) ? "Player: " : "AI0 B  " + getWeightString(aiControllers[0].w))  + " : " + String.valueOf(scores[0]), 50);
			canvas.drawTextCentered("AI1 G " + getWeightString(aiControllers[1 - ((!AllAI) ? 1 : 0)].w) + " : " + String.valueOf(scores[1]), 0);
			canvas.drawTextCentered("AI2 R " + getWeightString(aiControllers[2 - ((!AllAI) ? 1 : 0)].w) + " : " + String.valueOf(scores[2]), -50);
			canvas.drawTextCentered("AI3 Y " + getWeightString(aiControllers[3 - ((!AllAI) ? 1 : 0)].w) + " : " + String.valueOf(scores[3]), -100);

		}
	}

	public String getWeightString(float[] weights){
		String wString = "[";
		for (float w: weights){
			wString = wString + String.valueOf(w).substring(0, Math.min(5, String.valueOf(w).length())) + ",";
		}
		return wString + "]";
	}


	public void drawGamePiece(GameCanvas canvas, GamePiece gp, boolean highlight) {
	    if (gp == null) return;

		for(int t=0; t<gp.template.solids[gp.rotation].length; t++){
			Pair tile = gp.template.solids[gp.rotation][t];
			canvas.draw(getTexture(gp.tile), Color.WHITE, 0,0,gp.x+tile.x*TILE_SIZE, gp.y+tile.y*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
		}

		if (highlight) return;

		for(int t=0; t<gp.template.corners[gp.rotation].length; t++){
			Pair tile = gp.template.corners[gp.rotation][t];
			canvas.draw(getTexture(gp.tile), new Color(1,1,1,0.5f), 0,0,gp.x+tile.x*TILE_SIZE, gp.y+tile.y*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
		}
		for(int t=0; t<gp.template.edges[gp.rotation].length; t++){
			Pair tile = gp.template.edges[gp.rotation][t];
			canvas.draw(getTexture(gp.tile), new Color(1,1,1,0.2f), 0,0,gp.x+tile.x*TILE_SIZE, gp.y+tile.y*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
		}
	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		// Garbage collection here is sufficient.  Nothing to do
	}
	
	/**
	 * Resize the window for this player mode to the given dimensions.
	 *
	 * This method is not guaranteed to be called when the player mode
	 * starts.  If the window size is important to the player mode, then
	 * these values should be passed to the constructor at start.
	 *
	 * @param width The width of the game window
	 * @param height The height of the game window
	 */
	public void resize(int width, int height) {
	}

}