/*
 *	Author:      Romain Logean
 *	Date:        12 déc. 2020
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

public class Inky extends Ghost{

	private final static int MAX_DISTANCE_WHEN_SCARED=5;
	private final static int MAX_DISTANCE_WHEN_NOT_SCARED=10;
	private final int ORIGINAL_SPEED=SPEED;
	private final int AFRAID_SPEED=10;
	private Sprite[][]sprites;
	private Animation[] animations;
	private Queue<Orientation> path;
	private Path graphicPath = null;

	public Inky(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		sprites = RPGSprite.extractSprites("superpacman/ghost.inky", 2, 1, 1, this, 16, 16, new Orientation[] {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});
		animations = Animation.createAnimations(ANIMATION_DURATION / 2, sprites);
		takeRandomTargetCell();
	}
	
	@Override
	public void takeRandomTargetCell() {
		int maxDistance;
		if(isAfraid) {
			maxDistance=MAX_DISTANCE_WHEN_SCARED;
		}
		else {
			maxDistance=MAX_DISTANCE_WHEN_NOT_SCARED;
		}
		List<DiscreteCoordinates> safeField= new ArrayList<DiscreteCoordinates>();
		for(int x =0;x<=getOwnerArea().getWidth();++x) {
			for(int y=0; y<=getOwnerArea().getHeight();++y) {
				if(DiscreteCoordinates.distanceBetween(STARTING_POSITION, new DiscreteCoordinates(x,y))<=maxDistance) {
					safeField.add(new DiscreteCoordinates(x,y));					
				}
			}
		}
		randomInt = RandomGenerator.getInstance().nextInt(safeField.size());
		targetPos=safeField.get(randomInt);
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
	public void interactWithPacman() {
		if(!isAfraid) {
			targetPos=pacmanPos;
		}
		else {
			pacmanPos=null;
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		if(path != null && cheatMod) {
			graphicPath = new Path(this.getPosition(), new LinkedList<Orientation>(path));
			graphicPath.draw(canvas);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if(isAfraid) {
			SPEED=AFRAID_SPEED;
		}
		else{
			SPEED=ORIGINAL_SPEED;
		}
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
