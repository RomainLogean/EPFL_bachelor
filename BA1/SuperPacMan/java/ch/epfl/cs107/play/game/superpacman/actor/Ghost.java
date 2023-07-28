/*
 *	Author:      Romain Logean
 *	Date:        11 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import ch.epfl.cs107.play.game.actor.Acoustics;
import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.superpacman.handler.SuperPacmanInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Audio;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Ghost extends MovableAreaEntity implements Interactable, Interactor,Acoustics{

	private final static int GHOST_POINT=500;
	private final static int FIELD_OF_VIEW=5;
	protected final static int ANIMATION_DURATION=10;
	protected int SPEED=18;
	protected int randomInt;
	private static float timer;
	private boolean statChange = false;
	protected static boolean cheatMod = false;
	protected static boolean isAfraid=false;
	private boolean respawnSound=false;
	private SoundAcoustics sons;
	private Animation animation;
	private Sprite[] sprites;
	protected final DiscreteCoordinates STARTING_POSITION;
	protected DiscreteCoordinates targetPos;
	protected DiscreteCoordinates pacmanPos;
	private GhostHandler handler = new GhostHandler();
	protected Queue<Orientation> path;
	
	public Ghost(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		sprites= Sprite.extractSprites("superpacman/ghost.afraid", 2, 1, 1, this, 16, 16);
		animation = new Animation(ANIMATION_DURATION/2,sprites);	
		timerInitialise();
		this.STARTING_POSITION=position;
	}
	
	public abstract Orientation getNextOrientation();
	
	public abstract void animation();
	
	public abstract void specificDraw(Canvas canvas);
	
	public static void cheatMod() {
		cheatMod=true;
	}
	public static void normalMod() {
		cheatMod=false;
	}
	
	public void interactWithPacman() {}

	public void afraidDraw(Canvas canvas) {
		animation.draw(canvas);	
	}
	
	public void afraidAnimation() {
		animation.update(ANIMATION_DURATION);
	}
	
	public static void afraid() {
		isAfraid=true;
	}
	
	public static boolean getIsAfraid() {
		return isAfraid;
	}
	
	public boolean afraidStatChange() {
		if(isAfraid && !statChange) {
			statChange=true;
			return true;
		}
		else if (!isAfraid && statChange){
			statChange=false;
			return true;
		}
			return false;
	}
	
	public static void timerInitialise() {
		isAfraid=false;
		timer=Bonus.getTimer();
	}
	
	public void moveOrientate (Orientation desiredOrientation) {
		if(!isDisplacementOccurs() && this.getOwnerArea().canEnterAreaCells(this ,Collections.singletonList(getCurrentMainCellCoordinates().jump(desiredOrientation.toVector())))) {                     
			orientate(desiredOrientation);
			move(SPEED);
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(isAfraid) {
			afraidDraw(canvas);
		}
		else{
			specificDraw(canvas);
		}
	}	
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		afraidAnimation();
		animation();
		this.moveOrientate(this.getNextOrientation());
		
		if(isAfraid) {
			timer -= deltaTime;
			if(timer<0) {
				timerInitialise();
			}
		}
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((SuperPacmanInteractionVisitor)v).interactWith(this);
	}
	
	public void takeRandomTargetCell() {
		List<DiscreteCoordinates> safeField= new ArrayList<DiscreteCoordinates>();
		for(int x =0;x<=getOwnerArea().getWidth();++x) {
			for(int y=0; y<=getOwnerArea().getHeight();++y) {
					safeField.add(new DiscreteCoordinates(x,y));					
			}
		}
		randomInt = RandomGenerator.getInstance().nextInt(safeField.size());
		targetPos=safeField.get(randomInt);
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> fieldOfView= new ArrayList<DiscreteCoordinates>();
		for(int x =0;x<=getOwnerArea().getWidth();++x) {
			for(int y=0; y<=getOwnerArea().getHeight();++y) {
				if(DiscreteCoordinates.distanceBetween(getCurrentMainCellCoordinates(), new DiscreteCoordinates(x,y))<=Ghost.FIELD_OF_VIEW) {
					fieldOfView.add(new DiscreteCoordinates(x,y));					
				}
			}
		}
		return fieldOfView;
	}

	@Override
	public boolean wantsCellInteraction() {
		return false;
	}

	@Override
	public boolean wantsViewInteraction() {
		return true;
	}

	@Override
	public void interactWith(Interactable other) {
        other.acceptInteraction(handler); 
	}
	
	public void interaction(SuperPacmanPlayer p, Area a) {
		if(isAfraid) {
			if(STARTING_POSITION != this.getCurrentMainCellCoordinates()) {
				getOwnerArea().leaveAreaCells(this, getEnteredCells());
				setCurrentPosition(STARTING_POSITION.toVector());
				getOwnerArea().enterAreaCells(this, getCurrentCells());
				resetMotion();
				respawnSound=true;
				p.scoreIncrease(GHOST_POINT);
			}
		}
		else {
			p.damaged();
		}
	}
	
	@Override
	public void bip(Audio audio) {
		if(respawnSound) {
			sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/eat"));
			sons.shouldBeStarted();
			sons.bip(audio);
			respawnSound=false;
		}
	}
	
	class GhostHandler implements SuperPacmanInteractionVisitor{
		@Override
	    public void interactWith(SuperPacmanPlayer pacman){
			Ghost.this.interactWithPacman();
	    	pacmanPos=pacman.getPacmanMainCellCoordinates();
	    }
	}
}