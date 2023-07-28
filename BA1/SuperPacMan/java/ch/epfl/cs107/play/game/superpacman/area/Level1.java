/*
 *	Author:      Romain Logean
 *	Date:        27 nov. 2020
 */



package ch.epfl.cs107.play.game.superpacman.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.superpacman.actor.Gate;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Level1 extends SuperPacmanArea{

	private final static DiscreteCoordinates PLAYER_SPAWN_POSITION=new DiscreteCoordinates(15,6);
	
	@Override
	public String getTitle() {
		return "superpacman/Level1";
	}
	
	protected void createArea() {
        registerActor(new Background(this));
		registerActor(new Door("superpacman/Level2", (new Level2()).getSpawnPosition(), Logic.TRUE, this, Orientation.DOWN, new DiscreteCoordinates(14,0), new DiscreteCoordinates(15,0)));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(14,3),this));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(15,3),this));
	}

	@Override
	public DiscreteCoordinates getSpawnPosition() {
		return PLAYER_SPAWN_POSITION;
		
	}

}
