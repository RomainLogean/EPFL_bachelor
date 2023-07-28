/*
 *	Author:      Romain Logean
 *	Date:        8 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Audio;
import ch.epfl.cs107.play.window.Canvas;

public class Diamond extends CollectableAreaEntity{
	
	private Sprite sprite;
	private static int totalDiamond=0;
	
	public Diamond(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		this.sprite = new Sprite("superpacman/diamond", 1.f, 1.f,this);
		++totalDiamond;
	}
	
    @Override
    public void bip(Audio audio) {
    	if(playSound) {
    		sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/transactionFail"));
        	sons.shouldBeStarted();
        	sons.bip(audio);
    	}
    }
    
	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	
	@Override
	public int getpoints() {
		return 10;
	}
	
	public static int getTotalDiamond() {
		return totalDiamond;
	}
	
	public static void resetDiamondCounter() {
		totalDiamond=0;
	}
	
	@Override
	public void interaction(SuperPacmanPlayer p, Area a) {
		super.interaction(p,a);
		--totalDiamond;
		p.scoreIncrease(this.getpoints());
	}
	
}