/*
 *	Author:      Romain Logean
 *	Date:        15 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Audio;
import ch.epfl.cs107.play.window.Canvas;

public class Clyde extends Ghost{
	protected final static int FIELD_OF_VIEW=2;
	private Sprite[][]	sprites;
	private Animation[] animations;
	private TextGraphics text;
	private Orientation initialOrientation;
	private SoundAcoustics sons;
	private boolean playSound=false;
	private boolean soundHaveBeenPlayed=false;
	
	public Clyde(Area area, Orientation orientation, DiscreteCoordinates position, String text) {
		super(area, orientation, position);
		sprites = RPGSprite.extractSprites("superpacman/ghost.clyde", 2, 1, 1, this, 16, 16, new Orientation[] {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});
		animations = Animation.createAnimations(ANIMATION_DURATION / 2, sprites);
		this.text = new TextGraphics("''"+text+"''", 0.5f, Color.WHITE, Color.BLACK, 0.02f, true, true, this.getPosition().add(new Vector(-1.5f,1.5f)));
		targetPos=new DiscreteCoordinates(0,0);
		initialOrientation=orientation;
	}

	   @Override
	    public void bip(Audio audio) {
	    	if(playSound && !soundHaveBeenPlayed) {
	    		sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/dialogNext"));
	        	sons.shouldBeStarted();
	        	sons.bip(audio);
	        	soundHaveBeenPlayed=true;
	    	}
	    }
    
	@Override
	public void moveOrientate (Orientation desiredOrientation) {                   
			orientate(desiredOrientation);
	}
	
	@Override
	public Orientation getNextOrientation() {
		if(pacmanPos==null) {
			return initialOrientation;
		}
		else {
			if(pacmanPos.x > this.getCurrentMainCellCoordinates().x) {
				return Orientation.RIGHT;
			}
			else if(pacmanPos.x < this.getCurrentMainCellCoordinates().x) {
				return Orientation.LEFT;
			}
			else if (pacmanPos.y> this.getCurrentMainCellCoordinates().y){
				return Orientation.UP;
			}
			else {
				return Orientation.DOWN;
			}
		}
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> fieldOfView= new ArrayList<DiscreteCoordinates>();
		for(int x =0;x<=getOwnerArea().getWidth();++x) {
			for(int y=0; y<=getOwnerArea().getHeight();++y) {
				if(DiscreteCoordinates.distanceBetween(getCurrentMainCellCoordinates(), new DiscreteCoordinates(x,y))<=FIELD_OF_VIEW) {
					fieldOfView.add(new DiscreteCoordinates(x,y));					
				}
			}
		}
		return fieldOfView;
	}

	@Override
	public void animation() {
		this.animations[this.getOrientation().ordinal()].update(ANIMATION_DURATION);
		
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if(pacmanPos!=null) {
			playSound=true;
			text.draw(canvas);
		}
	}
	
	@Override
	public void specificDraw(Canvas canvas) {
		this.animations[this.getOrientation().ordinal()].draw(canvas);
	}
	
	@Override
	public void interaction(SuperPacmanPlayer p, Area a) {
		//volontairement vide car clyde n'a pas de cellInteraction.
	}
}
