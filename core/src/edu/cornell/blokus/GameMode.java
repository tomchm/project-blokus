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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;


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
	public static Tile[][] grid;
	public static final int GRID_X = (W - GRID_WIDTH * TILE_SIZE) / 2, GRID_Y = (H - GRID_HEIGHT * TILE_SIZE)/2;

	public static final int P_XMARGIN = 24;
	public static final int P_YMARGIN = 24;

	public PlayerArea p1_area;
	public PlayerArea p2_area;

	public GamePiece selected = null;
	public GamePiece mousePiece = null;

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
		grid = new Tile[GRID_HEIGHT][GRID_WIDTH];
		for(int i=0; i<GRID_HEIGHT; i++){
			for(int j=0; j<GRID_WIDTH; j++){
				grid[i][j] = Tile.BLANK;
			}
		}
		initializePlayerArea();
		Testing.init(p2_area, grid);
	}


	public void initializePlayerArea(){
		p1_area = new PlayerArea( (W - GRID_WIDTH * TILE_SIZE) / 4, H/2,(W - GRID_WIDTH * TILE_SIZE) / 2 - 2*P_XMARGIN, H - 2*P_YMARGIN , TILE_SIZE, Tile.BLUE);
		p1_area.setTexture(playerArea);
		for (int i = 0; i < p1_area.gamePieces.length; i++) {
		    allGamePieces.add(p1_area.gamePieces[i]);
        }

		p2_area = new PlayerArea( W - (W - GRID_WIDTH * TILE_SIZE) / 4 , H/2,(W - GRID_WIDTH * TILE_SIZE) / 2 - 2*P_XMARGIN, H - 2*P_YMARGIN , TILE_SIZE, Tile.RED);
		p2_area.setTexture(playerArea);
        for (int i = 0; i < p2_area.gamePieces.length; i++) {
            allGamePieces.add(p2_area.gamePieces[i]);
        }

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

		if (inputController.clicked) {
            if (inGrid(pos.x, pos.y) && mousePiece != null) {
                putPieceOnGrid(mousePiece);
				p1_area.removePiece(selected);
				allGamePieces.removeValue(selected, true);
            }
            selected = p1_area.getPieceAt(pos.x,  pos.y);
            if (selected != null ) {
                mousePiece = new GamePiece(pos.x, pos.y, 0,selected.template);
            }
            else {
                mousePiece = null;
            }
        }

        if (mousePiece != null) {
		    mousePiece.setXY(pos.x, pos.y);
        }
		// testing
		if(inputController.keyDown(Input.Keys.A)){
			Testing.getNextPiece();
		}
		if(inputController.keyDown(Input.Keys.S)){
			Testing.possibleMoves = Brain.getAllMoves(p1_area, grid);
		}

	}

	public boolean inGrid(float x, float y) {
        boolean tempA = x > GRID_X && x < GRID_X + GRID_WIDTH * TILE_SIZE;
        boolean tempB = y > GRID_Y && y < GRID_Y + GRID_HEIGHT * TILE_SIZE;
        return tempA  && tempB;
    }

    public void putPieceOnGrid(GamePiece gp) {
        for (int i = 0; i < GRID_HEIGHT; i ++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                if (gp.isContained(i * TILE_SIZE + GRID_X + TILE_SIZE/2, j * TILE_SIZE + GRID_Y + TILE_SIZE/2, TILE_SIZE)) {
                    grid[j][i] = Tile.BLUE;
                }
            }
        }
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
		p2_area.draw(canvas);

		for(int i=0; i<GRID_HEIGHT; i++){
			for(int j=0; j<GRID_WIDTH; j++){
				switch(grid[i][j]){
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

        if(Testing.gamePiece != null){
			for(int i=0; i<Testing.gamePiece.template.solids[Testing.gamePiece.rotation].length; i++){
				Pair pair = Testing.gamePiece.template.solids[Testing.gamePiece.rotation][i];
				canvas.draw(blueTile, Color.WHITE,0,0, GRID_X+(pair.x+Testing.gamePiece.x)*TILE_SIZE, GRID_Y+(pair.y+Testing.gamePiece.y)*TILE_SIZE, 0, TILE_SIZE/32.0f, TILE_SIZE/32.0f);
			}
			for(int t=0; t<Testing.gamePiece.template.corners[Testing.gamePiece.rotation].length; t++){
				Pair pair = Testing.gamePiece.template.corners[Testing.gamePiece.rotation][t];
				canvas.draw(greenTile, new Color(1,1,1,0.6f), 0,0,GRID_X+(pair.x+Testing.gamePiece.x)*TILE_SIZE, GRID_Y+(pair.y+Testing.gamePiece.y)*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
			}
			for(int t=0; t<Testing.gamePiece.template.edges[Testing.gamePiece.rotation].length; t++){
				Pair pair = Testing.gamePiece.template.edges[Testing.gamePiece.rotation][t];
				canvas.draw(redTile, new Color(1,1,1,0.6f), 0,0,GRID_X+(pair.x+Testing.gamePiece.x)*TILE_SIZE, GRID_Y+(pair.y+Testing.gamePiece.y)*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
			}
		}


	}

	public void drawGamePiece(GameCanvas canvas, GamePiece gp, boolean justBlue) {
	    if (gp == null) return;

		for(int t=0; t<gp.template.solids[gp.rotation].length; t++){
			Pair tile = gp.template.solids[gp.rotation][t];
			canvas.draw(blueTile, Color.WHITE, 0,0,gp.x+tile.x*TILE_SIZE, gp.y+tile.y*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
		}

		if (justBlue) return;

		for(int t=0; t<gp.template.corners[gp.rotation].length; t++){
			Pair tile = gp.template.corners[gp.rotation][t];
			canvas.draw(greenTile, new Color(1,1,1,0.6f), 0,0,gp.x+tile.x*TILE_SIZE, gp.y+tile.y*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
		}
		for(int t=0; t<gp.template.edges[gp.rotation].length; t++){
			Pair tile = gp.template.edges[gp.rotation][t];
			canvas.draw(redTile, new Color(1,1,1,0.6f), 0,0,gp.x+tile.x*TILE_SIZE, gp.y+tile.y*TILE_SIZE,0,TILE_SIZE/32.0f, TILE_SIZE/32.0f);
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