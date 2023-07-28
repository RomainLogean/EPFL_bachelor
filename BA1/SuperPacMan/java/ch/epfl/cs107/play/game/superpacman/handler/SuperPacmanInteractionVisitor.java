/*
 *	Author:      Romain Logean
 *	Date:        27 nov. 2020
 */

package ch.epfl.cs107.play.game.superpacman.handler;

import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;
import ch.epfl.cs107.play.game.superpacman.actor.Blinky;
import ch.epfl.cs107.play.game.superpacman.actor.Bonus;
import ch.epfl.cs107.play.game.superpacman.actor.Cherry;
import ch.epfl.cs107.play.game.superpacman.actor.Clyde;
import ch.epfl.cs107.play.game.superpacman.actor.Diamond;
import ch.epfl.cs107.play.game.superpacman.actor.Ghost;
import ch.epfl.cs107.play.game.superpacman.actor.Inky;
import ch.epfl.cs107.play.game.superpacman.actor.SuperPacmanPlayer;

public interface SuperPacmanInteractionVisitor extends RPGInteractionVisitor{
	
	default void interactWith(SuperPacmanPlayer p) {}
	default void interactWith(CollectableAreaEntity c) {}
	default void interactWith(Cherry c) {}
	default void interactWith(Bonus b) {}
	default void interactWith(Diamond d) {}
	default void interactWith(Ghost g) {}
	default void interactWith(Inky i) {}
	default void interactWith(Blinky b) {}
	default void interactWith(Clyde c) {}
}
