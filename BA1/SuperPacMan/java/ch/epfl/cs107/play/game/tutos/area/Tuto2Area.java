/*
 *	Author:      Romain Logean
 *	Date:        20 nov. 2020
 */

package ch.epfl.cs107.play.game.tutos.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.tutos.Tuto2;
import ch.epfl.cs107.play.game.tutos.Tuto2Behavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

public abstract class Tuto2Area extends Area{
	
	private Tuto2Behavior behavior;
	
	protected abstract void createArea();

	@Override
	public float getCameraScaleFactor() {
		return Tuto2.CAMERA_SCALE_FACTOR;
	}
	
	 @Override
	    public boolean begin(Window window, FileSystem fileSystem) {
	        if (super.begin(window, fileSystem)) {
	            // Set the behavior map

	            behavior = new Tuto2Behavior(window, getTitle());
	            setBehavior(behavior);
	        	createArea();	            
	            return true;
	        }
	        return false;
	    }

}
