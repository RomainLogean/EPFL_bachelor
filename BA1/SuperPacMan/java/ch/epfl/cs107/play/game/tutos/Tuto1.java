/*
 *	Author:      Romain Logean
 *	Date:        17 nov. 2020
 */



package ch.epfl.cs107.play.game.tutos;

import java.awt.Color;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.tutos.actor.SimpleGhost;
import ch.epfl.cs107.play.game.tutos.area.SimpleArea;
import ch.epfl.cs107.play.game.tutos.area.tuto1.Ferme;
import ch.epfl.cs107.play.game.tutos.area.tuto1.Village;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;


public class Tuto1 extends AreaGame{
	
	private SimpleGhost player;
	private SimpleArea area;
	
	public void createAreas(){
		addArea(new Village());
		addArea(new Ferme());
	}

	@Override
	public String getTitle() {
		return "Tuto1";
	}

	@Override
	public void end() {
		
	}
	
	public void switchArea() {
		if(area.getTitle()=="zelda/Ferme") {
			area = (SimpleArea) setCurrentArea("zelda/Village",true);
		}
		else {
			area = (SimpleArea) setCurrentArea("zelda/Ferme",true);
		}
		area.registerActor(player);
		area.setViewCandidate(player);
		
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		 Keyboard keyboard = getWindow().getKeyboard() ; 
		 Button keyUP = keyboard.get(Keyboard.UP) ; 
		 Button keyDOWN = keyboard.get(Keyboard.DOWN) ; 
		 Button keyLEFT = keyboard.get(Keyboard.LEFT) ; 
		 Button keyRIGHT = keyboard.get(Keyboard.RIGHT) ; 
		 
		 if(keyUP.isDown())
		 {
			 player.moveUp();
		 }
		 
		 if(keyDOWN.isDown())
		 {
			 player.moveDown();
		 }
		 
		 if(keyLEFT.isDown())
		 {
			 player.moveLeft();
		 }
		 
		 if(keyRIGHT.isDown())
		 {
			 player.moveRight();
		 }
		 
		 if(player.isWeak()) {
			 switchArea();
			 player.strengthen();
		 }
		 
	}
	
	@Override
	public boolean begin(Window window, FileSystem fileSystem) {
		if (super.begin(window, fileSystem)) { 
			
			player = new SimpleGhost(new Vector (18,7),"ghost.1");
			createAreas();
			area = (SimpleArea) setCurrentArea("zelda/Ferme",true);
			area.registerActor(player);
			area.setViewCandidate(player);
			
			

			return true; 
			}
			else return false;
	}
}
