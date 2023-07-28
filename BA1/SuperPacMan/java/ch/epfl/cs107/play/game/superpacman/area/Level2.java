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

public class Level2 extends SuperPacmanArea{

	private final static DiscreteCoordinates PLAYER_SPAWN_POSITION=new DiscreteCoordinates(15,29);

	@Override
	public String getTitle() {
		return "superpacman/Level2";
	}
	
	protected void createArea() {
        registerActor(new Background(this));
        
	    Key key1 =new Key(this, Orientation.RIGHT, new DiscreteCoordinates(3,16));
		registerActor(key1);
		Key key2 =new Key(this, Orientation.RIGHT, new DiscreteCoordinates(26,16));
		registerActor(key2);
		Key key3 =new Key(this, Orientation.RIGHT, new DiscreteCoordinates(2,8));
		registerActor(key3);
		Key key4 =new Key(this, Orientation.RIGHT, new DiscreteCoordinates(27,8));
		registerActor(key4);
		
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(8,14),key1));
		registerActor(new Gate(this, Orientation.DOWN,new DiscreteCoordinates(5,12),key1));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(8,10),key1));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(8,8),key1));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(21,14),key2));
		registerActor(new Gate(this, Orientation.DOWN,new DiscreteCoordinates(24,12),key2));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(21,10),key2));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(21,8),key2));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(10,2),key3,key4));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(19,2),key3,key4));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(12,8),key3,key4));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(17,8),key3,key4));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(14,3),this));
		registerActor(new Gate(this, Orientation.RIGHT,new DiscreteCoordinates(15,3),this));
		
		registerActor(new Door("superpacman/Temple",(new Temple()).getSpawnPosition(), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(14,0), new DiscreteCoordinates(15,0)));
	}

	@Override
	public DiscreteCoordinates getSpawnPosition() {
		return PLAYER_SPAWN_POSITION;
	}
}