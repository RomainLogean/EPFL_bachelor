/*
 *	Author:      Romain Logean
 *	Date:        8 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGraph;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.superpacman.area.SuperPacmanArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class Gate extends AreaEntity implements Logic {
	SuperPacmanArea area;
	ImageGraphics sprite;
	Logic signal1;
	Logic signal2;
	AreaGraph graph;
	
	
	public Gate(Area area, Orientation orientation, DiscreteCoordinates position, Logic signal1,Logic signal2) {
		super(area,orientation,position);
		if(orientation==Orientation.UP || orientation==Orientation.DOWN) {
			sprite = new Sprite("superpacman/gate", 1.f, 1.f, this, new RegionOfInterest(0, 0, 64, 64));
		}
		else {
			sprite = new Sprite("superpacman/gate", 1.f, 1.f, this, new RegionOfInterest(0, 64, 64, 64));
		}
		this.area= (SuperPacmanArea) area;
		this.signal1=signal1;
		this.signal2=signal2;
		graph=((SuperPacmanArea) this.getOwnerArea()).getBehaviorGraph();
		graph.setSignal(this.getCurrentMainCellCoordinates(), this);
	}	
	
	public Gate(Area area, Orientation orientation, DiscreteCoordinates position, Logic signal) {
		this(area, orientation, position, signal,Logic.TRUE);
	}
	
	public boolean gateSignal() {
		return(signal1.isOn() && signal2.isOn());
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		return !gateSignal();
	}
	
	@Override
	public boolean isCellInteractable() {
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
	}

	@Override
	public void draw(Canvas canvas) {
		if(!gateSignal()) {
		sprite.draw(canvas);
		}
	}

	@Override
	public boolean isOn() {
		return gateSignal();
	}

	@Override
	public boolean isOff() {
		return !gateSignal();
	}

	@Override
	public float getIntensity() {
		return 0;
	}
}
