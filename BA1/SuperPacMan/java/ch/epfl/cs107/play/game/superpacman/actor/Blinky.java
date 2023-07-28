/*
 *	Author:      Romain Logean
 *	Date:        11 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

public class Blinky extends Ghost{
	private Sprite[][] sprites;

	private Animation[] animations;
	

	public Blinky(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		sprites = RPGSprite.extractSprites("superpacman/ghost.blinky", 2, 1, 1, this, 16, 16, new Orientation[] {Orientation.UP, Orientation.RIGHT,Orientation.DOWN, Orientation.LEFT});
		animations = Animation.createAnimations(ANIMATION_DURATION / 2, sprites);
	}
	
	@Override
	public Orientation getNextOrientation() {
		randomInt = RandomGenerator.getInstance().nextInt(4);
		Orientation orientation = Orientation.fromInt(randomInt);
		return orientation;
	}
	
	@Override
	public void animation() {
		animations[this.getOrientation().ordinal()].update(ANIMATION_DURATION);
	}
	
	@Override
	public void specificDraw(Canvas canvas) {
		animations[this.getOrientation().ordinal()].draw(canvas);
	}
	
}
