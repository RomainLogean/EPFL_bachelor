/*
 *	Author:      Romain Logean
 *	Date:        8 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Audio;
import ch.epfl.cs107.play.window.Canvas;

public class Bonus extends CollectableAreaEntity{
	private final static int ANIMATION_DURATION=4;
	private final static int TIMER=30;
	private Sprite[] sprites = Sprite.extractSprites("superpacman/coin", 4, 1, 1, this, 16, 16);
	private Animation animation;

	public Bonus(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		animation = new Animation(ANIMATION_DURATION,sprites);
	}
	
    @Override
    public void bip(Audio audio) {
    	if(playSound) {
    		sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/fight"));
        	sons.shouldBeStarted();
        	sons.bip(audio);
    	}
    }
    
	public void animationUpdate() {
		animation.update(ANIMATION_DURATION);		
	}	
	
	public static int getTimer() {
		return TIMER;
	}
	
	@Override
	public void draw(Canvas canvas) {
		animationUpdate();
		animation.draw(canvas);
	}

	@Override
	public void interaction(SuperPacmanPlayer p, Area a) {
		super.interaction(p,a);
		Ghost.afraid();
	}
}
