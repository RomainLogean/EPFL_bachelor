/*
 *	Author:      Romain Logean
 *	Date:        8 déc. 2020
 */



package ch.epfl.cs107.play.game.areagame.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Acoustics;
import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.superpacman.actor.SuperPacmanPlayer;
import ch.epfl.cs107.play.game.superpacman.handler.SuperPacmanInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Audio;

public abstract class CollectableAreaEntity extends AreaEntity implements Interactable,Acoustics{
	protected SoundAcoustics sons;
	protected boolean playSound=false;

	public CollectableAreaEntity(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
	}
		
	public int getpoints() {
		return 0;
	}
	
    @Override
    public void bip(Audio audio) {
    	if(playSound) {
    		sons= new SoundAcoustics(ResourcePath.getSounds("superpacman/TransactionOk"));
        	sons.shouldBeStarted();
        	sons.bip(audio);
    	}
    }
	
	public void interaction(SuperPacmanPlayer p, Area a) {
		playSound=true;
		a.unregisterActor(this);
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
	

}
