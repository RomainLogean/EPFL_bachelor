/*
 *	Author:      Romain Logean
 *	Date:        27 nov. 2020
 */



package ch.epfl.cs107.play.game.superpacman.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGraph;
import ch.epfl.cs107.play.game.superpacman.actor.Diamond;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Window;

public abstract class SuperPacmanArea extends Area implements Logic{
	
	private SuperPacmanBehavior behavior;
	public final static float CAMERA_SCALE_FACTOR = 15.f;
	public static boolean pause=false;
	protected abstract void createArea();
	public abstract DiscreteCoordinates getSpawnPosition();
	
	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public float getCameraScaleFactor() {
		return CAMERA_SCALE_FACTOR;
	}
	
   @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
        	behavior = new SuperPacmanBehavior(window, getTitle());
            setBehavior(behavior);
            behavior.registerActors(this);
            createArea();
            return true;
        }
        return false;
   }

	@Override
	public boolean isOn() {
		return (Diamond.getTotalDiamond()==0);
	}

	@Override
	public boolean isOff() {
		return !this.isOn();
	}

	@Override
	public float getIntensity() {
		return 0;
	}
	
	public AreaGraph getBehaviorGraph() {
		return behavior.getGraph();
	}
	
	public static void pause() {
		pause = true;
	}
	public static void resume() {
		pause = false;
	}
	
	@Override
	public void update(float deltaTime) {
		if(!pause) {
			super.update(deltaTime);
		}
	}
}
