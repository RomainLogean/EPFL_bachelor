/*
 *	Author:      Romain Logean
 *	Date:        15 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.superpacman.actor.Clyde;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class RouteTemple extends SuperPacmanArea{
	private final static DiscreteCoordinates PLAYER_SPAWN_POSITION=new DiscreteCoordinates(5,5);

	@Override
	public String getTitle() {
		return "superpacman/RouteTemple";
	}
	
	@Override
	protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("superpacman/Temple", new DiscreteCoordinates(4,1), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(5,6)));
        registerActor(new Clyde(this,Orientation.DOWN,new DiscreteCoordinates(1,5),"Félicitation !"));
        registerActor(new Clyde(this,Orientation.UP,new DiscreteCoordinates(4,2),"Merci d'avoir joué!"));
	}
	
	@Override
	public DiscreteCoordinates getSpawnPosition() {
		return PLAYER_SPAWN_POSITION;
	}
}
