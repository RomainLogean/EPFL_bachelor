/*
 *	Author:      Romain Logean
 *	Date:        8 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Cherry extends CollectableAreaEntity{
	private Sprite sprite;

	public Cherry(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		this.sprite = new Sprite("superpacman/cherry", 1.f, 1.f,this);
	}
    
	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	@Override
	public int getpoints() {
		return 200;
	}
	
	@Override
	public void interaction(SuperPacmanPlayer p, Area a) {
		super.interaction(p,a);
		p.scoreIncrease(getpoints());
	}
}
