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

import com.badlogic.gdx.math.*;

import edu.cornell.blokus.util.FilmStrip;

import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.assets.AssetManager;

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
	private static final String BACKGROUND_FILE = "images/ice.png";
	
	// Asset loading is handled statically, so these are static variables
	/** The background image for the battle */
	private static Texture background; 
	private static Texture redTile, blueTile, greenTile, yellowTile, blankTile;

	public enum Tile {
		BLUE, RED, GREEN, YELLOW, BLANK
	}

	public static Tile[][] grid;
	public static final int GRID_X = 128, GRID_Y = 32;

    
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
		inputController  = new InputController(1);
		grid = new Tile[20][20];
		for(int i=0; i<20; i++){
			for(int j=0; j<20; j++){
				grid[i][j] = Tile.BLANK;
			}
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
		inputController.readInput();

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
		canvas.drawOverlay(background, true);
		for(int i=0; i<20; i++){
			for(int j=0; j<20; j++){
				switch(grid[i][j]){
					case BLANK:
						canvas.draw(blankTile, Color.WHITE, GRID_X+j*32, GRID_Y+i*32);
						break;
					case BLUE:
						canvas.draw(blueTile, Color.WHITE, GRID_X+j*32, GRID_Y+i*32);
						break;
					case GREEN:
						canvas.draw(greenTile, Color.WHITE, GRID_X+j*32, GRID_Y+i*32);
						break;
					case RED:
						canvas.draw(redTile, Color.WHITE, GRID_X+j*32, GRID_Y+i*32);
						break;
					case YELLOW:
						canvas.draw(yellowTile, Color.WHITE, GRID_X+j*32, GRID_Y+i*32);
						break;
				}
			}
		}

		Piece[] plist = Pieces.getInstance().plist;

		for(int i=1; i<2; i++){
			int ii = i+19;
			for(int r=0; r<plist[ii].rotations; r++){
				for(int t=0; t<plist[ii].solids[r].length; t++){
					canvas.draw(blueTile, Color.WHITE, GRID_X+r*256+plist[ii].solids[r][t].x*32, GRID_Y+i*256+plist[ii].solids[r][t].y*32);
				}
				for(int t=0; t<plist[ii].corners[r].length; t++){
					canvas.draw(greenTile, Color.WHITE, GRID_X+r*256+plist[ii].corners[r][t].x*32, GRID_Y+i*256+plist[ii].corners[r][t].y*32);
				}
				for(int t=0; t<plist[ii].edges[r].length; t++){
					canvas.draw(redTile, Color.WHITE, GRID_X+r*256+plist[ii].edges[r][t].x*32, GRID_Y+i*256+plist[ii].edges[r][t].y*32);
				}
			}
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