/*
 *	Author:      Romain Logean
 *	Date:        27 nov. 2020
 */

package ch.epfl.cs107.play.game.superpacman;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.game.superpacman.actor.SuperPacmanMenuGUI;
import ch.epfl.cs107.play.game.superpacman.actor.SuperPacmanPlayer;
import ch.epfl.cs107.play.game.superpacman.area.Level0;
import ch.epfl.cs107.play.game.superpacman.area.Level1;
import ch.epfl.cs107.play.game.superpacman.area.Level2;
import ch.epfl.cs107.play.game.superpacman.area.RouteTemple;
import ch.epfl.cs107.play.game.superpacman.area.SuperPacmanArea;
import ch.epfl.cs107.play.game.superpacman.area.Temple;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;


public class SuperPacman extends RPG{
	
	private final String startingArea = "superpacman/Level0";
	
	private static boolean isPaused=false; 
	private boolean isDown=false;
	private SuperPacmanPlayer player;
	private SuperPacmanMenuGUI menu ;
	private Keyboard keyboard;
	
	private void createAreas(){
		addArea(new Level0());
		addArea(new Level1());
		addArea(new Level2());
		addArea(new Temple());
		addArea(new RouteTemple());
	}
	
	@Override
	public String getTitle() {
		return "Super Pac-Man";
	}
	
	public static void pause() {
		if(!isPaused) {
			isPaused=true;
    		SuperPacmanArea.pause();
    		SuperPacmanMenuGUI.gamePause();
		}
	}
	
	public static void resume() {
		if(isPaused) {
			isPaused=false;
    		SuperPacmanArea.resume();
    		SuperPacmanMenuGUI.gameResume();
		}
	}
    
    @Override
	public boolean begin(Window window, FileSystem fileSystem) {
		if (super.begin(window, fileSystem)) {
			createAreas();
			SuperPacmanArea area = (SuperPacmanArea) setCurrentArea(startingArea, true);
			keyboard = area.getKeyboard();
			player = new SuperPacmanPlayer(area, Orientation.DOWN, area.getSpawnPosition());
			this.initPlayer(player);
			menu = new SuperPacmanMenuGUI("Menu", 0.f, 0.f, player);
			return true;
		}
    	return false;
    }
    
    @Override
    public void update(float deltaTime) {
    	menu.draw(getWindow());
    	if(!keyboard.get(Keyboard.ENTER).isDown()) {
    		isDown=false;
    	}
    	if(player.getPv()<=0) {
    		SuperPacmanMenuGUI.gameOver();
    	}
    	else if(keyboard.get(Keyboard.ENTER).isDown() && isPaused && !isDown) {
    		resume();
    		isDown=true;
    		super.update(deltaTime);
    	}
    	else if(keyboard.get(Keyboard.ENTER).isDown() && !isPaused && !isDown) {
    		isDown=true;
    		pause();
    		super.update(deltaTime);
    	}
    	else {
    		super.update(deltaTime);
    	}
	}

}