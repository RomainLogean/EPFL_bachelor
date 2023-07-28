/*
 *	Author:      Romain Logean
 *	Date:        14 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Path;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.superpacman.area.SuperPacmanArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

public class Pinky extends Ghost{
	
	private final static int MIN_AFRAID_DISTANCE = 5;
	private final static int MAX_RANDOM_ATTEMPT = 200;
	private Sprite[][]	sprites;
	private Animation[] animations;
	private Queue<Orientation> path;
	Path graphicPath = null;
		
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if(path != null && cheatMod) {
			graphicPath = new Path(this.getPosition(), new LinkedList<Orientation>(path));
			graphicPath.draw(canvas);
		}
	}

	public Pinky(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		sprites = RPGSprite.extractSprites("superpacman/ghost.pinky", 2, 1, 1, this, 16, 16, new Orientation[] {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});
		animations = Animation.createAnimations(ANIMATION_DURATION / 2, sprites);
	}
	
	@Override
	public Orientation getNextOrientation() {
		SuperPacmanArea area = (SuperPacmanArea)this.getOwnerArea();
		path = area.getBehaviorGraph().shortestPath(getCurrentMainCellCoordinates(), targetPos);
		if(path==null || afraidStatChange()) {
			takeRandomTargetCell();
			return getNextOrientation();
		}
		else {
			return path.poll();
		}
	}

	@Override
	public void takeRandomTargetCell() {
		if(pacmanPos!=null && isAfraid) {
			int attempt=1;
			int x=0;
			int y=0;
			do {
				List<DiscreteCoordinates> safeField= new ArrayList<DiscreteCoordinates>();
				for(x=0;x<=getOwnerArea().getWidth();++x) {
					for(y=0; y<=getOwnerArea().getHeight();++y) {
						safeField.add(new DiscreteCoordinates(x,y));					
					}
				}
				randomInt = RandomGenerator.getInstance().nextInt(safeField.size());
				targetPos=safeField.get(randomInt);
				++attempt;
			}while(attempt<=MAX_RANDOM_ATTEMPT && DiscreteCoordinates.distanceBetween(pacmanPos, new DiscreteCoordinates(x,y))>=MIN_AFRAID_DISTANCE);
			pacmanPos=null;
		}
		else {
			super.takeRandomTargetCell();
		}
	}

	@Override
	public void interactWithPacman() {
		if(!isAfraid) {
			targetPos=pacmanPos;
		}
	}
	
	@Override
	public void animation() {
		this.animations[this.getOrientation().ordinal()].update(ANIMATION_DURATION);
	}

	@Override
	public void specificDraw(Canvas canvas) {
		this.animations[this.getOrientation().ordinal()].draw(canvas);
	}
}
