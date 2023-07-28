/*
 *	Author:      Romain Logean
 *	Date:        17 nov. 2020
 */



package ch.epfl.cs107.play.game.tutos;

import java.awt.Color;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.tutos.actor.GhostPlayer;
import ch.epfl.cs107.play.game.tutos.actor.SimpleGhost;
import ch.epfl.cs107.play.game.tutos.area.SimpleArea;
import ch.epfl.cs107.play.game.tutos.area.Tuto2Area;
import ch.epfl.cs107.play.game.tutos.area.tuto2.Ferme;
import ch.epfl.cs107.play.game.tutos.area.tuto2.Village;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;


public class Tuto2 extends AreaGame{
	public final static float CAMERA_SCALE_FACTOR = 13.f;
	public final static float STEP = 0.05f;
	
	private GhostPlayer player;
	private Area area;
	private final String[] areas = {"zelda/Ferme", "zelda/Village"};
	private final DiscreteCoordinates[] startingPositions = {new DiscreteCoordinates(2,10), new DiscreteCoordinates(5,15)};
	private int areaIndex=0;
	public void createAreas(){
		addArea(new Village());
		addArea(new Ferme());
	}

	@Override
	public String getTitle() {
		return "Tuto2";
	}

	@Override
	public void end() {
		
	}
	
	protected void switchArea() {

		player.leaveArea();
		
		if(areaIndex==0) {
			areaIndex=1;
		}
		else {
			areaIndex=0;
		}

		Area currentArea = setCurrentArea(areas[areaIndex], true);
		player.enterArea(currentArea, startingPositions[areaIndex]);

		player.strengthen();
		
//		if(area.getTitle()=="zelda/Ferme") {
//			area = setCurrentArea("zelda/Village",true);
//		}
//		else {
//			area = setCurrentArea("zelda/Ferme",true);
//		}
//		area.registerActor(player);
//		area.setViewCandidate(player);
	}
	
	@Override
	public void update(float deltaTime) {
		if(player.isWeak()){
			switchArea();         
		}
		super.update(deltaTime);
		 
	}
	
	@Override
	public boolean begin(Window window, FileSystem fileSystem) {
		if (super.begin(window, fileSystem)) { 
			createAreas();
			areaIndex=0;
			area = setCurrentArea("zelda/Ferme",true);
			player = new GhostPlayer(area, Orientation.DOWN, startingPositions[areaIndex],"ghost.1");
			area.registerActor(player);
			area.setViewCandidate(player);
			
			return true; 
			}
			else return false;
	}
}