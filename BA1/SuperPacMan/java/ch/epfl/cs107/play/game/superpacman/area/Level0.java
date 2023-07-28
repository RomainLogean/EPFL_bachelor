/*
 *	Author:      Romain Logean
 *	Date:        27 nov. 2020
 */



package ch.epfl.cs107.play.game.superpacman.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.superpacman.actor.Gate;
import ch.epfl.cs107.play.game.superpacman.actor.Key;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Level0 extends SuperPacmanArea{
	
	private final DiscreteCoordinates PLAYER_SPAWN_POSITION=new DiscreteCoordinates(10,1);
	
	@Override
	public String getTitle() {
		return "superpacman/Level0";
	}

	protected void createArea() {
        registerActor(new Background(this));
        Key key =new Key(this, Orientation.RIGHT, new DiscreteCoordinates(3,4));
		registerActor(key);
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(5,8),key));
		registerActor(new Gate(this, Orientation.LEFT,new DiscreteCoordinates(6,8),key));
		registerActor(new Door("superpacman/Level1", (new Level1()).getSpawnPosition(), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(5,9), new DiscreteCoordinates(6,9)));
	}

	@Override
	public DiscreteCoordinates getSpawnPosition() {
		return PLAYER_SPAWN_POSITION;
	}
}
