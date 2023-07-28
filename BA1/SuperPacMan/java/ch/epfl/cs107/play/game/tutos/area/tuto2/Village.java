/*
 *	Author:      Romain Logean
 *	Date:        17 nov. 2020
 */



package ch.epfl.cs107.play.game.tutos.area.tuto2;
import ch.epfl.cs107.play.game.tutos.area.SimpleArea;
import ch.epfl.cs107.play.game.tutos.area.Tuto2Area;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.tutos.actor.SimpleGhost;


public class Village extends Tuto2Area{

	@Override
	public String getTitle() {
		return "zelda/Village";
	}

	@Override
	protected void createArea() {
		this.registerActor(new SimpleGhost(new Vector (16,6),"ghost.2"));
		this.registerActor(new Background(this));
		this.registerActor(new Foreground(this));
	}

}
