/*
 *	Author:      Romain Logean
 *	Date:        9 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class Key  extends CollectableAreaEntity implements Logic{

	private boolean taken;
	private Sprite sprite;
	
	public Key(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		sprite = new Sprite("superpacman/key", 1.f, 1.f,this);
		taken=false;
	}

	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	
	@Override
	public void interaction(SuperPacmanPlayer p, Area a) {
	super.interaction(p,a);
	taken=true;
	
	}

	@Override
	public boolean isOn() {
		return taken;
	}

	@Override
	public boolean isOff() {
		return !taken;
	}

	@Override
	public float getIntensity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
