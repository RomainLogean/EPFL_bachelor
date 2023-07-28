/*
 *	Author:      Romain Logean
 *	Date:        27 nov. 2020
 */

package ch.epfl.cs107.play.game.superpacman.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Acoustics;
import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.superpacman.area.SuperPacmanArea;
import ch.epfl.cs107.play.game.superpacman.handler.SuperPacmanInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Audio;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class SuperPacmanPlayer extends Player implements Interactor,Interactable,Acoustics{
	private final static int ANIMATION_DURATION=10;
	private final static int SPEED =8;
	public final static int PV_MAX=5;
	private static int pv;	
	private static int score;
	private int pvBonus;
	private boolean pvSound=false;
	private boolean damageSound=false;
	private SoundAcoustics sons;
	private SuperPacmanPlayerStatusGUI statusGUI = new SuperPacmanPlayerStatusGUI("Stats", 0.f, 0.f, this);
	private Sprite[][] sprites = RPGSprite.extractSprites("superpacman/pacman", 4, 1, 1, this, 64, 64, new Orientation[] {Orientation.DOWN, Orientation.LEFT, Orientation.UP, Orientation.RIGHT});
	private Animation[] animations = Animation.createAnimations(ANIMATION_DURATION / 4, sprites);
	private SuperPacmanPlayerHandler handler= new SuperPacmanPlayerHandler();
	
	public SuperPacmanPlayer(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		pv=3;
		score=0;
		pvBonus=0;
	}
	
	   @Override
	    public void bip(Audio audio) {
	    	if(pvSound) {
	    		sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/recovery"));
	        	sons.shouldBeStarted();
	        	sons.bip(audio);
	        	pvSound=false;
	    	}
	    	if(damageSound) {
	    		sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/oof"));
	        	sons.shouldBeStarted();
	        	sons.bip(audio);
	        	damageSound=false;
	    	}
	    	if(pv<=0) {
	    		sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/gameOver"));
	        	sons.shouldBeStarted();
	        	sons.bip(audio);
	    	}
	    }
	
	public void moveOrientate (Orientation desiredOrientation, Button b) {
		if(b.isDown()) {
			if(!isDisplacementOccurs() && this.getOwnerArea().canEnterAreaCells(this ,Collections.singletonList(getCurrentMainCellCoordinates().jump(desiredOrientation.toVector())))) {                     
				orientate(desiredOrientation);
				move(SPEED);
			}
		}
	}
	
	public void animations() {
		if(isDisplacementOccurs()) {
			animations[this.getOrientation().ordinal()].update(ANIMATION_DURATION);
		}
		else if(!isDisplacementOccurs()){
			animations[this.getOrientation().ordinal()].reset();
		}
	}
	
	public void pvUp() {
		if(((int)score/5000)>pvBonus) {
			++pvBonus;
			if(pv<PV_MAX) {
				++pv;
				pvSound=true;
			}
		}
	}
	
	@Override
    public void update(float deltaTime) {

		Keyboard keyboard= getOwnerArea().getKeyboard();
		if(keyboard.get(Keyboard.SPACE).isDown()) {
			Ghost.cheatMod();
		}
		else {
			Ghost.normalMod();
		}
		moveOrientate(Orientation.UP,keyboard.get(Keyboard.UP));
		moveOrientate(Orientation.DOWN,keyboard.get(Keyboard.DOWN));
		moveOrientate(Orientation.LEFT,keyboard.get(Keyboard.LEFT));
		moveOrientate(Orientation.RIGHT,keyboard.get(Keyboard.RIGHT));
		
		pvUp();
		animations();
		
        super.update(deltaTime);
    }	

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	
	public DiscreteCoordinates getPacmanMainCellCoordinates() {
		return this.getCurrentMainCellCoordinates();
	}

	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return true;
	}


	@Override
	public void draw(Canvas canvas) {
		animations[this.getOrientation().ordinal()].draw(canvas);
		statusGUI.draw(canvas);
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return null;
	}

	@Override
	public boolean wantsCellInteraction() {
		return true;
	}

	@Override
	public boolean wantsViewInteraction() {
		return false;
	}

	@Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
	((SuperPacmanInteractionVisitor)v).interactWith(this); 
	}

	public int getPv() {
		return pv;
	}
	
	public int getScore() {
		return score;
	}

	public void scoreIncrease(int points) {
		score+=points;
	}
	
	public String getScoreString() {
		return "SCORE : "+score;
	}
	
	public void damaged() {
		--pv;
		damageSound=true;
		getOwnerArea().leaveAreaCells(this, getEnteredCells());
		setCurrentPosition(((SuperPacmanArea)this.getOwnerArea()).getSpawnPosition().toVector());
		getOwnerArea().enterAreaCells(this, getCurrentCells()); 
		resetMotion();
	}
	
	class SuperPacmanPlayerHandler implements SuperPacmanInteractionVisitor{
		
		@Override
		public void interactWith(Door door) {
			SuperPacmanPlayer.this.setIsPassingADoor(door);
			Diamond.resetDiamondCounter();
			Ghost.timerInitialise();
		}
		
		@Override
		public void interactWith(CollectableAreaEntity c) {
			c.interaction(SuperPacmanPlayer.this,SuperPacmanPlayer.this.getOwnerArea());
		}	
		
		@Override
		public void interactWith(Ghost g) {
			g.interaction(SuperPacmanPlayer.this,SuperPacmanPlayer.this.getOwnerArea());
		}
	}
}
